package com.iit.movementtracking.recordinglevel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import com.iit.movementtracking.SendAudioActivity;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.widget.ProgressBar;

public class RecordAudio {
	
	public static final int SAMPLE_RATE = 44100;

	private AudioRecord mRecorder;
	private short[] mBuffer;
	private boolean mIsRecording = false;
	private short[] reCordSound = null;
	private Context mContext;
	private long startRecordingTime = 0;
	private long endRecordingTime = 0;

	public RecordAudio(Context mContext) {
		super();
		this.mContext = mContext;
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
	}
	
	/**
	 * Start to record the auido and put the butter into reCordSound
	 */

	public void startRecord() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				mRecorder.startRecording();
				mIsRecording = true;
				startRecordingTime = System.nanoTime();
				while (mIsRecording) {
					int readSize = mRecorder.read(mBuffer, 0, mBuffer.length);
					if(readSize != AudioRecord.ERROR_INVALID_OPERATION && readSize != AudioRecord.ERROR_BAD_VALUE){
					addBuffer(mBuffer);// add the mBuffer to RecordedBuffer
					}
				}
				
			} 			
		}).start();
	}
	
	/**
	 * Stop Recording and wirte the reCordSound information into record.xml
	 */
	public void stopRecordAndWriteBuffer(){
		mIsRecording = false;
		mRecorder.stop();	
		mRecorder.release();
		endRecordingTime = System.nanoTime();
		write2SDcard();
		
		
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
		File saveFile=new File("/sdcard/zhzhg"+SendAudioActivity.numbers+".xml");
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

}
