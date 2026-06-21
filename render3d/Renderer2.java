package render3d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

class Renderer2 extends Renderer {

	Renderer2(CoordinateTranslationAlgorithm coordinateTranslationAlgorithm) {
		super(coordinateTranslationAlgorithm);
	}
	
	
	
	@Override
	public Image render3DView(final BufferedImage background, final Camera camera, final List<Object3D> objects, final int width, final int height) {
		final Graphics2D g = obtainGraphics(width, height);
		
		final int maxDimension = Math.max(width, height);
		final int xOffset = (width - maxDimension) / 2;
		final int yOffset = (height - maxDimension) / 2;
		camera.setViewAngle(maxDimension * Scene.RADIANS_PER_PIXEL);
		
		//draw background		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		if (background != null) {
			g.drawImage(background,
					0, 0 , width, height,
					0, 0, background.getWidth(), background.getHeight(),
					null);
		}

		if (Scene.ANTI_ALIAS) {			
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		
		
		//compute and draw polygons
		objects
			.stream()
			.parallel()
			.map(object -> object.updatePositionRelativeToCamera(camera.getX(), camera.getY(), camera.getZ() )) //used to update spherical coordinates in objects relative to camera
			.filter(object -> object.getAverageDistance() > Scene.MIN_DRAW_DISTANCE && (object.getAverageDistance() < Scene.MAX_DRAW_DISTANCE || Scene.MAX_DRAW_DISTANCE < 0) ) //only objects that are within viewing distance
			.map(object -> object.getFaces()) //get all faces from each object
			.flatMap(faces -> faces.stream()) //convert to single stream of faces
			.map(face -> computeDrawingTask(camera, face, maxDimension, xOffset, yOffset)) //compute drawing task for each face
			.filter(Optional::isPresent) //filter out tasks that are off screen
			.map(Optional::get) //unpackage optional to drawing task
			.filter(task -> task != null) //filter out null values, this is the case when face was off screen and no drawing task was added
			.filter(task -> task.distance() >= Scene.MIN_DRAW_DISTANCE) //filter out polygons that are below minimum drawing distance
			.sorted((DrawingTask o1, DrawingTask o2) -> { //sort drawing tasks based on distance to camera
				if (o1.distance() < o2.distance()) return 1;
				else if (o1.distance() == o2.distance()) return 0;
				else return -1;
			})
			.sequential() //draw sequentially to ensure correct drawing sequence
			.forEach(task -> {
				if (task.fillPolygon()) {
					g.setColor(task.backgroundColor());
					g.fillPolygon(task.polygon());								
				}
				
				if (Scene.DRAW_POLYGON_COUNTOUR && task.drawPolygon()) {
					g.setColor(task.contourColor());
					g.drawPolygon(task.polygon());								
				}
			}); //draw polygons
		

		g.dispose();
		return view;
	}
	
	

}
