package com.example.sensorfunctiontest.test;

import com.example.tool.BandPassFilter;

import android.test.AndroidTestCase;
import android.util.Log;

public class TestBandPassFilter extends AndroidTestCase{
	private float[][] sampleCyclesforStride ={
			{0,0.75f,1},{0,0.35f,2},{0,-0.05f,1},{0,-0.4f,-1},{0,-0.7f,-2},{0,-0.3f,-1}
			
	};

	private double[] kernal = {1.0,2.0};
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
	}

	public  void testBandPassFilter(){
		float[][]filteredSample = BandPassFilter.filter2(sampleCyclesforStride, kernal);
		Log.i("fitler", "filter");
		
	}
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}


}
