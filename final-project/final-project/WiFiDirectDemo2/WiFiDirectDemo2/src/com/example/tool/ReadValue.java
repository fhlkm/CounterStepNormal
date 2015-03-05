package com.example.tool;

import com.example.constant.HDClass;


public class ReadValue {

	public static final int numberOfPeroid = (int)(HDClass.sampleRate*HDClass.duration);
	double timePeroid = HDClass.duration;
	double frequency = HDClass.sampleRate;

	
	
	/**
	 * It is used to find position, the mobile a try to get b, the data is the info get by mobile a
	 * @param data the data want to find
	 * @param start, the initialize value is zero
	 * @param theNumberOfonePeroid, the number of sample in one period
	 * @return the positon of the bigget value ,it means the the sound send start place
	 */
	public int getPeakValueofCorssCorrelation(float[] data, int start,int theNumberOfonePeroid){
		int position = 0;
		float maxValue = 0.5f;
		//if the threshold value is bigger than 0.5f,if it is less than 0.5 f keep finding it at next period
		for(int i=start;i<Math.min(data.length, start+theNumberOfonePeroid);i++){
			if(Math.abs(data[i])>maxValue){
				maxValue = data[i];
				position = i;
				
			}
		}
		int startPosition =start+ theNumberOfonePeroid;
		
		if(maxValue==0.5f){
			position= getPeakValueofCorssCorrelation(data,startPosition,startPosition+theNumberOfonePeroid);
		}
		return position;
		
		
		
	}
	/**
	 * 
	 * @param data
	 * @param crossCrorrelation the position of peak of cross correaltion
	 * @param theNumberOfonePeroid
	 * @return the positon of auto corrlation that after corss correlation and close to it 
	 */
	public int getPeakValueOfAutoCorrelation(float[] data, int crossCrorrelation,int theNumberOfonePeroid){
		int position =0;
		float maxValue = 0.5f;
		for(int i= crossCrorrelation;i>Math.max(0, crossCrorrelation-theNumberOfonePeroid);i--){
			if(Math.abs(data[i])>maxValue){
				maxValue = data[i];
				position = i;
			}
		}
		return position;
	}

	public int getPeriod() {
		return numberOfPeroid;
	}


	public double getTimePeroid() {
		return timePeroid;
	}

	public void setTimePeroid(double timePeroid) {
		this.timePeroid = timePeroid;
	}

	public double getFrequency() {
		return frequency;
	}

	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}
	/**
	 * 
	 * @param a11 Mobile A auto-correlation
	 * @param a12 Mobile A cross -correlastion with mobile B
	 * @param a22 Mobile B auto -correlation
	 * @param a21 Mobile B cross -crrelation
	 * @return the distance between A and B
	 */
	public double getDistance(float[] a11,float[] a12,float[] a22,float[] a21){
		int p_a12 = getPeakValueofCorssCorrelation(a12,0,numberOfPeroid);
		int p_a11 = getPeakValueOfAutoCorrelation(a11, p_a12,numberOfPeroid);
		
		int p_a21 = getPeakValueofCorssCorrelation(a21, 0, numberOfPeroid);
		int p_a22 = getPeakValueOfAutoCorrelation(a22,p_a21,numberOfPeroid);
		
		int timeA =p_a12-p_a11;
		int timeB = p_a21 -p_a22;
		double dt = timeA/frequency - (timePeroid-(timeB/frequency));
		return dt*340;
	}
	
	/**
	 * (TA3-TA1)-(TB3-TB1))
	 * TA3 is cross correlation AB
	 * TA1 is auto correlation AA
	 * TB3 is auto correlation BB
	 * TB1 is cross correlation BA
	 * @param a11
	 * @param a12
	 * @param timeDistance the distance mobile B, correlation B-correlaton A
	 * @return
	 */
	public double getDistance(float[] a11,float[] a12,int timeDistance){
		int p_a12 = getPeakValueofCorssCorrelation(a12,0,numberOfPeroid);
		int p_a11 = getPeakValueOfAutoCorrelation(a11, p_a12,numberOfPeroid);
		
		
		int timeA =p_a12-p_a11;
		int timeB = timeDistance;
		double dt = timeA/frequency - (timePeroid-(timeB/frequency));
		return dt*340;	
	}
	/**
	 * (TA3-TA1)-(TB3-TB1))
	 * TA3 is cross correlation AB
	 * TA1 is auto correlation AA
	 * TB3 is auto correlation BB
	 * TB1 is cross correlation BA
	 * @param positionAA the position of A autocorrelation
	 * @param positionAB the position of A B crosscorrelation
	 * @param timeDistance
	 * @return
	 */
	public double getDistance(int positionAA,int positionAB, int timeDistance){			
		int timeA = positionAB -positionAA;
		int timeB = timeDistance;
		double dt = timeA/frequency - (timePeroid-(timeB/frequency));
		return dt*340;
	}

}
