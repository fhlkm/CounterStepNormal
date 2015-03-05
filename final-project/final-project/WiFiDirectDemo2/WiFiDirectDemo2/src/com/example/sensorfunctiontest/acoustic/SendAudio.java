package com.example.sensorfunctiontest.acoustic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.constant.AcousticAction;
import com.example.constant.HDClass;
import com.example.sensorfunctiontest.wifidirect.WiFiDirectActivity;
import com.example.sensorfunctiontestttt.R;
import com.example.tool.Filter;
import com.example.tool.ReadAsset;
import com.example.tool.ReadValue;
/**
 * We should know the sampleRate, the frequence of tone and the duration
 * @author Tim
 *
 */
public class SendAudio  implements AcousticAction{
//	double duration = 0.5;                // seconds of the audio last
//	double freqOfTone = 2*1000;           // hz
//	int sampleRate = 44100;              // Sample Rate number
//	final int PERIOD = 127;           // period of  unique codes
	private final String LOG = "SendAudioActivity";
	private Button btnStartRecord = null;
	private Button btnReset = null;
	private RecordAudio  recordAudio = null;
	private long startSendTime = 0;
	private long endSendTime = 0;
	private double[]sendSample = null;
	public static  int numbers = 0;
	public Context mContext = null;
	private float[]receive = null;// the data, mobile received
	private int distance =0;// Mobile B, BA-BB
	private TextView dist = null;
    public SendAudio(Context context,Button btnStartRecord,Button btnReset){
    	this.mContext = context;
    	this.btnReset = btnReset;
    	this.btnStartRecord = btnStartRecord;
    	btnStartRecord.setOnClickListener(Listener);
    	btnReset.setOnClickListener(Listener);
    }

	
	public TextView getDist() {
		return dist;
	}
	public void setDist(TextView dist) {
		this.dist = dist;
	}


	private View.OnClickListener Listener = new View.OnClickListener(){

		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btn_start_record:
			 	
		    	recordAudio = new RecordAudio(mContext,SendAudio.this);
		    	
		    	recordAudio.startRecord();
				sendAudio();
				btnStartRecord.setEnabled(false);
				btnReset.setEnabled(true);
							
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
//				long startTime = System.currentTimeMillis();
				long sendAudioTime = System.nanoTime();
				Log.i(LOG,"sendTime:  "+ sendAudioTime+"");
				double dnumSamples = HDClass.duration * HDClass.sampleRate;// the number of sample
				dnumSamples = Math.ceil(dnumSamples);
				int numSamples = (int) dnumSamples;
				//		    	double sample[] = new double[numSamples];
				double sample[] = getSampleArray(numSamples);
				do{
					
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
							HDClass.sampleRate, AudioFormat.CHANNEL_OUT_MONO,
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
//					Log.i("Finish", "Finish");
//				}while(System.currentTimeMillis()- startTime<=HDClass.TIMEOFPERIOD);
				}while(true);// keep playing
//				endSendTime = System.nanoTime();
//				write2SDcard(sendSample);
				


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
    		if(i<HDClass.goldCodes[0].length*HDClass.CHIP_LENGTH){
    			sample[i] = HDClass.goldCodes[0][i/HDClass.CHIP_LENGTH]*Math.cos(HDClass.freqOfTone * 2 * Math.PI * i / (HDClass.sampleRate));
    		}else{
    			sample[i] = 0;
    		}
    	}
    	sendSample = sample;
    	return sample;
		
	}
	
	/**
	 * write recordSound information into record.xml
	 */
		private void write2SDcard(double[] sendSample){
			File saveFile=new File("/sdcard/sendRecord"+SendAudio.numbers+".xml");
			 try {
				 FileWriter fw=new FileWriter(saveFile);
				 BufferedWriter buffw=new BufferedWriter(fw);
				 PrintWriter pw=new PrintWriter(buffw);
				 if(null !=sendSample ){
					 
					 pw.println("********"+"startSendTime:  "+startSendTime+"************"+"SampleRate:  "+HDClass.sampleRate+
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

		/**
		 * The distance of mobile B, b21-b22
		 * @param distance the sample number between b21 and b22, b21>b22
		 */
		private void calDistance(final int distanceT){
			Thread mThread = new Thread(){

				@Override
				public void run() {
					super.run();
					ReadAsset mReadAsset = new ReadAsset((Activity)mContext);
					Filter mFilter = new Filter();
					receive = mFilter.hDFilter2(HDClass.BD_NUMERATOR, receive);// bandpass filter
					mFilter.multiplyFrequency(receive, HDClass.sampleRate, HDClass.freqOfTone);// multiply cos(wt)
					receive = mFilter.hDFilter2(HDClass.LD_NUMERATOR, receive);// low band pass filter, this is right
					float[] user = mReadAsset.getSendCode(HDClass.goldCodes[0], 40); 

					float[] receiveAB = new float[receive.length];//calculate cross correlation
					System.arraycopy(receive, 0, receiveAB, 0, receive.length);
					float[] user2 = mReadAsset.getSendCode(HDClass.goldCodes[1], 40);
					
					mFilter.correlation(receive, user, receive.length, HDClass.goldCodes[0].length*40);// correlation				
					mFilter.correlation(receiveAB, user2, receiveAB.length, HDClass.goldCodes[1].length*40);
					
					ReadValue rV = new ReadValue();
					int p_a12 = rV.getPeakValueofCorssCorrelation(receiveAB,0,ReadValue.numberOfPeroid);
					int p_a11 =rV.getPeakValueOfAutoCorrelation(receive, p_a12,ReadValue.numberOfPeroid);
					double distance = 0;
					if(distanceT ==0){// if we can't receive other peoples signal the distanceT is zero
						distance =0;
					}
					else{
						distance = rV.getDistance(p_a11, p_a12, distanceT);
					}
					int sendSampleNumber = p_a12-p_a11;
					

					//Log.i("position:", position+"");


					
					
					Message msg = new Message();
					Double dd = new Double(distance);
					msg.obj = dd;
					msg.what = sendSampleNumber;
					mHandler2.sendMessage(msg);
					
				}
				
			};
			mThread.start();
		}
		private Handler mHandler2 = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);		
				if(null != msg){
					Double dd =(Double) msg.obj;// the distance after calculated
					int distanceAB = msg.what;// mobile ab - moible aa, the distance we'll send to others
//					Toast.makeText(mContext, dd.toString(), Toast.LENGTH_SHORT).show();
					Log.i("distanceOfAB", distanceAB+"");
					if(null != dist){
						dist.setText(distanceAB+"");
					}
					((WiFiDirectActivity)mContext).getfragmentDetails().sendDistance(distanceAB);
					
				}
			}
			
		};

		/**
		 * After we recorded the data,we started to calculate it, recordAcousticData is the sample we recorded
		 */
		@Override
		public void finishcRecording(short[] recordAcousticData) {
			int number = 0;
			float []temp = new float[recordAcousticData.length];
			// change short to float
			for(short f:recordAcousticData){
				if(f!=0.0){// make suer we remove the zero points
					temp[number++] = f; 
				}
			}
			
			if(number>=1)// we have got samples
			{
				if(number>3000){
					receive=temp=Arrays.copyOfRange(temp, 0, 3000);
				}
				else{
					receive=temp=Arrays.copyOfRange(temp, 0, number-1);
				}
			}

			//the sample point position b21-b22,transfered by mobile B
			calDistance(distance);
			
		}
		public int getDistance() {
			return distance;
		}
		public void setDistance(int distance) {
			this.distance = distance;
		}
		
		

}
