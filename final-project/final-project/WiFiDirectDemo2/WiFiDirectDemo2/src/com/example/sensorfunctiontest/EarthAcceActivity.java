package com.example.sensorfunctiontest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.constant.HDClass;
import com.example.sensorfunctiontest.math.MathCal;
import com.example.sensorfunctiontest.view.CustomDrawableView;
import com.example.sensorfunctiontest.view.Point;
import com.example.sensorfunctiontestttt.R;
import com.example.tool.Filter;
import com.example.tool.TrackingOrientation;
import com.example.tool.TransferTool;

public class EarthAcceActivity extends Activity implements SensorEventListener {

	private final String log ="EarthAcceActivity";

	private SensorManager mSensorManager;
	private Sensor mAcceler;
	private Sensor mAgnet;
	private Sensor mOrientation;
	private TextView mText;
	//////////////////////////////////
	private int mRate = SensorManager.SENSOR_DELAY_NORMAL; 
	private int mCycle = 100; 
	private int mEventCycle = 100; 

	private float mAccuracyX = 0; 
	private float mAccuracyY = 0; 
	private float mAccuracyZ = 0; 

	private long lastUpdate = -1; 
	private long lastEvent = -1; 
	private float x = -999, y = -999, z = -999;  

	private TextView xAccer;
	private TextView yAccer;
	private TextView zAccer;
	private TextView strideLength;
	///////////////////////////////////////
	float[]  accelerometervalues =null;
	float[] orientationvalues=null;
	float[] geomagneticmatrix = null;
	///////////////////////////////////
	float[] values=new float[3];
	float[] rotate=new float[9];
	private Date mDate;
	private long initalTime = 0l; 
	private int sampleTimes = 1;
	/**
	 * The window is used to smooth samples
	 */
	private final int windowLength = 71;
	/**
	 * If it is false, means we did not do smooth ever before
	 * If is is true, we have already done smooth operation
	 */
	private boolean startSmooth = false;
	//	private int sampleRate = 0;
	/**
	 * The sample of step
	 */
	private List<float[]> strideSamepleRate = new ArrayList<float[]>();
	public static double  k = 0.0658;
	private EditText kInput = null;
	private static float stridelength = 0.0f;
	private TextView stride = null;
	private Button btn = null;
	private Button reset_btn = null;
	private boolean isReister = false;

	private static int sampleNumber = 0;
	private StringBuffer sBufferRaw = new StringBuffer();
	private StringBuffer sBufferFilter = new StringBuffer();
	static final float ALPHA = 0.25f; // if ALPHA = 1 OR 0, no filter applies.s
	private float[] prev = null;
	private float[][]lastCycleSample = null;
	public   TextView mStridesNumberView = null;
	private final float movingThreshold = 3.0f;
	private final int windowSize = 32;
	private int strides_Number = 0;
	private List<float[][]> allSampleList = new ArrayList<float[][]>();
	private float[][]allSampleArray = null;
	private CustomDrawableView mDrawTrackingView = null;
	private long preTime = 0;
	int pLastSmoothCirleStart = 0;// the first point that can't been smoothed
	int pLastPeriodPosition = 0;// the last steps period end point
	float[][]lastUnSmoothArray = new float[windowLength-1][3];// the array contains the samples that will be used for smooth of the next new samples arrays
	float[][]lastUnUsedSmoothSamples = null;// from last point of the steps period to the last smoothed point p
	private long timeStart =0;
	private int sampleRate = 0;
	private long timeOver = 0;
	float[]receive =null;

/*	int[][] goldCodes = {{1,1,1,1,1,-1,-1,1,1,-1,1,-1,-1,1,-1,-1,-1,-1,1,-1,1,-1,1,1,1,-1,1,1,-1,-1,-1},
			             {1,1,1,1,1,-1,-1,1,-1,-1,1,1,-1,-1,-1,-1,1,-1,1,1,-1,1,-1,1,-1,-1,-1,1,1,1,-1},
	                     {-1,-1,-1,1,-1,1,1,-1,-1,-1,1,1,-1,1,1,1,-1,-1,1,-1,-1,-1,-1,-1,1,1,1,-1,-1,-1,-1}};*/
	@Override
	public final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accer_show);

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAcceler = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mAgnet = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//		mText = (TextView)findViewById(R.id.txt_light);
		xAccer = (TextView)findViewById(R.id.xaccer);
		yAccer = (TextView)findViewById(R.id.yaccer);
		zAccer = (TextView)findViewById(R.id.zaccer);
		kInput = (EditText)findViewById(R.id.k_value);
		stride = (TextView)findViewById(R.id.stride_length);
		//		kInput.setOnClickListener(Listener);
		btn = (Button)findViewById(R.id.start);
		btn.setOnClickListener(Listener);
		reset_btn = (Button)findViewById(R.id.reset);
		reset_btn.setOnClickListener(Listener);
		mStridesNumberView = (TextView)findViewById(R.id.stride_number);
		mDrawTrackingView = (CustomDrawableView)findViewById(R.id.canvasview);

