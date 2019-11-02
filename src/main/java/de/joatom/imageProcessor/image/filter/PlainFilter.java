package de.joatom.imageProcessor.image.filter;


import java.awt.image.RGBImageFilter;

/**
 * @author Johannes Tomasoni
 *
 */
public class PlainFilter extends RGBImageFilter {

	public PlainFilter() {
		canFilterIndexColorModel = true;
	}

	@Override
	public int filterRGB(int x, int y, int rgb) {


		return rgb;
	}

}
