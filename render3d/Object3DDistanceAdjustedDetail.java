package render3d;

import java.awt.Color;
import java.util.Collection;

class Object3DDistanceAdjustedDetail extends Object3D {

	private final Object3D close, distant;
	private final double switchDistance;
	
	private Object3DDistanceAdjustedDetail(double x, double y, double z, double diameter, Object3D close, Object3D distant, double switchDistance) {
		super(x, y, z, diameter);
		this.close = close;
		this.distant = distant;
		this.switchDistance = switchDistance;
	}
	
	static Object3D createObject3D(Object3D close, Object3D distant, double switchDistance) {
		if (!close.getLocation().equals(distant.getLocation()) || close.diameter != distant.diameter) {
			throw new IllegalArgumentException("Both objects need to have same location and diameter");
		}
		Point3D location = close.getLocation();
		return new Object3DDistanceAdjustedDetail(location.x(), location.y(), location.z(), close.diameter, close, distant, switchDistance);
	}
	
	
	@Override
	public Object3D setColor(Color color) {
		close.setColor(color);
		distant.setColor(color);
		return this;
	}
	
	
	@Override
	public synchronized Object3D rotateX(double angle) {
		close.rotateX(angle);
		distant.rotateX(angle);
		return this;
	}
	
	@Override
	public synchronized Object3D rotateY(double angle) {
		close.rotateY(angle);
		distant.rotateY(angle);
		return this;
	}
	
	@Override
	public synchronized Object3D rotateZ(double angle) {
		close.rotateZ(angle);
		distant.rotateZ(angle);
		return this;
	}
	
	@Override
	public synchronized Object3D rotateAround(Point3D point, double theta) {
		close.rotateAround(point, theta);
		distant.rotateAround(point, theta);
		return this;
	}
	
	@Override
	public synchronized Object3D tiltAround(Point3D point, double phi) {
		close.tiltAround(point, phi);
		distant.tiltAround(point, phi);
		return this;
	}
	
	@Override
	public Object3D setColorAdjuster(ObjectColorModel adjuster) {
		super.setColorAdjuster(adjuster);
		close.setColorAdjuster(adjuster);
		distant.setColorAdjuster(adjuster);
		return this;
	}
	
	private Object3D getCurrentObject() {
		return (close.getAverageDistance() < switchDistance) ? close : distant; 
	}
	
	@Override
	public Object3D drawPolygon(boolean drawPolygon) {
		super.drawPolygon(drawPolygon);
		close.drawPolygon(drawPolygon);
		distant.drawPolygon(drawPolygon);
		return this;
	}
	
	@Override
	public Object3D fillPolygon(boolean fillPolygon) {
		super.fillPolygon(fillPolygon);
		close.fillPolygon(fillPolygon);
		distant.fillPolygon(fillPolygon);
		return this;
	}
	
	@Override
	synchronized Point3D getLocation() {
		return getCurrentObject().getLocation();
	}
	
	@Override
	public synchronized Object3D setLocation(double x, double y, double z) {
		close.setLocation(x, y, z);
		distant.setLocation(x, y, z);
		return this;
	}
	
	@Override
	public synchronized Object3D moveCartesianCoordinates(double deltaX, double deltaY, double deltaZ) {
		close.moveCartesianCoordinates(deltaX, deltaY, deltaZ);
		distant.moveCartesianCoordinates(deltaX, deltaY, deltaZ);
		return this;
	}
	
	public Object3D moveSphericalCoordinates(double r, double theta, double phi) {
		close.moveSphericalCoordinates(r, theta, phi);
		distant.moveSphericalCoordinates(r, theta, phi);
		return this;
	}
	
	@Override
	Collection<Face3D> getFaces() {
		return getCurrentObject().getFaces();
	}
	
	@Override
	Object3D addFace(Face3D face) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	double getAverageDistance() {
		return getCurrentObject().getAverageDistance();
	}
	
	@Override
	synchronized Object3D updatePositionRelativeToCamera(double x, double y, double z) {
		close.updatePositionRelativeToCamera(x, y, z);
		distant.updatePositionRelativeToCamera(x, y, z);
		return this;
	}
	
}


