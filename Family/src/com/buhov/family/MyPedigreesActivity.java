package com.buhov.family;

import com.buhov.family.FamilyData.FamilyData;
import com.buhov.family.FamilyData.FamilyDataException;
import com.buhov.family.FamilyHttpClient.Entities.*;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MyPedigreesActivity extends BaseActivity {

	private View progressView;
	private Pedigree[] pedigrees;
	private ListView contentListView;
	private AlertDialog deletionAlert;
	private ActionMode cabMode = null;
	private int selectedPosition = -1;
	
	private GetMyPedigreesTask getMyPedigreesTask;
	private DeletePedigreeTask deletePedigreeTask;
	private ArrayAdapter<Pedigree> pedigreesAdapter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pedigrees);
        this.progressView = findViewById(R.id.progress_my_pedigrees);
        this.contentListView = (ListView) findViewById(R.id.content_my_pedigrees);
        this.getMyPedigreesTask = null;
        this.pedigreesAdapter = null;
        // Register listeners for context menu
        this.contentListView.setOnItemLongClickListener(new PedigreeItemClickListener());
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	// Loading the pedigrees
        this.attemptGetMyPedigrees();
    }
    
    private void attemptGetMyPedigrees() {
    	if (this.getMyPedigreesTask != null) {
			return;
		}
    	
    	if(this.app.getLoginManager().hasLoggedUser()) {
        	MyPedigreesActivity.this.toggleView(true, progressView, null);
        	this.getMyPedigreesTask = new GetMyPedigreesTask();
        	this.getMyPedigreesTask.execute(this.app.getLoginManager().getLoggedUser());
        }
	}
    
    private void attemptDeletePedigree(int pedigreeId) {
    	if (this.deletePedigreeTask != null) {
			return;
		}
    	
    	if(this.app.getLoginManager().hasLoggedUser()) {
        	MyPedigreesActivity.this.toggleView(true, progressView, contentListView);
        	this.deletePedigreeTask = new DeletePedigreeTask(this.app.getLoginManager().getLoggedUser());
        	this.deletePedigreeTask.execute(pedigreeId);
        }
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_pedigrees, menu);
        return true;
    }
	
	public void refreshList() {
		pedigreesAdapter = new ArrayAdapter<Pedigree>(MyPedigreesActivity.this, 
				android.R.layout.simple_list_item_1, android.R.id.text1, this.pedigrees);
		contentListView.setAdapter(pedigreesAdapter);
	}
	
	
    public AlertDialog getPedigreeDeletionAlert(String title) {
    	
    	if(this.deletionAlert == null) {
    		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
    		dialogBuilder.setMessage(R.string.delete_message_pedigree);
    		dialogBuilder.setIcon(R.drawable.ic_remove);
    		
    		dialogBuilder.setNegativeButton(R.string.no, new OnClickListener() {
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				dialog.dismiss();
    			}
    		});
    		
    		dialogBuilder.setPositiveButton(R.string.yes, new OnClickListener() {
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				Pedigree selectedPedigree = (Pedigree)contentListView.getItemAtPosition(selectedPosition);
    				attemptDeletePedigree(selectedPedigree.getId());
					attemptGetMyPedigrees();
    			}
    		});
    		
    		this.deletionAlert = dialogBuilder.create();
    	}
		
    	this.deletionAlert.setTitle(title);
		return this.deletionAlert;
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
			getMyPedigreesTask = null;

			if (success) {
				MyPedigreesActivity.this.refreshList();
			}
			else {
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
			getMyPedigreesTask = null;
			MyPedigreesActivity.this.toggleView(false, progressView, null);
		}
	}
    
    private class DeletePedigreeTask extends AsyncTask<Integer, Void, Boolean> {

    	private User user;
    	private String error = null;
    	
    	public DeletePedigreeTask(User user) {
    		if(user == null) {
				throw new RuntimeException("No user specified.");
			}
    		this.user = user;
    	}
    	
		@Override
		protected Boolean doInBackground(Integer... params) {
			try {
				FamilyData data = MyPedigreesActivity.this.app.getFamilyData();
				MyPedigreesActivity.this.pedigrees = data.deletePedigree(this.user, params[0]);
				return true;
			}
			catch(FamilyDataException e) {
				this.error = e.getMessage();
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(final Boolean success) {
			deletePedigreeTask = null;

			if (success) {
				MyPedigreesActivity.this.refreshList();
			} 
			else {
				String title = getResources().getString(R.string.alert_title_deletion_failed);
				String message = this.error;
				int icon = R.drawable.ic_error;
				AlertDialog dialog = MyPedigreesActivity.this.getAlert(MyPedigreesActivity.this, message, title, icon);
				dialog.show();
			}
			
			MyPedigreesActivity.this.toggleView(false, progressView, contentListView);
		}

		@Override
		protected void onCancelled() {
			deletePedigreeTask = null;
			MyPedigreesActivity.this.toggleView(false, progressView, contentListView);
		}
    }

    private class PedigreesListActionModeCallback implements ActionMode.Callback {

    	private int selectedPosition;
    	private Pedigree selectedPedigree;
    	
    	@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
    		this.selectedPosition = MyPedigreesActivity.this.selectedPosition;
    		this.selectedPedigree = (Pedigree)contentListView.getItemAtPosition(this.selectedPosition);
			mode.setTitle(this.selectedPedigree.getTitle());
			mode.getMenuInflater().inflate(R.menu.my_pedigrees_context_menu, menu);
			return true;
		}
    	
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch(item.getItemId()) {
				case R.id.context_menu_view:
					Toast.makeText(MyPedigreesActivity.this, "View", Toast.LENGTH_SHORT).show();
				break;
				case R.id.context_menu_edit:
					Toast.makeText(MyPedigreesActivity.this, "Edit", Toast.LENGTH_SHORT).show();
				break;
				case R.id.context_menu_delete:
					Pedigree selectedPedigree = (Pedigree)contentListView.getItemAtPosition(this.selectedPosition);
					getPedigreeDeletionAlert(selectedPedigree.getTitle()).show();
					mode.finish();
				break;
			}
			return true;
		}

		@SuppressWarnings("deprecation")
		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mode = null;
			// Deselect the selected item
			contentListView.getChildAt(this.selectedPosition)
			.setBackgroundDrawable(contentListView.getBackground());
			MyPedigreesActivity.this.contentListView.setItemChecked(this.selectedPosition, false);
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}
    }
    
    private class PedigreeItemClickListener implements AdapterView.OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			if(cabMode != null) {
				cabMode.finish();
			}
			MyPedigreesActivity.this.selectedPosition = position;
			MyPedigreesActivity.this.contentListView.setItemChecked(position, true);
			view.setBackgroundResource(R.color.holo_blue_bright);
			cabMode = startActionMode(new PedigreesListActionModeCallback());
			return true;
	    }
    	
    }
    
}
