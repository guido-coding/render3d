package render3d;

/**
 * r = radius
 * theta = angle in horizontal plane
 * phi = angle in vertical plane
 */
record SphericalCoordinate(double r, double theta, double phi) {
	
	
	static SphericalCoordinate toSphericalCoordinate(double x, double y, double z) {
		double r = Math.sqrt(x*x + y*y + z*z);
		double phi = Math.acos(z / r);
		double theta = Math.atan2(y, x);
		
		if (phi < 0) phi += 2* Math.PI;
		if (theta < 0) theta += 2* Math.PI;
		
		return new SphericalCoordinate(r, theta, phi);
	}
	
	static Point3D toCartesianCoordinate(double r, double theta, double phi, double xOffset, double yOffset, double zOffset) {
		double x = r * Math.sin(phi)*Math.cos(theta);
		double y = r * Math.sin(phi)*Math.sin(theta);
		double z = r * Math.cos(phi);
		
		return new Point3D(x + xOffset, y + yOffset, z + zOffset);
	}
	
	static Point3D toCartesianCoordinate(double r, double theta, double phi) {
		return toCartesianCoordinate(r, theta, phi, 0,0,0);
	}
	
	static Point3D toCartesianCoordinate(SphericalCoordinate sphericalCoordinate) {
		return toCartesianCoordinate(sphericalCoordinate.r, sphericalCoordinate.theta, sphericalCoordinate.phi);
	}
	
	static SphericalCoordinate updatePositionRelativeToCamera(double x, double y, double z, double cameraX, double cameraY, double cameraZ) {
		double deltaX = x - cameraX;
		double deltaY = y - cameraY;
		double deltaZ = z - cameraZ;
		
		double r = Math.sqrt(deltaX*deltaX + deltaY*deltaY + deltaZ*deltaZ);
		double phi = Math.acos(deltaZ / r);
		double theta = Math.atan2(deltaY, deltaX);
		
		if (phi < 0) phi += 2* Math.PI;
		if (theta < 0) theta += 2* Math.PI;
		
		
		return new SphericalCoordinate(r, theta, phi);
	}
	
}
