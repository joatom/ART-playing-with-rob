package de.joatom.imageProcessor.image.factory;

import java.awt.image.BufferedImage;

import javax.swing.GrayFilter;

/**
 * @author Johannes Tomasoni
 *
 */
public class GrayImage extends AbstractImage {

	public GrayImage(BufferedImage imgOrg, String name, int grayness) {
		super(imgOrg, name, new GrayFilter(true, grayness));

	}


}
