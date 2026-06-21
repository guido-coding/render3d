package render3d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.Collection;

class Map2DRenderer {
	
	static Image renderTopView(Collection<Object3D> objects, Camera camera, Point3D centerOn, double projectionWidth, double projectionHeight) {
		BufferedImage image = new BufferedImage(200,200, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 200, 200);
		
		
		objects.stream()
		.flatMap(object -> object.getFaces().stream())
		.forEach(face -> {
			Polygon p = toPolygon(face, centerOn, projectionWidth, projectionHeight);
			g.setColor(face.getFillColor());
			g.fillPolygon(p);
		});
		
		

		
		//draw camera
		Point cam = toPoint(new Point3D(camera.getX(), camera.getY(), camera.getZ()), null, centerOn, projectionWidth, projectionHeight);
		g.setColor(Color.BLACK);
		g.fillOval(cam.x - 3, cam.y-3, 6, 6);
		
		//draw horizontal viewing cone
		Point3D camFocus = 
				SphericalCoordinate.toCartesianCoordinate(camera.getR(), camera.getTheta(), camera.getPhi(), camera.getX(), camera.getY(), camera.getZ());
		Point camFocus2D = toPoint(camFocus, null, centerOn, projectionWidth, projectionHeight);
		g.drawLine(cam.x, cam.y, camFocus2D.x, camFocus2D.y);
		
		double viewAngle = camera.getViewAngle()/2;
		double correctionFactor = 1/Math.cos(camera.getPhi() - Math.PI/2);
		viewAngle *= correctionFactor;
		
		camFocus = 
				SphericalCoordinate.toCartesianCoordinate(camera.getR(), camera.getTheta() - viewAngle, camera.getPhi(), camera.getX(), camera.getY(), camera.getZ());
		Point camFocus2DLeft = toPoint(camFocus, null, centerOn, projectionWidth, projectionHeight);
		g.drawLine(cam.x, cam.y, camFocus2DLeft.x, camFocus2DLeft.y);
		
		
		camFocus = 
				SphericalCoordinate.toCartesianCoordinate(camera.getR(), camera.getTheta() + viewAngle, camera.getPhi(), camera.getX(), camera.getY(), camera.getZ());
		Point camFocus2DRight = toPoint(camFocus, null, centerOn, projectionWidth, projectionHeight);
		g.drawLine(cam.x, cam.y, camFocus2DRight.x, camFocus2DRight.y);
		
		g.setColor(new Color(255,255,0,200));
		g.fillArc(cam.x-100,cam.y-100,200,200, -1*(int)Math.toDegrees( camera.getTheta() - viewAngle), -1*(int) Math.toDegrees(2*viewAngle));
		
		
		//draw vertical viewing angle
		g.setColor(Color.BLACK);
		g.drawLine(187, 100, 193, 100);
		
		g.setStroke(new BasicStroke(3));
		int vAngle = (int)(200-200*camera.getPhi()/Math.PI);
		g.drawLine(185, vAngle, 195, vAngle);
		
		//draw other information
		g.drawString("Scene top view", 10, 15);
		g.drawString("Theta angle: %.3f \u03C0".formatted(camera.getTheta()/Math.PI), 10, 180);
		g.drawString("Phi angle: %.3f \u03C0".formatted(camera.getPhi()/Math.PI), 10,192);
		
		g.drawRect(0, 0, 199, 199);
		g.dispose();
		
		return image;
	}
	
	private static Polygon toPolygon(Face3D face, Point3D centerOn, double projectionWidth, double projectionHeight) {
		Polygon polygon = new Polygon();
		
		face.getPoints().stream()
		.map(point -> toPoint(point, face, centerOn, projectionWidth, projectionHeight))
		.forEach(point -> polygon.addPoint(point.x, point.y));
		
		return polygon;
		
	}
	
	private static Point toPoint(Point3D point, Face3D face, Point3D centerOn, double projectionWidth, double projectionHeight) {
		Point3D offset; 
		if (face == null) {
			offset = new Point3D(0,0,0);
		} else {
			offset = face.getParent().getLocation();
		}
		
		
		int x = 100 + (int)(200*((point.x() + offset.x() - centerOn.x())/projectionWidth));
		int y = 100 + (int)(200*((point.y() + offset.y()- centerOn.y())/projectionHeight));
		
		return new Point(x,y);
	}

}
