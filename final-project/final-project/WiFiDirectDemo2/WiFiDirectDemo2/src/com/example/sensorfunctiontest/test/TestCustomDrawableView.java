package com.example.sensorfunctiontest.test;

import android.test.AndroidTestCase;
import android.util.Log;

import com.example.sensorfunctiontest.view.Point;
import com.example.tool.TrackingOrientation;

public class TestCustomDrawableView  extends AndroidTestCase{

	
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
	}

	public void testCalCulPosition(){
		TrackingOrientation tri = new TrackingOrientation();
		tri.setState(TrackingOrientation.STATE.BACKWARD);
		tri.setStrideLength((float)Math.sqrt(2)/2);
		tri.setAngle((float)Math.atan(1));
		Point point = new Point();
		point.setTrOri(tri);
//		float [] mPosition = point.calCulPosition(null);

//		assertEquals(mPosition[0],0.5);
//		assertEquals(mPosition[1],0.5);
		tri.setAngle(-(float)Math.atan(1));
		point.setTrOri(tri);
//		mPosition = point.calCulPosition(null);
//		assertEquals(mPosition[0],0.5);
//		assertEquals(mPosition[1],-0.5);
//		Log.i("east", mPosition[0]+"");
//		Log.i("North", mPosition[1]+"");
		
		
	}
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}


}
