package com.example.tool;
/**
 * We will track the movement. If it is Forward means north, Backward means south.
 * The positive angle means the North (South if it is backward)coordinate clockwise rotation
 * The negative angle means the North (South if it is backward)coordinate contrarotate rotation
 * @author Tim
 *
 */
public class TrackingOrientation {
	 /**
	  * Forward means moving orientation is north or east,
	  *  Backword means moving orientatino is south or west
	  * @author Tim
	  *
	  */
	private float strideLength = 0.0f;
	public static enum STATE{
		FORWARD,BACKWARD
	}

	private float angle=0;// in radian
	private STATE state= STATE.FORWARD;
	public float getAngle() {
		return angle;
	}
	public void setAngle(float angle) {
		this.angle = angle;
	}
	public STATE getState() {
		return state;
	}
	public void setState(STATE state) {
		this.state = state;
	}
	public float getStrideLength() {
		return strideLength;
	}
	public void setStrideLength(float strideLength) {
		this.strideLength = strideLength;
	}
	
	
	
	
	
	

}
