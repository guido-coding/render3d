package render3d;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

public final class Face3D implements Comparable<Face3D> {
	
	private final List<Point3D> points;
	private final AtomicReference<Color> color = new AtomicReference<>();
	private final AtomicReference<Double> averageDistance = new AtomicReference<>();
	private final AtomicReference<Object3D> parentObject = new AtomicReference<>();
	private final AtomicReference<ObjectColorModel> colorAdjuster = new AtomicReference<>();
	
	
	Face3D(Point3D[] points) {
		this.points = new CopyOnWriteArrayList<Point3D>();
		averageDistance.set(0.0);
		color.set(Color.BLACK);
		for (Point3D point : points) {
			this.points.add(point);
		}
		color.set(Color.BLACK);
	}
	
	
	Collection<Point3D> getPoints() {
		return Collections.unmodifiableList(points);
	}
	
	public Face3D setColorAdjuster(ObjectColorModel adjuster) {
		colorAdjuster.set(adjuster);
		return this;
	}
	
	ObjectColorModel getColorAdjuster() {
		return colorAdjuster.get();
	}
	
	
	Face3D setParent(Object3D object) {
		parentObject.set(object);
		return this;
	}
	
	Object3D getParent() {
		return parentObject.get();
	}

	
	Face3D setColor(Color color) {
		this.color.set(color);
		return this;
	}
	
	Color getColor() {
		return color.get();
	}
	
	Color getFillColor() {
		//return color;
		
		ObjectColorModel colorAdjuster = getColorAdjuster();
		if (colorAdjuster != null) {
			return colorAdjuster.getAdjustedColor(color.get(), this, parentObject.get());
		} else {			
			return color.get();
		}
		
	}
	
	Color getContourColor() {
		//return color;
		
		ObjectColorModel colorAdjuster = getColorAdjuster();
		if (colorAdjuster != null) {
			return colorAdjuster.getContourColor(color.get(), null, parentObject.get());
		} else {			
			return Color.BLACK;
		}
		
	}
	
	
	private void updateAverageDistance() {
		double sum = 0;
		for (Point3D point : points) {
			sum += point.getR();
		}
		averageDistance.set(sum/points.size());
	}
	
	double getAverageDistance() {
		return averageDistance.get();
	}

	@Override
	public int compareTo(Face3D o) {
		if (o.getAverageDistance() == getAverageDistance()) return 0; 
		return o.getAverageDistance() <= getAverageDistance() ? -1 : 1;
	}
	
	void updatePositionRelativeToCamera(double xOffset, double yOffset, double zOffset, double camX, double camY, double camZ) {
		for (Point3D point : points) {
			point.setSphericalCoordinate(SphericalCoordinate.updatePositionRelativeToCamera(point.x() + xOffset, point.y() + yOffset, point.z() + zOffset, camX, camY, camZ));
		}
		updateAverageDistance();
	}
	
}