package com.example.proto;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class PlayBackActivity extends Activity{
	private ImageButton playButton,pauseButton;
	private AudioTrack audioTrack;
	private RatingBar ratingBar;
	private Button btnSubmit;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_playback);
          playButton = (ImageButton)findViewById(R.id.imageButton1);
          pauseButton = (ImageButton)findViewById(R.id.imageButton2);
          addListenerOnRatingBar();
      	  addListenerOnButton();
          playButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				audioTrack.play();
			}
        	  
          });
          
          pauseButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				audioTrack.pause();
			}
        	  
          });
          playRecord();
    }
	
	public void addListenerOnRatingBar() {
		 
		ratingBar = (RatingBar) findViewById(R.id.ratingBar);
	 
		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			public void onRatingChanged(RatingBar ratingBar, float rating,
				boolean fromUser) {
	 
	 
			}
		});
	  }
	 
	  public void addListenerOnButton() {
	 
		ratingBar = (RatingBar) findViewById(R.id.ratingBar);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
	 
		//if click on me, then display the current rating value.
		btnSubmit.setOnClickListener(new OnClickListener() {
	 
			@Override
			public void onClick(View v) {
	 
				Toast.makeText(PlayBackActivity.this,
					String.valueOf(ratingBar.getRating()),
						Toast.LENGTH_SHORT).show();
	 
			}
	 
		});
	 
	  }
	  
	void playRecord(){
		  
		  //File file = new File(Environment.getExternalStorageDirectory(), "test.pcm");
		  File file = MainActivity.currAf.myfile;
		  if (file == null) {
		  System.out.print("NULL NULL NULL");
		  }
		        int shortSizeInBytes = Short.SIZE/Byte.SIZE;
		  
		  int bufferSizeInBytes = (int)(file.length()/shortSizeInBytes);
		  short[] audioData = new short[bufferSizeInBytes];
		  
		  try {
		   InputStream inputStream = new FileInputStream(file);
		   BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
		   DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
		   
		   int i = 0;
		   while(dataInputStream.available() > 0){
		    audioData[i] = dataInputStream.readShort();
		    i++;
		   }
		   
		   dataInputStream.close();
		   
		   audioTrack = new AudioTrack(
		     AudioManager.STREAM_MUSIC,
		     11025,
		     AudioFormat.CHANNEL_CONFIGURATION_MONO,
		     AudioFormat.ENCODING_PCM_16BIT,
		     bufferSizeInBytes,
		     AudioTrack.MODE_STREAM);
		   
		   audioTrack.play();
		   audioTrack.write(audioData, 0, bufferSizeInBytes);

		   
		  } catch (FileNotFoundException e) {
		   e.printStackTrace();
		  } catch (IOException e) {
		   e.printStackTrace();
		  }
		 }
}