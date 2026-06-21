package render3d;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class FixedBackground extends Background {

	private final BufferedImage backgroundTemplate;
	
	public FixedBackground (String filename) throws IOException {
		backgroundTemplate = ImageIO.read(new File(filename));
	}

	@Override
	BufferedImage getBackground(double theta, double phi, double horizontalViewAngle, double verticalViewAngle) {
		return backgroundTemplate;
	}
	
}
