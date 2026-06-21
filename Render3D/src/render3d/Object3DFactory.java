package render3d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Object3DFactory {

	
	/**
	 * 
	 * @param objects objects to merge
	 * @return
	 */
	public static Object3D mergeObjects(Object3D... objects) {
		if (objects.length == 0) throw new IllegalArgumentException("No objects to merge");
		
		Point3D location = objects[0].getLocation();
		double diameter = 0;
		for (Object3D o : objects) {
			if (o.diameter > diameter) diameter = o.diameter;
			if (!location.equals(o.getLocation())) throw new IllegalArgumentException("Location of objects must match to be able to merge objects");
		}
		
		Object3D object = new Object3D(location.x(), location.y(), location.z(), diameter);
		
		for (Object3D o : objects) {
			for (Face3D f : o.getFaces()) {
				object.addFace(f);
			}
		}
		
		return object;
	}
	
	/**
	 * set color of each face in the object to the specified color
	 * @param object
	 * @param color
	 * @return
	 */
	public static Object3D setColor(Object3D object, Color color) {
		object.getFaces().forEach(face -> face.setColor(color));
		return object;
	}
	
	
	/**
	 * Create a sphere at location x, y, z with radius 5.
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public static Object3D createSphere(double x, double y, double z) {
		double r = 5;
		Color color = new Color(100,100,255,255);
		return createSphere(x, y, z, r, color);
	}
	
	/**
	 * Create a disk at location x, y, z. Inner radius of disk of innerR and outer radius of outerR.
	 * @param x
	 * @param y
	 * @param z
	 * @param innerR
	 * @param outerR
	 * @param color
	 * @return
	 */
	public static Object3D createDisk(double x, double y, double z, double innerR, double outerR, Color color) {
		Object3D object = new Object3D(x, y, z, outerR);
		
		int steps = 50;
		Point3D previousOuterPoint = null;
		Point3D previousInnerPoint = null;
		
		for (int i=0; i<=steps; i++) {
			double theta = -1 * Math.PI + i*2*Math.PI/steps;
			//calculate position
			double xpOuter = outerR * Math.cos(theta);
			double ypOuter = outerR * Math.sin(theta);
			Point3D pOuter = new Point3D(xpOuter, ypOuter, z);
			
			double xpInner = innerR * Math.cos(theta);
			double ypInner = innerR * Math.sin(theta);
			Point3D pInner = new Point3D(xpInner, ypInner, z);
			
			if (i != 0) {
				Face3D face = new Face3D(new Point3D[] {
						new Point3D(previousInnerPoint.x(), previousInnerPoint.y(), previousInnerPoint.z() ),
						new Point3D(previousOuterPoint.x(), previousOuterPoint.y(), previousOuterPoint.z() ),
						pOuter,
						pInner
				});
				face.setColor(color);
				object.addFace(face);
			}
			previousOuterPoint = pOuter;
			previousInnerPoint = pInner;
		}
		
		return object;
	}
	
	/**
	 * Create a cone at location (top of cone) at x, y, z. Base of cone with diameter base and height of cone (base to top) of length height.
	 * @param x
	 * @param y
	 * @param z
	 * @param height
	 * @param base
	 * @return
	 */
	public static Object3D createCone(double x, double y, double z, double height, double base) {
		return createCone(x, y, z, height, base, 20);
	}
	
	/**
	 * Create a cone at location (top of cone) at x, y, z. Base of cone with diameter base and height of cone (base to top) of length height.
	 * @param x
	 * @param y
	 * @param z
	 * @param height
	 * @param base
	 * @param steps number of polygons used to render the cone, excluding the base plate of the cone.
	 * @return
	 */
	public static Object3D createCone(double x, double y, double z, double height, double base, int steps) {
		Object3D object = new Object3D(x, y, z, height);
		
		Point3D[] basePoints = new Point3D[steps+1];
		
		for (int i=0; i<=steps; i++) {
			double theta = -1 * Math.PI + i*2*Math.PI/steps;
			//calculate position
			double xpOuter = base*0.5 * Math.cos(theta);
			double ypOuter = base*0.5 * Math.sin(theta);
			basePoints[i] = new Point3D(xpOuter, ypOuter, height);
			
			if (i != 0) {
				Face3D face = new Face3D(new Point3D[] {
						new Point3D(basePoints[i-1].x(), basePoints[i-1].y(), basePoints[i-1].z() ),
						new Point3D(basePoints[i].x(), basePoints[i].y(), basePoints[i].z() ),
						new Point3D(0, 0, 0)
				});
				object.addFace(face);
			}

		}
		
		object.addFace(new Face3D(basePoints));
		
		return object;
	}
	
	/**
	 * Create a cylinder at location (top of cone) at x, y, z. Bases of cylinder with diameter base and height of cylinder (base to top) of length height.
	 * @param x
	 * @param y
	 * @param z
	 * @param height
	 * @param base
	 * @return
	 */
	public static Object3D createCylinder(double x, double y, double z, double height, double base) {
		return createCylinder(x, y, z, height, base, 20);
	}
	
	/**
	 * Create a cylinder at location (top of cone) at x, y, z. Bases of cylinder with diameter base and height of cylinder (base to top) of length height.
	 * @param x
	 * @param y
	 * @param z
	 * @param height
	 * @param base
	 * @param steps number of polygons used to render the cylinder, excluding the base plates of the cylinder.
	 * @return
	 */
	public static Object3D createCylinder(double x, double y, double z, double height, double base, int steps) {
		Object3D object = new Object3D(x, y, z, height);
		
		Point3D[] basePoints = new Point3D[steps+1];
		Point3D[] topPoints = new Point3D[steps+1];
		
		for (int i=0; i<=steps; i++) {
			double theta = -1 * Math.PI + i*2*Math.PI/steps;
			//calculate position
			double xpOuter = base*0.5 * Math.cos(theta);
			double ypOuter = base*0.5 * Math.sin(theta);
			basePoints[i] = new Point3D(xpOuter, ypOuter, height);
			topPoints[i] = new Point3D(xpOuter, ypOuter, 0);
			
			if (i != 0) {
				Face3D face = new Face3D(new Point3D[] {
						new Point3D(basePoints[i-1].x(), basePoints[i-1].y(), basePoints[i-1].z() ),
						new Point3D(basePoints[i].x(), basePoints[i].y(), basePoints[i].z() ),
						new Point3D(basePoints[i].x(), basePoints[i].y(), 0 ),
						new Point3D(basePoints[i-1].x(), basePoints[i-1].y(), 0 ),
				});
				object.addFace(face);
			}

		}
		
		object.addFace(new Face3D(basePoints));
		object.addFace(new Face3D(topPoints));
		
		return object;
	}
	
	/**
	 * create a sphere at location x, y, z with radius r.
	 * 20 x 20 (=400) polygons are used
	 * @param x
	 * @param y
	 * @param z
	 * @param r
	 * @param color
	 * @return
	 */
	public static Object3D createSphere(double x, double y, double z, double r, Color color) {
		return createSphere(x, y, z, r, color, 20);
	}
	
	/**
	 * create a sphere at location x, y, z with radius r.
	 * steps x steps polygons are used
	 * @param x
	 * @param y
	 * @param z
	 * @param r
	 * @param color
	 * @param steps
	 * @return
	 */
	public static Object3D createSphere(double x, double y, double z, double r, Color color, int steps) {
		
		Object3D object = new Object3D(x, y, z, r);
		
		Point3D[] previouspoints = null;
		//Iterate from bottom to top
		for (int i=0; i<=steps; i++) {
			double phi = i*Math.PI/steps;
			
			//interate over horizontal plane
			Point3D[] points = new Point3D[steps+1];
			for (int j=0; j<=steps; j++) {
				double theta = -1 * Math.PI + j*2*Math.PI/steps;
				
				//calculate position
				double xp = r * Math.sin(phi)*Math.cos(theta);
				double yp = r * Math.sin(phi)*Math.sin(theta);
				double zp = r * Math.cos(phi);
				
				points[j] = new Point3D(xp, yp, zp);
			}
			if (i != 0) {
				for (int j=0; j<steps; j++) {
					Face3D face = new Face3D(new Point3D[] {
							previouspoints[j].copy(),
							previouspoints[j+1].copy(),
							points[j+1].copy(),
							points[j].copy()
					});
					face.setColor(color);
					object.addFace(face);
				}
			}
			previouspoints = points;
		}
		
		return object;
	}
	
	/**
	 * Creates a 3D polygon at location 0, 0, 0
	 * @param x
	 * @param y
	 * @param z
	 * @param color
	 * @return
	 */
	public static Object3D createPolygon3D(double x[], double y[], double z[], Color color) {
		return createPolygon3D(0,0,0,x, y, z, color);
	}
	
	/**
	 * Creates a 3D polygon at location xRef, yRef, zRef
	 * @param xRef
	 * @param yRef
	 * @param zRef
	 * @param x
	 * @param y
	 * @param z
	 * @param color
	 * @return
	 */
	public static Object3D createPolygon3D(double xRef, double yRef, double zRef, double x[], double y[], double z[], Color color) {
		if (x.length!= y.length || y.length != z.length) {
			throw new IllegalArgumentException("Dimensions of point vectors do not match");
		}
		
		Point3D[] points = new Point3D[x.length];
		for (int i=0; i<points.length; i++) {
			points[i] = new Point3D(x[i], y[i], z[i]);
		}
		
		Face3D face = new Face3D(points);
		face.setColor(color);
		
		
		Object3D object = new Object3D(xRef, yRef, zRef);
		object.addFace(face);
		
		return object;
		
	}
	
	
	
	/**
	 * Creates a piramid at location x, y, z.
	 * @param x
	 * @param y
	 * @param z
	 * @param height
	 * @param base
	 * @return
	 */
	public static Object3D createPiramid(double x, double y, double z, double height, double base) {
		Object3D object = new Object3D(x, y, z);
		
		object.addFace( //front
				new Face3D(new Point3D[] {
					new Point3D(0, 0, 0), //top
					new Point3D(-base/2, base/2, height),
					new Point3D(base/2, base/2, height),
				}));
		
		object.addFace( //right side
				new Face3D(new Point3D[] {
					new Point3D(0, 0, 0), //top
					new Point3D(base/2, base/2, height),
					new Point3D(base/2, -base/2, height),
				}));
		
		object.addFace( //left side
				new Face3D(new Point3D[] {
					new Point3D(0, 0, 0), //top
					new Point3D(-base/2, base/2, height),
					new Point3D(-base/2, -base/2, height),
				}));
		
		object.addFace( //back
				new Face3D(new Point3D[] {
					new Point3D(0, 0, 0), //top
					new Point3D(-base/2, -base/2, height),
					new Point3D(base/2, -base/2, height),
				}));
		
		object.addFace( //bottom
				new Face3D(new Point3D[] {
					new Point3D(-base/2, base/2, height),
					new Point3D(base/2, base/2, height),
					new Point3D(base/2, -base/2, height),
					new Point3D(-base/2, -base/2, height),
				}));
		
		return object;
	}
	
	/**
	 * Creates a rectangular prism starting at location x1, y1, z1 towards location x2, y2, z2. dimension of the other sides is set to width. 
	 * @param x1
	 * @param y1
	 * @param z1
	 * @param x2
	 * @param y2
	 * @param z2
	 * @param width
	 * @return
	 */
	public static Object3D createRectangularPrism(double x1, double y1, double z1, double x2, double y2, double z2, double width) {
		
		double phi = Math.atan2(y2-y1, x2-x1);
		double r = Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1) + (z2-z1)*(z2-z1));
		double theta = Math.acos((z2-z1)/r);
		
		double xOffsetHorizontal = width * Math.sin(phi);
		double yOffsetHorizontal = -1*width * Math.cos(phi); 
		
		theta += Math.PI/2;
		theta %= Math.PI;
		
		double xOffsetVertical = width * Math.cos(phi) * Math.sin(theta);
		double yOffsetVertical = width * Math.sin(phi) * Math.sin(theta);
		double zOffset = width * Math.cos(theta);
		
		double xCenter = (x2 + x1)/2;
		double yCenter = (y2 + y1)/2;
		double zCenter = (z2 + z1)/2;
		
		Object3D object = new Object3D(xCenter, yCenter, zCenter);
		
		x1 -= xCenter;
		x2 -= xCenter;
		y1 -= yCenter;
		y2 -= yCenter;
		z1 -= zCenter;
		z2 -= zCenter;
		
		object.addFace( //front
			new Face3D(new Point3D[] {
				new Point3D(x1, y1, z1), //bottom left
				new Point3D(x2, y2, z2), //bottom right
				new Point3D(x2+xOffsetVertical, y2+yOffsetVertical, z2+zOffset), //top right
				new Point3D(x1+xOffsetVertical, y1+yOffsetVertical, z1+zOffset), //top left
			}));
		
		object.addFace( // back
				new Face3D(new Point3D[] {
					new Point3D(x1+xOffsetHorizontal, y1+yOffsetHorizontal, z1), //bottom left
					new Point3D(x2+xOffsetHorizontal, y2+yOffsetHorizontal, z2),	// bottom right
					new Point3D(x2+xOffsetHorizontal + xOffsetVertical, y2+yOffsetHorizontal+yOffsetVertical, z2+zOffset), //top right
					new Point3D(x1+xOffsetHorizontal + xOffsetVertical, y1+yOffsetHorizontal+yOffsetVertical, z1+zOffset), // top left
				}));
		
		object.addFace( //bottom
				new Face3D(new Point3D[] {
						new Point3D(x1, y1, z1), //front left
						new Point3D(x2, y2, z2),	// front right
						new Point3D(x2+xOffsetHorizontal, y2+yOffsetHorizontal, z2), //back right	
						new Point3D(x1+xOffsetHorizontal, y1+yOffsetHorizontal, z1), //back left
				}));
		
		object.addFace( //top
				new Face3D(new Point3D[] {
						new Point3D(x2 + xOffsetVertical, y2+yOffsetVertical, z2+zOffset), //right front
						new Point3D(x1 + xOffsetVertical, y1+yOffsetVertical, z1+zOffset), //left front
						new Point3D(x1+xOffsetHorizontal + xOffsetVertical, y1+yOffsetHorizontal+yOffsetVertical, z1+zOffset), //left back
						new Point3D(x2+xOffsetHorizontal + xOffsetVertical, y2+yOffsetHorizontal+yOffsetVertical, z2+zOffset), //right back
				}));

		return object;
	}
	
	/**
	 * Create a cube at location x, y, z with dimension size.
	 * @param size
	 * @param x
	 * @param y
	 * @param z
	 * @param color
	 * @return
	 */
	public static Object3D createCube(double size, double x, double y, double z, Color color) {
		Object3D object = new Object3D(x, y, z, size);
		
		x = 0; y = 0; z = 0;
		
		List<Face3D> faces = new ArrayList<Face3D>();
		Face3D face = new Face3D(new Point3D[] {
				new Point3D(x, y, z),
				new Point3D(x, y+size, z),
				new Point3D(x, y+size, z+size),
				new Point3D(x, y, z+size),
		});
		face.setColor(color);
		faces.add(face);
		
		face = new Face3D(new Point3D[] {
				new Point3D(x, y+size, z),
				new Point3D(x+size, y+size, z),
				new Point3D(x+size, y+size, z+size),
				new Point3D(x, y+size, z+size),
		});
		face.setColor(color);
		faces.add(face);
		
		face = new Face3D(new Point3D[] {
				new Point3D(x, y, z),
				new Point3D(x+size, y, z),
				new Point3D(x+size, y, z+size),
				new Point3D(x, y, z+size),
		});
		face.setColor(color);
		faces.add(face);
		
		face = new Face3D(new Point3D[] {
				new Point3D(x+size, y, z),
				new Point3D(x+size, y+size, z),
				new Point3D(x+size, y+size, z+size),
				new Point3D(x+size, y, z+size),
		});
		face.setColor(color);
		faces.add(face);
		
		face = new Face3D(new Point3D[] {
				new Point3D(x, y, z),
				new Point3D(x, y+size, z),
				new Point3D(x+size, y+size, z),
				new Point3D(x+size, y, z),
		});
		face.setColor(color);
		faces.add(face);
		
		face = new Face3D(new Point3D[] {
				new Point3D(x, y, z+size),
				new Point3D(x, y+size, z+size),
				new Point3D(x+size, y+size, z+size),
				new Point3D(x+size, y, z+size),
		});
		face.setColor(color);
		faces.add(face);
		
		for (Face3D f : faces) {			
			object.addFace(f);
		}
		return object;
	}
	
	/**
	 * Create a cube at location x, y, z with dimension 10.
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public static Object3D createCube(double x, double y, double z) {
		Object3D object = new Object3D(x, y, z, 10);
		
		x = 0; y = 0; z = 0;
		
		List<Face3D> faces = new ArrayList<Face3D>();
		Face3D face = new Face3D(new Point3D[] {
				new Point3D(5 + x, -5 + y, -5+z),
				new Point3D(5 + x, -5 + y, 5+z),
				new Point3D(5 + x, 5 + y, 5+z),
				new Point3D(5 + x, 5 + y, -5+z)
		});
		face.setColor(new Color(255,100,100));
		faces.add(face);
		
		face = new Face3D(new Point3D[] {
				new Point3D(-5+x, 5+y, -5+z),
				new Point3D(-5+x, 5+y, 5+z),
				new Point3D(5+x, 5+y, 5+z),
				new Point3D(5+x, 5+y, -5+z)
		});
		face.setColor(new Color(255,255,100));
		faces.add(face);
		
		face = new Face3D(new Point3D[] {
				new Point3D(-5+x, -5+y, -5+z),
				new Point3D(-5+x, -5+y, 5+z),
				new Point3D(5+x, -5+y, 5+z),
				new Point3D(5+x, -5+y, -5+z)
		});
		face.setColor( new Color(100,255,255));
		faces.add(face);
		
		face = new Face3D(new Point3D[] {
				new Point3D(-5+x, -5+y, -5+z),
				new Point3D(-5+x, -5+y, 5+z),
				new Point3D(-5+x, 5+y, 5+z),
				new Point3D(-5+x, 5+y, -5+z)
		});
		face.setColor( new Color(100,100,255));
		faces.add(face);
		
		face = new Face3D(new Point3D[] {
				new Point3D(-5+x, -5+y, -5+z),
				new Point3D(-5+x, 5+y, -5+z),
				new Point3D(5+x, 5+y, -5+z),
				new Point3D(5+x, -5+y, -5+z)
		});
		face.setColor( new Color(100,100,100));
		faces.add(face);
		
		face = new Face3D(new Point3D[] {
				new Point3D(-5+x, -5+y, 5+z),
				new Point3D(-5+x, 5+y, 5+z),
				new Point3D(5+x, 5+y, 5+z),
				new Point3D(5+x, -5+y, 5+z)
		});
		face.setColor( new Color(100,100,100));
		faces.add(face);
		
		for (Face3D f : faces) {			
			object.addFace(f);
		}
		return object;
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param width
	 * @param length
	 * @param steps
	 * @return
	 */
	public static Object3D createPlane(final double x, final double y, final double z, final double width, final double length, int steps) {
		Object3D object = new Object3D(x, y, z, length);
		
		double dx = width / steps;
		double dy = length / steps;
		
		for (int i=0; i<steps;i++) {
			for (int j=0; j<steps; j++) {
				object.addFace(new Face3D(new Point3D[] {
						new Point3D(i*dx - width/2, j*dy - length/2, 0),
						new Point3D(i*dx+dx - width/2, j*dy - length/2, 0),
						new Point3D(i*dx+dx - width/2, j*dy+dy - length/2, 0),
						new Point3D(i*dx - width/2, j*dy+dy - length/2, 0),
				}));
				
				
			}
		}
		
		return object;
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param width
	 * @param length
	 * @param steps
	 * @param switchDistance
	 * @return
	 */
	public static Object3D createProxyPlane(final double x, final double y, final double z, final double width, final double length, int steps, double switchDistance) {
		Object3D close = createPlane(x, y, z, width, length, steps);
		
		Object3D distant = new Object3D(x,y,z,length);
		distant.addFace(new Face3D(new Point3D[] {
				new Point3D(-width/2, -length/2, 0),
				new Point3D(width/2, -length/2, 0),
				new Point3D(width/2, length/2, 0),
				new Point3D(-width/2, length/2, 0),
		}));
	
		
		
		return Object3DDistanceAdjustedDetail.createObject3D(close, distant, switchDistance);
	}
	
	
}
