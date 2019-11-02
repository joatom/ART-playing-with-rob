package de.joatom.imageProcessor.image.factory.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.distance.ManhattanDistance;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.joatom.imageProcessor.image.factory.AbstractImage;


/**
 * @author Johannes Tomasoni
 *
 */
public class ImageClusterDecorator {

	public enum ColorType {
		ARGB, GRAY
	}

	private static final Logger LOGGER = LogManager.getLogger(ImageClusterDecorator.class.getName());
	private int[] pix;
	private int[] clusteredPix;
	private int[][] argbPix;
	private int[] grayPix;
	private ColorType colType;

	private Histogram histogram;
	private Histogram histogramAlpha;
	private Histogram histogramRed;
	private Histogram histogramGreen;
	private Histogram histogramBlue;

	private double[] percValues;
	private double[] percValuesAlpha;
	private double[] percValuesRed;
	private double[] percValuesGreen;
	private double[] percValuesBlue;

	public ImageClusterDecorator(AbstractImage img, ColorType imgType,
			boolean createHistogram, boolean createPercentile) {
		this.pix = img.getPix();

		this.colType = imgType;

		if (createHistogram) {
			histogram = new Histogram();
			if (imgType == ColorType.ARGB) {
				histogramAlpha = new Histogram();
				histogramRed = new Histogram();
				histogramGreen = new Histogram();
				histogramBlue = new Histogram();
			}
		}

		if (createPercentile) {
			percValues = new double[this.pix.length];
				percValuesAlpha = new double[this.pix.length]; 
				percValuesRed = new double[this.pix.length];
				percValuesGreen = new double[this.pix.length];
				percValuesBlue = new double[this.pix.length];
		}

		argbPix = new int[this.pix.length][4];
		grayPix = new int[this.pix.length];

		for (int i = 0; i < this.pix.length; i++) {
			switch (imgType) {
			case ARGB:
				argbPix[i] = getARGBValues(this.pix[i]);
				if (createHistogram) {
					// argb
					histogram.add(pix[i]);
					// alpha
					histogramAlpha.add(argbPix[i][0]);
					// red
					histogramRed.add(argbPix[i][1]);
					// green
					histogramGreen.add(argbPix[i][2]);
					// blue
					histogramBlue.add(argbPix[i][3]);
				}
				if (createPercentile) {
					percValues[i] = (pix[i]);
					percValuesAlpha[i] = /* .add */(argbPix[i][0]);
					percValuesRed[i] = (argbPix[i][1]);
					percValuesGreen[i] = (argbPix[i][2]);
					percValuesBlue[i] = (argbPix[i][3]);
				}
				break;
			case GRAY:
				grayPix[i] = (this.pix[i] >> 0) & 0xff;
				if (createHistogram) {
					histogram.add(grayPix[i]);
				}
				if (createPercentile) {
					percValues[i] = (pix[i]);
					percValuesAlpha[i] = 255;
					percValuesRed[i] = grayPix[i];
					percValuesGreen[i] = grayPix[i];
					percValuesBlue[i] = grayPix[i];
				}
				break;
			default:
				break;
			}
		}
	}

	public double getPercentile(double p) {
		Percentile perc = new Percentile(p);
		return perc.evaluate(percValues);
	}

	public double getPercentileAlpha(double p) {
		Percentile perc = new Percentile(p);
		return perc.evaluate(percValuesAlpha);
	}

	public double getPercentileRed(double p) {
		Percentile perc = new Percentile(p);
		return perc.evaluate(percValuesRed);
	}

	public double getPercentileGreen(double p) {
		Percentile perc = new Percentile(p);
		return perc.evaluate(percValuesGreen);
	}

	public double getPercentileBlue(double p) {
		Percentile perc = new Percentile(p);
		return perc.evaluate(percValuesBlue);
	}

	public int[] generateCluster(int numOfClusters) {
		List<LocationWrapper> clusterInput = new ArrayList<LocationWrapper>(
				percValues.length);
		for (int i = 0; i < percValues.length; i = i + 1) {
			clusterInput.add(new LocationWrapper(new double[] {
					percValuesAlpha[i], percValuesRed[i], percValuesGreen[i],
					percValuesBlue[i] }, i));
		}
		KMeansPlusPlusClusterer<LocationWrapper> clusterer = new KMeansPlusPlusClusterer<LocationWrapper>(
				numOfClusters,
				10000,
				new ManhattanDistance(),
				new JDKRandomGenerator(),
				KMeansPlusPlusClusterer.EmptyClusterStrategy.LARGEST_POINTS_NUMBER);
		List<CentroidCluster<LocationWrapper>> clusterResults = clusterer
				.cluster(clusterInput);

		this.clusteredPix = new int[pix.length];// pix.clone();
		// output the clusters
		for (int i = 0; i < clusterResults.size(); i++) {
			LOGGER.debug("Cluster " + i);
			LOGGER.debug("A:"
					+ (int) clusterResults.get(i).getCenter().getPoint()[0]
					+ " R:"
					+ (int) clusterResults.get(i).getCenter().getPoint()[1]
					+ " G:"
					+ (int) clusterResults.get(i).getCenter().getPoint()[2]
					+ " B:"
					+ (int) clusterResults.get(i).getCenter().getPoint()[3]);
			for (LocationWrapper l : clusterResults.get(i).getPoints()) {
				this.clusteredPix[l.getPixIndex()] = convertARGBValues(
						(int) clusterResults.get(i).getCenter().getPoint()[0],
						(int) clusterResults.get(i).getCenter().getPoint()[1],
						(int) clusterResults.get(i).getCenter().getPoint()[2],
						(int) clusterResults.get(i).getCenter().getPoint()[3]);
				// LOGGER.debug(l.getPixIndex());
			}
		}
		
		return this.clusteredPix;
	}

	public int[] getClusteredPix() {
		return this.clusteredPix;
	}

	public Histogram getHistogram() {
		return histogram;
	}

	public ColorType getImgType() {
		return colType;
	}

	public static int[] getARGBValues(int argbValue) {
		int[] argb = new int[4];
		argb[0] = (argbValue >> 24) & 0xff; // alpha;
		argb[1] = (argbValue >> 16) & 0xff; // red;
		argb[2] = (argbValue >> 8) & 0xff; // green;
		argb[3] = (argbValue >> 0) & 0xff; // blue;
		return argb;
	}

	public static int convertARGBValues(int a, int r, int g, int b) {
		return (a & 0xff000000) | (r << 16) | (g << 8) | (b << 0);
	}
}
