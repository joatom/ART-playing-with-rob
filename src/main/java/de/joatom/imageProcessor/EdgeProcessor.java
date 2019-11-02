package de.joatom.imageProcessor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.joatom.imageProcessor.edgeProcessing.EdgeDetector;
import de.joatom.imageProcessor.edgeProcessing.EdgePoint;
import de.joatom.imageProcessor.image.factory.AbstractImage;
import de.joatom.imageProcessor.image.factory.GrayImage;
import de.joatom.imageProcessor.image.factory.HueImage;
import de.joatom.imageProcessor.image.factory.PlainImage;
import de.joatom.imageProcessor.image.filter.Sharpener;
import de.joatom.imageProcessor.image.filter.Smoother;

/**
 * @author Johannes Tomasoni
 *
 */
public class EdgeProcessor {

	private static final Logger LOGGER = LogManager.getLogger(EdgeProcessor.class.getName());

	static String path = "pix/";

	public static void execute(final String picuterName, final String outPostfix, final int numOfClusters, int grayness,
			Smoother.Type smootherType, EdgeDetector.Type edgeDetectorType, Sharpener.Type sharpenerType,
			double hyst_t1, double hyst_t2, AbstractImage.LayerMode imageLayerMode, final boolean invertOutput) {

		try {

			// ### Load Image ###

			LOGGER.info(String.format("Loading... %s", path + picuterName));
			BufferedImage imgOrg = ImageIO.read(new File(path + picuterName));

			// ### Color processing ###

			// Generate HueImage
			LOGGER.info("Generating hueImage");
			HueImage hueImage = new HueImage(imgOrg, picuterName);
			hueImage.save(path, outPostfix + "_hue");

			// Generate color clustered Image
			LOGGER.info(String.format("Generating clustered Image with %d color clusters", numOfClusters));
			PlainImage clusterdImage = new PlainImage(imgOrg, picuterName);
			clusterdImage.applyCluster(numOfClusters);
			clusterdImage.save(path, outPostfix + "_cluster");

			// Generate clustered GrayImage
			LOGGER.info(
					String.format("Convert clustered into gray Image with %d percent brightness/grayness", grayness));
			GrayImage grayImage = new GrayImage(clusterdImage.getImg(), picuterName, grayness);
			grayImage.setImageClusterDecorator(clusterdImage.getImageClusterDecorator());
			grayImage.save(path, outPostfix + "_gray");

			// ### Edge processing ###

			final int width = grayImage.getImg().getWidth();
			final int height = grayImage.getImg().getHeight();

			// Smooth gray Picture
			LOGGER.info(String.format("Smooth gray Image using %s", smootherType));
			grayImage.setPix(Smoother.smooth(grayImage, smootherType));
			grayImage.save(path, outPostfix + "_smoothed");

			// Apply Canny-Algorithm
			// https://en.wikipedia.org/wiki/Canny_edge_detector

			// Edge and direction detection with Sobel
			LOGGER.info(String.format("Do edge detection using %s", edgeDetectorType));
			EdgePoint edgePoints[] = EdgeDetector.detectEdges(grayImage, edgeDetectorType);

			// Sharpen blury edges with Non-maximum suppression
			LOGGER.info(String.format("Sharpen edges with %s", sharpenerType));
			EdgePoint sharpEdgePoints[] = Sharpener.sharpen(edgePoints, width, height, sharpenerType);

			// Remove week edges with Hysteresis and mark where edges stop
			LOGGER.info(String.format("Remove week edges with threshold t1 %f and t1 %f", hyst_t1, hyst_t2));
			sharpEdgePoints = EdgeDetector.hysteresis(grayImage, sharpEdgePoints, hyst_t1, hyst_t2);

			// draw edges on image
			LOGGER.info(String.format("Draw edges on Image in %s mode", imageLayerMode));
			grayImage.applyEdgeMap(sharpEdgePoints, width, height, invertOutput, imageLayerMode);

			grayImage.save(path, outPostfix);

			LOGGER.info("Done!");
		} catch (IOException e) {
			LOGGER.info("Bild nicht gefunden");
			e.printStackTrace();
		}

	}

}
