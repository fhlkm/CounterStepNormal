package com.iit.movementtracking.record;

import android.app.Activity;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.example.movementtracking.R;

public class AudioDemo extends Activity implements OnClickListener {
    private static final String TAG = "AudioDemo";
    private static final String isPlaying = "Media is Playing"; 
    private static final String notPlaying = "Media has stopped Playing"; 

    MediaPlayer player;
    Button playerButton;

   
    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: " + v);
        if (v.getId() == R.id.play) {
            playPause();
        }
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_record);

        //player = MediaPlayer.create(this, R.raw.robotrock);
        player.setLooping(false); // Set looping

        // Get the button from the view
        playerButton = (Button) this.findViewById(R.id.play);
        playerButton.setText(R.string.stop_label);
        playerButton.setOnClickListener(this);

        // Begin playing selected media
        demoPlay();

        // Release media instance to system
        player.release();
    }

    @Override
    public void onPause() {
        super.onPause();
        player.pause();
    }

    // Initiate media player pause
    private void demoPause(){
        player.pause();
        playerButton.setText(R.string.play_label);
        Toast.makeText(this, notPlaying, Toast.LENGTH_LONG).show();
        Log.d(TAG, notPlaying);
    }

    // Initiate playing the media player
    private void demoPlay(){
        player.start();
        playerButton.setText(R.string.stop_label);
        Toast.makeText(this, isPlaying, Toast.LENGTH_LONG).show();
        Log.d(TAG, isPlaying);
    }

    // Toggle between the play and pause
    private void playPause() {
        if(player.isPlaying()) {
            demoPause();
        } else {
            demoPlay();
        }   
    }

	
	
}