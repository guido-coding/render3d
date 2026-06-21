package render3d;

interface CoordinateTranslationAlgorithm {
	
	
	default RelativeScreenCoordinate getRelativeScreenCoordinate(Camera camera, Point3D point) {
		return getRelativeScreenCoordinate(camera, point,  new Object3D(0,0,0));
	}
	
	/**
	 * 
	 * @param camera reference to Camera object from which the scene is observed
	 * @param point reference to Point3D object for which the coordinate is calculated
	 * @param object reference to the Object3D object for which the point is part of
	 * @return
	 */
	RelativeScreenCoordinate getRelativeScreenCoordinate(Camera camera, Point3D point, Object3D object);
	
	default boolean isBehind(Camera camera, Point3D point) {
		double check = (point.getTheta() - camera.getTheta())/Math.PI;
		if (check > 2) check -= 2;
		if (check < 0) check += 2;
		return (check > 0.5 && check < 1.5); // point is past vertical plane
	}

}

/**
 * Relative screen coordinate where
 *  x --> 0..1 (0=left, 0.5=center, 1=right)
 *  y --> 0..1 (0=top, 0.5=center, 1=bottom)
 *  distance from camera, used to draw objects in appropriate sequence
 */
record RelativeScreenCoordinate(double x, double y, double distance) {
}