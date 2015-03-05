package com.example.sensorfunctiontest.view;

import com.example.tool.TrackingOrientation;

public class Point{
	private float centerX;
	private float centerY;
	private TrackingOrientation trOri = null;
	public float getCenterX() {
		return centerX;
	}
	public void setCenterX(float centerX) {
		this.centerX = centerX;
	}
	public float getCenterY() {
		return centerY;
	}
	public void setCenterY(float centerY) {
		this.centerY = centerY;
	}
	// we use prePoint status and position to calculate our position  if ,we are the first point 
	//we just put our position in the center of the canavas
	public void calCulPosition(Point prePoint){
		float north_distance = 0;
		float east_distance = 0;
		if(null == trOri)
			return;
		if(trOri.getState()==TrackingOrientation.STATE.FORWARD){

			north_distance = (float)Math.abs((trOri.getStrideLength()*Math.cos(trOri.getAngle())));
			east_distance = (float)(trOri.getStrideLength()*Math.sin(trOri.getAngle()));

		}else{// backward

			north_distance = (float)Math.abs((trOri.getStrideLength()*Math.cos(trOri.getAngle())))*(-1);
			east_distance = (float)(trOri.getStrideLength()*Math.sin(trOri.getAngle()))*(-1);

		}
		if(null != prePoint){			
			centerX = prePoint.getCenterX()+east_distance;
			centerY = prePoint.getCenterY()-north_distance;
		}

	}
	public TrackingOrientation getTrOri() {
		return trOri;
	}
	public void setTrOri(TrackingOrientation trOri) {
		this.trOri = trOri;
	}


}