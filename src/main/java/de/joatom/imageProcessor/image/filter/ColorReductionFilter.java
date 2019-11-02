package de.joatom.imageProcessor.image.filter;

import java.awt.image.RGBImageFilter;

/**
 * @author Johannes Tomasoni
 *
 */
public class ColorReductionFilter extends RGBImageFilter {
	
	    
	
        public ColorReductionFilter() {
            canFilterIndexColorModel = true;
        }

        @Override
		public int filterRGB(int x, int y, int rgb) {
            return ((rgb & 0xff00ff00)
                    | ((rgb & 0xff0000) >> 16)
                    | ((rgb & 0xff) << 16));
        }
    

}
