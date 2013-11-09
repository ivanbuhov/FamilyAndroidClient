package com.buhov.family;

import com.buhov.FamilyHttpClient.FamilyHttpClient;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.*;

public class FamilyApplication extends Application {
	
	private FamilyHttpClient familyClient;
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	public FamilyHttpClient getFamilyClient() {
		if(this.familyClient == null) {
			this.familyClient = new FamilyHttpClient();
		}
		return this.familyClient;
	}
}
