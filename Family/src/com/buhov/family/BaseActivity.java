package com.buhov.family;

import android.os.Build;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class BaseActivity extends Activity {

	protected FamilyApplication app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.app = (FamilyApplication)getApplication();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
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

	// Shows the progress UI and hides the login form.
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public void showProgress(final boolean show, final View contentView, final View progressView) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			progressView.setVisibility(View.VISIBLE);
			progressView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							progressView.setVisibility(show ? View.VISIBLE : View.GONE);
						}
					});

			contentView.setVisibility(View.VISIBLE);
			contentView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							contentView.setVisibility(show ? View.GONE : View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			progressView.setVisibility(show ? View.VISIBLE : View.GONE);
			contentView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

}
