package render3d.userinput;

import render3d.CameraController;
import render3d.CameraMovement;
import render3d.CameraMovement.Coordinate3D;
import render3d.CameraMovement.MoveDirection;

public class AbstractInputHandler implements CameraController {
	
	
	private final ValidMoveChecker validMoveChecker;
	private final CameraMovement camMovement;
	
	private boolean forwardPressed, backwardPressed, leftPressed, rightPressed, upPressed, downPressed;
	private double mouseRelX, mouseRelY;
	
	private double panSpeed = 0.05;
	private double moveSpeed = 1;
	
	
	AbstractInputHandler(CameraMovement camMovement, ValidMoveChecker validMoveChecker) {
		this.validMoveChecker = validMoveChecker;
		this.camMovement = camMovement;
	}
	
	
	
	protected void setForwardPressed(boolean forwardPressed) {
		this.forwardPressed = forwardPressed;
	}

	protected void setBackwardPressed(boolean backwardPressed) {
		this.backwardPressed = backwardPressed;
	}

	protected void setLeftPressed(boolean leftPressed) {
		this.leftPressed = leftPressed;
	}

	protected void setRightPressed(boolean rightPressed) {
		this.rightPressed = rightPressed;
	}

	protected void setUpPressed(boolean upPressed) {
		this.upPressed = upPressed;
	}

	protected void setDownPressed(boolean downPressed) {
		this.downPressed = downPressed;
	}

	protected void setMouseRelX(double mouseRelX) {
		this.mouseRelX = mouseRelX;
	}

	protected void setMouseRelY(double mouseRelY) {
		this.mouseRelY = mouseRelY;
	}

	public double getPanSpeed() {
		return panSpeed;
	}

	public double getMoveSpeed() {
		return moveSpeed;
	}

	/**
	 * 
	 * @param panSpeed
	 * @return instance of this object to allow method chaining
	 */
	public AbstractInputHandler setPanSpeed(double panSpeed) {
		this.panSpeed = panSpeed;
		return this;
	}

	/**
	 * 
	 * @param moveSpeed
	 * @return instance of this object to allow method chaining
	 */
	public AbstractInputHandler setMoveSpeed(double moveSpeed) {
		this.moveSpeed = moveSpeed;
		return this;
	}

	
	

	
	/**
	 * 
	 * Update camera position, should be called for each next frame to be rendered.
	 */
	public void updateCamera() {
		if  (mouseRelX < 0.4) {
			panLeft();
		} else if (mouseRelX > 0.6) {
			panRight();
		}
		if (forwardPressed) {
			moveForward();
		} else if (backwardPressed) {
			move(MoveDirection.BACKWARD);
		} else if (leftPressed) {
			move(MoveDirection.LEFT);
		} else if (rightPressed) {
			move(MoveDirection.RIGHT);
		} else if (upPressed) {
			move(MoveDirection.UP);
		} else if (downPressed) {
			move(MoveDirection.DOWN);
		}
		
		/*
		double phi = (mouseRelY*1.2-0.1); // -0.1 ... 1.1
		if (phi < 0) phi = 0;
		if (phi > 1) phi = 1;
		phi *= Math.PI;
		*/
		camMovement.setVerticalViewAngle(mouseRelY*Math.PI);
	}
	
	

	protected void panLeft() {
		double factor = Math.abs(0.5 - mouseRelX);
		camMovement.advanceHorizontalViewAngle(-1*factor*panSpeed*Math.PI);
	}
	
	protected void panRight() {
		double factor = Math.abs(0.5 - mouseRelX);
		camMovement.advanceHorizontalViewAngle(factor*panSpeed*Math.PI);
	}

	protected void move(MoveDirection direction) {
		Coordinate3D p = camMovement.nextMovePoint3D(moveSpeed, direction);
		if (validMoveChecker.isValidMove(
				p.x(), 
				p.y(),
				p.z())) {			
			camMovement.moveCameraTo(p.x(), p.y(), p.z());
		} else if (validMoveChecker.isValidMove(
				camMovement.getCurrentCameraPosition3D().x(), 
				p.y(),
				p.z())) {
			camMovement.moveCameraTo(camMovement.getCurrentCameraPosition3D().x(), p.y(), p.z());
		} else if (validMoveChecker.isValidMove(
				p.x(),
				camMovement.getCurrentCameraPosition3D().y(), 
				p.z())) {
			camMovement.moveCameraTo(
					p.x(), 
					camMovement.getCurrentCameraPosition3D().y(), 
					p.z());
		} else if (validMoveChecker.isValidMove(
				p.x(),
				p.y(),
				camMovement.getCurrentCameraPosition3D().z() 
				)) {
			camMovement.moveCameraTo(
					p.x(), 
					p.y(),
					camMovement.getCurrentCameraPosition3D().z() 
					);
		}
	}
	
	protected void moveForward() {
		move(MoveDirection.FORWARD);
	}
}
