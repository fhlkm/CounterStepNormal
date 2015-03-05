package com.example.tool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Arrays;

import android.util.Log;

import com.example.sensorfunctiontest.math.MathCal;


public class Filter {
	 //  lowPassFilteringFactor the parameters that is used to do low pass filter,
	 //lowPassFilteringFactor needs to be between 0.01 and 0.99 float
	
	private static final double  lowPassFilteringFactor = 0.9;

	private final static float rate = 1;// in order to show the the tracking we'll zoom in the rate
	/**
	 * 
	 * @param lastAcc  last acceleration
	 * @param currentAcc current acceleration that will be filtered
	 * @return the currentAcceleration after low pass filter
	 */
/*	public  double lowPassfilter(double lastAcc, double currentAcc){		
		 currentAcc = (currentAcc * lowPassFilteringFactor) + (lastAcc* (1.0f - lowPassFilteringFactor));
		 return currentAcc;
		 
	}*/
	
	public void bandPassfilter(){
		
	}
	/**
	 * Return the frequecny in one second,
	 * @param samples is the samples in one minutes
	 * @return
	 */
	public static int getFrequency(double[]samples){
		int frequency = 0;
		for(double b:samples){
			if(b==0){
				frequency++;
			}
		}
		return frequency;	
		
	}
	/**
	 * 
	 * @param earthAxis the east, north and vertical acceleration,earthAix[0][0] is east, earthAix[0][1] is north, earthAix[0][2] is vertical
	 */
	public void getOneSetpInfo(double[][]earthAxis){

		boolean start = false;// Start the calculate cycle;
		int numberOfZero = -1; // The number of earthAxis[i][2], when it is even, we just finish one cycle
		int cycleStart = 0;// the start of a new cycle
		int cycleEnd = 0;// the end of new cycle

		for(int i =0;i<earthAxis.length;i++){
			if(earthAxis.length>i){
				if(!start&&earthAxis[i][2]==0&&earthAxis[i+1][2]>0){
					start = true;
					cycleStart = i;
				}
				if(start){
					if(earthAxis[i][2]==0){
						numberOfZero++;
					}
					if(earthAxis[i][2]==0&&numberOfZero%2==0&&numberOfZero !=0){
						cycleEnd = i;
						double [][] cycleSampeles = new double[cycleEnd - cycleStart +1][3];
						System.arraycopy(earthAxis, cycleStart, cycleSampeles, 0, cycleEnd-cycleStart+1);
						Log.i("printArray", Arrays.toString(cycleSampeles));
						cycleStart = cycleEnd;
					}
				}

			}
		}
		
		
	}
	
