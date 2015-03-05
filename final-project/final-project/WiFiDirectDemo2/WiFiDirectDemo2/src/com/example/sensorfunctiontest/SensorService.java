package com.example.sensorfunctiontest;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class SensorService extends Service
	implements SensorEventListener{
	
	private SensorManager mSensorManager;
	ActivityManager mActivityManager;
	SensorEventListener sel;
	String sddir="/mnt/sdcard/";
	float[] Rotation = new float[9];
	float[] Inclination = new float[9];
	float[] angle= new float[3];
	float[] mag = new float[3];
	float[] acce = new float[3];
	float[] grav = new float[3];
	SimpleDateFormat df = new SimpleDateFormat("HH-mm-ss");//������������
	
	
	FileOutputStream os_acc1;
	PrintStream osw_acc1;
	FileOutputStream os_acc2;
	PrintStream osw_acc2;
	FileOutputStream os_ori;
	PrintStream osw_ori;
	FileOutputStream os_gyr;
	PrintStream osw_gyr;
	FileOutputStream os_task;
	PrintStream osw_task;
	

	Timer timer;
	Process proc;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate(){
		super.onCreate();
		
		
		
		String time=df.format(new Date());
		

		mActivityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
		
		sel=this;
//		System.out.println("Create "+Settings.num);
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		mSensorManager.unregisterListener(this);
//		tt.interrupt();
		timer.cancel();
//		proc.destroy();
		try {
			osw_acc1.close();
			os_acc1.close();
			osw_acc2.close();
			os_acc2.close();
			osw_ori.close();
			os_ori.close();
			osw_gyr.close();
			os_gyr.close();
			osw_task.close();
			os_task.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		float[] values = event.values;
		int sensorType = event.sensor.getType();
		long time = event.timestamp;
		switch(sensorType){
		case Sensor.TYPE_ACCELEROMETER:
			acce=values.clone();
			SensorManager.getRotationMatrix(Rotation,Inclination,grav,mag);
			float gravity=Rotation[6]*acce[0]+Rotation[7]*acce[1]+Rotation[8]*acce[2];
			float north=Rotation[3]*acce[0]+Rotation[4]*acce[1]+Rotation[5]*acce[2];
			float east=Rotation[0]*acce[0]+Rotation[1]*acce[1]+Rotation[2]*acce[2];
			osw_acc1.println(""+time+","+north+","+east+","+gravity);
//			osw_acc1.flush();
			
			
			break;
		case Sensor.TYPE_GYROSCOPE:
			osw_gyr.println(""+time+","+values[0]+","+values[1]+","+values[2]);
//			osw_gyr.flush();
			break;
		case Sensor.TYPE_LINEAR_ACCELERATION:
//			Log.i("leo", "liner");
			osw_acc2.println(""+time+","+values[0]+","+values[1]+","+values[2]);
//			osw_acc2.flush();
			break;
		case Sensor.TYPE_ORIENTATION:
			osw_ori.println(""+time+","+values[0]+","+values[1]+","+values[2]);
//			osw_ori.flush();
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			mag=values.clone();
			break;
		case Sensor.TYPE_GRAVITY:
			grav=values.clone();
			break;
		}
	}
	private static void writeLine(OutputStream os, String value)
			throws IOException {
		String line = value + "\n";
		os.write(line.getBytes());
	}
}
