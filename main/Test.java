package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;

import render3d.Object3DFactory;
import render3d.Scene;

public class Test extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Scene scene;
	private final Image image;
	
	Test() {
		scene = new Scene();
		
		JFrame frame = new JFrame("Demo");
		frame.add(this);
		frame.setSize(1000, 1000);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		
		
		scene
		.focusOn(0, 10, 10)
		.setCamera(-100, 0, 0)
		;

		scene.addObject(
				Object3DFactory.createCube(0, 0, 0)
				.setColor(Color.RED)
				.drawPolygon(true)
			);
		
		double angle = 0.03*Math.PI;
		double phi = 0.0*Math.PI;
		
		scene.addObject(
				Object3DFactory.createCube(0, 0, 0)
				.setColor(Color.GREEN)
				.drawPolygon(true)
				.tiltAround(scene.getCameraLocation(), phi)
				.rotateAround(scene.getCameraLocation(), angle)
			);
		
		
		scene.addObject(
				Object3DFactory.createCube(0, 0, 0)
				.setColor(Color.YELLOW)
				.drawPolygon(true)
				.rotateAround(scene.getCameraLocation(), -angle)
				.tiltAround(scene.getCameraLocation(), -phi)
			);
			
				
		Scene.MAX_DRAW_DISTANCE = -1;
		Scene.MIN_DRAW_DISTANCE = -1;
		Scene.DRAW_MINIMAP = true;
		Scene.DRAW_POLYGON_COUNTOUR = true;
		
		
		
		image = scene.get3DView(1000, 1000);
		
		frame.setVisible(true);
	}
	
	
	public static void main(String arg[]) {
		new Test();
	}
	
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}
	
}
