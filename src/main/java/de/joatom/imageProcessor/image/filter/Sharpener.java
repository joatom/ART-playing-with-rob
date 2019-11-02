package de.joatom.imageProcessor.image.filter;

import de.joatom.imageProcessor.edgeProcessing.EdgePoint;

/**
 * @author Johannes Tomasoni
 *
 */
public class Sharpener {

	public enum Type {
		NMS
	}

	// Sharpen blury edges with Non-maximum suppression
	// https://en.wikipedia.org/wiki/Canny_edge_detector#Non-maximum_suppression
	// non-maximum suppression ==> line-width to 1px
	public static int nms(EdgePoint[] sp) {
		EdgePoint center = sp[4];
		int ret = center.getValue();
		try {
			for (int i = 0; i < sp.length; i++) {
				if ((center.getDirection() == 0 && (i == 3 || i == 5))
						|| (center.getDirection() == 45 && (i == 0 || i == 8))
						|| (center.getDirection() == 90 && (i == 1 || i == 7))
						|| (center.getDirection() == 135 && (i == 2 || i == 6))

				/*
				 * (center.getDirection() == 0 && i != 1 && i != 4 && i != 7) ||
				 * (center.getDirection() == 45 && i != 2 && i != 4 && i != 6) ||
				 * (center.getDirection() == 90 && i != 3 && i != 4 && i != 5) ||
				 * (center.getDirection() == 135 && i != 0 && i != 4 && i != 8)
				 */
				) {
					if (sp[i].getValue() > center.getValue()) {
						return 0;
					}
				}
			}
		} catch (NullPointerException n) {
		}

		return ret;
	}

	public static EdgePoint[] sharpen(EdgePoint[] edgePoints, int width, int height, Sharpener.Type type) {
		EdgePoint[] sharpEdgePoints = new EdgePoint[height * width];

		for (int i = 0; i < edgePoints.length; i++) {
			int x = i % width;
			int y = (int) Math.floor(i / width);
			if (x != 0 && y != 0 && x < width - 1 && y < height - 1) {
				EdgePoint sp[] = { edgePoints[(i - width) - 1], edgePoints[(i - width)], edgePoints[(i - width) + 1],
						edgePoints[i - 1], edgePoints[i], edgePoints[i + 1], edgePoints[(i + width) - 1],
						edgePoints[(i + width)], edgePoints[(i + width) + 1] };
				int s = 0;
				if (type == Sharpener.Type.NMS) {
					s = Sharpener.nms(sp);
				}

				sharpEdgePoints[i] = new EdgePoint(s, edgePoints[i].getDirection());

			}
		}

		return sharpEdgePoints;
	}

}
