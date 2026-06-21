package render3d;

import java.awt.Color;

public class ObjectColorModel {
	

	private final ColorAdjuster backgroundAdjuster, contourAdjuster;
	
	public ObjectColorModel(ColorAdjuster backgroundAdjuster, ColorAdjuster contourAdjuster) {
		this.backgroundAdjuster = backgroundAdjuster;
		this.contourAdjuster = contourAdjuster;

	}
	
	
	public Color getAdjustedColor(Color color, Face3D face, Object3D object) {
		return backgroundAdjuster.getAdjustedColor(color, face, object);
	}
	
	public Color getContourColor(Color color, Face3D face, Object3D object) {
		return contourAdjuster.getAdjustedColor(color, face, object);
	}

	
	
	
	public static Color unadjustedColor(Color color, Face3D face, Object3D object) {
		return color;
	}
	
	public static Color blackColor(Color color, Face3D face, Object3D object) {
		return Color.BLACK;
	}
	
	public static Color darker(Color color, Face3D face, Object3D object) {
		return color.darker();
	}
	
	public static Color brightnessBasedOnFaceDistance(Color color, Face3D face, Object3D object) {
		double brightnessfactor = 1 - face.getAverageDistance()/20;
		
		if (brightnessfactor<0) brightnessfactor=0;

			//brightnessfactor = Math.pow(brightnessfactor, 0.5);
		
		
		int r = (int)(brightnessfactor * color.getRed());
		int b = (int)(brightnessfactor * color.getBlue());
		int gr = (int)(brightnessfactor * color.getGreen());
		
		return new Color(
				r < 255 ? r : 255,
				gr < 255 ? gr : 255,
				b < 255 ? b : 255,
				color.getAlpha()
				);
	}
	
	
	public static Color brightnessBasedOnRelativeFaceDistance(Color color, Face3D face, Object3D object) {
		double brightnessfactor;
		if (object.diameter > 0) {			
			brightnessfactor = ((object.getAverageDistance() - face.getAverageDistance() + object.diameter) / (2*object.diameter));	
		} else {
			brightnessfactor = 1;
		}
		
		int r = (int)(brightnessfactor * color.getRed());
		int b = (int)(brightnessfactor * color.getBlue());
		int gr = (int)(brightnessfactor * color.getGreen());
		
		return new Color(
				r < 255 ? r : 255,
				gr < 255 ? gr : 255,
				b < 255 ? b : 255,
				color.getAlpha()
				);
	}

	public static Color colorBasedOnZValue(Color color, Face3D face, Object3D object) {
		double averageZ = 0;
		int n = 0;
		for (Point3D p : face.getPoints()) {
			averageZ += p.z();
			n++;
		}
		averageZ = Math.abs(averageZ / n);
		
		int factor = 100;
		int r = (int)(averageZ*factor);
		if (r<0) r = 0;
		if (r > 255) r = 255;
		int g = 255 - r;
		
		return new Color(r, g, 0, color.getAlpha());
	}
	
}
