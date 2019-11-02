package de.joatom.imageProcessor.image.factory.util;

import org.apache.commons.math3.ml.clustering.Clusterable;

/**
 * @author Johannes Tomasoni
 *
 */
public class LocationWrapper implements Clusterable {
    private double[] points;
    private int pixIndex;

    public LocationWrapper(double[] points,int pixIndex) {
        this.points = points;
        this.pixIndex = pixIndex;
    }


    public double[] getPoint() {
        return points;
    }
    
    public int getPixIndex(){
    	return pixIndex;
    }
}
        