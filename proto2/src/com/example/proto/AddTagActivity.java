package com.example.proto;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddTagActivity extends Activity {
	
	Button startRec, stopRec, playBack, done;
	double x, y;
	Boolean recording = false;
	File myFile;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtag);
        Intent intent = getIntent();
        x = Double.parseDouble(intent.getStringExtra("lat"));
        y = Double.parseDouble(intent.getStringExtra("long"));
        //TextView tv2 = (TextView) findViewById(R.id.textView2);
        //tv2.setText("Your Location:" + intent.getStringExtra("lat") + " , " + intent.getStringExtra("long"));
        done = (Button)findViewById(R.id.doneButton);
        //done.setText("Your Location:" + intent.getStringExtra("lat") + " , " + intent.getStringExtra("long"));
        //setContentView(R.layout.activity_addtag);
        startRec = (Button)findViewById(R.id.startrec);
        //stopRec = (Button)findViewById(R.id.stoprec);
        playBack = (Button)findViewById(R.id.playback);
        //String s = "Your Location:" + intent.getStringExtra("lat") + " , " + intent.getStringExtra("long");
        //System.out.println("MAYAYAMAYHFFODFF:" + intent.getStringExtra("lat"));
        //playBack.setText(s);
        startRec.setOnClickListener(startRecOnClickListener);
        //stopRec.setOnClickListener(stopRecOnClickListener);
        playBack.setOnClickListener(playBackOnClickListener);
        done.setOnClickListener(doneOnClickListener);

    }
    
    OnClickListener startRecOnClickListener
    = new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			if (!recording) {
			startRec.setText("Stop Recording");
			Thread recordThread = new Thread(new Runnable(){

				@Override
				public void run() {
					recording = true;
					startRecord();
				}
				
			});
			
			recordThread.start();
			} else {
				startRec.setText("Start Recording");
				recording = false;
			}

		}};
		
	/*OnClickListener stopRecOnClickListener
	= new OnClickListener(){
		
		@Override
		public void onClick(View arg0) {
			recording = false;
		}};*/
		
	OnClickListener playBackOnClickListener
	    = new OnClickListener(){

			@Override
			public void onClick(View v) {
				playRecord();
			}
		
	};
	
	OnClickListener doneOnClickListener
    = new OnClickListener(){

		@Override
		public void onClick(View v) {
			if (myFile != null) {
			//MarkerOptions marker = new MarkerOptions().position(new LatLng(x, y))
			//		.title("New Clue 2");
			System.out.println("in ADDTAG");
			EditText et=(EditText)findViewById(R.id.editText1);
			String txt=et.getText().toString();
			HashMap<Integer, AudioFile> temp= MainActivity.db;
			temp.put(MainActivity.count, new AudioFile(txt, x, y, ""+MainActivity.count, myFile));
			//MainActivity.count += 1;
			Intent intent = new Intent(AddTagActivity.this, MainActivity.class);
			MainActivity.db = temp;
			MainActivity.count += 1;
			startActivity(intent);
			//MainActivity.myMap.addMarker(marker);
			}
		}
	
};
	
	
	
	private void startRecord(){

		File file = new File(Environment.getExternalStorageDirectory(), "test2.pcm"); 
				
		try {
			file.createNewFile();
			
			OutputStream outputStream = new FileOutputStream(file);
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
			DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
			
			int minBufferSize = AudioRecord.getMinBufferSize(11025, 
					AudioFormat.CHANNEL_CONFIGURATION_MONO, 
					AudioFormat.ENCODING_PCM_16BIT);
			
			short[] audioData = new short[minBufferSize];
			
			AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
					11025,
					AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT,
					minBufferSize);
			
			audioRecord.startRecording();
			
			while(recording){
				int numberOfShort = audioRecord.read(audioData, 0, minBufferSize);
				for(int i = 0; i < numberOfShort; i++){
					dataOutputStream.writeShort(audioData[i]);
				}
			}
			
			audioRecord.stop();
			dataOutputStream.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		myFile = file;
	}

	void playRecord(){
		
		//File file = new File(Environment.getExternalStorageDirectory(), "test.pcm");
		File file = myFile;
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
			
			AudioTrack audioTrack = new AudioTrack(
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
