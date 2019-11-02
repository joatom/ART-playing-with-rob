package de.joatom.imageProcessor;

import de.joatom.imageProcessor.edgeProcessing.EdgeDetector;
import de.joatom.imageProcessor.image.factory.AbstractImage;
import de.joatom.imageProcessor.image.filter.Sharpener;
import de.joatom.imageProcessor.image.filter.Smoother;

/**
 * @author Johannes Tomasoni
 * 
 * Put a picture in the pix folder (e.g. Test.jpg).
 * Call App Test.jpg to run the EdgeProcessor. Generated Pictures will be placed in the pix folder.
 *
 */
public class App {
	public static void main(String[] args) {
		EdgeProcessor.execute(args[0], "out", 32, 40, Smoother.Type.LINEAR, EdgeDetector.Type.PREWITT, Sharpener.Type.NMS, 90,
				98, AbstractImage.LayerMode.EDGE_POINTS, true);
	}
}