	/**
	 * We analys the walking direction depends on the peak's position. In one period of vertical curve, if the north(float[1]) peak appears on vertical descending peroid
	 * we will direct to north, otherwise ,we are walking south
 	 * float[0] direct to west,west is negative,east is positive 
	 * float[1] direct to north,  positive is north ,negative is south
	 * float[2] direct to the ground
	 * @param cycleSamples the samples in one cycle
	 */
	public static TrackingOrientation walkingAnalys(float[][]cycleSamples){
		TrackingOrientation track = new TrackingOrientation();
		
		float temp = 0;
		float verticalPeak = 0;// vertical velocity
		float verticalValley = 0;
		int verticalPeakPosition = 0;
		int vertcialValleyPosition = 0;
		
		float northPeak = 0;
		float northValley = 0;
		float eastPeak =0;
		float eastValley = 0;
		int northPeakPosition = 0;
		int northValleyPosition = 0;
		int eastPeakPosition = 0;
		int eastValleyPosition = 0;
		
		for(int i =0;i<cycleSamples.length;i++){
			// get vertical Peak
			if(cycleSamples[i][2]>=verticalPeak){
				verticalPeak = cycleSamples[i][2];
				verticalPeakPosition = i;			 
			}
			//get vertical valley
			if(cycleSamples[i][2]<verticalValley){
				verticalValley = cycleSamples[i][2];
				vertcialValleyPosition = i;
			}
			// north peak value an position
			if(cycleSamples[i][1]>= northPeak){
				northPeak = cycleSamples[i][1];
				northPeakPosition = i;
			}
			// north valley value and position
			if(cycleSamples[i][1]<northValley){
				northValley = cycleSamples[i][1];
				northValleyPosition = i;
			}
			// east peak value and position
			if(cycleSamples[i][0]>=eastPeak){
				eastPeak = cycleSamples[i][0];
				eastPeakPosition = i;
			}
			// east valley value and positon
			if(cycleSamples[i][0]<eastValley){
				eastValley = cycleSamples[i][0];
				eastValleyPosition = i;
			}
						
		}
		
		track = getOrientattion(track,verticalPeak,verticalValley,verticalPeakPosition,
				vertcialValleyPosition, northPeak,northValley, northPeakPosition,  northValleyPosition, eastPeak,
				eastValley, eastPeakPosition, eastValleyPosition,cycleSamples);
		Log.i("EastPeakValue:  ", eastPeak+"");;
		Log.i("EastPeakValley:  ", eastValley+"");;
		Log.i("EastPeakValuePosition:  ", eastPeakPosition+"");;
		Log.i("EastPeakValleyPosition:  ", eastValleyPosition+"");;
		
		Log.i("NorthValue:  ", northPeak+"");;
		Log.i("NorthValley:  ", northValley+"");;
		
		Log.i("NorthValue:  ", northPeakPosition+"");;
		Log.i("NorthValley:  ", northValleyPosition+"");;
		
		
		Log.i("verticalValue:  ", verticalPeak+"");;
		Log.i("EastNorthValley:  ", verticalValley+"");;
		
		Log.i("EastNorthValue:  ", verticalPeakPosition+"");;
		Log.i("EastNorthValley:  ", vertcialValleyPosition+"");;
		return track;
	}
	/**
	 * All of the value must be in one period.
	 * 
	 * @param track
	 * @param verticalPeakValue
	 * @param verticalValleyValue
	 * @param verticalPeakPositon
	 * @param verticalValleyPosition
	 * @param northPeakValue
	 * @param northValleyValue
	 * @param northPeakPositon
	 * @param northValleyPosition
	 * @param eastPeakValue
	 * @param eastValleyValue
	 * @param eastPeakPosition
	 * @param eastValleyPosition
	 * @return
	 */
	public static TrackingOrientation getOrientattion(TrackingOrientation track, float verticalPeakValue,float verticalValleyValue,int verticalPeakPositon,
			int verticalValleyPosition, float northPeakValue,float northValleyValue, int northPeakPosition, int northValleyPosition, float eastPeakValue,
			float eastValleyValue, int eastPeakPosition, int eastValleyPosition,float[][]cycleSamples){
		if(null != track){
			if(Math.abs(eastPeakValue)>=Math.abs(eastValleyValue)){// the width upper is wider than under
				if(eastPeakPosition>verticalPeakPositon&&eastPeakPosition<=verticalValleyPosition){
					track.setState(TrackingOrientation.STATE.BACKWARD);
				}else{
					track.setState(TrackingOrientation.STATE.FORWARD);
				}
			}else{// the width under is wider than upper
				if(eastValleyPosition>verticalPeakPositon&&eastValleyPosition <= verticalValleyPosition){
					track.setState(TrackingOrientation.STATE.FORWARD);
				}else{
					track.setState(TrackingOrientation.STATE.BACKWARD);
				}
			}
			double tanAngle = 0;
			if(northPeakValue>=eastPeakValue){
				if(cycleSamples[northPeakPosition][1]==0){
					tanAngle =Math.PI;
				}else{
					tanAngle = cycleSamples[northPeakPosition][0]/cycleSamples[northPeakPosition][1];
				}
				track.setAngle((float)Math.atan(tanAngle));
			}else{
				if(cycleSamples[northPeakPosition][1]==0){
					tanAngle =Math.PI;
				}else{
					tanAngle = cycleSamples[eastPeakPosition][0]/cycleSamples[eastPeakPosition][1];
				}
				track.setAngle((float)Math.atan(tanAngle));
			}
			
			float strideLength = MathCal.getStrideLength(cycleSamples)/rate;
			track.setStrideLength(strideLength);
		}
		return track;
		
	}
	/**
	 * @deprecated
	 * This method doesn't doing very well because the horizontal curve is not exact sine curve. It is possible that the width between the upper and under  is different
	 * float[0] direct to west,west is negative,east is positive 
	 * float[1] direct to north,  positive is north ,negative is south
	 * float[2] direct to the ground
	 * We check form the start point to the peak point. In this arrange , if the NorthAcceleration peak is bigger than NorthAcceleartion we can inter it is forward
	 * The angle is 0-180
	 * @param cycleSamples the samples in one cycle
	 */
	public static TrackingOrientation cycleAnalys(float[][]cycleSamples){
		int peakPosition = 0;// vertical velocity
		
		float temp = 0;
		for(int i =0;i<cycleSamples.length;i++){
			if(cycleSamples[i][2]>=temp){
				temp = cycleSamples[i][2];
				peakPosition = i;
				
			}
			
		}
		double northPeak = 0;
		double northValley = 0;
		double eastPeak =0;
		double eastValley = 0;
		int northPeakPosition = 0;
		int eastPeakPosition = 0;
		for(int j = 0;j<=peakPosition;j++){
			if(cycleSamples[j][1]>northPeak){
				northPeak = cycleSamples[j][1];//get the north peak value
				northPeakPosition = j;// get the position of north peak value
				
				
			}
			if(cycleSamples[j][0]>eastPeak){
				eastPeak = cycleSamples[j][0];
				eastPeakPosition = j;
			}
			
			if(cycleSamples[j][1]<northValley){// get the north valley value
				northValley = cycleSamples[j][1];// get the position of north valley value
			}
			if(cycleSamples[j][0]<eastValley){// get the north valley value
				eastValley = cycleSamples[j][0];// get the position of north valley value
			}
			
		}
		TrackingOrientation track = new TrackingOrientation();
		if(Math.abs(northPeak)>=Math.abs(northValley)){
			track.setState(TrackingOrientation.STATE.FORWARD);
			Log.i("Status", "Forward");
		}else{
			track.setState(TrackingOrientation.STATE.BACKWARD);
		}
		float northValue =0;
		float eastValue =0;
		if(Math.abs(northPeak)>=Math.abs(eastValley)){
		northValue = cycleSamples[northPeakPosition][1];// at northPeakPosition, the value of north
		eastValue = cycleSamples[northPeakPosition][0];// at northPeakPosition , the value of east
		}else{
			northValue = cycleSamples[eastPeakPosition][1];// at eastPeakPosition, the value of north
			eastValue = cycleSamples[eastPeakPosition][0];// at eastPeakPosition , the value of east
		}
		if(northValue == 0){
			track.setAngle((float)Math.PI/2);
		}else{
			track.setAngle((float)Math.atan(eastValue/northValue));
		}
		float strideLength = MathCal.getStrideLength(cycleSamples)/rate;
		track.setStrideLength(strideLength);
		return track;
		
	}
	
