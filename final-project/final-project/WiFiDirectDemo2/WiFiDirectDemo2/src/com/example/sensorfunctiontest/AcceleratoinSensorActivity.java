package com.example.sensorfunctiontest;



import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import com.example.sensorfunctiontestttt.R;

public class AcceleratoinSensorActivity extends Activity implements SensorEventListener{

	private SensorManager mSensorManager;
	private Sensor mAcceler;
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


	@Override
	public final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accer_show);

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAcceler = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		xAccer = (TextView)findViewById(R.id.xaccer);
		yAccer = (TextView)findViewById(R.id.yaccer);
		zAccer = (TextView)findViewById(R.id.zaccer);
		 mSensorManager.registerListener(this, mAcceler, SensorManager.SENSOR_DELAY_NORMAL);

	}

	@Override
	public final void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Do something here if sensor accuracy changes.
	}

	@Override
	public final void onSensorChanged(SensorEvent event) {
		// The light sensor returns a single value.
		// Many sensors return 3 values, one for each axis.
		// lux = event.values[0];
		// Do something with this sensor value.
		//mText.setText(lux+"");
		if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) return; 

		long curTime = System.currentTimeMillis(); 
		if (lastUpdate == -1 || (curTime - lastUpdate) > mCycle) { 
			lastUpdate = curTime; 
			float lastX = x; 
			float lastY = y; 
			float lastZ = z; 
			x = event.values[0]; 
			y = event.values[1]; 
			z = event.values[2]; 
			if(null != xAccer&&null!=yAccer&&null !=zAccer){
				xAccer.setText(x+"");
				yAccer.setText(y+"");
				zAccer.setText(z+"");
			}
			if (lastEvent == -1 || (curTime - lastEvent) > mEventCycle) { 
				if ( 
						(mAccuracyX >= 0 && Math.abs(x - lastX) > mAccuracyX) 
						|| (mAccuracyY >= 0 && Math.abs(y - lastY) > mAccuracyY) 
						|| (mAccuracyZ >= 0 && Math.abs(z - lastZ) > mAccuracyZ) 
						) { 
					lastEvent = curTime; 
				} 
			} 
		} 
	}  




	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mAcceler, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}




}
