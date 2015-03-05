package com.example.sensorfunctiontest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sensorfunctiontestttt.R;

public class NewSensorActivity  extends Activity implements SensorEventListener {

	float[] Rotation = new float[9];
	float[] Inclination = new float[9];
	float[] angle= new float[3];
	float[] mag = new float[3];
	float[] acce = new float[3];
	float[] grav = new float[3];

	private SensorManager mSensorManager;
	private Sensor mAcceler;
	private Sensor mAgnet;
	private Sensor mGravity;
	private List<float[]> strideSamepleRate = new ArrayList<float[]>();
	private Button startBtn;
	private Button stopBtn;
	private TextView northView;
	private TextView eastView;
	private TextView verticalView;
	private int number = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zh_layout);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAcceler = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mAgnet = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		initWidget();


	}

	private void initWidget(){
		startBtn = (Button)findViewById(R.id.btn_start1);
		stopBtn = (Button)findViewById(R.id.btn_stop1);
		northView = (TextView)findViewById(R.id.north);
		eastView = (TextView)findViewById(R.id.east);
		verticalView = (TextView)findViewById(R.id.vertical);
		startBtn.setOnClickListener(Listener);
		stopBtn.setOnClickListener(Listener);


	}
	View.OnClickListener Listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btn_start1:
				number++;
				regisgter();
				break;
			case R.id.btn_stop1:
				unRegister();
				printSample(strideSamepleRate,"raw"+number);;
			/*	float[][] samples  = TransferTool.getfloatFromList(strideSamepleRate);
				float [][] copy = new float[strideSamepleRate.size()][3];
				MathCal.arrayCopy(samples, 0, copy, 0, strideSamepleRate.size());
				copy= Filter.hDFilterVertical(HDClass.HD1to3,copy);// vertical
				printSample(copy,"smooth"+number);;*/
				break;
			default:break;
			}

		}
	};
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float[] values = event.values;
		int sensorType = event.sensor.getType();
		long time = event.timestamp;
		switch(sensorType){
		case Sensor.TYPE_ACCELEROMETER:
			acce=values.clone();
			SensorManager.getRotationMatrix(Rotation,Inclination,grav,mag);
			float gravity=Rotation[6]*acce[0]+Rotation[7]*acce[1]+Rotation[8]*acce[2]-9.8f;
			float north=Rotation[3]*acce[0]+Rotation[4]*acce[1]+Rotation[5]*acce[2];
			float east=Rotation[0]*acce[0]+Rotation[1]*acce[1]+Rotation[2]*acce[2];
			float[] earthAxis ={gravity,north,east};
			strideSamepleRate.add(earthAxis);
			northView.setText("north:"+north);
			eastView.setText("east"+north);
			verticalView.setText("vertical"+gravity);



			break;
		case Sensor.TYPE_GYROSCOPE:

			break;
		case Sensor.TYPE_LINEAR_ACCELERATION:

			break;
		case Sensor.TYPE_ORIENTATION:

			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			mag=values.clone();
			break;
		case Sensor.TYPE_GRAVITY:
			grav=values.clone();
			break;
		}

	}

	private void regisgter(){

		mSensorManager.registerListener(NewSensorActivity.this, mAcceler, SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(NewSensorActivity.this, mAgnet, SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(NewSensorActivity.this, mGravity, SensorManager.SENSOR_DELAY_GAME);

	}

	private void unRegister(){
		mSensorManager.unregisterListener(NewSensorActivity.this);
	}
	private void printSample(StringBuffer sBuffer,String name){


		//		Toast.makeText(this, "Finish", Toast.LENGTH_LONG).show();
		//			sBuffer.append("\n"+"*********sampleLenght  "+sBuffer.length()+"*********************"+"\n");
		write2SDcard(name,sBuffer);


	}

	private void printSample(List<float[]> samples,String name){
		StringBuffer sBuilderNorth = new StringBuffer();
		StringBuffer sBuilderEast = new StringBuffer();
		StringBuffer sBuilderVertical = new StringBuffer();
		for(int i =0;i<samples.size();i++){
			sBuilderVertical.append(samples.get(i)[0]+",");
			sBuilderNorth.append(samples.get(i)[1]+",");
			sBuilderEast.append(samples.get(i)[2]+",");
		}

		printSample(sBuilderEast,name+"east");
		printSample(sBuilderNorth,name+"north");
		printSample(sBuilderVertical,name+"vertical");
	}
	private void printSample(float [][]samples,String name){
		StringBuffer sBuilderNorth = new StringBuffer();
		StringBuffer sBuilderEast = new StringBuffer();
		StringBuffer sBuilderVertical = new StringBuffer();
		
		for(int i =0;i<samples.length;i++){
			sBuilderVertical.append(samples[i][0]+",");
			sBuilderNorth.append(samples[i][1]+",");
			sBuilderEast.append(samples[i][2]+",");
		}

		printSample(sBuilderEast,name+"east");
		printSample(sBuilderNorth,name+"north");
		printSample(sBuilderVertical,name+"vertical");
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


}
