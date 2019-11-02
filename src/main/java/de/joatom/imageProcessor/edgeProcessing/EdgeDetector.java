package de.joatom.imageProcessor.edgeProcessing;

import java.util.ArrayList;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import de.joatom.imageProcessor.image.factory.AbstractImage;
import de.joatom.imageProcessor.image.factory.util.Histogram;

/**
 * @author Johannes Tomasoni
 *
 */
public class EdgeDetector {

	public enum Type {
		SOBEL, PREWITT
	};

	private static int[] sobelV1 = { 1, 2, 1 };
	private static int[] sobelV2 = { 1, 0, -1 };
	private static int[] prewittV1 = { 1, 1, 1 };
	private static int[] prewittV2 = { 1, 0, -1 };

	// Edge detection and direction
	// https://de.wikipedia.org/wiki/Sobel-Operator
	public static EdgePoint sobel(int[] img) {
		int gX = 0;
		int gY = 0;

		for (int i = 0; i < img.length; i++) {
			gX = gX + img[i] * EdgeDetector.sobelV1[(int) Math.floor(i / EdgeDetector.sobelV1.length)]
					* EdgeDetector.sobelV2[i % EdgeDetector.sobelV2.length];
			gY = gY + img[i] * EdgeDetector.sobelV2[(int) Math.floor(i / EdgeDetector.sobelV2.length)]
					* EdgeDetector.sobelV1[i % EdgeDetector.sobelV1.length];
		}
		;

		// System.out.println(Math.atan2(gY, gX) * 180 / Math.PI + ": "
		// + Math.round((Math.atan2(gY, gX) * 180 / Math.PI) / 45) * 45); //
		// teta:winkel
		// // todo:
		// // runden
		// // auf
		// // 0,90,135,
		// // 45
		return new EdgePoint(Math.abs(gX) + Math.abs(gY),
				(int) ((Math.round((Math.atan2(gY, gX) * 180 / Math.PI) / 45) * 45) + 180) % 180);
	}

	// Edge detection and direction
	// https://en.wikipedia.org/wiki/Prewitt_operator
	public static EdgePoint prewitt(int[] img) {
		int gX = 0;
		int gY = 0;

		for (int i = 0; i < img.length; i++) {
			gX = gX + img[i] * EdgeDetector.prewittV1[(int) Math.floor(i / EdgeDetector.prewittV1.length)]
					* EdgeDetector.prewittV2[i % EdgeDetector.prewittV2.length];
			gY = gY + img[i] * EdgeDetector.prewittV2[(int) Math.floor(i / EdgeDetector.prewittV2.length)]
					* EdgeDetector.prewittV1[i % EdgeDetector.prewittV1.length];
		}
		;

		// System.out.println(Math.atan2(gY, gX) * 180 / Math.PI + ": "
		// + Math.round((Math.atan2(gY, gX) * 180 / Math.PI) / 45) * 45); //
		// teta:winkel
		// // todo:
		// // runden
		// // auf
		// // 0,90,135,
		// // 45
		return new EdgePoint(Math.abs(gX) + Math.abs(gY),
				(int) ((Math.round((Math.atan2(gY, gX) * 180 / Math.PI) / 45) * 45) + 180) % 180);
	}

	// Edge and direction detection with Sobel or Prewitt operator
	// https://de.wikipedia.org/wiki/Sobel-Operator
	public static EdgePoint[] detectEdges(AbstractImage img, EdgeDetector.Type type) {
		int width = img.getImg().getWidth();
		int height = img.getImg().getHeight();
		int[] pix = img.getPix();
		
		EdgePoint edgePoints[] = new EdgePoint[height * width];

		for (int i = 0; i < pix.length; i++) {
			int x = i % width;
			int y = (int) Math.floor(i / width);
			if (x != 0 && y != 0 && x < width - 1 && y < height - 1) {
				int mx[] = { pix[(i - width) - 1], pix[(i - width)], pix[(i - width) + 1], pix[i - 1], pix[i],
						pix[i + 1], pix[(i + width) - 1], pix[(i + width)], pix[(i + width) + 1] };

				EdgePoint point = EdgeDetector.sobel(mx);

				if (type == EdgeDetector.Type.SOBEL) {
					point = EdgeDetector.sobel(mx);
				} else if (type == EdgeDetector.Type.PREWITT) {
					point = EdgeDetector.prewitt(mx);
				}

				edgePoints[y * width + x] = point;

			}
		}

		return edgePoints;
	}

