package com.buhov.family;

import com.buhov.FamilyHttpClient.FamilyHttpClient;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public class BaseActivity extends Activity {

	private FamilyHttpClient familyClient;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.base, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()){
			case R.id.menu_item_settings:
				this.startActivity(new Intent(this, SettingsActivity.class));
			case R.id.menu_item_register:
				this.startActivity(new Intent(this, RegisterActivity.class));
			break;
		}
		
		return true;
	}

	protected FamilyHttpClient getFamilyClient() {
		if(this.familyClient == null) {
			this.familyClient = new FamilyHttpClient();
		}
		return this.familyClient;
	}

	public AlertDialog getAlert(Context context, String message, String title, int iconResource) {
		AlertDialog dialog = new AlertDialog.Builder(context).create();
		if(message != null) {
			dialog.setMessage(message);
		}
		if(title != null) {
			dialog.setTitle(title);
		}
		if(iconResource != 0) {
			dialog.setIcon(iconResource);
		}
		return dialog;
	}
}