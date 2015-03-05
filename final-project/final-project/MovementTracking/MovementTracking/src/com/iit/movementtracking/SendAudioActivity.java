package com.iit.movementtracking;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.movementtracking.R;
import com.iit.movementtracking.recordinglevel.RecordAudio;
/**
 * We should know the sampleRate, the frequence of tone and the duration
 * @author Tim
 *
 */
public class SendAudioActivity extends Activity {
	double duration = 0.5;                // seconds of the audio last
	double freqOfTone = 2*1000;           // hz
	int sampleRate = 44100;              // Sample Rate number
//	final int PERIOD = 127;           // period of  unique codes
	private final String LOG = "SendAudioActivity";
	private Button btnStartRecord = null;
	private Button btnReset = null;
	private RecordAudio  recordAudio = null;
	private long startSendTime = 0;
	private long endSendTime = 0;
	private double[]sendSample = null;
	public static  int numbers = 0;

	int[][] goldCodes = {{1,1,1,1,1,-1,-1,1,1,-1,1,-1,-1,1,-1,-1,-1,-1,1,-1,1,-1,1,1,1,-1,1,1,-1,-1,-1},
			             {1,1,1,1,1,-1,-1,1,-1,-1,1,1,-1,-1,-1,-1,1,-1,1,1,-1,1,-1,1,-1,-1,-1,1,1,1,-1},
	                     {-1,-1,-1,1,-1,1,1,-1,-1,-1,1,1,-1,1,1,1,-1,-1,1,-1,-1,-1,-1,-1,1,1,1,-1,-1,-1,-1}};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.send_record_audio);
    	btnStartRecord = (Button) findViewById(R.id.btn_start_record);
    	btnReset = (Button)findViewById(R.id.btn_reset);
    	btnStartRecord.setOnClickListener(Listener);
    	btnReset.setOnClickListener(Listener);
    	
    }

	
	private View.OnClickListener Listener = new View.OnClickListener(){

		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btn_start_record:
			 	
		    	recordAudio = new RecordAudio(SendAudioActivity.this);
		    	
		
				sendAudio();
				btnStartRecord.setEnabled(false);
				btnReset.setEnabled(true);
				recordAudio.startRecord();			
				break;
			case R.id.btn_reset:
				btnReset.setEnabled(false);
				btnStartRecord.setEnabled(true);
			
				recordAudio.stopRecordAndWriteBuffer();
				numbers++;
				
				break;
			default:
				break;
			}
			
		}
		
	};
	  
	/**
	 * Every audio last 0.5s ,we play 6 times ,in total 3 s.
	 */
	private void sendAudio(){
		Thread mThread = new Thread(){

			@Override
			public void run() {
				super.run();
				long startTime = System.currentTimeMillis();
				long sendAudioTime = System.nanoTime();
				Log.i(LOG,"sendTime:  "+ sendAudioTime+"");
				do{
					double dnumSamples = duration * sampleRate;// the number of sample
					dnumSamples = Math.ceil(dnumSamples);
					int numSamples = (int) dnumSamples;
					//		    	double sample[] = new double[numSamples];
					double sample[] = getSampleArray(numSamples);
					byte generatedSnd[] = new byte[2 * numSamples];


					// convert to 16 bit pcm sound array
					// assumes the sample buffer is normalised.
					int idx = 0;

					for (double dVal : sample) {
						short val = (short) (dVal * 32767);
						generatedSnd[idx++] = (byte) (val & 0x00ff);
						generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);//unsigned right
					}



					AudioTrack audioTrack = null;                                   // Get audio track
					audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
							sampleRate, AudioFormat.CHANNEL_OUT_MONO,
							AudioFormat.ENCODING_PCM_16BIT,  generatedSnd.length,
							AudioTrack.MODE_STATIC);
					audioTrack.write(generatedSnd, 0, generatedSnd.length);     // Load the track


					audioTrack.play();                                          // Play the track
					startSendTime =  System.nanoTime();

					int x =0;
					do{                                                     // Montior playback to find when done
						if (audioTrack != null) 
							x = audioTrack.getPlaybackHeadPosition(); // audioTrack.getPlaybackHeadPosition/sampleRate is play time
						else 
							x = numSamples;            
					}while (x<numSamples);// if x == numSamples, it meas the audio is finished

					if (audioTrack != null)
					{
						audioTrack.stop();
						audioTrack.release(); 
					}
					Log.i("Finish", "Finish");
				}while(System.currentTimeMillis()- startTime<=3*1000);
				endSendTime = System.nanoTime();
				write2SDcard(sendSample);
				


			}

		};
		mThread.start();
	}
	/**
	 * Every unique codes of Gold code can only send once, and the chip length is 40 samples
	 * fill the sample arrays
	 * @param numSamples the number of samples
	 * @return the filled samples
	 */
	private double[] getSampleArray(int numSamples){
		double[] sample = new double[numSamples];
		
    	for (int i = 0; i < numSamples; ++i) {      // Fill the sample array
    		if(i<goldCodes[1].length*40){
    			sample[i] = goldCodes[1][i/40]*Math.cos(freqOfTone * 2 * Math.PI * i / (sampleRate));
    		}else{
    			sample[i] = 0;
    		}
    		/*if(i%40==0&&i<goldCodes[1].length*40){// add gold code,chip lenght is 40 samples, and every goldCodes can only send once
    			sample[i] = goldCodes[1][index%goldCodes[1].length]*Math.sin(freqOfTone * 2 * Math.PI * i / (sampleRate));
    			index++;
    		}else{
    			sample[i] = Math.sin(freqOfTone * 2 * Math.PI * i / (sampleRate));
    		}*/
    	}
    	sendSample = sample;
    	return sample;
		
	}
	
	/**
	 * write recordSound information into record.xml
	 */
		private void write2SDcard(double[] sendSample){
			File saveFile=new File("/sdcard/sendRecord"+SendAudioActivity.numbers+".xml");
			 try {
				 FileWriter fw=new FileWriter(saveFile);
				 BufferedWriter buffw=new BufferedWriter(fw);
				 PrintWriter pw=new PrintWriter(buffw);
				 if(null !=sendSample ){
					 
					 pw.println("********"+"startSendTime:  "+startSendTime+"************"+"SampleRate:  "+sampleRate+
							 "\n"+"********"+"EndStopTime:  "+endSendTime+"************"+"\n"
							 +" The length of Array: "+sendSample.length+"\n"
							 +"RecordingTime : "+(endSendTime-startSendTime)+" ns "+"************"+"\n"
							 +Arrays.toString(sendSample)+"\n" );
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

		@Override
		public void onBackPressed() {
			super.onBackPressed();
			this.finish();  
			System.exit(0);  
		}

}
