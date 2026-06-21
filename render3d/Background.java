package render3d;

import java.awt.image.BufferedImage;

public abstract class Background {

	/**
	 * 
	 * @param theta angle in horizontal plane in spherical coordinates
	 * @param phi angle in vertical plane in spherical coordinates
	 * @param horizontalViewAngle
	 * @param verticalViewAngle
	 * @return background image
	 */
	abstract BufferedImage getBackground(double theta, double phi, double horizontalViewAngle, double verticalViewAngle);
	
}
