package com.iit.movementtracking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;
public class Saudioclient extends Thread
{

	protected AudioRecord m_in_rec ; 
	protected int         m_in_buf_size ;
	protected byte []     m_in_bytes ;
	protected boolean     m_keep_running ;
	protected Socket      s;
	protected DataOutputStream dout;
	protected LinkedList<byte[]>  m_in_q ;
	/////////////////
	protected AudioTrack m_out_trk ;
	protected int        m_out_buf_size ;
	protected byte []    m_out_bytes ;
	private DataInputStream din;

	private void initPlay(){
		m_out_buf_size = AudioTrack.getMinBufferSize(8000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);

		m_out_trk = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT,
				m_out_buf_size,
				AudioTrack.MODE_STREAM);

		m_out_bytes=new byte[m_out_buf_size];
		
		
	}
	public void run()
	{
		try
		{
			byte [] bytes_pkg ;
			m_in_rec.startRecording() ;
			m_out_trk.play() ;
			while(m_keep_running)
			{
				m_in_rec.read(m_in_bytes, 0, m_in_buf_size) ;
				bytes_pkg = m_in_bytes.clone() ;
				if(m_in_q.size() >= 2)
				{
					
					din.read(m_out_bytes);
//					bytes_pkg = m_out_bytes.clone() ;
					m_out_trk.write(bytes_pkg, 0, bytes_pkg.length) ;
					
//					dout.write(m_in_q.removeFirst() , 0, m_in_q.removeFirst() .length);
					
				}
				m_in_q.add(bytes_pkg) ;
				
				
			}

			m_in_rec.stop() ;
			m_in_rec = null ;
			m_in_bytes = null ;
			dout.close();

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void init()
	{
		initPlay();
		m_in_buf_size =  AudioRecord.getMinBufferSize(8000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);

		m_in_rec = new AudioRecord(MediaRecorder.AudioSource.MIC,
				8000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT,
				m_in_buf_size) ;

		m_in_bytes = new byte [m_in_buf_size] ;

		m_keep_running = true ;
		m_in_q=new LinkedList<byte[]>();
//		startClient();



	}

	/*private void startClient(){
		Thread mThread = new Thread(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				try
				{
					s=new Socket("192.168.1.67",4332);
					dout=new DataOutputStream(s.getOutputStream());
					//new Thread(R1).start();
				}
				catch (UnknownHostException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			
			
		};
		mThread.start();
	}*/
	public void free()
	{
		m_keep_running = false ;
		try {
			Thread.sleep(1000) ;
		} catch(Exception e) {
			Log.d("sleep exceptions...\n","") ;
		}
	}
}