	/**
	 * @deprecated
	 * float[0] direct to north, positive is north ,negative is south
	 * float[1] direct to west, west is negative,east is positive
	 * float[2] direct to the ground
	 * We check form the start point to the peak point. In this arrange , if the NorthAcceleration peak is bigger than NorthAcceleartion we can inter it is forward
	 * The angle is 0-180
	 * @param cycleSamples the samples in one cycle
	 */
	public static TrackingOrientation cycleAnalys2(float[][]cycleSamples){
		float aveNorth = MathCal.getAve(cycleSamples,0);
		float aveSouth = MathCal.getAve(cycleSamples, 1);
		TrackingOrientation track = new TrackingOrientation();
		if(aveNorth>=0){
			track.setState(TrackingOrientation.STATE.FORWARD);
			Log.i("Status", "Forward");
		}else{
			track.setState(TrackingOrientation.STATE.BACKWARD);
		}
	
		if(aveNorth == 0){
			track.setAngle((float)Math.PI/2);
		}else{
			track.setAngle((float)Math.atan(aveSouth/aveNorth));
		}
		
		float strideLength = MathCal.getStrideLength(cycleSamples)/rate;
		track.setStrideLength(strideLength);
		return track;
		
	}
	
	/**
	 * Confirm the status of one step
	 */
	private void getOrientation(double stepEast, double stepNorth){
		TrackingOrientation track = new TrackingOrientation();
		
		
		
	}
/**
 * Get Variance of stride
 * @param samples
 * @return
 */
	public static float getVariance(float[][]samples){
		if(samples.length == 0)
			return 0.0f;
		float avg = MathCal.getAve(samples);
		float variance = 0.0f;
		for(int i=0;i<samples.length;i++){
			variance+=Math.pow((samples[i][2]-avg),2);			
		}
		variance = new BigDecimal(variance).divide(new BigDecimal(samples.length), 3, BigDecimal.ROUND_HALF_EVEN).floatValue();
		return variance;
		
	}

/**
 * Get variance of one step 
 * @param samples
 * @return
 */
	public static  float getVariance(float[]samples){
		if(samples.length == 0)
			return 0.0f;
		float avg = MathCal.getAve(samples);
		float variance = 0.0f;
		for(int i=0;i<samples.length;i++){
			variance+=Math.pow((samples[i]-avg),2);			
		}
		variance = new BigDecimal(variance).divide(new BigDecimal(samples.length), 3, BigDecimal.ROUND_HALF_EVEN).floatValue();
		return variance;
		
	}

