package com.limbocat.secondmate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class SecondMate extends Activity implements OnClickListener {
	private static final String TAG = "SecondMate";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        View exitButton = this.findViewById(R.id.exit_button);
        exitButton.setOnClickListener(this);
        View nmeaFilterButton = this.findViewById(R.id.nmea_filter_button);
        nmeaFilterButton.setOnClickListener(this);
        
        Log.d(TAG, "SecondMate: NMEA Prefix: " + Settings.getNmeaPrefix(getBaseContext()));
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.settings:
    		startActivity(new Intent(this, Settings.class));
    		return true;
    	// More items go hear (if any)...
    	}
    	return false;
    }
    
    public void onClick(View v) {
    	switch (v.getId()) {
    	case R.id.exit_button:
    		finish();
    		break;
    	case R.id.nmea_filter_button:
    		String[] filter = Settings.getNmeaFilter();
    		for (String s: filter) {
    			Log.d(TAG, "onClickFilterButton: nmeaFilter: " + s);
    		}
    		break;
    	}
    }
}