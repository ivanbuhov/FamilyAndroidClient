package com.buhov.family;

import java.util.List;

import android.content.Intent;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;

public class SettingsActivity extends PreferenceActivity {
	@Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()){
			case R.id.menu_item_my_pedigrees:
				this.startActivity(new Intent(this, MyPedigreesActivity.class));
			break;
			case R.id.menu_item_settings:
				this.startActivity(new Intent(this, SettingsActivity.class));
			break;
			case R.id.menu_item_login:
				this.startActivity(new Intent(this, LoginActivity.class));
			break;
		}
		
		return true;
	}

}
