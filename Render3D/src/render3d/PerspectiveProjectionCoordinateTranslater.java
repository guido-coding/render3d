package render3d;

class PerspectiveProjectionCoordinateTranslater implements CoordinateTranslationAlgorithm {
	
	/*
	 * https://computergraphics.stackexchange.com/questions/9992/which-perspective-projection-matrix-to-use
	 * https://www.reddit.com/r/opengl/comments/1ax5zkk/trouble_understanding_perspective_projection/
	 * https://en.wikipedia.org/wiki/Gnomonic_projection
	 * https://github.com/tsoding/formula/blob/main/index.js
	 * https://www.youtube.com/watch?v=U0_ONQQ5ZNM
	 * 
	 */


	PerspectiveProjectionCoordinateTranslater() {
		//System.out.println("initialized");
	}
	
	@Override
	public RelativeScreenCoordinate getRelativeScreenCoordinate(Camera camera, Point3D point, Object3D object) {
		//translate point to camera coordinates system
		Point3D pointInCameraCoordinates = new Point3D(
				point.x() + object.getLocation().x() - camera.getX(), 
				point.y() + object.getLocation().y() - camera.getY(),
				point.z() + object.getLocation().z() - camera.getZ());
		
		//rotate point around z axis to the camera coordinate system
		pointInCameraCoordinates.rotateZ(-camera.getTheta());
		
		//rotate point around y axis to the camera coordinate system
		pointInCameraCoordinates.rotateY(0.5 * Math.PI - camera.getPhi());
		
		//calculate angles
		//horizontal ratio
		double h = Math.atan2(pointInCameraCoordinates.y(), pointInCameraCoordinates.x()); 
		
		//vertical ratio
		double v = Math.atan2(pointInCameraCoordinates.z(), pointInCameraCoordinates.x());
		
		double cam = camera.getViewAngle();
		
		//return result
		RelativeScreenCoordinate screen = new RelativeScreenCoordinate(
				h/cam + 0.5,
				-v/cam + 0.5,
				pointInCameraCoordinates.x());
		
		
		return screen;
	}
	
	/*
	public static void main(String arg[]) {
		Point3D p = new Point3D(10, 0, -50);
		
		Scene scene = new Scene();
		scene
		.focusOn(p.x(), p.y(), -40)
		.setCamera(-100, 0, 0)
		;
		
		PerspectiveProjectionCoordinateTranslater c = new PerspectiveProjectionCoordinateTranslater();
		c.getRelativeScreenCoordinate(scene.getCamera(), p);
	}
	*/

}
