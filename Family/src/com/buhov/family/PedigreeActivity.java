package com.buhov.family;

import com.buhov.family.PedigreeNode.PersonNode;
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
import android.widget.FrameLayout;
import android.widget.TextView;

public class PedigreeActivity extends BaseActivity {
	
	public static final String EXTRA_PEDIGREE_ID = "EXTRA_PEDIGREE_ID";
	public static final String EXTRA_PEDIGREE_TITLE = "EXTRA_PEDIGREE_TITLE";
	public static final String EXTRA_PEDIGREE_OWNERID = "EXTRA_PEDIGREE_OWNERID";

	private Pedigree pedigreeFromIntent;
	private PedigreeFull pedigreeFull;
	private PedigreeNode pedigreeNode;
	
	private View progressView;
	private FrameLayout pedigreeFrameLayout;
	private PedigreeView pedigreeView;
	
	private GetPedigreeTask getPedigreeTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.initPedigreeFromIntent();
		this.setTitle(this.pedigreeFromIntent.getTitle());
		setContentView(R.layout.activity_pedigree);
		this.progressView = findViewById(R.id.progress_pedigree);
		this.pedigreeFrameLayout = (FrameLayout)findViewById(R.id.pedigree_tree);
		// Initializes the PedigreeView
		this.pedigreeView = new PedigreeView(this, null);
		this.pedigreeView.setAnonymousDisplayName(getResources().getString(R.string.anonymous_display_name));
		this.pedigreeView.setNoPeopleMessage(getResources().getString(R.string.no_people_message));
		this.pedigreeFrameLayout.addView(this.pedigreeView);
	}
	
	@Override
    protected void onResume() {
    	super.onResume();
    	// Loading the pedigrees
        this.attemptGetPedigree(false);
    }
	
	private void initPedigreeFromIntent() {
		int id = getIntent().getExtras().getInt(EXTRA_PEDIGREE_ID);
		String title = getIntent().getExtras().getString(EXTRA_PEDIGREE_TITLE);
		int ownerId = getIntent().getExtras().getInt(EXTRA_PEDIGREE_OWNERID);
		this.pedigreeFromIntent = new Pedigree(id, title, ownerId);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pedigree, menu);
		return true;
	}

	private void refreshTree() {
		this.pedigreeNode = new PedigreeNode(this.pedigreeFull);
		PersonNode person = this.pedigreeNode.getPerson(4); // 4 - magic value
		this.pedigreeView.setPersonNode(person);
	}
	
	private void attemptGetPedigree(boolean obligatoryServerUpdate) {
		if (this.getPedigreeTask != null) {
			return;
		}
    	
    	if(this.app.getLoginManager().hasLoggedUser()) {
        	PedigreeActivity.this.toggleView(true, progressView, pedigreeFrameLayout);
        	this.getPedigreeTask = new GetPedigreeTask(this.pedigreeFromIntent.getId(), obligatoryServerUpdate);
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
			
			PedigreeActivity.this.toggleView(false, progressView, pedigreeFrameLayout);
		}

		@Override
		protected void onCancelled() {
			getPedigreeTask = null;
			PedigreeActivity.this.toggleView(false, progressView, pedigreeFrameLayout);
		}
	}
}
