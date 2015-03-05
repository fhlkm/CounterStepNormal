package com.example.sensorfunctiontest.test;

import java.util.Arrays;

import android.test.AndroidTestCase;
import android.util.Log;

import com.example.tool.ReadAsset;

public class TestReadAsset extends AndroidTestCase{

	int[][] goldCodes = {{1,1,1,1,1,-1,-1,1,1,-1,1,-1,-1,1,-1,-1,-1,-1,1,-1,1,-1,1,1,1,-1,1,1,-1,-1,-1},
			             {1,1,1,1,1,-1,-1,1,-1,-1,1,1,-1,-1,-1,-1,1,-1,1,1,-1,1,-1,1,-1,-1,-1,1,1,1,-1},
	                     {-1,-1,-1,1,-1,1,1,-1,-1,-1,1,1,-1,1,1,1,-1,-1,1,-1,-1,-1,-1,-1,1,1,1,-1,-1,-1,-1}};
	public void testSendCode(){
		ReadAsset readAsset = new ReadAsset();
		float [] data = readAsset.getSendCode(goldCodes[0], 40);
		String s = Arrays.toString(data);
		Log.i("data------>", s);
		
	}

}
