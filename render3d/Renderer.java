package render3d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@SuppressWarnings("unused")
class Renderer implements RenderingAlgorithm {

	protected Image view;
	
	private final CoordinateTranslationAlgorithm coordinateTranslationAlgorithm;


	
	
	Renderer(CoordinateTranslationAlgorithm coordinateTranslationAlgorithm) {
		System.setProperty("sun.java2d.opengl", "true");
		this.coordinateTranslationAlgorithm = coordinateTranslationAlgorithm;
	}
	
	
	/*
	 * used to easily switch between using a BufferedImage or VolatileImage
	 */
	protected Graphics2D obtainGraphics(int width, int height) {
		//initialize	
		view = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		if (view == null || sizeNOk(width, height)) {			
			//view = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleVolatileImage(width, height);
		}
		
		if (view instanceof BufferedImage image) return image.createGraphics();
		else if (view instanceof VolatileImage image) return image.createGraphics();
		else throw new IllegalStateException("Unexpected image format");
	}
	
	protected boolean sizeNOk(int width, int height) {
		if (view instanceof BufferedImage image) return image.getWidth() != width || image.getHeight() != height;
		else if (view instanceof VolatileImage image) return image.getWidth() != width || image.getHeight() != height;
		else throw new IllegalStateException("Unexpected image format");
	}
	
	
	
	
	
	@Override
	public Image render3DView(Camera camera, List<Object3D> objects, int width, int height) {
		return render3DView(null, camera, objects, width, height);
	}
	
	
	@Override
	public Image render3DView(BufferedImage background, Camera camera, List<Object3D> objects, int width, int height) {
		final Graphics2D g = obtainGraphics(width, height);
		
		final int maxDimension = Math.max(width, height);
		final int xOffset = (width - maxDimension) / 2;
		final int yOffset = (height - maxDimension) / 2;
		camera.setViewAngle(maxDimension * Scene.RADIANS_PER_PIXEL);
		
		if (Scene.ANTI_ALIAS) {			
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		
		//draw background		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		if (background != null) {
			g.drawImage(background,
					0, 0 , width, height,
					0, 0, background.getWidth(), background.getHeight(),
					null);
		}

		//Calculate relative location of all objects to camera
		objects.stream()
			.parallel()
			.forEach(object -> object.updatePositionRelativeToCamera(camera.getX(), camera.getY(), camera.getZ() ));
		
		
		//Sort objects based on distance
		Collections.sort(objects);
		
		//compute polygons and setup drawing tasks
		Map<Face3D, DrawingTask> map = objects.stream()
			.parallel()
			.filter(object -> object.getAverageDistance() > Scene.MIN_DRAW_DISTANCE && (object.getAverageDistance() < Scene.MAX_DRAW_DISTANCE || Scene.MAX_DRAW_DISTANCE < 0) ) //only objects that are within viewing distance
			.map(object -> object.getFaces()) //get all faces from each object
			.flatMap(faces -> faces.stream()) //convert to single stream of faces
			.map(face -> computeDrawingTask(camera, face, maxDimension, xOffset, yOffset)) //compute drawing task for each face
			.filter(Optional::isPresent) //filter out tasks that are off screen
			.map(Optional::get) //unpackage optional to drawing task
			.collect(Collectors.toConcurrentMap(task -> task.face(), task -> task)); //add to map
	
		//draw polygons
		objects.stream()
			.map(object -> object.getFaces()) //get all faces from each object
			.flatMap(faces -> faces.stream()) //convert to single stream
			.map(map::get) //lookup drawing task
			.filter(task -> task != null) //filter out null values, this is the case when face was off screen and no drawing task was added
			.forEach(task -> {
				if (task.fillPolygon) {
					g.setColor(task.backgroundColor);
					g.fillPolygon(task.polygon);								
				}
				
				if (Scene.DRAW_POLYGON_COUNTOUR && task.drawPolygon) {
					g.setColor(task.contourColor);
					g.drawPolygon(task.polygon);								
				}
			}); //draw polygons
		
		g.dispose();
		return view;
	}
	
	
	protected Optional<DrawingTask> computeDrawingTask(Camera camera, Face3D face, int maxDimension, int xOffset, int yOffset) {
		Polygon p = new Polygon();
		
		boolean hasPointOnLeft = false;
		boolean hasPointOnRight = false;
		boolean hasPointOnTop = false;
		boolean hasPointOnBottom = false;
		boolean xInView = false;
		boolean yInView = false;
		boolean allBehind = true;
		
		Object3D object = face.getParent();
		
		double distance = 0;
		
		for (Point3D point : face.getPoints()) {
			//Calculate relative coordinates and actual coordinates and add to polygon
			final RelativeScreenCoordinate relCoor = coordinateTranslationAlgorithm.getRelativeScreenCoordinate(camera, point, object);
			p.addPoint((int)(maxDimension * relCoor.x() + xOffset), (int)(maxDimension * relCoor.y() + yOffset));
			distance += relCoor.distance();
			
			if (relCoor.x() < 0) hasPointOnLeft = true;
			if (relCoor.x() > 1) hasPointOnRight = true;
			if (relCoor.y() < 0) hasPointOnTop = true;
			if (relCoor.y() > 1) hasPointOnBottom = true;
			if (relCoor.x() > 0 && relCoor.x() < 1) xInView = true;
			if (relCoor.y() > 0 && relCoor.y() < 1) yInView = true;
			if (!coordinateTranslationAlgorithm.isBehind(camera, point)) allBehind = false;
		}
		
		if (allBehind && Scene.REJECT_FACES_BEHIND) {
			return Optional.empty();
		}
		
		//Check if the faces that do not have points on screen have points on all sides of the screen in which case the center of the face might also be on screen
		//if not, then return early
		if (xInView && yInView) { 
			//point can be on screen
		} else if (hasPointOnLeft && hasPointOnRight && hasPointOnTop && hasPointOnBottom) {	
			//points on all side of screen, possible in view
		} else if (xInView && hasPointOnTop && hasPointOnBottom) {
			//possibly in view in vertical plane
		} else if (yInView && hasPointOnLeft && hasPointOnRight) {
			//possibly in view in horizontal plane
		} else {
			//System.out.println("Scene.class: Drawing of face rejected");
			return Optional.empty();
		}
		
		distance /= p.npoints;
		
		if (distance < 0) {
			//System.err.println("behind");
			return Optional.empty();
		}
		distance = face.getAverageDistance();
		
		DrawingTask task = new DrawingTask(
				face,
				p, 
				face.getFillColor(),
				face.getContourColor(),
				object.drawPolygon(),
				object.fillPolygon(),
				distance);

		return Optional.of(task);
	}
	
	
	
	protected static record DrawingTask(Face3D face, Polygon polygon, Color backgroundColor, Color contourColor, boolean drawPolygon, boolean fillPolygon, double distance) {	}

	

}
