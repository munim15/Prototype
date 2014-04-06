package com.example.proto;

import java.io.File;

public class AudioFile {

	public String title;
	public boolean ispublictag = true;
	public File myfile;
	public double x;
	public double y;
	public String mySnip;
	
	public AudioFile(String t, double lat, double lon, String snip, File fl) {
		title = t;
		x = lat;
		y = lon;
		mySnip = snip;
		myfile = fl;
		//ispublictag = flag;
		//myfile = fl;
	}
	

}
