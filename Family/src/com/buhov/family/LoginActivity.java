package com.buhov.family;

import com.buhov.family.FamilyData.FamilyData;
import com.buhov.family.FamilyData.FamilyDataException;
import com.buhov.family.FamilyHttpClient.Entities.User;
import com.buhov.family.FamilyHttpClient.Entities.UserDTO;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class LoginActivity extends BaseActivity {
	
	private UserRegistrationTask registrationTask = null;
	private UserLoginTask loginTask = null;

	// Temporarily store the values from the text
	private String username;
	private String password;
	
	// UI references
	private EditText usernameView;
	private EditText passwordView;
	private View inputFormView;
	private View statusView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		// Set up the login form and adding the event listeners
		this.usernameView = (EditText) findViewById(R.id.username);
		this.passwordView = (EditText) findViewById(R.id.password);
		this.passwordView.setOnEditorActionListener(new onPasswordEditorActionListener());

		this.inputFormView = findViewById(R.id.input_form);
		this.statusView = findViewById(R.id.status);

		findViewById(R.id.register_button).setOnClickListener(new onRegisterButtonClickedListener());
		findViewById(R.id.login_button).setOnClickListener(new onLoginButtonClickedListener());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	public void attemptRegister() {
		if (registrationTask != null || loginTask != null) {
			return;
		}

		// Reset errors.
		usernameView.setError(null);
		passwordView.setError(null);

		// Store values at the time of the registration attempt.
		username = usernameView.getText().toString();
		password = passwordView.getText().toString();

		if (Utils.validateUserInput(LoginActivity.this, usernameView, passwordView)) {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			LoginActivity.this.showProgress(true, inputFormView, statusView);
			registrationTask = new UserRegistrationTask();
			registrationTask.execute(new UserDTO(username, password));
		}
	}
	
	public void attemptLogin() {
		if (registrationTask != null || loginTask != null) {
			return;
		}

		// Reset errors.
		usernameView.setError(null);
		passwordView.setError(null);

		// Store values at the time of the login attempt.
		username = usernameView.getText().toString();
		password = passwordView.getText().toString();

		if (Utils.validateUserInput(LoginActivity.this, usernameView, passwordView)) {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			LoginActivity.this.showProgress(true, inputFormView, statusView);
			loginTask = new UserLoginTask();
			loginTask.execute(new UserDTO(username, password));
		}
	}
	
	// Represents an asynchronous registration task used to register a user in the system.
	public class UserRegistrationTask extends AsyncTask<UserDTO, Void, Boolean> {
		
		private FamilyApplication application = (FamilyApplication)getApplication();
		private User user;
		private String error;
		
		@Override
		protected Boolean doInBackground(UserDTO... params) {
			try {
				FamilyData data = this.application.getFamilyData();
				this.user = data.Register(params[0]);
				return true;
			}
			catch(FamilyDataException e) {
				this.error = e.getMessage();
				return false;
			}
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			registrationTask = null;
			LoginActivity.this.showProgress(false, inputFormView, statusView);

			if (success) {
				LoginActivity.this.finish();
				startActivity(new Intent(LoginActivity.this, MyPedigreesActivity.class));
				LoginActivity.this.app.LoginWith(user);
				
			} else {
				String title = getResources().getString(R.string.alert_title_registration_failed);
				String message = this.error;
				int icon = R.drawable.ic_error;
				AlertDialog dialog = LoginActivity.this.getAlert(LoginActivity.this, message, title, icon);
				dialog.show();
			}
		}

		@Override
		protected void onCancelled() {
			registrationTask = null;
			LoginActivity.this.showProgress(false, inputFormView, statusView);
		}
	}
	
	// Represents an asynchronous login task used to login a user in the system.
	public class UserLoginTask extends AsyncTask<UserDTO, Void, Boolean> {
		
		private FamilyApplication application = (FamilyApplication)getApplication();
		private User user;
		private String error;
		
		@Override
		protected Boolean doInBackground(UserDTO... params) {
			try {
				FamilyData data = this.application.getFamilyData();
				this.user = data.Login(params[0]);
				return true;
			}
			catch(FamilyDataException e) {
				this.error = e.getMessage();
				return false;
			}
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			loginTask = null;
			LoginActivity.this.showProgress(false, inputFormView, statusView);

			if (success) {
				LoginActivity.this.finish();
				startActivity(new Intent(LoginActivity.this, MyPedigreesActivity.class));
				LoginActivity.this.app.LoginWith(user);
				
			} else {
				String title = getResources().getString(R.string.alert_title_login_failed);
				String message = this.error;
				int icon = R.drawable.ic_error;
				AlertDialog dialog = LoginActivity.this.getAlert(LoginActivity.this, message, title, icon);
				dialog.show();
			}
		}

		@Override
		protected void onCancelled() {
			loginTask = null;
			LoginActivity.this.showProgress(false, inputFormView, statusView);
		}
	}
	
	// An event listener, listening for typing in the password filed
	private class onPasswordEditorActionListener implements OnEditorActionListener {
		@Override
		public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
			if (actionId == R.id.login || actionId == EditorInfo.IME_NULL) {
				attemptLogin();
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

	private class onLoginButtonClickedListener implements OnClickListener {
		
		@Override
		public void onClick(View v) {
			attemptLogin();
		}
		
	}
}
