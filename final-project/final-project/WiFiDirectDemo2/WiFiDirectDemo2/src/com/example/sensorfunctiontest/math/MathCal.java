package com.example.sensorfunctiontest.math;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.util.Log;

import com.example.sensorfunctiontest.EarthAcceActivity;
import com.example.tool.Filter;

public class MathCal {
	private static int stridesNumber = 0;
	

	public static int getStridesNumber() {
		return stridesNumber;
	}
	public static void setStridesNumber(int stridesNumber) {
		MathCal.stridesNumber = stridesNumber;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	/**
	 * return the stride lenght when person is at great paces
	 * 
	 * @param max  the max vertical acceleratoin of one step 
	 * @param min  the min vertical acceleration of one step
	 * @param avg  average vertcial acceleration of one step
	 * @param Samples	acceleration samples' number ,when acceleration is bigger than 0
	 * @param K  constant value
	 */

	public static float getStrideLength(float max, float min, float avg,float[][] samples,float k){
		double avg_T = 0;
		for(int i =0;i<samples.length;i++){


			for(int j =0;j<=i;j++){
				avg_T= avg_T +(samples[j][2]-avg);
			}

		}

		double temp = (max - min)*avg_T;
		BigDecimal bigDecimal = new BigDecimal(temp);
		bigDecimal = bigDecimal.divide(new BigDecimal(avg-min),3,BigDecimal.ROUND_HALF_EVEN);
		/*if(bigDecimal.doubleValue()<=0){
			Log.i("show", "big decimal is negative");
		}else{*/

		if(bigDecimal.doubleValue()<0){
			StringBuffer sBuffer = new StringBuffer();
			for(int i =0;i<samples.length;i++){
				sBuffer.append(samples[i][2]+",");
				Filter.write2SDcard("NahCycle",sBuffer);
			}
		}
//		if(bigDecimal.doubleValue()>0){
		avg_T = (float) k*Math.sqrt(Math.abs(bigDecimal.doubleValue()));
		
//		}else{
//			avg_T = 0;
//		}
		//		}

		return (float)avg_T;


	}
	/**
	 * 
	 * @param samplesCycles the samples in one cycyle
	 * @return
	 */

	public static float getStrideLength(float[][] samplesCycles){
		int halfCycleSample = 0;
		for(int i = 0;i<samplesCycles.length;i++){
			if(samplesCycles[i][2]>0){
				halfCycleSample++;				
			}
		}
		float[][] positiveSample = new float[halfCycleSample][3];
		System.arraycopy(samplesCycles, 0,positiveSample ,0,halfCycleSample);
		double stridLength = 0;
		if(EarthAcceActivity.k>0)
			stridLength = getStrideLength(getMax(samplesCycles), getMin(samplesCycles), getAve(samplesCycles),positiveSample,(float)EarthAcceActivity.k);
		else
			stridLength = getStrideLength(getMax(samplesCycles), getMin(samplesCycles), getAve(samplesCycles),positiveSample,1.0f);
			


		return (float)stridLength;

	}
	/**
	 * return the arctan of the min(Accle_e,Accle_n)/max(Accle_e,Accle_n)
	 * @param Accle_e
	 * @param Accle_n
	 * @return
	 */
	private static double getAngle(double Accle_e, double Accle_n){
		double angle = 0.0;
		BigDecimal eDecimal = new BigDecimal(Accle_e);
		BigDecimal nDecimal = new BigDecimal(Accle_n);
		if(Math.abs(Accle_e)>Math.abs(Accle_n)){
			angle = Math.atan(nDecimal.divide(eDecimal).doubleValue());

		}else{
			angle = Math.atan(eDecimal.divide(eDecimal).doubleValue());
		}

		return angle;

	}
	/**
	 * 
	 * @param sample
	 * @return get the max value of sample
	 */
	public static float getMax(float[][]sample){
		float max = 0;
		for(int i = 0;i<sample.length;i++){
			if(sample[i][2]>max){
				max = sample[i][2];
			}
		}
		return max;
	}


	/**
	 * 
	 * @param sample
	 * @return get the min value of sample
	 */
	public static float getMin(float[][]sample){
		float min = 0;
		for(int j = 0;j<sample.length;j++){
			if(sample[j][2]<min){
				min = sample[j][2];
			}
		}
		return min;
	}
	/**
	 * 
	 * @param sample 
	 * @return get the average value of sample
	 */
	public synchronized static float getAve(float[][]sample){
		double average = 0;
		for(int i = 0;i<sample.length;i++){
			average+=sample[i][2];
		}
		average = new BigDecimal(average).divide(new BigDecimal(sample.length),3,BigDecimal.ROUND_HALF_EVEN).doubleValue();
		return  (float)average;

	}
	
	/**
	 * 
	 * @param sample 
	 * @return get the average value of sample
	 */
	public synchronized static float getAve(float[][]sample,int index){
		double average = 0;
		for(int i = 0;i<sample.length;i++){
			average+=sample[i][index];
		}
		average = new BigDecimal(average).divide(new BigDecimal(sample.length),3,BigDecimal.ROUND_HALF_EVEN).doubleValue();
		return  (float)average;

	}
	public synchronized static float getAve(float[]sample){
		double average = 0;
		for(int i = 0;i<sample.length;i++){
			average+=sample[i];
		}
		average = new BigDecimal(average).divide(new BigDecimal(sample.length),3,BigDecimal.ROUND_HALF_EVEN).doubleValue();
		return  (float)average;

	}
	/**
	 * Get the length of several steps
	 * @param the list keep the step information, the length of the steps means the number of step
	 * @return
	 */
	public static float getStepsLength(List<float[][]>steps){
		float stepsLength = 0.0f;
		for(float[][]step:steps){
			stepsLength = stepsLength + getStrideLength(step);
		}
		return stepsLength;

	}
	
	
	/**
	 * @param earthAxis
	 * the east, north and vertical acceleration,earthAix[0][0] is east(positive), earthAix[0][1] is north(positive), earthAix[0][2] is vertical
	 * @return get the steps information about samples, every item is one step
	 */
	public static List<float[][]> getSetps(float[][]earthAxis){
		List<float[][]> steps = new ArrayList<float[][]>();
		boolean start = false;// Start the calculate cycle;
		int cycleStart = -100;// the start of a new cycle
		int cycleEnd = -100;// the end of new cycle
		float [][] cycleSampeles = null;
		for(int i =0;i<earthAxis.length;i++){
			if(earthAxis.length>i+1){
				if(start){
					cycleEnd = i;
					if(earthAxis[i][2]<0&&earthAxis[i+1][2]>0){					
						cycleSampeles = new float[cycleEnd - cycleStart +1][3];
						System.arraycopy(earthAxis, cycleStart, cycleSampeles, 0, cycleEnd-cycleStart+1);
						steps.add(cycleSampeles);
						Log.i("printArray", Arrays.toString(cycleSampeles));
						cycleStart = cycleEnd;
					}
					
				}
				if(!start&&earthAxis[i][2]<0&&earthAxis[i+1][2]>0){
					start = true;
					cycleStart = i+1;
				}
				

			}
		}
		return steps;
		
		
	}
	

	public static void arrayCopy(float[][]src, int srcPos, float[][] dest, int destPos, int length){
		for(int i=srcPos;i<srcPos+length;i++){
			dest[destPos++]= src[i].clone();
		}
	}


}
