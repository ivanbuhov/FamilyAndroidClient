package com.buhov.family;

import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Load the preferences from an XML resource
		String settings = getArguments().getString("preferences");
		Resources resources = getResources();
        if (settings.equals(resources.getString(R.string.pref_header_title_account))) {
            addPreferencesFromResource(R.xml.pref_account);
        }
        else if(settings.equals(resources.getString(R.string.pref_header_title_others))) {
        	addPreferencesFromResource(R.xml.pref_others);
        }
        // addPreferencesFromResource(R.xml.settings_default);
	}
}
