package render3d;


class Camera {
	private double x, y, z; //coordinates in cartesian coordinate system
	
	/*
	 * coordinates in sphereical coordinate system. 
	 * theta = horizontal plane, 
	 * phi = vertical plane.
	 * theta --> -pi ... +pi
	 * theta = 0 when looking along the x axis in the positive direction
	 * phi --> 0 ... pi
	 * phi = 0.5*pi when looking in the horizontal plane, 0 when looking up and pi when looking down.
	 * 
	 */
	private double theta, viewphi, r; 
	
	private Point3D focalPoint = new Point3D(0,0,0);
	private double viewangle = 0.1 * Math.PI*2; //camera field angle, can change when screen size changes. A square field is assumed. In case of non square actual fields, the size of the longest edge is taken as the square field dimension and data from the shortest edge are clipped at the ends.
	
	
	Camera(double x, double y, double z) {		
		setLocation(x,y,z);
	}
	
	Camera() {
		this(50,0,0);
	}
	
	synchronized double getX() {
		return x;
	}
	
	synchronized double getY() {
		return y;
	}
	
	synchronized double getZ() {
		return z;
	}
	
	/**
	 * 
	 * @return camera angle relative to focal point in the horizontal plane
	 */
	synchronized double getTheta() {
		return theta;
	}
	
	/**
	 * 
	 * @return camera angle relative to the focal point in the vertical plane
	 */
	synchronized double getPhi() {
		return viewphi;
	}
	
	/**
	 * 
	 * @return distance between camera and focal point
	 */
	synchronized double getR() {
		return r;
	}
	
	synchronized Point3D getFocalPoint() {
		return focalPoint;
	}
	
	/**
	 * Set new location of camera. Spherical coordinates are also updated.
	 * @param x
	 * @param y
	 * @param z
	 */
	synchronized void setLocation(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		updateAngles();
	}
	
	synchronized void setViewAngle(double viewangle) {
		this.viewangle = viewangle;
	}
	
	synchronized double getViewAngle() {
		return viewangle;
	}
	
	void centerOnOrigin() {
		centerOn(new Point3D(0,0,0));
	}
	
	synchronized void centerOn(Point3D point) {
		focalPoint = point;
		updateAngles();
	}
	
	private synchronized void updateAngles() {
		double deltaX = focalPoint.x() - this.x;
		double deltaY = focalPoint.y() - this.y;
		double deltaZ = focalPoint.z() - this.z;
		
		r = Math.sqrt(deltaX*deltaX + deltaY*deltaY + deltaZ*deltaZ); //distance from camera to focal point
		viewphi = Math.acos(deltaZ / r); // vertical camera angle to focal point
		
		theta = Math.atan2(deltaY, deltaX); //horizontal camera angle to focal point (angle in horizontal plane)
		
		if (viewphi < 0) viewphi += 2* Math.PI;
	}
	
	
	

	

}



