package com.example.proto;

import java.util.Timer;
import java.util.TimerTask;
import com.example.proto.MyLocation;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class MyLocation {
	
	LocationManager locationManager;
	LocationResult locationResult;
	Timer timer;
	boolean gps_on = false;
	boolean network_on = false;
	
	public static final long LOCATION_REFRESH_TIME_IN_MS= 1000*60*5;
	LocationListener gpsLocationListener = new LocationListener(){
		@Override
		public void onLocationChanged(Location location){
			timer.cancel();
			locationResult.gotLocation(location);
			locationManager.removeUpdates(this);
			locationManager.removeUpdates(networkLocationListener);
		}

		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
			
		}
	};
	
	LocationListener networkLocationListener = new LocationListener(){
		public void onLocationChanged(Location location){
			timer.cancel();
			locationResult.gotLocation(location);
			locationManager.removeUpdates(this);
			locationManager.removeUpdates(gpsLocationListener);
		}

		

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
	};
	public boolean getLocation(Context context, LocationResult result){
		locationResult = result; 
		if (locationManager == null){
			locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		}
		try{
			gps_on = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		}catch (Exception e){
			System.out.println("exception in gps provider");
		}
		try{
			network_on = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		}catch(Exception e){
			System.out.println("exception in network provider");
		}
		
		if (gps_on){
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsLocationListener);
		}
		if (network_on){
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, networkLocationListener);
		}
		if (!gps_on && !network_on){
			return false;
		}
		timer = new Timer();
		timer.schedule(new GetLastLocation(), 20000);
		return true;
		
	}
	class GetLastLocation extends TimerTask{
		@Override
		public void run(){
			locationManager.removeUpdates(gpsLocationListener);
			locationManager.removeUpdates(networkLocationListener);
			android.location.Location networkLocation= null;
			android.location.Location gpsLocation = null;
			
			
			
			if (gps_on){
				gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			}
			if(network_on){
				networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

			}
			if(gpsLocation !=null && networkLocation !=null){
				if(networkLocation.getTime() > gpsLocation.getTime()){
					locationResult.gotLocation(gpsLocation);
				}else {
					locationResult.gotLocation(networkLocation);
				}
				return;
			}
			
			if (gpsLocation !=null){
				locationResult.gotLocation(gpsLocation);
			}
			if (networkLocation != null){
				locationResult.gotLocation(networkLocation);
			}
		}
	}
	public static abstract class LocationResult{
		public abstract void gotLocation(Location location);
	}
}
