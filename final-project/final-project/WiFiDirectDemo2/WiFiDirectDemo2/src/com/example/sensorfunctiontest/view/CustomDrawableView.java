package com.example.sensorfunctiontest.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

import com.example.tool.TrackingOrientation;

public class CustomDrawableView extends View {
	Paint paint = new Paint();
	private float pre_CenterX = 0;
	private float pre_CenterY = 0;
	float half_Rect_length = 5;
	List<Point> pointList = new ArrayList<Point>();

	public CustomDrawableView(Context context) {
		super(context);
		paint.setColor(0xff00ff00);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(2);
		paint.setAntiAlias(true);
	};

	public CustomDrawableView(Context context, AttributeSet attrs){
		super(context, attrs);
		paint.setColor(0xff00ff00);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(2);
		paint.setAntiAlias(true);
	}

	public CustomDrawableView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		paint.setColor(0xff00ff00);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(2);
		paint.setAntiAlias(true);
	}
	protected void onDraw(Canvas canvas) {
		int width = getWidth();
		int height = getHeight();
		int centerx = width/2;
		int centery = height/2;
		pre_CenterY = centery;
		pre_CenterX = centerx;
		Point prePoint = null;
		canvas.drawRect(centerx-half_Rect_length, centery-half_Rect_length, centerx+half_Rect_length, centery+half_Rect_length, paint);
		//	     canvas.drawLine(centerx, 0, centerx, height, paint);
		//	     canvas.drawLine(0, centery, width, centery, paint);
		for(int i = 0;i<pointList.size();i++){
			Point p = pointList.get(i);
			if(i == 0){
				p.setCenterX(pre_CenterX);
				p.setCenterY(pre_CenterY);
			}else{
				p.calCulPosition(prePoint);
			}
			
			float p_centerx = p.getCenterX();
			float p_centery = p.getCenterY();
			canvas.drawRect(p_centerx-half_Rect_length, p_centery-half_Rect_length, p_centerx+half_Rect_length, p_centery+half_Rect_length, paint);
			canvas.drawLine(pre_CenterX, pre_CenterY, p_centerx, p_centery, paint);
			pre_CenterY = p_centery;
			pre_CenterX = p_centerx;
			prePoint = p;
		}
		/*// Rotate the canvas with the azimut     
	     if (azimut != null)
	       canvas.rotate(-azimut*360/(2*3.14159f), centerx, centery);
	     paint.setColor(0xff0000ff);
	     canvas.drawLine(centerx, -1000, centerx, +1000, paint);
	     canvas.drawLine(-1000, centery, 1000, centery, paint);
	     canvas.drawText("N", centerx+5, centery-10, paint);
	     canvas.drawText("S", centerx-10, centery+15, paint);*/
		paint.setColor(0xff00ff00);
	}

	public List<Point> getPointList() {
		return pointList;
	}

	public void setPointList(List<Point> pointList) {
		this.pointList = pointList;
	}

	public float getPre_CenterX() {
		return pre_CenterX;
	}

	public void setPre_CenterX(float pre_CenterX) {
		this.pre_CenterX = pre_CenterX;
	}

	public float getPre_CenterY() {
		return pre_CenterY;
	}

	public void setPre_CenterY(float pre_CenterY) {
		this.pre_CenterY = pre_CenterY;
	}


	
	


}


