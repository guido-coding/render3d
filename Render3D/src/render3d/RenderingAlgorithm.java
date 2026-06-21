package render3d;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;

interface RenderingAlgorithm {

	Image render3DView(BufferedImage background, Camera camera, List<Object3D> objects, int width, int height);
	Image render3DView(Camera camera, List<Object3D> objects, int width, int height);
	
}
