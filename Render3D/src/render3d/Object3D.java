package render3d;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;



public class Object3D implements Comparable<Object3D> {
	
	private final List<Face3D> faces;
	private final AtomicReference<Double> averageDistance;
	private final AtomicReference<ObjectColorModel> colorAdjuster;
	private volatile boolean drawPolygon = true;
	private volatile boolean fillPolygon = true;
	
	private double x, y, z;
	final double diameter;
	
	Object3D() {
		this(0,0,0,0);
	}
	
	Object3D( double diameter) {
		faces = new CopyOnWriteArrayList<Face3D>();
		averageDistance = new AtomicReference<Double>();
		averageDistance.set(0.0);
		
		colorAdjuster = new AtomicReference<>();
		
		this.diameter = diameter;
	}
	
	
	Object3D(double x, double y, double z, double diameter) {
		this(diameter);
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	Object3D(double x, double y, double z) {
		this (x, y, z, 1);
	}
	
	public Object3D setColor(Color color) {
		getFaces().forEach(face -> face.setColor(color));
		return this;
	}
	
	/**
	 * Rotate object around x axis
	 * @param angle rotation angle in radians
	 * @return reference to to this object, to be used for method chaining.
	 */
	public synchronized Object3D rotateX(double angle) {
		getFaces().stream()
		.flatMap(face -> face.getPoints().stream())
		.parallel()
		.forEach(point -> point.rotateX(angle));
		return this;
	}
	
	/**
	 * Rotate object around y axis
	 * @param angle rotation angle in radians
	 * @return reference to to this object, to be used for method chaining.
	 */
	public synchronized Object3D rotateY(double angle) {
		getFaces().stream()
		.flatMap(face -> face.getPoints().stream())
		.parallel()
		.forEach(point -> point.rotateY(angle));
		return this;
	}
	
	/**
	 * Rotate object around z axis
	 * @param angle rotation angle in radians
	 * @return reference to to this object, to be used for method chaining.
	 */
	public synchronized Object3D rotateZ(double angle) {
		getFaces().stream()
		.flatMap(face -> face.getPoints().stream())
		.parallel()
		.forEach(point -> point.rotateZ(angle));
		return this;
	}
	
	/**
	 * Rotate object around a specified point around the z axis. Updates both the object location and rotation of the object itself.
	 * @param point point around which object is rotated
	 * @param theta angle rotation angle in radians
	 * @return reference to to this object, to be used for method chaining.
	 */
	public synchronized Object3D rotateAround(Point3D point, double theta) {
		double xNew = (x-point.x()) * Math.cos(theta) - (y-point.y()) * Math.sin(theta);
		double yNew = (x-point.x()) * Math.sin(theta) + (y-point.y()) * Math.cos(theta);
		
		x = xNew + point.x();
		y = yNew + point.y();
		
		rotateZ(theta);
		
		return this;
	}
	
	/**
	 * Rotate object around a specified point around the y axis. Updates both the object location and rotation of the object itself.
	 * @param point point around which object is rotated
	 * @param phi angle rotation angle in radians
	 * @return reference to to this object, to be used for method chaining.
	 */
	public synchronized Object3D tiltAround(Point3D point, double phi) {
		double xNew = (x-point.x())*Math.cos(phi) + (z-point.z())*Math.sin(phi);
		double zNew = (z-point.z())*Math.cos(phi) - (x-point.x())*Math.sin(phi);
				
		x = xNew + point.x();
		z = zNew + point.z();
		
		rotateY(phi);
		
		return this;
	}
	
	public Object3D setColorAdjuster(ObjectColorModel adjuster) {
		colorAdjuster.set(adjuster);
		faces.forEach(face -> face.setColorAdjuster(adjuster));
		return this;
	}
	
	ObjectColorModel getColorAdjuster() {
		return colorAdjuster.get();
	}
	
	public Object3D drawPolygon(boolean drawPolygon) {
		this.drawPolygon = drawPolygon;
		return this;
	}
	
	public Object3D fillPolygon(boolean fillPolygon) {
		this.fillPolygon = fillPolygon;
		return this;
	}
	
	boolean drawPolygon() {
		return drawPolygon;
	}
	
	boolean fillPolygon() {
		return fillPolygon;
	}
	
	/**
	 * 
	 * @return new instance of an Point3D containing the location (center of mass) of this Object3D
	 */
	synchronized Point3D getLocation() {
		return new Point3D(x, y, z);
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return reference to this object, to be used for method chaining
	 */
	public synchronized Object3D setLocation(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	/**
	 * 
	 * @param deltaX
	 * @param deltaY
	 * @param deltaZ
	 * @return reference to this object, to be used for method chaining
	 */
	public synchronized Object3D moveCartesianCoordinates(double deltaX, double deltaY, double deltaZ) {
		x += deltaX;
		y += deltaY;
		z += deltaZ;
		return this;
	}
	
	/**
	 *  Move object in spherical coordinates
	 * @param r distance that object is moved
	 * @param theta angle in horizontal plane (x-y plane)
	 * @param phi angle in vertical plane (towards z axis)
	 * @return reference to this object, to be used for method chaining
	 */
	public Object3D moveSphericalCoordinates(double r, double theta, double phi) {
		Point3D point = SphericalCoordinate.toCartesianCoordinate(r, theta, phi);
		return moveCartesianCoordinates(point.x(), point.y(), point.z());
	}
	
	
	
	Collection<Face3D> getFaces() {
		return Collections.unmodifiableList(faces);
	}
	
	/**
	 * 
	 * @param face
	 * @return reference to this object, to be used for method chaining
	 */
	Object3D addFace(Face3D face) {
		faces.add(face);
		face.setParent(this);
		return this;
	}
	
	/**
	 * 
	 * @return reference to this object, to be used for method chaining
	 */
	private Object3D updateAverageDistance() {
		double sum = 0;
		for (Face3D face : faces) {
			sum += face.getAverageDistance();
		}
		averageDistance.set(sum / faces.size());
		return this;
	}
	
	/**
	 * 
	 * @return average distance among all points of this object towards the point specified in the method updatePositionRelativeToCamera(double x, double y, double z)
	 */
	double getAverageDistance() {
		return averageDistance.get();
	}

	@Override
	public int compareTo(Object3D o) {
		if (o.getAverageDistance() == getAverageDistance()) return 0; 
		return o.getAverageDistance() <= getAverageDistance() ? -1 : 1;
	}

	/**
	 * Update spherical coordinates of each point in this object relative to the specified location.
	 * @param x
	 * @param y
	 * @param z
	 * @return reference to this object, to be used for method chaining
	 */
	synchronized Object3D updatePositionRelativeToCamera(double x, double y, double z) {
		for (Face3D face : faces) {
			face.updatePositionRelativeToCamera(this.x, this.y, this.z, x, y, z);
		}
		updateAverageDistance();
		Collections.sort(faces);
		return this;
	}
	
	public String toString() {
		return "x: " + x + ", y: " + y + ", z: " + z; 
	}
	

}

