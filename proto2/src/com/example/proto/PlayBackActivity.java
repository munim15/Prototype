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
	private Button playButton;
	private ImageButton pauseButton;
	private AudioTrack audioTrack;
	private RatingBar ratingBar;
	private Button btnSubmit;
	private boolean paused = false;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_playback);
          ((TextView) findViewById(R.id.titl)).setText(MainActivity.currAf.title);
          playButton = (Button)findViewById(R.id.play);
          //pauseButton = (ImageButton)findViewById(R.id.imageButton2);
          addListenerOnRatingBar();
      	  addListenerOnButton();
          playButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//System.out.println("GSFSDFSADFASFASDFSDFADFSADFSADFADFASFSDFSDF");
				//audioTrack.play();
				Thread loopbackThread;
				if(!paused) {
					paused = true;
					playButton.setBackground(getResources().getDrawable(R.drawable.stop_red));
					loopbackThread = new Thread(new Runnable() {

						@Override
						public void run() {
							playRecord();
						}
						
					});
					
					loopbackThread.start();
					//playRecord();
				} else {
					playButton.setBackground(getResources().getDrawable(R.drawable.play_red));
					//System.out.println("TTTTTTTTTTTTTJJJJJJJJJJJJJJJJJKKKKKKKK");
					audioTrack.pause();
					//loopbackThread.interrupt();
					//audioTrack.stop();
					paused = false;
				}

			}
        	  
          });
          
          /*pauseButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				System.out.println("GSFSDFSADFASFASDFSDFADFSADFSADFADFASFSDFSDF");
				audioTrack.pause();
				paused = true;
			}
        	  
          });*/
          //playRecord();
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
					String.valueOf("You Rated " + MainActivity.currAf.title + ": "
				+ ratingBar.getRating() + "/4.0 \n Thanks for the feedback!"),
						Toast.LENGTH_LONG).show();
	 
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
		// set setNotificationMarkerPosition accouding audio length
		   audioTrack.setNotificationMarkerPosition(bufferSizeInBytes);

		  // now add OnPlaybackPositionUpdateListener to audioTrack 
		      audioTrack.setPlaybackPositionUpdateListener(
		                               new AudioTrack.OnPlaybackPositionUpdateListener() {
		          @Override
		          public void onMarkerReached(AudioTrack track) {
		              // do your work here....
		        	  playButton.setBackground(getResources().getDrawable(R.drawable.play_red));
		        	  paused = false;
		          }

				@Override
				public void onPeriodicNotification(AudioTrack arg0) {
					// TODO Auto-generated method stub
					
				}
		      });
		   
		   audioTrack.play();
		   audioTrack.write(audioData, 0, bufferSizeInBytes);
		   
		  } catch (FileNotFoundException e) {
		   e.printStackTrace();
		  } catch (IOException e) {
		   e.printStackTrace();
		  }
		 }
}