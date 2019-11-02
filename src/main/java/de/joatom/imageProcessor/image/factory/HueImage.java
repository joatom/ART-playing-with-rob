package de.joatom.imageProcessor.image.factory;

import java.awt.image.BufferedImage;

import de.joatom.imageProcessor.image.filter.HueFilter;;

/**
 * @author Johannes Tomasoni
 *
 */
public class HueImage extends AbstractImage {

	public HueImage(BufferedImage imgOrg, String name) {
		super(imgOrg, name, new HueFilter());

	}

}
