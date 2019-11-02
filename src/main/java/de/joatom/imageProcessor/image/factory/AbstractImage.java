package de.joatom.imageProcessor.image.factory;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.joatom.imageProcessor.edgeProcessing.EdgePoint;
import de.joatom.imageProcessor.image.factory.util.ImageClusterDecorator;
import de.joatom.imageProcessor.image.factory.util.ImageClusterDecorator.ColorType;

/**
 * @author Johannes Tomasoni
 * 
 */
public abstract class AbstractImage {

	private static final Logger LOGGER = LogManager.getLogger(AbstractImage.class.getName());

	public enum LayerMode {
		EDGE_POINTS, EDGES, POINTS, MIX_ALL, MIX_EDGES, MIX_POINTS
	}

	BufferedImage img;
	String name;
	ImageClusterDecorator imageClusterDecorator;

	public AbstractImage(BufferedImage imgOrg, String name, RGBImageFilter filter) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();

		// apply filter
		ImageProducer hueProducer = new FilteredImageSource(imgOrg.getSource(), filter);

		// create HueGfx
		Image hueImage = toolkit.createImage(hueProducer);
		this.img = new BufferedImage(hueImage.getWidth(null), hueImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics2D bHue = this.img.createGraphics();
		bHue.drawImage(hueImage, 0, 0, null);
		bHue.dispose();

		this.name = name;
	}

	public void save(String path, String pos) throws IOException {
		ImageIO.write(this.img, "png", new File(path + pos + "_" + this.name));
	};

	public void applyEdgeMap(EdgePoint[] edgePoints, int width, int height, boolean invertOutput, LayerMode layerMode) {
		for (int i = 0; i < edgePoints.length; i++) {
			int x = i % width;
			int y = (int) Math.floor(i / width);
			if (x != 0 && y != 0 && x < width - 1 && y < height - 1) {
				int s = edgePoints[i].getValue();

				// draw
				if (edgePoints[i].getEdgeId() > 0) {
					s = invertOutput ? 255 - s : s;

					if (edgePoints[i].isEndPoint()) {
						// set to Red
						if (layerMode == LayerMode.MIX_ALL || layerMode == LayerMode.POINTS
								|| layerMode == LayerMode.EDGE_POINTS || layerMode == LayerMode.MIX_POINTS) {
							img.setRGB(x, y, (255 & 0xff000000) | (255 << 16) | (0 << 8) | (0 << 0));
						}
						else {
							img.setRGB(x, y, (255 & 0xff000000) | (s << 16) | (s << 8) | (s << 0));
						}
					} else {
						// set edge color
						if (layerMode == LayerMode.MIX_ALL || layerMode == LayerMode.EDGES
								|| layerMode == LayerMode.EDGE_POINTS || layerMode == LayerMode.MIX_EDGES) {
							img.setRGB(x, y, (255 & 0xff000000) | (s << 16) | (s << 8) | (s << 0));
						}
					}
				} else {
					// set background color
					if (layerMode == LayerMode.EDGE_POINTS || layerMode == LayerMode.EDGES
							|| layerMode == LayerMode.POINTS) {
						int bg = invertOutput ? 255 : 0;
						img.setRGB(x, y, (255 & 0xff000000) | (bg << 16) | (bg << 8) | (bg << 0));
					}
				}
			}
		}
	}

	// Cluster Image Colors
	public void applyCluster(int numOfClusters) {
		this.imageClusterDecorator = new ImageClusterDecorator(this, ColorType.ARGB, true, true);
		int[] argb = ImageClusterDecorator.getARGBValues((int) this.imageClusterDecorator.getPercentile(95));
		LOGGER.debug("p95: a: " + argb[0] + " r: " + argb[1] + " g: " + argb[2] + " b: " + argb[3]);
		img.setRGB(0, 0, img.getWidth(), img.getHeight(), imageClusterDecorator.generateCluster(numOfClusters), 0,
				img.getWidth());

	}

	public int[] getPix() {
		return img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
	};

	public void setPix(int[] pix) {
		this.img.setRGB(0, 0, img.getWidth(), img.getHeight(), pix, 0, img.getWidth());
	};

	public ImageClusterDecorator getImageClusterDecorator() {
		return imageClusterDecorator;
	}

	public void setImageClusterDecorator(ImageClusterDecorator imageClusterDecorator) {
		this.imageClusterDecorator = imageClusterDecorator;
	}

	public BufferedImage getImg() {
		return img;
	}

	public void setImg(BufferedImage img) {
		this.img = img;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
