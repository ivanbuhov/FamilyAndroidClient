package com.buhov.family;

import java.util.List;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceActivity.Header;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.view.Menu;

public class SettingsActivity extends PreferenceActivity {
	@Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

}