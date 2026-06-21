package render3d;

class CameraOrbit implements CameraController {
	
	private final Camera camera;
	
	private final double r;
	
	private double phi, theta;
	
	private double thetaStepSize = 0.01 * 2*Math.PI;
	private double phiStepSize = 0.005 * 2*Math.PI;
	private double minPhi = 0.3;
	
	private final Point3D orbitAround;
	
	
	
	CameraOrbit(Camera camera, double r, Point3D orbitAround) {
		this(camera, r, orbitAround, orbitAround);
	}
	
	CameraOrbit(Camera camera, double r, Point3D orbitAround, Point3D centerOn) {
		this.camera = camera;
		this.r = r;
		this.orbitAround = orbitAround;
		centerOn(centerOn);
	}
	
	/**
	 * sets parameters for camera orbit in which the camera orbits around a point in cartesian coordinates
	 * 
	 * @param camera
	 * @param r radius of orbit
	 * @param orbitAround
	 * @param centerOn
	 * @param thetaStepSize step size (speed) at which camera orbits in the horizontal plane. Step size is in radians per step. step size typically is << 1.
	 * @param phiStepSize step size (speed) at which the camera orbits in the vertical plane. Step size is in radians per step. step size typically is << 1.
	 * @param minPhi tilt angle in which the camera orbits in the vertical plane. 0.5 is in the fully horizontal plane. 0 is fully in the vertical plane.
	 */
	CameraOrbit(Camera camera, double r, Point3D orbitAround, Point3D centerOn, double thetaStepSize, double phiStepSize, double minPhi) {
		this(camera, r, orbitAround, centerOn);
		setOrbitParameters(thetaStepSize, phiStepSize, minPhi);
	}
	
	void centerOn(Point3D point) {
		camera.centerOn(point);
	}
	
	/**
	 * 
	 * @param thetaStepSize step size for orbit in horizontal plane
	 * @param phiStepSize step size for "wobble" in vertical plane
	 * @param minPhi minimum angle for camera to deviate from poles. for minPhi = 0.5*PI, camera will remain in horizontal plane. minPhi = 0 allows camera to tilt allt he way to poles.
	 */
	synchronized void setOrbitParameters(double thetaStepSize, double phiStepSize, double minPhi) {
		this.thetaStepSize = thetaStepSize;
		this.phiStepSize = phiStepSize;
		this.minPhi = minPhi;
	}
	
	/**
	 * Update camera position within the orbit and update camera locations accordingly
	 */
	public synchronized void updateCamera() {
		theta += thetaStepSize;
		phi += phiStepSize;

		double phiAdj = (((Math.sin(phi)+1)/2)*(1-2*minPhi)+minPhi)*Math.PI;

		double x = r*Math.cos(theta)*Math.sin(phiAdj) + orbitAround.x();
		double y = r*Math.sin(theta)*Math.sin(phiAdj) + orbitAround.y();
		double z = r*Math.cos(phiAdj) + orbitAround.z();		
		
		camera.setLocation(x, y, z);
	}
	
	
}
