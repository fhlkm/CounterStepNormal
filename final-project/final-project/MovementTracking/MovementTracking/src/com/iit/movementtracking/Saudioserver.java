package com.iit.movementtracking;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class Saudioserver extends Thread
{  
	protected AudioTrack m_out_trk ;
	protected int        m_out_buf_size ;
	protected byte []    m_out_bytes ;
	protected boolean    m_keep_running ;
	private Socket s;
	private DataInputStream din;
	public void init()
	{
		startClient();
	}

	private void startClient(){
		Thread mThread = new Thread(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				try
				{
					s=new Socket("192.168.1.67",4331);
					din=new DataInputStream(s.getInputStream());

					m_keep_running = true ;


					m_out_buf_size = AudioTrack.getMinBufferSize(8000,
							AudioFormat.CHANNEL_CONFIGURATION_MONO,
							AudioFormat.ENCODING_PCM_16BIT);

					m_out_trk = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
							AudioFormat.CHANNEL_CONFIGURATION_MONO,
							AudioFormat.ENCODING_PCM_16BIT,
							m_out_buf_size,
							AudioTrack.MODE_STREAM);

					m_out_bytes=new byte[m_out_buf_size];

					// new Thread(R1).start();

				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
			}
			
		};
		mThread.start();
	}
	public void free()
	{
		m_keep_running = false ;
		try {
			Thread.sleep(1000) ;
		} catch(Exception e) {
			Log.d("sleep exceptions...\n","") ;
		}
	}

	public void run()
	{
		byte [] bytes_pkg = null ;
		m_out_trk.play() ;
		while(m_keep_running) {
			try
			{
				din.read(m_out_bytes);
				bytes_pkg = m_out_bytes.clone() ;
				m_out_trk.write(bytes_pkg, 0, bytes_pkg.length) ;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

		}

		m_out_trk.stop() ;
		m_out_trk = null ;
		try {
			din.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}