	public static boolean followEdge(int curPos, EdgePoint[] edgePoints , int edgeNr, int t1, int width,
			int[] pix) {
		if (edgePoints[curPos] == null)
			return true;
		else if (edgePoints[curPos].getEdgeId() != 0) {
			// already been here
			return edgePoints[curPos].getEdgeId() != edgeNr;
		} else if (edgePoints[curPos] != null && edgePoints[curPos].getValue() < t1) {
			edgePoints[curPos].setEdgeId(-1);
			return true;
		}

		// set edgeValue
		edgePoints[curPos].setEdgeId(new Integer(edgeNr));

		// look for Points in both directions
		int dir = edgePoints[curPos].getDirection();
		int newPos1 = -1;
		int newPos2 = -1;
		int leftFallback = -1;
		int rightFallback = -1;

		switch (dir) {
		case 0:
			newPos1 = (curPos - width);
			if (edgePoints[newPos1] == null || edgePoints[newPos1].getValue() < t1) {
				leftFallback = (curPos - width) - 1;
				rightFallback = (curPos - width) + 1;
				if (edgePoints[leftFallback] == null) {
					newPos1 = rightFallback;
				} else if (edgePoints[rightFallback] == null) {
					newPos1 = leftFallback;
				} else {
					newPos1 = (edgePoints[leftFallback].getValue() >= edgePoints[rightFallback].getValue())
							? leftFallback
							: rightFallback;
				}
			}
			newPos2 = (curPos + width);
			if (edgePoints[newPos2] == null || edgePoints[newPos2].getValue() < t1) {
				leftFallback = (curPos + width) - 1;
				rightFallback = (curPos + width) + 1;
				if (edgePoints[leftFallback] == null) {
					newPos2 = rightFallback;
				} else if (edgePoints[rightFallback] == null) {
					newPos2 = leftFallback;
				} else {
					newPos2 = (edgePoints[leftFallback].getValue() >= edgePoints[rightFallback].getValue())
							? leftFallback
							: rightFallback;
				}
			}
			edgePoints[curPos].setLeftColor(pix[Math.max(0, curPos - 3)]);
			edgePoints[curPos].setRightColor(pix[Math.min(curPos + 3, pix.length - 1)]);
			break;
		case 45:
			newPos1 = (curPos - width) + 1;
			if (edgePoints[newPos1] == null || edgePoints[newPos1].getValue() < t1) {
				leftFallback = (curPos - width);
				rightFallback = (curPos) + 1;
				if (edgePoints[leftFallback] == null) {
					newPos1 = rightFallback;
				} else if (edgePoints[rightFallback] == null) {
					newPos1 = leftFallback;
				} else {
					newPos1 = (edgePoints[leftFallback].getValue() >= edgePoints[rightFallback].getValue())
							? leftFallback
							: rightFallback;
				}
			}
			newPos2 = (curPos + width) - 1;
			if (edgePoints[newPos2] == null || edgePoints[newPos2].getValue() < t1) {
				leftFallback = (curPos) - 1;
				rightFallback = (curPos + width);
				if (edgePoints[leftFallback] == null) {
					newPos2 = rightFallback;
				} else if (edgePoints[rightFallback] == null) {
					newPos2 = leftFallback;
				} else {
					newPos2 = (edgePoints[leftFallback].getValue() >= edgePoints[rightFallback].getValue())
							? leftFallback
							: rightFallback;
				}
			}
			edgePoints[curPos].setLeftColor(pix[Math.max(0, curPos - 3 * width - 3)]);
			edgePoints[curPos].setRightColor(pix[Math.min(curPos + 3 * width + 3, pix.length - 1)]);
			break;
		case 90:
			newPos1 = (curPos + 1);
			if (edgePoints[newPos1] == null || edgePoints[newPos1].getValue() < t1) {
				leftFallback = (curPos - width) + 1;
				rightFallback = (curPos + width) + 1;
				if (edgePoints[leftFallback] == null) {
					newPos1 = rightFallback;
				} else if (edgePoints[rightFallback] == null) {
					newPos1 = leftFallback;
				} else {
					newPos1 = (edgePoints[leftFallback].getValue() >= edgePoints[rightFallback].getValue())
							? leftFallback
							: rightFallback;
				}
			}
			newPos2 = (curPos - 1);
			if (edgePoints[newPos2] == null || edgePoints[newPos2].getValue() < t1) {
				leftFallback = (curPos - width) - 1;
				rightFallback = (curPos + width) - 1;
				if (edgePoints[leftFallback] == null) {
					newPos2 = rightFallback;
				} else if (edgePoints[rightFallback] == null) {
					newPos2 = leftFallback;
				} else {
					newPos2 = (edgePoints[leftFallback].getValue() >= edgePoints[rightFallback].getValue())
							? leftFallback
							: rightFallback;
				}
			}
			edgePoints[curPos].setLeftColor(pix[Math.max(0, curPos - 3 * width)]);
			edgePoints[curPos].setRightColor(pix[Math.min(curPos + 3 * width, pix.length - 1)]);
			break;
		case 135:
			newPos1 = (curPos + width) + 1;
			if (edgePoints[newPos1] == null || edgePoints[newPos1].getValue() < t1) {
				leftFallback = (curPos) + 1;
				rightFallback = (curPos + width);
				if (edgePoints[leftFallback] == null) {
					newPos1 = rightFallback;
				} else if (edgePoints[rightFallback] == null) {
					newPos1 = leftFallback;
				} else {
					newPos1 = (edgePoints[leftFallback].getValue() >= edgePoints[rightFallback].getValue())
							? leftFallback
							: rightFallback;
				}
			}
			newPos2 = (curPos - width) - 1;
			if (edgePoints[newPos2] == null || edgePoints[newPos2].getValue() < t1) {
				leftFallback = (curPos - width);
				rightFallback = (curPos) - 1;
				if (edgePoints[leftFallback] == null) {
					newPos2 = rightFallback;
				} else if (edgePoints[rightFallback] == null) {
					newPos2 = leftFallback;
				} else {
					newPos2 = (edgePoints[leftFallback].getValue() >= edgePoints[rightFallback].getValue())
							? leftFallback
							: rightFallback;
				}
			}
			edgePoints[curPos].setLeftColor(pix[Math.max(0, curPos - 3 * width + 3)]);
			edgePoints[curPos].setRightColor(pix[Math.min(curPos + 3 * width - 3, pix.length - 1)]);
			break;
		default:
			break;
		}

		boolean endPointDir1 = false;
		boolean endPointDir2 = false;

		// follow directions
		if (newPos1 >= 0 && newPos1 < edgePoints.length && edgePoints[newPos1] != null) {
			endPointDir1 = followEdge(newPos1, edgePoints, edgeNr, t1, width, pix);
		} else {
			endPointDir1 = true;
		}
		if (newPos2 >= 0 && newPos2 < edgePoints.length && edgePoints[newPos2] != null) {
			endPointDir2 = followEdge(newPos2, edgePoints, edgeNr, t1, width, pix);
		} else {
			endPointDir2 = true;
		}
		edgePoints[curPos].setEndPoint(endPointDir1 || endPointDir2);
		// SinglePoint is not an edge
		if (endPointDir1 && endPointDir2) {
			edgePoints[curPos].setEdgeId(-1);
		}
		return false;
	}

