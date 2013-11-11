package com.buhov.family;

import com.buhov.family.FamilyData.FamilyData;
import com.buhov.family.FamilyData.FamilyDataException;
import com.buhov.family.FamilyHttpClient.Entities.*;
import com.buhov.family.LoginActivity.UserLoginTask;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MyPedigreesActivity extends BaseActivity {

	private View progressView;
	private GetMyPedigreesTask myPedigreesTask;
	private ArrayAdapter<Pedigree> pedigreesAdapter;
	private Pedigree[] pedigrees;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pedigrees);
        this.progressView = findViewById(R.id.progress_my_pedigrees);
        this.myPedigreesTask = null;
        this.pedigreesAdapter = null;
        
        // Getting the pedigrees
        if(this.app.getLoginManager().hasLoggedUser()) {
        	this.attemptGetMyPedigrees(this.app.getLoginManager().getLoggedUser());
        }
    }
    
    private void attemptGetMyPedigrees(User user) {
    	if (this.myPedigreesTask != null) {
			return;
		}

    	MyPedigreesActivity.this.toggleView(true, progressView, null);
    	this.myPedigreesTask = new GetMyPedigreesTask();
    	this.myPedigreesTask.execute(user);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_pedigrees, menu);
        return true;
    }
    
    public class GetMyPedigreesTask extends AsyncTask<User, Void, Boolean> {
		
		private String error;
		
		@Override
		protected Boolean doInBackground(User... params) {
			try {
				FamilyData data = MyPedigreesActivity.this.app.getFamilyData();
				MyPedigreesActivity.this.pedigrees = data.getPedigrees(params[0]);
				return true;
			}
			catch(FamilyDataException e) {
				this.error = e.getMessage();
				return false;
			}
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			myPedigreesTask = null;

			if (success) {
				MyPedigreesActivity.this.pedigreesAdapter = new ArrayAdapter<Pedigree>(MyPedigreesActivity.this, 
						android.R.layout.simple_list_item_1, android.R.id.text1, MyPedigreesActivity.this.pedigrees);
				ListView pedigreesListView = (ListView) findViewById(R.id.content_my_pedigrees);
				pedigreesListView.setAdapter(MyPedigreesActivity.this.pedigreesAdapter);
			} else {
				String title = getResources().getString(R.string.alert_title_loading_data_failed);
				String message = this.error;
				int icon = R.drawable.ic_error;
				AlertDialog dialog = MyPedigreesActivity.this.getAlert(MyPedigreesActivity.this, message, title, icon);
				dialog.show();
			}
			
			MyPedigreesActivity.this.toggleView(false, progressView, null);
		}

		@Override
		protected void onCancelled() {
			myPedigreesTask = null;
			MyPedigreesActivity.this.toggleView(false, progressView, null);
		}


	}
    
}
