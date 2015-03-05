package com.example.sensorfunctiontest.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.test.ActivityInstrumentationTestCase;

import com.example.sensorfunctiontest.EarthAcceActivity;

public class TestEarthAcceActivity  extends
ActivityInstrumentationTestCase<EarthAcceActivity>{
	private float[][] samples2 = {{0,0,-1},{0,0,-2},{0,0,-1},
			{0,0,1},{0,0,2},{0,0,1},{0,0,-1},{0,0,-2},{0,0,-1},
			{0,0,1},{0,0,2},{0,0,1},{0,0,-1},{0,0,-2},{0,0,-1},
			{0,0,1},{0,0,2},{0,0,1},{0,0,-1},{0,0,-2},{0,0,-1},
			{0,0,1},{0,0,2},{0,0,1}};

	public TestEarthAcceActivity(String pkg,
			Class<EarthAcceActivity> activityClass) {
		super(pkg, activityClass);
		// TODO Auto-generated constructor stub
	}

	public TestEarthAcceActivity(){
		super("com.example.sensorfunctiontest",EarthAcceActivity.class);
	}
	public void testgetLeftOfLastCycle(){
		int length = getActivity().getLeftOfLastCycle(samples2).length;
		assertEquals(3,length);
		
	}
	public void testgetLeftOfLastCycle2(){
		List<float[]> strideSamepleRate = new ArrayList<float[]>();
		for(int i = 0;i<samples2.length;i++){
			float[] s = {samples2[i][0],samples2[i][1],samples2[i][2]};
			strideSamepleRate.add(s);
		}
		int length = getActivity().getLeftOfLastCycle(strideSamepleRate).length;
		assertEquals(3,length);
		
	}
	

}