	public static void write2SDcard(String name,StringBuffer sBuffer){
		File saveFile=new File("/sdcard/"+name+".txt");
		 try {
			 FileWriter fw=new FileWriter(saveFile);
			 BufferedWriter buffw=new BufferedWriter(fw);
			 PrintWriter pw=new PrintWriter(buffw);
			 pw.println(sBuffer.toString());
			 pw.close();
			 buffw.close();
			 fw.close();
        
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void write2SDcard(String name,String sInfo){
		File saveFile=new File("/sdcard/"+name+".txt");
		 try {
			 FileWriter fw=new FileWriter(saveFile);
			 BufferedWriter buffw=new BufferedWriter(fw);
			 PrintWriter pw=new PrintWriter(buffw);
			 pw.println(sInfo);
			 pw.close();
			 buffw.close();
			 fw.close();
        
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * @deprecated 
	 * This method doesn't use anymore. Because we'll use band pass filter instead of it
	 * 
	 * We try to smooth the samples by with the previous and after halfwindow samples
	 * @param samples
	 * @param windowLength it must be odd
	 */
	public static void smoothSample(float[][] samples,int windowLength){
		if(samples.length>=windowLength){
			for(int i = windowLength/2;i<samples.length-(windowLength-1)/2;i++){
				float[]preSum = getPreHalfWindow(samples,i,windowLength);
				float[]postSum = getPostHalfWindow(samples,i,windowLength);
				samples[i][0] = (preSum[0]+postSum[0]+samples[i][0])/windowLength;
				samples[i][1] = (preSum[1]+postSum[1]+samples[i][1])/windowLength;
				samples[i][2] = (preSum[2]+postSum[2]+samples[i][2])/windowLength;
			}
		}
		
	}
	/***
	 * Get the half window samples sum before K
	 * @param samples
	 * @param k is the postion of the items in samples
	 * @return the return item according to samples[0], samples[1], samples[2],
	 */
	private static float[]  getPreHalfWindow(float[][] samples,int k,int windowLength){
		
		float sumX = 0;
		float sumY = 0;
		float sumZ = 0;
		for(int i =k;i>0&&i>k-windowLength/2;i--){
			sumY = sumY+samples[i-1][0];
			sumX = sumX+samples[i-1][1];
			sumZ = sumZ+samples[i-1][2];
			
		}
		float[] axis = {sumY,sumX,sumZ};
		return axis;
		
	}
	/**
	 * Get the half window samples sum after k
	 * @param samples
	 * @param k is the postion of the items in samples
	 * @return is the postion of the items in samples
	 */
	private static float[] getPostHalfWindow(float[][] samples, int k,int windowLength){
		float sumX = 0;
		float sumY = 0;
		float sumZ = 0;
		for(int i =k;i>=k&&i<k+windowLength/2;i++){
			sumY = sumY+samples[i+1][0];
			sumX = sumX+samples[i+1][1];
			sumZ = sumZ+samples[i+1][2];
		}
		float[] axis = {sumY,sumX,sumZ};
		return axis;
	}
	/**
	 * add the items in pre and post
	 * @param pre
	 * @param post
	 * @return the float array have the sum of pre and post
	 */
	private static float[] addArray(float[] pre,float[] post){
		float[] axis = new float[pre.length];
		for(int i=0;i<post.length;i++){
			axis[i] = pre[i]+post[i];
		}
		
		return axis;
		
	}
	/**
	 * Beause the last point is the new point,we try to smooth the samples use the new sample just added
	 * @param samples
	 * @param the length of the smooth window
	 */
	public static void smoothNewSample(float[][]samples,int windowLength){
		float[][] endSamples = new float[windowLength][3];
		System.arraycopy(samples, samples.length-windowLength,endSamples, 0,windowLength);
		Filter.smoothSample(endSamples,windowLength);
		System.arraycopy(endSamples, 0,samples, samples.length-windowLength,windowLength);
	}
	/**
	 * Find the last zero point of steps period
	 * @param samples
	 * @param the half length of smooth window
	 * @return the position of the last zero point
	 */
	public static int endPeroidPosition(float[][]samples,int halfWindow){
		int pSmoothEnd = samples.length - halfWindow-1;
		int pPeriodEnd = pSmoothEnd;
		for(int i =pSmoothEnd;i>0;i--){
			if(samples[i][2]>0&&samples[i-1][2]<=0){
				pPeriodEnd = i-1;
				break;
			}
		}
		return pPeriodEnd;

	}
	
	public synchronized static float[][] hDFilterVertical(float[]Hd, float[][]data){
		if(data == null)
			return data;
		float filterData[][] = new float[data.length][3];
		for(int i =0;i<data.length;i++){
			filterData[i][0] = data[i][0];
			filterData[i][1] = data[i][1];
			int k = i;
			for(int j =0;j<Hd.length;j++){
				if(k>=0){
				filterData[i][0] = filterData[i][0] +Hd[j]*data[k][0];
				filterData[i][1] = filterData[i][1] +Hd[j]*data[k][1];
				filterData[i][2] = filterData[i][2] +Hd[j]*data[k][2];
				k--;
				}
			}
		}
		return filterData;
	}
	
	public synchronized static float[][] hDFilterVerticalHorizontal(float[]Hd, float[][]data){
		if(data == null)
			return data;
		float filterData[][] = new float[data.length][3];
		for(int i =0;i<data.length;i++){
			filterData[i][2] = data[i][2];
			int k = i;
			for(int j =0;j<Hd.length;j++){
				if(k>=0){
				filterData[i][0] = filterData[i][0] +Hd[j]*data[k][0];
				filterData[i][1] = filterData[i][1] +Hd[j]*data[k][1];
//				filterData[i][2] = filterData[i][2] +Hd[j]*data[k][2];
				k--;
				}
			}
		}
		return filterData;
	}
	public  float[]hDFilter2(float[]Hd, float[]data){
		if(data == null)
			return data;
		float filterData[] = new float[data.length];
		for(int i =0;i<data.length;i++){
			int k = i;
			for(int j =0;j<Hd.length;j++){
				if(k>=0){
				filterData[i] = filterData[i] +Hd[j]*data[k];
				k--;
				}
			}
		}
		return filterData;
	}
	/**
	 * 
	 * @param rec  recevied signal
	 * @param user user's signal
	 * @param samplesNumber  
	 * @param cycleSample  the cycle's sample,the sound we want to send gold[0].length*40
	 * @return the array after correlation
	 */
	public   void correlation(float[] rec, float[]user, int samplesNumber, int cycleSample ){
		StringBuffer s = new StringBuffer();
		for(float f:rec){
			s.append(f+",");
		}
		write2SDcard("rec",s.toString());
		s =new StringBuffer();
		for(float f:user){
			s.append(f+",");
		}
		write2SDcard("user",s.toString());
		long start = System.currentTimeMillis();
		for(int i=0;i<=samplesNumber-cycleSample;i++){
			float[] c_j = new float[cycleSample];
			System.arraycopy(rec, i,c_j, 0,  cycleSample);
			subMeanValue(c_j);
			subMeanValue(user);
			float numberator =0f;
			float denominator1 = 0f;
			float denominator2 = 0f;
			for(int k =0;k<cycleSample;k++){
				//???
				numberator += user[k]*c_j[k];
				denominator1 += Math.pow(user[k], 2);
				denominator2 += Math.pow(c_j[k], 2);
			}
									
		
			float denominator =(float) (Math.sqrt(denominator2)*Math.sqrt(denominator1));
			rec[i] = numberator/denominator;
			
		}
		long end = System.currentTimeMillis();
		long usingTime = end-start;
		Log.i("Time", "number: "+rec.length+"Time: "+usingTime);
	}
	
	public static void pearsonCorrelation(){
		
	}
	
	
	/**
	 * 
	 * @param samples
	 * @return the items subtrack the mean value
	 */
	public  void subMeanValue(float[] samples){
		float ave = MathCal.getAve(samples);
		for(int i =0;i<samples.length;i++){
			samples[i] = samples[i]-ave;
		}
		
	}
	/**
	 * receive * cos(wt). the length of receive and sampleRate is equal
	 * @param receive the signal received in one second
	 * @param sampleRate  
	 */
	public  void multiplyFrequency( float[] receive,int sampleRate,int freqOfTone){


		for (int i = 0; i < Math.min(receive.length, sampleRate); ++i) {      // Fill the sample array
			float cos_wt =(float)	Math.cos(freqOfTone * 2 * Math.PI * i / (sampleRate));
			receive[i] = receive[i]*cos_wt;    			  		

		}
	}
}
