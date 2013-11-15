package com.buhov.family;

import com.buhov.family.FamilyData.FamilyData;
import com.buhov.family.FamilyData.FamilyDataException;
import com.buhov.family.FamilyHttpClient.Entities.Pedigree;
import com.buhov.family.FamilyHttpClient.Entities.PedigreeFull;
import com.buhov.family.FamilyHttpClient.Entities.User;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class PedigreeActivity extends BaseActivity {
	
	public static final String EXTRA_PEDIGREE_ID = "EXTRA_PEDIGREE_ID";
	public static final String EXTRA_PEDIGREE_TITLE = "EXTRA_PEDIGREE_TITLE";
	public static final String EXTRA_PEDIGREE_OWNERID = "EXTRA_PEDIGREE_OWNERID";

	private Pedigree pedigree;
	private PedigreeFull pedigreeFull;
	
	private View progressView;
	private TextView testText;
	
	private GetPedigreeTask getPedigreeTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.initPedigree();
		this.setTitle(this.pedigree.getTitle());
		setContentView(R.layout.activity_pedigree);
		this.progressView = findViewById(R.id.progress_pedigree);
		this.testText = (TextView)findViewById(R.id.test_text);
	}
	
	@Override
    protected void onResume() {
    	super.onResume();
    	// Loading the pedigrees
        this.attemptGetPedigree(false);
    }
	
	private void initPedigree() {
		int id = getIntent().getExtras().getInt(EXTRA_PEDIGREE_ID);
		String title = getIntent().getExtras().getString(EXTRA_PEDIGREE_TITLE);
		int ownerId = getIntent().getExtras().getInt(EXTRA_PEDIGREE_OWNERID);
		this.pedigree = new Pedigree(id, title, ownerId);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pedigree, menu);
		return true;
	}

	private void refreshTree() {
		for(int i = 0; i < this.pedigreeFull.getPeople().length; i++) {
			this.testText.append(this.pedigreeFull.getPeople()[i].getDisplayName() + ", ");
		}
	}
	
	private void attemptGetPedigree(boolean obligatoryServerUpdate) {
		if (this.getPedigreeTask != null) {
			return;
		}
    	
    	if(this.app.getLoginManager().hasLoggedUser()) {
        	PedigreeActivity.this.toggleView(true, progressView, testText);
        	this.getPedigreeTask = new GetPedigreeTask(this.pedigree.getId(), obligatoryServerUpdate);
        	this.getPedigreeTask.execute(this.app.getLoginManager().getLoggedUser());
        }
	}
	
	private class GetPedigreeTask extends AsyncTask<User, Void, Boolean> {
		
    	private boolean obligatoryServerUpdate;
		private String error;
		private int pedigreeId;
		
		public GetPedigreeTask(int pedigreeId, boolean obligatoryServerUpdate) {
			this.pedigreeId = pedigreeId;
			this.obligatoryServerUpdate = obligatoryServerUpdate;
		}
		
		@Override
		protected Boolean doInBackground(User... params) {
			try {
				FamilyData data = PedigreeActivity.this.app.getFamilyData();
				PedigreeActivity.this.pedigreeFull = data.getPedigree(params[0], this.pedigreeId, this.obligatoryServerUpdate);
				return true;
			}
			catch(FamilyDataException e) {
				this.error = e.getMessage();
				return false;
			}
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			getPedigreeTask = null;

			if (success) {
				PedigreeActivity.this.setTitle(pedigreeFull.getTitle());
				PedigreeActivity.this.refreshTree();
			}
			else {
				String title = getResources().getString(R.string.alert_title_loading_data_failed);
				String message = this.error;
				int icon = R.drawable.ic_error;
				AlertDialog dialog = PedigreeActivity.this.getAlert(PedigreeActivity.this, message, title, icon);
				dialog.show();
			}
			
			PedigreeActivity.this.toggleView(false, progressView, testText);
		}

		@Override
		protected void onCancelled() {
			getPedigreeTask = null;
			PedigreeActivity.this.toggleView(false, progressView, testText);
		}
	}
}
