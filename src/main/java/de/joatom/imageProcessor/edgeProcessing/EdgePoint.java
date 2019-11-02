package de.joatom.imageProcessor.edgeProcessing;

/**
 * @author Johannes Tomasoni
 *
 */
public class EdgePoint {
	
	private int value;
	private int direction;
	private int edgeId;
	private boolean isEndPoint;
	private int leftColor;
	private int rightColor;
	
	public EdgePoint() {
	}

	public EdgePoint(int value, int direction) {
		this.value = value;
		this.direction = direction;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public int getEdgeId() {
		return edgeId;
	}

	public void setEdgeId(int edgeId) {
		this.edgeId = edgeId;
	}

	public boolean isEndPoint() {
		return isEndPoint;
	}

	public void setEndPoint(boolean isEndPoint) {
		this.isEndPoint = isEndPoint;
	}

	public int getLeftColor() {
		return leftColor;
	}

	public void setLeftColor(int leftColor) {
		this.leftColor = leftColor;
	}

	public int getRightColor() {
		return rightColor;
	}

	public void setRightColor(int rightColor) {
		this.rightColor = rightColor;
	}

}