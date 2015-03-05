package com.iit.movementtracking;

import java.io.IOException;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.os.Bundle;
import android.util.Log;

import com.example.movementtracking.R;

public class AndroidPlayAndRecord extends Activity {
	//variables
	private int audioSource = MediaRecorder.AudioSource.MIC;
	private int samplingRate = 44100; /* in Hz*/
	private int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
	private int bufferSize = AudioRecord.getMinBufferSize(samplingRate, channelConfig, audioFormat);
	private int sampleNumBits = 16;
	private int numChannels = 1;
	private AudioRecord recorder;
	private boolean isRecording = false;
	private AudioTrack audioPlayer;
	private byte[] data = new byte[bufferSize+500];
	int[] mSampleRates = new int[] { 8000, 11025, 22050, 44100 };
	private MediaRecorder mMediaRecorder;

	//////////////////////////////////////////
	private int frameByteSize = 2048; // for 1024 fft size (16bit sample size)
//	protected Saudioserver     m_player ;
	protected Saudioclient     m_recorder ;
	
	private int maxSize = 0;


	byte[] buffer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		doSend();
		doRecord();
/*
//		m_player = new Saudioserver() ;
		m_recorder = new Saudioclient() ;

//		m_player.init() ;
		m_recorder.init() ;

		m_recorder.start() ;
//		m_player.start() ;
*/	}

	private void doSend(){
		/*audioPlayer = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_MONO,
				AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);

		if(audioPlayer.getPlayState() != AudioTrack.PLAYSTATE_PLAYING)
			audioPlayer.play();*/
		int recBufsize = AudioTrack.getMinBufferSize(samplingRate, channelConfig, audioFormat);
		audioPlayer = new AudioTrack(MediaRecorder.AudioSource.MIC, samplingRate, channelConfig, channelConfig, recBufsize,AudioTrack.MODE_STREAM);
		audioPlayer.play();
		
	}




	public AudioRecord findAudioRecord() {
				int recBufSize = AudioRecord.getMinBufferSize(samplingRate, channelConfig, audioFormat); // need to be larger than size of a frame
				AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, samplingRate, channelConfig, channelConfig, recBufSize);
				buffer = new byte[frameByteSize];
				maxSize = recBufSize;
				return audioRecord;
		      
/*
		for (int rate : mSampleRates) {
			for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT }) {
				for (short channelConfig : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO }) {
					try {
						Log.d("d----", "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: "
								+ channelConfig);
						int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);
						maxSize = bufferSize;

						if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
							// check if we can instantiate and have a success
							AudioRecord recorder = new AudioRecord(AudioSource.DEFAULT, rate, channelConfig, audioFormat, bufferSize);

							if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
								return recorder;
						}
					} catch (Exception e) {
						Log.e("e---", rate + "Exception, keep trying.",e);
					}
				}
			}
		}
		return null;*/
	}
	private void doMediaRecord(){
		mMediaRecorder = new MediaRecorder();
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		try {
			mMediaRecorder.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mMediaRecorder.start();

	}
	private void doRecord(){



		recorder = findAudioRecord();
		//		recorder.release();
		if(null == recorder){
			recorder = new AudioRecord(audioSource, samplingRate, channelConfig, audioFormat, bufferSize);
			if(recorder.getState()== AudioRecord.STATE_UNINITIALIZED){
				return;
			}
			recorder.startRecording();
			isRecording = true;
		}

		if(recorder.getState() == AudioRecord.STATE_UNINITIALIZED){
			Log.i("dddd", "unlimited");
			//			return;
		}
		recorder.startRecording();
		isRecording = true;
		//capture data and record to file
		int readBytes=0, writtenBytes=0;
		do{
			readBytes = recorder.read(data, 0, maxSize);

			if(AudioRecord.ERROR_INVALID_OPERATION != readBytes){
				writtenBytes += audioPlayer.write(data, 0, readBytes);
			}
		} while(isRecording);

		Log.i("finished----", "finished--------");


	}


}





