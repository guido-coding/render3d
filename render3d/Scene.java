package render3d;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public final class Scene {
	
	private final Camera camera;
	private final List<Object3D> objects;
	private CameraOrbit orbit;
	private Background background;
	
	private final CoordinateTranslationAlgorithm coordinateTranslationAlgorithm = new PerspectiveProjectionCoordinateTranslater();
	private final RenderingAlgorithm renderer = new Renderer2(coordinateTranslationAlgorithm);
	private final List<CameraController> cameraControllers;
	
	public static double MIN_DRAW_DISTANCE = 0;
	public static double MAX_DRAW_DISTANCE = 100;
	public static boolean REJECT_FACES_BEHIND = true;
	public static boolean DRAW_POLYGON_COUNTOUR = false;
	public static boolean ANTI_ALIAS = false;
	public static double RADIANS_PER_PIXEL = (0.08*2*Math.PI)/1000;
	public static boolean DRAW_MINIMAP = false;
	
	

	
	
	public Scene() {
		camera = new Camera();
		objects = new CopyOnWriteArrayList<Object3D>();
		cameraControllers = new CopyOnWriteArrayList<>();
	}
	
	
	/**
	 * Report metrics on the scene to be rendered
	 */
	public void report() {
		System.out.println("Scene report:");
		System.out.println("Objects: " + objects.size());
		System.out.println("Polygons: " +
		objects.stream()
		.mapToInt(object -> object.getFaces().size())
		.sum());
		System.out.println("-------------");
	}
	
	
	public CameraMovement createCameraMovement() {
		return new CameraMovement(this);
	}
	
	/**
	 * 
	 * @param controller
	 * @return instance to this Scene object, to be used for method chaining
	 */
	public Scene addCameraController(CameraController controller) {
		if (controller == null) return this;
		cameraControllers.add(controller);
		return this;
	}
	
	/**
	 * 
	 * @param controller
	 * @return instance to this Scene object, to be used for method chaining
	 */
	public Scene removeCameraController(CameraController controller) {
		cameraControllers.remove(controller);
		return this;
	}
	
	/**
	 * 
	 * @param filename filename of image file to be used as background
	 * @return instance to this Scene object, to be used for method chaining
	 * @throws IOException thrown when loading the image file fails
	 */
	public Scene setBackground(String filename) throws IOException {
		background = new MovingBackground(filename);
		return this;
	}
	
	/**
	 * 
	 * @param background
	 * @return instance to this Scene object, to be used for method chaining
	 */
	public Scene setBackground(Background background) {
		this.background = background;
		return this;
	}
	
	
	
	/**
	 * sets parameters for camera orbit in which the camera orbits around a point in cartesian coordinates
	 * @param r radius of sphere
	 * @param orbitAroundX
	 * @param orbitAroundY
	 * @param orbitAroundZ
	 * @return instance to this Scene object, to be used for method chaining
	 */
	public Scene setCameraOrbit(double r, double orbitAroundX, double orbitAroundY, double orbitAroundZ) {
		orbit = new CameraOrbit(camera, r, new Point3D(orbitAroundX,orbitAroundY,orbitAroundZ));
		cameraControllers.add(orbit);
		return this;
	}
	
	
	public void cancelCameraOrbit() {
		cameraControllers.remove(orbit);
		orbit = null;
	}
	
	/**
	 * Sets additional orbit parameters
	 * @param thetaStepSize step size (speed) at which camera orbits in the horizontal plane. Step size is in radians per step. step size typically is << 1.
	 * @param phiStepSize step size (speed) at which the camera orbits in the vertical plane. Step size is in radians per step. step size typically is << 1.
	 * @param minPhi tilt angle in which the camera orbits in the vertical plane. 0.5 is in the fully horizontal plane. 0 is fully in the vertical plane.
	 * @return instance to this Scene object, to be used for method chaining
	 */
	public Scene setOrbitParameters(double thetaStepSize, double phiStepSize, double minPhi) {
		orbit.setOrbitParameters(thetaStepSize, phiStepSize, minPhi);
		return this;
	}
	
	/**
	 * Sets the camera focal point, the point to which the camera is pointed too. camera angles will be automatically updated if the camera location changes.
	 * @param x
	 * @param y
	 * @param z
	 * @return instance to this Scene object, to be used for method chaining
	 */
	public Scene focusOn(double x, double y, double z) {
		camera.centerOn(new Point3D(x,y,z));
		return this;
	}
	
	
	/**
	 * sets location of camera in cartesian coordinates
	 * @param x
	 * @param y
	 * @param z
	 * @return instance to this Scene object, to be used for method chaining
	 */
	public Scene setCamera(double x, double y, double z) {
		camera.setLocation(x, y, z);
		return this;
	}
	
	/**
	 * sets location of camera relative to cartesian coordinates x, y, z and adjust to spherical coordinates r (range), theta (horizontal angle), phi (vertical angle).
	 * @param x
	 * @param y
	 * @param z
	 * @param r
	 * @param theta
	 * @param phi
	 * @return instance to this Scene object, to be used for method chaining
	 */
	public Scene setCamera(double x, double y, double z, double r, double theta, double phi) {
		Point3D p = SphericalCoordinate.toCartesianCoordinate(r, theta, phi);
		camera.setLocation(x+p.x(), y+p.y(), z+p.z());
		camera.centerOn(new Point3D(x,y,z));
		return this;
	}
	
	/**
	 * 
	 * @return get new Point3D instance containing the camera location
	 */
	public Point3D getCameraLocation() {
		return new Point3D(camera.getX(), camera.getY(), camera.getZ());
	}
	
	/**
	 * Deprecated, method does nothing
	 */
	@Deprecated
	public void close() {

	}
	
	/**
	 * 
	 * @param object
	 * @return instance to this Scene object, to be used for method chaining
	 */
	public Scene addObject(Object3D object) {
		objects.add(object);
		return this;
	}
	
	/**
	 * 
	 * @param objects
	 * @return instance to this Scene object, to be used for method chaining
	 */
	public Scene addObjects(Collection<Object3D> objects) {
		this.objects.addAll(objects);
		return this;
	}
	
	/**
	 * 
	 * @param object
	 * @return instance to this Scene object, to be used for method chaining
	 */
	public Scene removeObject(Object3D object) {
		objects.remove(object);
		return this;
	}
	
	/**
	 * 
	 * @return immutable list view of all objects
	 */
	public List<Object3D> getObjects() {
		return Collections.unmodifiableList(objects);
	}
	
	
	/**
	 * get location of point in Scene's cartesian coordinate space x,  y, z.
	 * @param width screen width in pixels
	 * @param height screen height in pixels
	 * @param x
	 * @param y
	 * @param z
	 * @return Point containing screen coordinate (in pixels)
	 */
	public Point getLocationOnScreen(int width, int height, double x, double y, double z) {
		Point3D point = new Point3D(x,y,z);
		point.setSphericalCoordinate(SphericalCoordinate.updatePositionRelativeToCamera(point.x(), point.y(), point.z(), camera.getX(), camera.getY(), camera.getZ()));
		RelativeScreenCoordinate c = coordinateTranslationAlgorithm.getRelativeScreenCoordinate(camera, point);
		
		int maxDimension = Math.max(width, height);
		int xOffset = (width - maxDimension) / 2;
		int yOffset = (height - maxDimension) / 2;
		
		int xP = (int)(maxDimension * c.x() + xOffset); 
		int yP = (int)(maxDimension * c.y() + yOffset);
		
		return new Point(xP, yP);
	}
	
	
	
	/**
	 * update the camera location according to its orbit
	 */
	private void updateScene() {
		cameraControllers.forEach(CameraController::updateCamera);
	}
	
	

	
	/**
	 * updates the camera position and then returns a rendered image
	 * @param width image width
	 * @param height height
	 * @return rendered image
	 */
	public Image next3DView(final int width, final int height) {
		updateScene();
		return get3DView(width, height);
	}
	
	

	/**
	 * returns a rendered image without updating camera position
	 * @param width
	 * @param height
	 * @return
	 */
	public Image get3DView(final int width, final int height) {
		/*
		Image render3D;
		if (background == null) {			
			render3D = renderer.render3DView(camera, getObjects(), width, height);
		} else {
			BufferedImage bg = background.getBackground(camera.getTheta(), camera.getPhi(), width * RADIANS_PER_PIXEL, height*RADIANS_PER_PIXEL);
			render3D = renderer.render3DView(bg, camera, getObjects(), width, height);
		}
		
		Timer.startActivity("minimap");
		if (DRAW_MINIMAP) {
			Graphics g = render3D.getGraphics();
			g.drawImage(getTopView(), width-250, height - 250, null);			
		}
		Timer.startActivity(Timer.IDLE);
		return render3D;
		*/

		
		FutureTask<Image> ft1 = new FutureTask<>(new Callable<>() {
			@Override
			public Image call() throws Exception {
				Image render3D;
				if (background == null) {			
					render3D = renderer.render3DView(camera, objects, width, height);
				} else {
					BufferedImage bg = background.getBackground(camera.getTheta(), camera.getPhi(), width * RADIANS_PER_PIXEL, height*RADIANS_PER_PIXEL);
					render3D = renderer.render3DView(bg, camera, objects, width, height);
				}
				return render3D;
			}
			
		});
		
		FutureTask<Image> ft2 = new FutureTask<>(new Callable<>() {
			@Override
			public Image call() throws Exception {
				if (DRAW_MINIMAP) {
					return getTopView();
				} else {
					return null;
				}
			}
		});
		
		
		Thread.ofVirtual().start(ft1);
		Thread.ofVirtual().start(ft2);
		
		Image render3D;
		try {
			render3D = ft1.get(1000, TimeUnit.MILLISECONDS);
			Image topView = ft2.get(1000, TimeUnit.MILLISECONDS);
			if (topView != null) {
				Graphics g = render3D.getGraphics();
				g.drawImage(topView, width-250, height - 250, null);			
			}
			return render3D;
		} catch (InterruptedException e) {
			return null;
		} catch (ExecutionException e) {
			e.printStackTrace();
			IllegalStateException ex = new IllegalStateException(e.getMessage());
			ex.setStackTrace(e.getStackTrace());
			throw ex;
		} catch (TimeoutException e) {
			e.printStackTrace();
			ft1.cancel(true);
			ft2.cancel(true);
			
			IllegalStateException ex = new IllegalStateException(e.getMessage());
			ex.setStackTrace(e.getStackTrace());
			throw ex;
		}
		
		
	}
	
	
	/**
	 * Create a topview minimap of the scene
	 * @param projectionWidth horizontal section of the cartesian coordinate space of the scene that is included in the minimap
	 * @param projectionHeight vertical section of the cartesian coordinate space of the scene that is included in the minimap
	 * @return
	 */
	public Image getTopView(double projectionWidth, double projectionHeight) {
		return Map2DRenderer.renderTopView(getObjects(), camera, new Point3D(0,0,0), projectionWidth, projectionHeight);
	}
	
	/**
	 * Create a topview minimap of the scene. minimap zoom is adjusted to include the entire scene (including camera position).
	 * @return
	 */
	public Image getTopView() {
		Bounds bounds = getBounds();
		
		double dx = bounds.xMax() - bounds.xMin();
		double dy = bounds.yMax() - bounds.yMin();
		
		double maxDim = 2*Math.max(dx, dy);
		
		return getTopView(maxDim, maxDim);
	}
	
	
	
	/**
	 * get min and max coordinates (x, y, z) of all objects and camera within the cartesian coordinate space of Scene object.
	 * @return
	 */
	public Bounds getBounds() {
		double xMin = 0;
		double xMax = 0;
		double yMin = 0;
		double yMax = 0;
		double zMin = 0;
		double zMax = 0;
		
		for (Object3D object : getObjects()) {
			Point3D objectPoint = object.getLocation();
			for (Face3D face : object.getFaces()) {
				for (Point3D point : face.getPoints()) {
					if (point.x() + objectPoint.x() < xMin) xMin = point.x() + objectPoint.x();
					if (point.x() + objectPoint.x() > xMax) xMax = point.x() + objectPoint.x();
					if (point.y() + objectPoint.y() < yMin) yMin = point.y() + objectPoint.y();
					if (point.y() + objectPoint.y() > yMax) yMax = point.y() + objectPoint.y();
					if (point.z() + objectPoint.z() < zMin) zMin = point.z() + objectPoint.z();
					if (point.z() + objectPoint.z() > zMax) zMax = point.z() + objectPoint.z();
				}
			}
		}

		if (camera.getX() < xMin) xMin = camera.getX();
		if (camera.getX() > xMax) xMax = camera.getX();
		if (camera.getY() < yMin) yMin = camera.getY();
		if (camera.getY() > yMax) yMax = camera.getY();
		if (camera.getZ() < zMin) zMin = camera.getZ();
		if (camera.getZ() > zMax) zMax = camera.getZ();
		
		return new Bounds(xMin, xMax, yMin, yMax, zMin, zMax);
	}
	
	/**
	 * min and max coordinates (x, y, z) of all objects and camera within the cartesian coordinate space of Scene object.
	 */
	public static record Bounds(double xMin, double xMax, double yMin, double yMax, double zMin, double zMax) {
		
	}
	
	
}



