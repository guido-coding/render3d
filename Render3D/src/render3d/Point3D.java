package render3d;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public final class Point3D {
	//coordinate relative to center of mass of an Object3D
	private double x, y, z;
	
	//location in spherical coordinates relative to camera
	private final AtomicReference<SphericalCoordinate> relativeToCam = new AtomicReference<>();
	
	
	Point3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public synchronized double x() {
		return x;
	}
	
	public synchronized double y() {
		return y;
	}
	
	public synchronized double z() {
		return z;
	}
	
	synchronized Point3D copy() {
		return new Point3D(x, y, z);
	}
	
	/**
	 * 
	 * @param angle
	 * @return reference to this object, to be used for method chaining
	 */
	synchronized Point3D rotateZ(double angle) {
		double xNew = x * Math.cos(angle) - y * Math.sin(angle);
		double yNew = x * Math.sin(angle) + y * Math.cos(angle);
		
		x = xNew;
		y = yNew;
		
		return this;
	}
	
	/**
	 * 
	 * @param angle
	 * @return reference to this object, to be used for method chaining
	 */
	synchronized Point3D rotateY(double angle) {
		double xNew = x*Math.cos(angle) + z*Math.sin(angle);
		double zNew = z*Math.cos(angle) - x*Math.sin(angle);
				
		x = xNew;
		z = zNew;
		
		return this;
	}
	
	/**
	 * 
	 * @param angle
	 * @return reference to this object, to be used for method chaining
	 */
	synchronized Point3D rotateX(double angle) {
		double yNew = y*Math.cos(angle) - z*Math.sin(angle);
		double zNew = y*Math.sin(angle) + z*Math.cos(angle);
		y = yNew;
		z = zNew;
		
		return this;
	}
	
	/**
	 * 
	 * @param point
	 * @param theta
	 * @return reference to this object, to be used for method chaining
	 */
	synchronized Point3D rotateAround(Point3D point, double theta) {
		double xNew = (x-point.x()) * Math.cos(theta) - (y-point.y()) * Math.sin(theta);
		double yNew = (x-point.x()) * Math.sin(theta) + (y-point.y()) * Math.cos(theta);
		
		x = xNew + point.x();
		y = yNew + point.y();
		
		rotateZ(theta);
		
		return this;
	}
	
	/**
	 * 
	 * @param point
	 * @param phi
	 * @return reference to this object, to be used for method chaining
	 */
	synchronized Point3D tiltAround(Point3D point, double phi) {
		double xNew = (x-point.x())*Math.cos(phi) + (z-point.z())*Math.sin(phi);
		double zNew = (z-point.z())*Math.cos(phi) - (x-point.x())*Math.sin(phi);
				
		x = xNew + point.x();
		z = zNew + point.z();
		
		rotateY(phi);
		
		return this;
	}
	
	
	

	
	
	/**
	 * 
	 * @param relativeToCam
	 * @return reference to this object, to be used for method chaining
	 */
	Point3D setSphericalCoordinate(SphericalCoordinate relativeToCam) {
		this.relativeToCam.set(relativeToCam);
		return this;
	}
	
	double getR() { 
		SphericalCoordinate relativeToCam = this.relativeToCam.get();
		if (relativeToCam == null) throw new IllegalStateException("Spherical coordinate of point is not initialized");
		return relativeToCam.r();
	}
	
	double getTheta() { 
		SphericalCoordinate relativeToCam = this.relativeToCam.get();
		if (relativeToCam == null) throw new IllegalStateException("Spherical coordinate of point is not initialized");
		return  relativeToCam.theta();
	}
	
	double getPhi() { 
		SphericalCoordinate relativeToCam = this.relativeToCam.get();
		if (relativeToCam == null) throw new IllegalStateException("Spherical coordinate of point is not initialized");
		return relativeToCam.phi();
	}
	
	
	
	@Override
	public boolean equals(Object p) {
		if (this == p) return true;
		if (p instanceof Point3D point) {
			return (x == point.x && y == point.y && point.z == z);
		} else { 
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		int hashCode;
		synchronized(this) {			
			hashCode = Objects.hash(x, y, z);
		}
		return hashCode;
	}
	
	@Override
	public String toString() {
		String value;
		synchronized(this) {
			value = "x: " + x() + "; y: " + y() + "; z: " + z();
		}
		return value;
	}
	
	
	
	
}