	public static EdgePoint[] hysteresis(AbstractImage img, EdgePoint[] edgePoints, double perc_t1, double perc_t2
			) {
		
		final int width = img.getImg().getWidth();
		final int height = img.getImg().getHeight();
		int[] pix = img.getPix();
		final Percentile pt1 = new Percentile(perc_t1); // 90
		final Percentile pt2 = new Percentile(perc_t2); // 95

		// Remove week edges with Hysteresis and mark where edges stop
		// https://de.wikipedia.org/wiki/Canny-Algorithmus#Hysterese
		// extract edge values
		Histogram histogram = new Histogram();
		ArrayList<Double> percValues = new ArrayList<Double>();
		for (EdgePoint e : edgePoints) {
			if (e != null) {
				int s = e.getValue();
				histogram.add(s);
				percValues.add((double) s);
			}
		}

		int histoFirstKey = histogram.firstKey();
		int histoRange = histogram.lastKey() - histogram.firstKey();

		// define T1 and T1 Threshold for hysteresis
		int t1 = (int) Math.max(pt1.evaluate(ArrayUtils.toPrimitive(percValues.toArray(new Double[percValues.size()]))),
				1);
		int t2 = (int) pt2.evaluate(ArrayUtils.toPrimitive(percValues.toArray(new Double[percValues.size()])));

		int edgeNr = 1;
		for (int i = 0; i < edgePoints.length; i++) {
			int x = i % width;
			int y = (int) Math.floor(i / width);
			if (x != 0 && y != 0 && x < width - 1 && y < height - 1) {
				int s = edgePoints[i].getValue();
				// filter noisy edges
				if (s < t1) {
					// no edge
					edgePoints[i].setEdgeId(-1);
				} else if (s >= t2 && edgePoints[i].getEdgeId() == 0) {
					// follow edge
					EdgeDetector.followEdge(new Integer(i), edgePoints, edgeNr, t1, width, pix);
					edgeNr++;
				}
				// apply Scaling to fit between 0 and 255
				s = (s - histoFirstKey) * 255 / (histoRange);
				edgePoints[i].setValue(s);

			}
		}
		
		return edgePoints;
	}

}