//		read();
	}
	private View.OnClickListener Listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.start:
				String value = kInput.getText().toString();
				MathCal.setStridesNumber(0);
				if(value.length()<=0){
					Toast.makeText(EarthAcceActivity.this, "Please Input", Toast.LENGTH_LONG).show();
				}else{
//					k = Double.parseDouble(value);
					mDate  = new Date();
					initalTime = mDate.getTime();/*// NORMAL IS 10, ui is 30, GAME 100
					mSensorManager.registerListener(EarthAcceActivity.this, mAcceler, SensorManager.SENSOR_DELAY_UI);
					mSensorManager.registerListener(EarthAcceActivity.this, mAgnet, SensorManager.SENSOR_DELAY_UI);*/	

					mSensorManager.registerListener(EarthAcceActivity.this, mAcceler, SensorManager.SENSOR_DELAY_GAME);
					mSensorManager.registerListener(EarthAcceActivity.this, mAgnet, SensorManager.SENSOR_DELAY_GAME);
					isReister = true;
					timeStart = System.currentTimeMillis();


				}
				break;
			case R.id.reset:
				reset();
				break;

			}


		}
	};


	@Override
	public final void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Do something here if sensor accuracy changes.
	}

	@Override
	public final void onSensorChanged(SensorEvent event) {
		if(!isReister){
			return;
		}
		mDate = new Date();
		if(null != strideSamepleRate && strideSamepleRate.size()>=100){// if the # of samples is equal or bigger than 300
			sampleNumber++;
			if(sBufferRaw.length()>0){
				sBufferRaw.delete(0, sBufferRaw.length()-1);
			}
			//			float[][] samples = null;
			float[][] samples  = TransferTool.getfloatFromList(strideSamepleRate);
			// spend two much time
			if(Filter.getVariance(samples)>movingThreshold){// we try to make sure we are moving, we set a threshold, if the sample variance bigger than threshold ,we think it is moving
				
				if(null == allSampleArray){
					allSampleArray = new float[samples.length][3];
					System.arraycopy(samples, 0, allSampleArray, 0, samples.length);
				}else{
					// add the new sampling to the end
					float[][]temp = new float[allSampleArray.length+samples.length][3];
					System.arraycopy(allSampleArray, 0, temp, 0, allSampleArray.length);
					System.arraycopy(samples, 0, temp, allSampleArray.length, samples.length);
					allSampleArray = temp;
					samples = null;
				}
				float [][] copy = new float[allSampleArray.length][3];
				MathCal.arrayCopy(allSampleArray, 0, copy, 0, allSampleArray.length);
				
				
			
//				Filter.smoothSample(copy, windowLength);
//				copy = Filter.hDFilter(HDClass.HD1to3,copy);
//				List<float[][]>stepsList = MathCal.getSetps(copy);
				updateSteps(copy);
				
				strideSamepleRate.clear();
				
				final int sampleLength = 100;
				
				// east
				if(sampleNumber>=sampleLength){
					Toast.makeText(this, "Finish", Toast.LENGTH_LONG).show();
					sBufferRaw = new StringBuffer();
					for(int i =0;i<allSampleArray.length;i++){
						sBufferRaw.append(allSampleArray[i][0]+",");
					}
					printSample(sBufferRaw,"unSmoothedY"+sampleTimes);
					
				}
			/*	if(sampleNumber>=sampleLength){
					sBufferRaw = new StringBuffer();
					for(int i =0;i<copy.length;i++){
						sBufferRaw.append(copy[i][0]+",");
					}
					printSample(sBufferRaw,"smoothedY"+sampleTimes);
				}*/
				
				//north
				if(sampleNumber>=sampleLength){
					sBufferRaw = new StringBuffer();
					for(int i =0;i<allSampleArray.length;i++){
						sBufferRaw.append(allSampleArray[i][1]+",");// north
					}
					printSample(sBufferRaw,"unSmoothedX"+sampleTimes);
				}
/*				if(sampleNumber>=sampleLength){
					sBufferRaw = new StringBuffer();
					for(int i =0;i<copy.length;i++){
						sBufferRaw.append(copy[i][1]+",");
					}
					printSample(sBufferRaw,"smoothedX"+sampleTimes);
				}
		*/
				
				if(sampleNumber>=sampleLength){
					sBufferRaw = new StringBuffer();
					for(int i =0;i<allSampleArray.length;i++){
						sBufferRaw.append(allSampleArray[i][2]+",");
					}
					printSample(sBufferRaw,"unSmoothedZ"+sampleTimes);
				}
/*				if(sampleNumber>=sampleLength){
					sBufferRaw = new StringBuffer();
					for(int i =0;i<copy.length;i++){
						sBufferRaw.append(copy[i][2]+",");
					}
					printSample(sBufferRaw,"smoothedZ"+sampleTimes);
				}
			*/
				strideSamepleRate.clear();
			
			}else{// depend on our experience if the variance is smaller than threshold, then the person is stay stable,so we clear it
				strideSamepleRate.clear();
				startSmooth = false;

			}
		}

		switch(event.sensor.getType()){
		case Sensor.TYPE_ACCELEROMETER:
			accelerometervalues = event.values.clone();
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			geomagneticmatrix =event.values.clone();
			break;
		default:

			break;
		}
		sampleRate++;
		timeOver = System.currentTimeMillis();
		if(timeOver-timeStart>=1000){
//			Log.i("sampleRate", sampleRate+"");
			sampleRate=0;
			timeStart = timeOver;
		}
		if (geomagneticmatrix != null && accelerometervalues!= null) {   
			float[] R = new float[9];
			float[] I = new float[9];
			boolean success = 	SensorManager.getRotationMatrix(R, I, accelerometervalues, geomagneticmatrix);
			if (success) {
				float orientation[] = new float[3];
				SensorManager.getOrientation(R, orientation);
				float[] earthAxis =  getTrueAcceleraton(accelerometervalues,orientation);
				// After we change the algorithm ,we low pass-filter will perturb our result, so we omit the low-pass filter
				/*	if(null != prev){
				earthAxis = lowpassfilter(prev,earthAxis,0.8f);
				}*/
				prev = earthAxis;
				earthAxis[2] = earthAxis[2]-9.81f;
				strideSamepleRate.add(earthAxis);

				if(null != xAccer&&null!=yAccer&&null !=zAccer){
					zAccer.setText(earthAxis[2]+"");
					xAccer.setText(earthAxis[0]+"");
					yAccer.setText(earthAxis[1]+"");

				}

			}




		}
	}  

	/**
	 * 
	 * @param accelerometervalues accelerometervalues get from Sensor.TYPE_ACCELEROMETER
	 * @param orientationvalues get from SensorManager.getOrientation(R, orientation);
	 * @return the earth geodetic coordinates
	 * float[0] direct to west,west is negative,east is positive 
	 * float[1] direct to north,  positive is north ,negative is south
	 * float[2] direct to the ground
	 *
	 */
	private float[] getTrueAcceleraton(float []accelerometervalues,float[]orientationvalues){
		float trueacceleration1 =(float) (accelerometervalues[0]*(Math.cos(orientationvalues[2])*Math.cos(orientationvalues[0])+Math.sin(orientationvalues[2])*Math.sin(orientationvalues[1])*Math.sin(orientationvalues[0])) + accelerometervalues[1]*(Math.cos(orientationvalues[1])*Math.sin(orientationvalues[0])) + accelerometervalues[2]*(-Math.sin(orientationvalues[2])*Math.cos(orientationvalues[0])+Math.cos(orientationvalues[2])*Math.sin(orientationvalues[1])*Math.sin(orientationvalues[0])));
		float trueacceleration2 = (float) (accelerometervalues[0]*(-Math.cos(orientationvalues[2])*Math.sin(orientationvalues[0])+Math.sin(orientationvalues[2])*Math.sin(orientationvalues[1])*Math.cos(orientationvalues[0])) + accelerometervalues[1]*(Math.cos(orientationvalues[1])*Math.cos(orientationvalues[0])) + accelerometervalues[2]*(Math.sin(orientationvalues[2])*Math.sin(orientationvalues[0])+ Math.cos(orientationvalues[2])*Math.sin(orientationvalues[1])*Math.cos(orientationvalues[0])));
		float trueacceleration3 = (float) (accelerometervalues[0]*(Math.sin(orientationvalues[2])*Math.cos(orientationvalues[1])) + accelerometervalues[1]*(-Math.sin(orientationvalues[1])) + accelerometervalues[2]*(Math.cos(orientationvalues[2])*Math.cos(orientationvalues[1])));
		return new float[]{trueacceleration1,trueacceleration2,trueacceleration3};
	}


	@Override
	protected void onResume() {
		super.onResume();
		// the sample rate is 10  when we use sensor_DELAY_NORMAL
		//the smaple rate is 100  when we use sensor_DELAY_GAME
		//the sample reate is 32  when we use sensor_DELAY_ui
		/*mSensorManager.registerListener(this, mAcceler, SensorManager.SENSOR_DELAY_UI);
		mSensorManager.registerListener(this, mAgnet, SensorManager.SENSOR_DELAY_UI);
		initalTime = mDate.getTime();*/
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	/*
	public float[] lowpassfilter(float[] sensor, float[] prev, double alpha){	
		return new double[]{sensor[0] * alpha + prev[0] * (1 - alpha), 
		sensor[1] * alpha + prev[1] * (1 - alpha), 
		sensor[2] * alpha + prev[2] * (1 - alpha)};
		}*/
	/**
	 * 
	 * @return people's stride rate
	 */
	protected int getSrideRate(){
		return 0;
	}

	/**
	 * 
	 */

	protected float getStrideLength(){
		return 0;
	}

	private float getRate(double[] samples){





		return 0;
	}


	private void printSample(StringBuffer sBuffer,String name){


//		Toast.makeText(this, "Finish", Toast.LENGTH_LONG).show();
		//			sBuffer.append("\n"+"*********sampleLenght  "+sBuffer.length()+"*********************"+"\n");
		Filter.write2SDcard(name,sBuffer);


	}


	public static double[] filter(double[] input, double[] prev) {

		if (input == null || prev == null)
			throw new NullPointerException("input and prev float arrays must be non-NULL");
		if (input.length != prev.length)
			throw new IllegalArgumentException("input and prev must be the same length");

		for (int i = 0; i < input.length; i++) {
			prev[i] = prev[i] + ALPHA * (input[i] - prev[i]);
		}

		return prev;
	}

	public static float[] lowpassfilter(float[] lastAcc, float[] currentAcc, float lowPassFilteringFactor)
	{
		currentAcc[0] = (currentAcc[0] * lowPassFilteringFactor) + (lastAcc[0] * (1.0f - lowPassFilteringFactor));
		currentAcc[1]= (currentAcc[1]* lowPassFilteringFactor) + (lastAcc[1] * (1.0f - lowPassFilteringFactor));
		currentAcc[2] = (currentAcc[2] * lowPassFilteringFactor) + (lastAcc[2]* (1.0f - lowPassFilteringFactor));

		return currentAcc;
	}

	public float[][] getLeftOfLastCycle(float[][]samples){
		float[][] lastCycleSample = null;
		if(null != samples)
			for(int i = samples.length-1;i>=0;i--){
				if(i-1>=0&&samples[i-1][2]<0&&samples[i][2]>0){
					lastCycleSample = new float[samples.length-i][3];
					System.arraycopy(samples, i, lastCycleSample, 0, samples.length-i);
					break;

				}

			}
		return lastCycleSample;

	}
	public float[][] getLeftOfLastCycle(List<float[]> samples){
		float[][] lastCycleSample = null;
		if(null != samples)
			lastCycleSample = getLeftOfLastCycle(TransferTool.getfloatFromList(samples));

		return lastCycleSample;

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();  
		System.exit(0);  
	}
	/**
	 * We will draw the moving tracking line depends on the samples
	 */
	private void drawTracking(){
		Point point = null;
		for(int i =0;i<allSampleList.size();i++){
			float[][] oneStepSample = allSampleList.get(i);
			TrackingOrientation traOri = Filter.walkingAnalys(oneStepSample);
			point = new Point();
			point.setTrOri(traOri);
			mDrawTrackingView.getPointList().add(point);			
		}
		mDrawTrackingView.invalidate();
	}
	
	/**
	 * We will draw the moving tracking line depends on the samples
	 */
	private void drawTracking(List<float[][]>  sampleList){
		Point point = null;
		for(int i =0;i<sampleList.size();i++){
			float[][] oneStepSample = sampleList.get(i);
			TrackingOrientation traOri = Filter.walkingAnalys(oneStepSample);
			point = new Point();
			point.setTrOri(traOri);
			mDrawTrackingView.getPointList().add(point);			
		}
		mDrawTrackingView.invalidate();
	}


	private void reset(){
		mSensorManager.unregisterListener(EarthAcceActivity.this);
		mSensorManager.unregisterListener(EarthAcceActivity.this);
		stridelength = 0.0f;
		isReister = false;
		strideSamepleRate.clear();
		allSampleArray = null;
		sampleNumber =0;
		sampleTimes++;
		mDrawTrackingView.getPointList().clear();
		mDrawTrackingView.invalidate();
		
	}

	private void updateSteps(final float [][] copy ){
		Thread mThread = new Thread(){

			@Override
			public void run() {
				super.run();
				float[][] steps= Filter.hDFilterVertical(HDClass.HD1to3,copy);// vertical 
				steps = Filter.hDFilterVerticalHorizontal(HDClass.HD1to3Half, steps);// horizontal
				List<float[][]>stepsList = MathCal.getSetps(steps);
				stridelength = MathCal.getStepsLength(stepsList);
				strides_Number = stepsList.size();
				Message msg = new Message();
				msg.what = stepsList.size();
				msg.obj = stepsList;
				mHandler.sendMessage(msg);
				
				
			}
			
		};
		mThread.start();
		
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);		
			if(null != msg){
			stride.setText(stridelength+"");
			mStridesNumberView.setText(strides_Number+"");
			List<float[][]>stepsList =(List<float[][]>) msg.obj;
			mDrawTrackingView.getPointList().clear();
//			drawTracking(stepsList);
			}
		}
		
	};
	
	private Handler mHandler2 = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);		
			if(null != msg){
				Double dd =(Double) msg.obj;
				Toast.makeText(EarthAcceActivity.this, dd.toString(), Toast.LENGTH_SHORT).show();
				
			}
		}
		
	};


	

}
