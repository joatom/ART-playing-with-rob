package de.joatom.imageProcessor.image.factory;

import java.awt.image.BufferedImage;

import de.joatom.imageProcessor.image.filter.PlainFilter;;

/**
 * @author Johannes Tomasoni
 *
 */
public class PlainImage extends AbstractImage {

	public PlainImage(BufferedImage imgOrg, String name) {
		super(imgOrg, name, new PlainFilter());

	}

}
