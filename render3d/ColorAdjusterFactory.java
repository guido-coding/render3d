package render3d;

import java.awt.Color;

public class ColorAdjusterFactory {

	public static ObjectColorModel getType1ColorAdjuster() {
		return new ObjectColorModel(ObjectColorModel::brightnessBasedOnFaceDistance, ObjectColorModel::darker);
	}
	
	public static ObjectColorModel getType1ColorAdjuster(double scale) {
		return new ObjectColorModel(new AdjustBrightnessBasedOnDistance(scale), ObjectColorModel::darker);
	}
	
	public static ObjectColorModel getType2ColorAdjuster() {
		return new ObjectColorModel(ObjectColorModel::brightnessBasedOnRelativeFaceDistance, ObjectColorModel::blackColor);
	}
	
	public static ObjectColorModel getType3ColorAdjuster() {
		return new ObjectColorModel(ObjectColorModel::colorBasedOnZValue, ObjectColorModel::blackColor);
	}
	
	public static ObjectColorModel getType4ColorAdjuster(double min, double max) {
		return new ObjectColorModel(new AdjustColorToRelativeZValue(min, max), ObjectColorModel::blackColor);
	}
	
	public static ObjectColorModel getContourColorAdjuster() {
		return new ObjectColorModel(ObjectColorModel::unadjustedColor, ObjectColorModel::darker);
	}
	
	
}


class AdjustBrightnessBasedOnDistance implements ColorAdjuster {
	
	private final double scale;
	
	AdjustBrightnessBasedOnDistance(double scale) {
		this.scale = scale;
	}
	
	public Color getAdjustedColor(Color color, Face3D face, Object3D object) {
		double brightnessfactor = 1 - face.getAverageDistance()/scale;
		
		if (brightnessfactor<0.1) brightnessfactor=0.1;
		
		
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
}



class AdjustColorToRelativeZValue implements ColorAdjuster {

	private final double min, max;
	
	AdjustColorToRelativeZValue(double min, double max) {
		this.min = min;
		this.max = max;
	}
	
	@Override
	public Color getAdjustedColor(Color color, Face3D face, Object3D object) {
		double averageZ = 0;
		int n = 0;
		for (Point3D p : face.getPoints()) {
			averageZ += p.z();
			n++;
		}
		averageZ = averageZ / n;
		
		double relativeZ = (averageZ - min) / (max - min); 
		if (relativeZ < 0) relativeZ = 0;
		if (relativeZ > 1) relativeZ = 1;
		
		int r, g;
		if (relativeZ > 0.5) {			
			r = 255;
			g = (int)(255*2 - 255*relativeZ*2);
		} else {		
			r = (int)(255 * relativeZ*2);
			g = 255;
		}
		
		return new Color(r, g, 0, color.getAlpha());
	}
	
}