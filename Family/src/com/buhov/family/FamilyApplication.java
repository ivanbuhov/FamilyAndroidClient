package com.buhov.family;

import com.buhov.family.FamilyData.*;
import com.buhov.family.FamilyDb.*;
import com.buhov.family.FamilyHttpClient.*;
import com.buhov.family.FamilyHttpClient.Entities.User;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

public class FamilyApplication extends Application {
	
	private FamilyData familyData;
	private User loggedUser;
	
	@Override
	public void onCreate() {
		super.onCreate();
		this.loggedUser = null;
	}
	
	public FamilyData getFamilyData() {
		if(this.familyData == null) {
			this.familyData = new FamilyData(this, 
					new FamilyDb(new FamilyDbHelper(this)),
					new FamilyHttpClient(this));
		}
		return this.familyData;
	}
	
	public User getLoggedUser() {
		return this.loggedUser;
	}
	
	public void LoginWith(User user) {
		this.loggedUser = user;
	}
	
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnectedOrConnecting());
	}
	
	public boolean isOnlineModeEnabled() {
		boolean onlineModeEnabled = PreferenceManager.getDefaultSharedPreferences(this)
				.getBoolean(this.getResources().getString(R.string.pref_key_online_mode), true);
		return onlineModeEnabled;
	}

}
