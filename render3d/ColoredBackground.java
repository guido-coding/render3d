package render3d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

class ColoredBackground extends Background {
	
	private final BufferedImage background;
	
	ColoredBackground(Color color) {
		background = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = background.createGraphics();
		g.setColor(color);
		g.fillRect(0, 0, 100, 100);
		g.dispose();
	}
	

	@Override
	BufferedImage getBackground(double theta, double phi, double horizontalViewAngle, double verticalViewAngle) {
		return background;
	}

}
