package com.iit.movementtracking;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.example.movementtracking.R;

public class SendAudio2Activity  extends Activity{

	Thread t;
	int sr = 44100;
	boolean isRunning = true;
	SeekBar fSlider;
	double sliderval;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// point the slider to thwe GUI widget
		fSlider = (SeekBar) findViewById(R.id.frequency);

		// create a listener for the slider bar;
		OnSeekBarChangeListener listener = new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) { }
			public void onStartTrackingTouch(SeekBar seekBar) { }
			public void onProgressChanged(SeekBar seekBar, 
					int progress,
					boolean fromUser) {
				if(fromUser) sliderval = progress / (double)seekBar.getMax();
			}
		};

		// set the listener on the slider
		fSlider.setOnSeekBarChangeListener(listener);

		// start a new thread to synthesise audio
		t = new Thread() {
			public void run() {
				// set process priority
				setPriority(Thread.MAX_PRIORITY);
				// set the buffer size
				int buffsize = AudioTrack.getMinBufferSize(sr,
						AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
				// create an audiotrack object
				AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
						sr, AudioFormat.CHANNEL_OUT_MONO,
						AudioFormat.ENCODING_PCM_16BIT, buffsize,
						AudioTrack.MODE_STREAM);

				short samples[] = new short[buffsize];
				int amp = 10000;
				double twopi = 8.*Math.atan(1.);
				double fr = 440.f;
				double ph = 0.0;

				// start audio
				audioTrack.play();

				// synthesis loop
				while(isRunning){
					fr =  440 + 440*sliderval;
					for(int i=0; i < buffsize; i++){
						samples[i] = (short) (amp*Math.sin(ph));
						ph += twopi*fr/sr;
					}
					Log.i("audio", "playing--------");
//					audioTrack.write(audioData, offsetInBytes, sizeInBytes)
					audioTrack.write(samples, 0, buffsize);
				}
				audioTrack.stop();
				audioTrack.release();
			}
		};
		t.start();        
	}

	

	public void onDestroy(){
		super.onDestroy();
		isRunning = false;
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		t = null;
	}


}
