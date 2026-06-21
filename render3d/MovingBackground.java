package render3d;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class MovingBackground extends Background {
	
	private final BufferedImage backgroundTemplate;
	private final int width, height;
	
	public MovingBackground(String filename) throws IOException {
		BufferedImage source = ImageIO.read(new File(filename));
		backgroundTemplate = new BufferedImage(source.getWidth()*3, source.getHeight(), BufferedImage.TYPE_INT_ARGB);
		width = source.getWidth();
		height = source.getHeight();
		Graphics2D g = backgroundTemplate.createGraphics();
		g.drawImage(source, 0, 0, null);
		g.drawImage(source, source.getWidth(), 0, null);
		g.drawImage(source, source.getWidth()*2, 0, null);
		g.dispose();
	}

	BufferedImage getBackground(double theta, double phi, double horizontalViewAngle, double verticalViewAngle) {
		while (theta < 0) theta += 2*Math.PI; 
		
		double relXPos = theta / (2*Math.PI); // 0...1 
		
		int width = (int)(this.width * (horizontalViewAngle / (2*Math.PI)));
		int height = (int)(this.height * (verticalViewAngle / (2*Math.PI)));
		
		
		int x = (int)(this.width + relXPos*this.width);
		int y = (int)(this.height * phi / (2*Math.PI));
		y -= height /2;
		
		x = x<0 ? 0 : x;
		y = y<0 ? 0 : y;
		
		return backgroundTemplate.getSubimage(x, y, width, height);
	}
	
}
