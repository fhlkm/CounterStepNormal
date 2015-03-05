package com.example.tool;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;

public class ReadAsset {
	private Activity mActivity;


	
	public ReadAsset(Activity mActivity) {
		super();
		this.mActivity = mActivity;
	}

	public  ReadAsset(){
		
	}

	public float[] readAsset(String file){
		float[] floatArray = null;
		  
		try {
			 InputStream is= mActivity.getAssets().open(file);
	         // We guarantee that the available method returns the total
	           // size of the asset...  of course, this does mean that a single
	           // asset can't be more than 2 gigs.
	           int size = is.available();
	           
	           // Read the entire asset into a local byte buffer.
	           byte[] buffer = new byte[size];
	           is.read(buffer);
	           is.close();
	           
	           // Convert the buffer into a string.
	           String text  = new String(buffer);
	           floatArray = string2float(text.trim().split(","));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
           
  
           return floatArray;
	}
	
	private float[] string2float(String [] sArrays){
		float[] arrays = new float[sArrays.length];
		for(int i =0;i<arrays.length;i++){
			arrays[i] =Float.parseFloat(sArrays[i]);
		}
		return arrays;
		
	}
	
	public float[] getSendCode(int[] goldCode, int chip){
		float [] newCode = new float[goldCode.length*chip];
		for(int i=0;i<goldCode.length;i++ )
			for(int j =0;j<chip;j++){
				newCode[i*chip+j] = goldCode[i];
			}
		return newCode;
		
	}

}
