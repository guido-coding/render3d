package render3d.userinput;


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;

import render3d.CameraMovement;

public class BasicSwingInputHandler extends AbstractInputHandler implements MouseMotionListener, KeyListener {
	


	
	private BasicSwingInputHandler(CameraMovement camMovement, ValidMoveChecker validMoveChecker) {
		super(camMovement, validMoveChecker);
	}
	
	/**
	 * Creates input handler where camera movement is restricted as specified according to the provided ValidMoveChecker.
	 * @param component JFrame component to which event handlers will be subscribed.
	 * @param scene
	 * @param validMoveChecker
	 * @return
	 */
	public static AbstractInputHandler createBasicSwingInputHandler(JFrame component, CameraMovement camMovement, ValidMoveChecker validMoveChecker) {
		BasicSwingInputHandler handler = new BasicSwingInputHandler(camMovement, validMoveChecker);
		component.addKeyListener(handler);
		component.addMouseMotionListener(handler);
		return handler;
	}
	
	/**
	 * Creates input handler accepting free movement of camera
	 * @param component JFrame component to which event handlers will be subscribed.
	 * @param scene
	 * @return
	 */
	public static AbstractInputHandler createBasicSwingInputHandler(JFrame component, CameraMovement camMovement) {
		return BasicSwingInputHandler.createBasicSwingInputHandler(component, camMovement, (_,_,_) -> true);
	}
	
	
	/*
	 * Key handlers
	 */
	@Override
	public void keyTyped(KeyEvent e) {	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			setLeftPressed (true);
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			setRightPressed(true);
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			setForwardPressed(true);
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			setBackwardPressed(true);
		} else if (e.getKeyCode() == KeyEvent.VK_W) {
			setUpPressed(true);
		} else if (e.getKeyCode() == KeyEvent.VK_S) {
			setDownPressed(true);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			setForwardPressed(false);
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			setBackwardPressed(false);
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			setLeftPressed (false);
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			setRightPressed(false);
		} else if (e.getKeyCode() == KeyEvent.VK_W) {
			setUpPressed(false);
		} else if (e.getKeyCode() == KeyEvent.VK_S) {
			setDownPressed(false);
		}
		
	}
	
	
	
	
	
	/*
	 * Mouse handlers
	 */
	
	@Override
	public void mouseDragged(MouseEvent e) {	}

	@Override
	public void mouseMoved(MouseEvent e) {	
		setMouseRelX( (double) e.getX() / e.getComponent().getWidth());
		setMouseRelY( (double) e.getY() / e.getComponent().getHeight());
	}
	
	
	

	
	

	

}
