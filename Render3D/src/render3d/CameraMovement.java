package render3d;

public class CameraMovement {
	
	private double theta, phi;
	
	private final double r;
	private final Scene scene;

	public enum MoveDirection {
		FORWARD,
		BACKWARD,
		LEFT,
		RIGHT,
		UP,
		DOWN,
	}
	
	CameraMovement(Scene scene) {
		r = 10;
		this.scene = scene;
	}
	
	/**
	 * Set camera angles towards specified point
	 * @param deltaX
	 * @param deltaY
	 * @param deltaZ
	 */
	public void initializeAngles(double deltaX, double deltaY, double deltaZ) {
		SphericalCoordinate coor = SphericalCoordinate.toSphericalCoordinate(deltaX, deltaY, deltaZ);
		theta = coor.theta();
		phi = coor.phi();
		updateFocalPoint();
	}
	
	/**
	 * set camera angle in vertical plane
	 * @param phi vertical angle
	 */
	public void setVerticalViewAngle(double phi) {
		this.phi = phi;
		updateFocalPoint();
	}
	
	/**
	 * Set camera angle in horizontal plane
	 * @param theta
	 */
	public void setHorizontalViewAngle(double theta) {
		this.theta = theta;
		updateFocalPoint();
	}
	
	/**
	 * Update camera angle in horizontal plane. Both negative and postive angles can be specified.
	 * @param theta
	 */
	public void advanceHorizontalViewAngle(double theta) {
		this.theta += theta;
		updateFocalPoint();
	}
	
	/**
	 * Get next coordinate when moving in the forward direction in a 2D plane (no vertical adjustment). Camera position itself is not updated.
	 * @param distance
	 * @return
	 */
	public Coordinate2D nextMovePoint(double distance) {
		return nextMovePoint(distance, MoveDirection.FORWARD);
	}
	
	/**
	 * Get current camera location in 2D plane
	 * @return
	 */
	public Coordinate2D getCurrentCameraPosition() {
		return new Coordinate2D(scene.getCameraLocation().x(), scene.getCameraLocation().y());
	}
	
	/**
	 * Get current camera location in 3D plane
	 * @return
	 */
	public Coordinate3D getCurrentCameraPosition3D() {
		Point3D location = scene.getCameraLocation();
		return new Coordinate3D(location.x(), location.y(), location.z());
	}
	
	/**
	 * Get next coordinate when moving in the forward direction in a 3D plane (no vertical adjustment). Camera position itself is not updated.
	 * @param distance
	 * @param direction
	 * @return
	 */
	public Coordinate3D nextMovePoint3D(double distance, MoveDirection direction) {
		double offset, movementPhi;
		switch (direction) {
		case FORWARD:
			offset = 0;
			movementPhi = phi;
			if (movementPhi < 0.05 * Math.PI) movementPhi = 0;
			if (movementPhi > 0.95 * Math.PI) movementPhi = Math.PI;
			break;
		case BACKWARD:
			offset = Math.PI;
			movementPhi = Math.PI - phi;
			if (movementPhi < 0.05 * Math.PI) movementPhi = 0;
			if (movementPhi > 0.95 * Math.PI) movementPhi = Math.PI;
			break;
		case LEFT:
			offset = -0.5 * Math.PI;
			movementPhi = 0.5*Math.PI;
			break;
		case RIGHT:
			offset = 0.5 * Math.PI;
			movementPhi = 0.5*Math.PI;
			break;
		case UP:
			offset = 0.5 * Math.PI;
			movementPhi = 0;
			break;
		case DOWN:
			offset = 0.5 * Math.PI;
			movementPhi = Math.PI;
			break;
		default:
			throw new IllegalStateException("Undefined direction");
		}
		
		Point3D camLocation = scene.getCameraLocation();
		Point3D p = SphericalCoordinate.toCartesianCoordinate(distance, theta+offset, movementPhi, camLocation.x(), camLocation.y(), camLocation.z());
		return new Coordinate3D(p.x(), p.y(), p.z());
	}
	
	/**
	 * Get next coordinate when moving in the specified direction in a 2D plane (no vertical adjustment). Camera position itself is not updated.
	 * @param distance
	 * @param direction
	 * @return
	 */
	public Coordinate2D nextMovePoint(double distance, MoveDirection direction) {
		
		double offset;
		switch (direction) {
		case FORWARD:
			offset = 0;
			break;
		case BACKWARD:
			offset = Math.PI;
			break;
		case LEFT:
			offset = -0.5 * Math.PI;
			break;
		case RIGHT:
			offset = 0.5 * Math.PI;
			break;
		default:
			throw new IllegalStateException("Undefined direction");
		}
		
		Point3D camLocation = scene.getCameraLocation();
		
		double x = distance * Math.cos(theta+offset) + camLocation.x();
		double y = distance * Math.sin(theta+offset) + camLocation.y();
		
		return new Coordinate2D(x,y);
	}
	
	/**
	 * Update camera position
	 * @param x
	 * @param y
	 * @param z
	 */
	public void moveCameraTo(double x, double y, double z) {
		scene.setCamera(x, y, z);
		updateFocalPoint();
	}
	
	
	private void updateFocalPoint() {
		Point3D camLocation = scene.getCameraLocation();
		
		Point3D focalPoint = SphericalCoordinate.toCartesianCoordinate(r, theta, phi);
		scene.focusOn(
				focalPoint.x() + camLocation.x(), 
				focalPoint.y() + camLocation.y(), 
				focalPoint.z() + camLocation.z());
	}
	
	
	
	public static record Coordinate2D(double x, double y) {}
	
	public static record Coordinate3D(double x, double y, double z) {}
	
}
