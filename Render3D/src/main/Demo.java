package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import render3d.ColorAdjusterFactory;
import render3d.Object3D;
import render3d.Object3DFactory;
import render3d.ObjectColorModel;
import render3d.Scene;

/**
 * Demonstration of render3d
 */
@SuppressWarnings("serial")
public class Demo extends JPanel {

	private final Scene scene;
	private Image image;
	private volatile boolean running = true;
	
	private Object3D o2 = Object3DFactory.createCube(-5, 0, -10);
	
	
	Demo() {
		scene = new Scene();
		buildScene();
		
		JFrame frame = new JFrame("Demo");
		frame.add(this);
		frame.setSize(1000, 1000);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				running = false;
			}
		});
		frame.setVisible(true);
		
		Thread t = new Thread(() -> {
			while (running) {
				image = scene.next3DView(getSize().width, getSize().height);
				o2.rotateZ(0.15);
				o2.rotateX(0.08);
				o2.rotateY(-0.06);
				repaint();
				try {
					Thread.sleep(20);
				} catch(Exception e) {}
			}
		});
		t.start();
	}
	
	private void buildScene() {
		
		Object3D o1 = Object3DFactory.createSphere(8,0,0, 5, new Color(230,250,50))
		.setColorAdjuster(ColorAdjusterFactory.getType2ColorAdjuster());
		scene.addObject(o1);
		
		//Object3D o2 = Object3DFactory.createCube(-5, 0, -10);
		//o2.rotate(-1);
		scene.addObject(o2);
		
		scene.addObject(Object3DFactory.createRectangularPrism(0, 0, 0, 10, 0, 0, 0.2));
		scene.addObject(Object3DFactory.createRectangularPrism(0, 0, 0, 0, 10, 0, 0.2));
		scene.addObject(Object3DFactory.createRectangularPrism(0, 0, 0, 0, 0, 10, 0.2));
		
		scene.addObject(Object3DFactory.createPiramid(3, 3, 3, 10, 10)
				.setColor(new Color(255,0,0,150))
				.drawPolygon(true)
				.setColorAdjuster(new ObjectColorModel(ObjectColorModel::unadjustedColor, ObjectColorModel::blackColor))
				.rotateX(Math.PI/2));
		
		scene.addObject(Object3DFactory.createCone(3, 3, 3, 10, 10)
				.setColor(new Color(255,100,0,150))
				.drawPolygon(true)
				.setColorAdjuster(new ObjectColorModel(ObjectColorModel::unadjustedColor, ObjectColorModel::blackColor))
				.rotateX(Math.PI*1.5)
		);
		
		scene.addObject(Object3DFactory.createCylinder(15, 3, 8, 10, 10)
				.setColor(new Color(255,100,0,150))
				.drawPolygon(true)
				.setColorAdjuster(new ObjectColorModel(ObjectColorModel::unadjustedColor, ObjectColorModel::blackColor))
				.rotateX(Math.PI*0.3)
				.moveSphericalCoordinates(10, 0, 0.5)
		);
		
		scene.addObject(Object3DFactory.createProxyPlane(15, 3, 8, 10, 10, 3, 10)
				.setColor(new Color(255,100,0,150))
				.drawPolygon(true)
				.setColorAdjuster(new ObjectColorModel(ObjectColorModel::unadjustedColor, ObjectColorModel::blackColor))
				//.rotateX(Math.PI*0.3)
				//.moveSphericalCoordinates(10, 0, 0.5)
			);
		
		scene.addObject(Object3DFactory.createProxyPlane(15, 3, 8, 10, 10, 3, 10)
				.setColor(new Color(255,100,0,150))
				.drawPolygon(true)
				.setColorAdjuster(new ObjectColorModel(ObjectColorModel::unadjustedColor, ObjectColorModel::blackColor))
				.rotateX(Math.PI*0.5)
				//.moveSphericalCoordinates(10, 0, 0.5)
			);
		
		Scene.MAX_DRAW_DISTANCE = -1;
		
		scene
		.setCameraOrbit(100, 0,0,0)
		.setOrbitParameters(0.05, 0.03, 0.1)
		.focusOn(0, 0, 0)
		;
		
		Scene.RADIANS_PER_PIXEL = (0.07*2*Math.PI)/1000;
		Scene.DRAW_POLYGON_COUNTOUR = true;
		Scene.DRAW_MINIMAP = true;
		
		scene.report();
	}
	
	public static void main(String arg[]) {
		new Demo();
	}
	
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}
	
}
