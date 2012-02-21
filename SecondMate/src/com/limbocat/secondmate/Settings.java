package com.limbocat.secondmate;

import android.content.Context;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.util.Log;
import android.os.Bundle;

public class Settings extends PreferenceActivity {
	// Preference names and default values
	private static final String NMEA_PREFIX = "nmea_prefix";
	private static final String NMEA_PREFIX_DEF = "II";
	private static final String NMEA_SENTENCES = "nmea_sentences";
	private static String[] NMEA_SENTENCE_FILTER = {"HDM", "GLL"};
	
	private static final String TAG = "Settings";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		this.setNmeaSentenceFilter(getBaseContext());
	}
	
	/** Get the current value of the NMEA prefix */
	public static String getNmeaPrefix(Context context) {
		Log.d(TAG, "getNmeaPrefix: inside");
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString(NMEA_PREFIX, NMEA_PREFIX_DEF);
	}
	
	/** Get an array of NMEA sentences */
	private void setNmeaSentenceFilter(Context context) {
		Log.d(TAG, "setNmeaSentenceFilter: start");
		
		PreferenceCategory prefCat = (PreferenceCategory) findPreference(NMEA_SENTENCES);
		String[] nmeaFilter = new String[prefCat.getPreferenceCount()];
		String key;
		String title;
		int i;
		int j = 0;
		for (i = 0; i < prefCat.getPreferenceCount(); i++) {
			Log.d(TAG, "setNmeaSentenceFilter: inside for loop");
			key = prefCat.getPreference(i).getKey();
			title = (String) prefCat.getPreference(i).getTitle();
			if (prefCat.getPreference(i).isEnabled()) {
				Log.d(TAG, "setNmeaSentenceFilter: inside isEnabled if statement");
				Log.d(TAG, "key " + key +" : title : " + title);
				nmeaFilter[j] = title;
				j++;
			}
		}
		
				
		for (String s: nmeaFilter) {
			Log.d(TAG, "setNmeaSentenceFilter: endMethod and nmeaFilter: " + s);
		}
		NMEA_SENTENCE_FILTER = nmeaFilter;
		
	}
	
	public static String[] getNmeaFilter() {
		if (NMEA_SENTENCE_FILTER != null) {
			return NMEA_SENTENCE_FILTER;
		} else {
			return null;
		}
	}
	

}
