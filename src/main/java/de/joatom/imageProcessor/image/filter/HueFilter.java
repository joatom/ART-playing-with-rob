package de.joatom.imageProcessor.image.filter;

import java.awt.Color;
import java.awt.image.RGBImageFilter;

/**
 * @author Johannes Tomasoni
 *
 */
public class HueFilter extends RGBImageFilter {

	public HueFilter() {
		canFilterIndexColorModel = true;
	}

	@Override
	public int filterRGB(int x, int y, int rgb) {
		float[] hsb = new float[3];
		float s;
		float v;
		Color.RGBtoHSB((rgb >> 16) & 0xff, (rgb >> 8) & 0xff,
				(rgb >> 0) & 0xff, hsb);

		s = hsb[1] < 0.2 ? 0 : 1;
		v = hsb[2] < 0.2 ? 0 : 1;

		return Color.HSBtoRGB(hsb[0], s, v);
	}

}
