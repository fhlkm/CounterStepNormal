package com.example.sensorfunctiontest;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;

import com.example.sensorfunctiontest.math.MathCal;
import com.example.sensorfunctiontest.view.CustomDrawableView;
import com.example.sensorfunctiontest.view.Point;
import com.example.tool.Filter;
import com.example.tool.TrackingOrientation;

	
	public class TestDrawActivity extends Activity{
	private float [][] cycleAnalysample ={{-1,-2,-200},{1,2,200},{2.5f,5,500},{1,2,200},{-1,-2,-200},{-2.5f,-5,-500},{-1,-2,-200},{1,2,200},{2.5f,5,500},{1f,2,200},
			{-1,-2,-200},{1,2,200},{2.5f,5,500},{1,2,200},{-1,-2,-200},{-2.5f,-5,-500},{-1,-2,-200},{1,2,200},{2.5f,5,500},{1f,2,200},
			{-1,-2,-200},{1,2,200},{2.5f,5,500},{1,2,200},{-1,-2,-200},{-2.5f,-5,-500},{-1,-2,-200},{1,2,200},{2.5f,5,500},{1f,2,200},
			{-1,-2,-200},{1,2,200},{2.5f,5,500},{1,2,200},{-1,-2,-200},{-2.5f,-5,-500},{-1,-2,-200},{1,2,200},{2.5f,5,500},{1f,2,200}
	
	};
	
	private float [][] cycleAnalysample2 ={{0,-2,-200},{0,2,200},{0,5,500},{0,2,200},{0,-2,-200},{0,-5,-500},{0,-2,-200},{0,2,200},{0,5,500},{0f,2,200},
			{0,-2,-200},{0,2,200},{0,5,500},{0,2,200},{0,-2,-200},{0,-5,-500},{0,-2,-200},{0,2,200},{0,5,500},{0f,2,200},
			{0,-2,-200},{0,2,200},{0,5,500},{0,2,200},{0,-2,-200},{0,-5,-500},{0,-2,-200},{0,2,200},{0,5,500},{0f,2,200},
			{0,-2,-200},{0,2,200},{0,5,500},{0,2,200},{0,-2,-200},{0,-5,-500},{0,-2,-200},{0,2,200},{0,5,500},{0f,2,200}
	
	};
	
	private float [][] cycleAnalysample3 ={{-1,0,-200},{1,0,200},{2.5f,0,500},{1,0,200},{-1,0,-200},{-2.5f,0,-500},{-1,0,-200},{1,0,200},{2.5f,0,500},{1f,0,200},
			{-1,0,-200},{1,0,200},{2.5f,0,500},{1,0,200},{-1,0,-200},{-2.5f,0,-500},{-1,0,-200},{1,0,200},{2.5f,0,500},{1f,0,200},
			{-1,0,-200},{1,0,200},{2.5f,0,500},{1,0,200},{-1,0,-200},{-2.5f,0,-500},{-1,0,-200},{1,0,200},{2.5f,0,500},{1f,0,200},
			{-1,0,-200},{1,0,200},{2.5f,0,500},{1,0,200},{-1,0,-200},{-2.5f,0,-500},{-1,0,-200},{1,0,200},{2.5f,0,500},{1f,0,200}
	
	};
	CustomDrawableView mCustomDrawableView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		 mCustomDrawableView = new CustomDrawableView(this);
		setContentView(mCustomDrawableView);
		List<float[][]>stepsList = MathCal.getSetps(cycleAnalysample3);
		drawTracking(stepsList);
	}

	
	private void drawTracking(List<float[][]>  sampleList){
		Point point = null;
		for(int i =0;i<sampleList.size();i++){
			float[][] oneStepSample = sampleList.get(i);
			TrackingOrientation traOri = Filter.walkingAnalys(oneStepSample);
			point = new Point();
			point.setTrOri(traOri);
			mCustomDrawableView.getPointList().add(point);			
		}
		mCustomDrawableView.invalidate();
	}

	

}
