package com.taralite.e_presensi.location;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

public class GPSTracker extends Service implements LocationListener {

	private final Context mContext;

	// flag for GPS status
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	// flag for GPS status
	boolean canGetLocation = false;

	Location location; // location
	double latitude; // latitude
	double longitude; // longitude
	long time;
	long gps;
	long network = 0;
	double speed;
	double derajat;
	double akurat;
	String Provider;
	int ll = 0;
	String semu,kec;

	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 30000; // 1 minute

	// Declaring a Location Manager
	protected LocationManager locationManager;

	public GPSTracker(Context context) {
		this.mContext = context;
		getLocation();
	}

	public Location getLocation() {
		try {
			locationManager = (LocationManager) mContext
					.getSystemService(LOCATION_SERVICE);

			// getting GPS status
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			System.out.println("Is GPS " + isGPSEnabled);
			System.out.println("Is Network " + isNetworkEnabled);
			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
			} else {
				this.canGetLocation = true;
				if (isGPSEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.GPS_PROVIDER, 40000,
							20, this);

					System.out.println("dari GGPPSSS....!!!!!!!!!");

					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						if (location != null) {
							ll = 1;
							latitude = location.getLatitude();
							longitude = location.getLongitude();
							time = location.getTime();
							System.out.println("Time dari GPS " + time);
							Provider = "GPS";
							speed = location.getSpeed();
							//System.out.println("Spedd yag di dapat " + speed);
							derajat = location.getBearing();
							akurat = location.getAccuracy();
							//System.out.println("akurasi dari GPS Provider "						+ akurat);

							/*
							 * try{ Location location2 =
							 * locationManager.getLastKnownLocation
							 * (LocationManager.NETWORK_PROVIDER); time =
							 * location2.getTime();
							 * System.out.println(" Time dari Netork "+time);
							 * }catch(Exception w){ time = location.getTime();
							 * System.out.println("Time dari GPS "+time); }
							 */

						} else {
							ll = 2;
							System.out.println("GPSNYA kosong");
						}
					}
				}
				if (ll == 2) {
					System.out.println("dari NETWORK....!!!!!!!!!");
					if (isNetworkEnabled) {// if GPS Enabled get lat/long using
											// GPS Services
						if (location == null) {
							locationManager.requestLocationUpdates(
									LocationManager.NETWORK_PROVIDER,
									40000, 20, this);
							System.out.println("111111111111111");
							if (locationManager != null) {
								location = locationManager
										.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
								System.out.println("222222222222");
								if (location != null) {
									System.out.println("3333333333333");
									latitude = location.getLatitude();
									longitude = location.getLongitude();
									time = location.getTime();
									Provider = "Network";
									//System.out.println("Time dari Network "								+ time);
									speed = location.getSpeed();
									derajat = location.getBearing();
									akurat = location.getAccuracy();
									//System.out.println("akurasi dari Network "									+ akurat);

								}
							}
						}else{
							System.out.println("NETWORK kosong");
						}
					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return location;
	}

	/**
	 * Stop using GPS listener Calling this function will stop using GPS in your
	 * app
	 * */
	public void stopUsingGPS() {
		if (locationManager != null) {
			locationManager.removeUpdates(GPSTracker.this);
			
		}
	}

	/**
	 * Function to get latitude
	 * */
	public double getLatitude() {
		if (location != null) {
			latitude = location.getLatitude();
		}

	//Toast.makeText(getApplication(), "latitude"+latitude, Toast.LENGTH_LONG).show();
		return latitude;
	}

	public String Provider() {

		return Provider;

	}

	/**
	 * Function to get longitude
	 * */
	public double getLongitude() {
		if (location != null) {
			longitude = location.getLongitude();
		}

		
		return longitude;
	}

	public long getTime() {
		

		/*
		 * try { Location location2 = locationManager
		 * .getLastKnownLocation(LocationManager.NETWORK_PROVIDER); time =
		 * location2.getTime(); System.out.println(" Time dari Netork " + time);
		 * } catch (Exception w) { System.out.println("getTime 2"); time =
		 * location.getTime(); System.out.println("Time dari GPS " + time); }
		 * System.out.println("Return Time dari GPSTracker " + time);
		 */
		return time;
	}

	public long getTime2() {
		time = location.getTime();
		
		gps = time;
		return gps;

	}

	public String getSpeed() {
		//System.out.println("speeddnyaa "+speed);
		String ceke = Double.toString(speed);
		for(int d=0;d<5;d++){
			char hj=ceke.charAt(d);
			String m=String.valueOf(hj);
			if(m.equals(".")){
				d=5;
			}else{
				if(semu==null){
					semu=m;
				}else{
					semu=semu+m;
				}
				
			}
		}
		int sem=Integer.valueOf(semu);
		if(sem>=200){
			kec="00";
		}else{
			kec=semu;
		}
		
		

		
		return kec;
	}

	public double getBearing() {
		if (location != null) {
			derajat = location.getBearing();
		}
		return derajat;
	}

	public double getAcurat() {
		if (location != null) {
			akurat = location.getAccuracy();
			//System.out.println("akurasi dari GPSTracker " + akurat);
		}

		return akurat;
	}

	/**
	 * Function to check GPS/wifi enabled
	 * 
	 * @return boolean
	 * */
	public boolean canGetLocation() {
		return this.canGetLocation;
	}

	/**
	 * Function to show settings alert dialog On pressing Settings button will
	 * lauch Settings Options
	 * */
	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

		// Setting Dialog Title
		alertDialog.setTitle("GPS is settings");

		// Setting Dialog Message
		alertDialog
				.setMessage("GPS is not enabled. Do you want to go to settings menu?");

		// On pressing Settings button
		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						mContext.startActivity(intent);
					}
				});

		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		// Showing Alert Message
		alertDialog.show();
	}

	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
