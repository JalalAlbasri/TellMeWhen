package com.tellmewhen.stocks;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;

/**
 * The Main Activity.
 * 
 * This activity starts up the RegisterActivity immediately, which communicates
 * with your App Engine backend using Cloud Endpoints. It also receives push
 * notifications from backend via Google Cloud Messaging (GCM).
 * 
 * Check out RegisterActivity.java for more details.
 */
public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();
	
	public static final String SHARED_PREFERENCES = "SharedPreferences";
	public static final String DEVICE_ID_KEY = "device_id";
	public static final String DEFAULT_DEVICE_ID = "";
	
	private SharedPreferences prefs;
	private String deviceId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		prefs = getSharedPreferences(MainActivity.SHARED_PREFERENCES, MainActivity.MODE_PRIVATE);
		deviceId = prefs.getString(DEVICE_ID_KEY, DEFAULT_DEVICE_ID);
		Log.d(TAG, "deviceInfoId: " + deviceId);
		if (deviceId.equals(DEFAULT_DEVICE_ID)) {
			//No registraction id saved.
			//Start up RegisterActivity right away
			Log.d(TAG, "Device Id not Found, Attempting to register.");
			Intent registrationIntent = new Intent(this, RegisterActivity.class);
			startActivity(registrationIntent);
		}
		else {
			Log.d(TAG, "deviceId: " + deviceId + ", Starting intent alert");
			Intent alertIntent = new Intent(this, AlertListActivity.class);
			startActivity(alertIntent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
