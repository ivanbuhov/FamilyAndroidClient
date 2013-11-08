package com.buhov.family;

import com.buhov.FamilyHttpClient.FamilyHttpClient;
import com.buhov.FamilyHttpClient.FamilyServiceException;
import com.buhov.FamilyHttpClient.Entities.User;
import com.buhov.FamilyHttpClient.Entities.UserDTO;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class RegisterActivity extends BaseActivity {
	
	private static final int MIN_USERNAME_LENGTH = 3;
	private static final int MAX_USERNAME_LENGTH = 30;
	private static final int MIN_PASSWORD_LENGTH = 5;
	private static final int MAX_PASSWORD_LENGTH = 30;
	
	private UserRegistrationTask registrationTask = null;

	// Temporarily store the values from the text
	private String username;
	private String password;
	
	// UI references
	private EditText usernameView;
	private EditText passwordView;
	private View registrationFormView;
	private View registrationStatusView;
	private TextView registrationStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_register);

		// Set up the login form and adding the event listeners
		this.usernameView = (EditText) findViewById(R.id.username);
		this.passwordView = (EditText) findViewById(R.id.password);
		this.passwordView.setOnEditorActionListener(new onPasswordEditorActionListener());

		this.registrationFormView = findViewById(R.id.register_form);
		this.registrationStatusView = findViewById(R.id.register_status);
		this.registrationStatusMessageView = (TextView) findViewById(R.id.register_status_message);

		findViewById(R.id.register_button).setOnClickListener(new onRegisterButtonClickedListener());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptRegister() {
		if (registrationTask != null) {
			return;
		}

		// Reset errors.
		usernameView.setError(null);
		passwordView.setError(null);

		// Store values at the time of the registration attempt.
		username = usernameView.getText().toString();
		password = passwordView.getText().toString();

		boolean cancelRegistration = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(password)) {
			passwordView.setError(getString(R.string.error_field_required));
			focusView = passwordView;
			cancelRegistration = true;
		} else if (password.length() < MIN_PASSWORD_LENGTH) {
			passwordView.setError(getString(R.string.error_short_password));
			focusView = passwordView;
			cancelRegistration = true;
		} else if(password.length() > MAX_PASSWORD_LENGTH) {
			passwordView.setError(getString(R.string.error_long_password));
			focusView = passwordView;
			cancelRegistration = true;
		}

		// Check for a valid username.
		if (TextUtils.isEmpty(username)) {
			usernameView.setError(getString(R.string.error_field_required));
			focusView = usernameView;
			cancelRegistration = true;
		} else if (username.length() < MIN_USERNAME_LENGTH) {
			usernameView.setError(getString(R.string.error_short_username));
			focusView = usernameView;
			cancelRegistration = true;
		} else if(username.length() > MAX_USERNAME_LENGTH) {
			usernameView.setError(getString(R.string.error_long_username));
			focusView = usernameView;
			cancelRegistration = true;
		}

		if (cancelRegistration) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			registrationStatusMessageView.setText(R.string.register_progress_signing_in);
			showProgress(true);
			registrationTask = new UserRegistrationTask();
			registrationTask.execute(new UserDTO(username, password));
		}
	}

	// Shows the progress UI and hides the login form.
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			this.registrationStatusView.setVisibility(View.VISIBLE);
			this.registrationStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							registrationStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			this.registrationFormView.setVisibility(View.VISIBLE);
			this.registrationFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							registrationFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			registrationStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			registrationFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	// Represents an asynchronous registration task used to authenticate the user.
	public class UserRegistrationTask extends AsyncTask<UserDTO, Void, Boolean> {
		
		private FamilyApplication application = (FamilyApplication)getApplication();
		private User user;
		private String error;
		
		@Override
		protected Boolean doInBackground(UserDTO... params) {
			try {
				FamilyHttpClient client = this.application.getFamilyClient();
				this.user = client.Register(params[0]);
				return true;
			}
			catch(FamilyServiceException e) {
				this.error = e.getMessage();
				return false;
			}
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			registrationTask = null;
			showProgress(false);

			if (success) {
				RegisterActivity.this.finish();
			} else {
				String title = getResources().getString(R.string.alrt_title_registration_failed);
				String message = this.error;
				int icon = R.drawable.ic_error;
				AlertDialog dialog = RegisterActivity.this.getAlert(RegisterActivity.this, message, title, icon);
				dialog.show();
			}
		}

		@Override
		protected void onCancelled() {
			registrationTask = null;
			showProgress(false);
		}
	}
	
	// An event listener, listening for typing in the password filed
	private class onPasswordEditorActionListener implements OnEditorActionListener {
		@Override
		public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
			if (actionId == R.id.register || actionId == EditorInfo.IME_NULL) {
				attemptRegister();
				return true;
			}
			return false;
		}
		
	}

	private class onRegisterButtonClickedListener implements OnClickListener {
		
		@Override
		public void onClick(View v) {
			attemptRegister();
		}
		
	}
}