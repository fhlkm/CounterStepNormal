package com.example.sensorfunctiontest.acoustic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.example.constant.AcousticAction;
import com.example.constant.HDClass;

public class RecordAudio {

	public static final int SAMPLE_RATE = HDClass.sampleRate;

	private AudioRecord mRecorder;
	private short[] mBuffer;
	private boolean mIsRecording = false;
	private short[] reCordSound = null;
	private Context mContext;
	private long startRecordingTime = 0;
	private long endRecordingTime = 0;
	public AcousticAction mAcoustincAction = null;

	public RecordAudio(Context mContext,AcousticAction mAcoustincAction ) {
		super();
		this.mContext = mContext;
		this.mAcoustincAction = mAcoustincAction;

		initRecorder();
	}

	/**
	 * init the parameter such as sample Rate, etc 
	 */
	private void initRecorder() {
		int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		mBuffer = new short[bufferSize];
		mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT, bufferSize);
		//		mRecorder.startRecording();
	}

	/**
	 * Start to record the auido and put the butter into reCordSound
	 */


	public void startRecord() {
		mIsRecording = true;
		new Thread(new Runnable() {
			@Override
			public void run() {

//				do{
					if(mIsRecording){
						mRecorder.startRecording();

						long startRecord = System.currentTimeMillis();
						long stopRecord = System.currentTimeMillis();
						reCordSound = null;
						// we just send 0.3s data 
						while (stopRecord-startRecord<=300) {
							stopRecord = System.currentTimeMillis();
							int readSize = mRecorder.read(mBuffer, 0, mBuffer.length);
							if(readSize != AudioRecord.ERROR_INVALID_OPERATION && readSize != AudioRecord.ERROR_BAD_VALUE){
								addBuffer(mBuffer);// add the mBuffer to RecordedBuffer
							}

						}
						mAcoustincAction.finishcRecording(reCordSound);
						mRecorder.stop();
						long timeStart = System.currentTimeMillis();
						Thread.currentThread();
						try {
							// we just stop 1.5s ,because calculate the data will spend a  lot of time,so we'll record data every 1.5s
							Thread.sleep(3000);
							long timeEnd = System.currentTimeMillis();
							long time = timeEnd - timeStart;
							Log.i("Sleeping----", time+"");
						} 
						catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
//				}while(true);


			} 			
		}).start();
	}

	/**
	 * Stop Recording and wirte the reCordSound information into record.xml
	 */
	public void stopRecordAndWriteBuffer(){
		mIsRecording = false;
		mRecorder.stop();	
		endRecordingTime = System.nanoTime();
		//		write2SDcard();


	}


	/**
	 * Add the src to the RecordSound
	 * @param exists the data already exists
	 * @return data = src + exists
	 */
	private void addBuffer(short[] src){
		if(null == reCordSound){
			reCordSound = new short[src.length];
			System.arraycopy(src, 0, reCordSound, 0, src.length);
		}else{
			short[]buffers = new short[src.length+reCordSound.length];
			System.arraycopy(reCordSound, 0, buffers, 0, reCordSound.length);
			System.arraycopy(src, 0, buffers, reCordSound.length, src.length);
			reCordSound = buffers;
		}

	}

	/**
	 * write recordSound information into record.xml
	 */
	private void write2SDcard(){
		File saveFile=new File("/sdcard/zhzhg"+SendAudio.numbers+".xml");
		try {
			FileWriter fw=new FileWriter(saveFile);
			BufferedWriter buffw=new BufferedWriter(fw);
			PrintWriter pw=new PrintWriter(buffw);
			if(null !=reCordSound ){

				pw.println("********"+"startRecordingTime:  "+startRecordingTime+"************"+"SampleRate:  "+SAMPLE_RATE+
						"\n"+"********"+"EndRecordingTime:  "+endRecordingTime+"************"+"\n"
						+" The length of Array: "+reCordSound.length+"\n"
						+"RecordingTime : "+(endRecordingTime-startRecordingTime)+" ns "+"************"+"\n"
						+Arrays.toString(reCordSound)+"\n" );
			}
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

	public AcousticAction getmAcoustincAction() {
		return mAcoustincAction;
	}

	public void setmAcoustincAction(AcousticAction mAcoustincAction) {
		this.mAcoustincAction = mAcoustincAction;
	}


}
