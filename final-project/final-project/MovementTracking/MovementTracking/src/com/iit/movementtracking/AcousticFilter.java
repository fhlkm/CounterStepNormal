package com.iit.movementtracking;

public class AcousticFilter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	private float  correlation(int n,float[] code1,float[]code2){
		float correlationValue = 0;
		for(int r=0;r<=n-1;r++)
			for(int k=0;k<=n-1-r;k++){
				correlationValue += code1[k]*code2[k+r];
			}
		return correlationValue;			
		
	}

}
