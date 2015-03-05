package com.example.tool;

import java.util.List;

public class TransferTool {

	
	public static float[][] getfloatFromList(List <float[]>mList){
		float [][] sample = null;
		if(null != mList){
			sample = new float[mList.size()][mList.get(0).length];
			for(int i = 0;i<mList.size();i++){
				for(int j = 0;j<mList.get(0).length;j++){
					sample[i][j]= mList.get(i)[j];
				}
			}
		}
		return sample;
		
	}
	
	

	/**
	 * Add the float[][] from src to the tail of des
	 * @param des
	 * @param src
	 */
	public static void listMove(List<float[][]> des,List<float[][]> src ){
		for(int i =0;i<src.size();i++){
			des.add(src.get(i));
		}
		
	}
	
	public static float[]getVerticalSample(float[][]samples){
		float[] verticalSample = new float[samples.length];
		for(int i =0;i<samples.length;i++){
			verticalSample[i]= samples[i][2];
		}
		return verticalSample;
		
	}
	public static void setBandPassVerticalSample(float[][]samples,float[]verticalSample){
		for(int i =0;i<samples.length;i++){
			samples[i][2] = verticalSample[i];
		}
		
	}

}
