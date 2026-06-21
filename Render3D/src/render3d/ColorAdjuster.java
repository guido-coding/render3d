package render3d;

import java.awt.Color;

public interface ColorAdjuster {

	/**
	 * 
	 * @param color original color
	 * @param face reference to Face3D object to get the adjusted color for
	 * @param object reference to Object3D object to get adjusted color for
	 * @return
	 */
	public Color getAdjustedColor(Color color, Face3D face, Object3D object);
	
}
