package com.buhov.family;

import com.buhov.family.PedigreeNode.PersonNode;
import com.buhov.family.FamilyData.FamilyData;
import com.buhov.family.FamilyData.FamilyDataException;
import com.buhov.family.FamilyHttpClient.Entities.Pedigree;
import com.buhov.family.FamilyHttpClient.Entities.PedigreeFull;
import com.buhov.family.FamilyHttpClient.Entities.Person;
import com.buhov.family.FamilyHttpClient.Entities.User;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

public class PedigreeActivity extends BaseActivity {
	
	public static final String EXTRA_PEDIGREE_ID = "EXTRA_PEDIGREE_ID";
	public static final String EXTRA_PEDIGREE_TITLE = "EXTRA_PEDIGREE_TITLE";
	public static final String EXTRA_PEDIGREE_OWNERID = "EXTRA_PEDIGREE_OWNERID";
	
	public static final String STATE_KEY_TREE_MODE = "STATE_KEY_TREE_MODE";
	public static final String STATE_KEY_PERSON_ID = "STATE_KEY_PERSON_ID";

	private boolean treeMode = false;
	private int previousPersonIndex = 0;
	
	// Menu buttons
	private MenuItem btnPeopleList;
	
	// ActionMode
	private ActionMode cabMode = null;
	private int selectedPosition = -1;
	
	private Pedigree pedigreeFromIntent;
	private PedigreeFull pedigreeFull;
	private PedigreeNode pedigreeNode;
	
	private View progressView;
	private ListView peopleListView;
	private ArrayAdapter<Person> peopleAdapter;
	private FrameLayout pedigreeFrameLayout;
	private PedigreeView pedigreeView;
	
	private GetPedigreeTask getPedigreeTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.initPedigreeFromIntent();
		this.setTitle(this.pedigreeFromIntent.getTitle());
		// Initiaizes views
		setContentView(R.layout.activity_pedigree);
		this.progressView = findViewById(R.id.progress_pedigree);
		this.peopleListView = (ListView) findViewById(R.id.people_list);
		this.pedigreeFrameLayout = (FrameLayout)findViewById(R.id.pedigree_tree);
		// Initializes the PedigreeView
		this.pedigreeView = new PedigreeView(this, null);
		this.pedigreeView.setAnonymousDisplayName(getResources().getString(R.string.anonymous_display_name));
		this.pedigreeView.setNoPeopleMessage(getResources().getString(R.string.no_people_message));
		this.pedigreeFrameLayout.addView(this.pedigreeView);
		// Restore the state of the activity (for example if the activity was rotated)
		if (savedInstanceState != null) {
			boolean startTreeMode = savedInstanceState.getBoolean(STATE_KEY_TREE_MODE, false);
			this.previousPersonIndex = savedInstanceState.getInt(STATE_KEY_PERSON_ID, 0);
			if(startTreeMode) {
				this.startTreeMode();
			}
	    }
		// Register touch events
		this.peopleListView.setOnItemClickListener(new PersonItemClickListener());
		this.peopleListView.setOnItemLongClickListener(new PersonItemLongClickListener());
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
		this.btnPeopleList = menu.findItem(R.id.menu_item_list);
		
		this.adjustOptionsMenu();
		return true;
	}
	
	private void adjustOptionsMenu() {
		if(this.btnPeopleList != null) {
			this.btnPeopleList.setVisible(this.treeMode);
			this.invalidateOptionsMenu();
		}
	}
	
	@Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	switch(item.getItemId()){
			case R.id.menu_item_refresh:
				this.attemptGetPedigree(true);
			break;
			case R.id.menu_item_list:
				this.endTreeMode();
			break;
			default:
				return super.onMenuItemSelected(featureId, item);
		}
    	
    	return true;
    }

	@Override
	protected void onSaveInstanceState (Bundle outState) {
	    super.onSaveInstanceState(outState);
	    if(this.treeMode) {
		    outState.putBoolean(STATE_KEY_TREE_MODE, this.treeMode);
		    outState.putInt(STATE_KEY_PERSON_ID, this.pedigreeView.getPersonId());
	    }
	}
	
	private void refreshActivity() {
		this.pedigreeNode = new PedigreeNode(this.pedigreeFull);
		this.setTitle(pedigreeFull.getTitle());
		this.refreshList();
		if(this.treeMode) {
			this.refreshTree();
		}
	}
	
	private void refreshList() {
		this.peopleAdapter = new ArrayAdapter<Person>(this, android.R.layout.simple_list_item_1, 
				android.R.id.text1, this.pedigreeFull.getPeople());
		this.peopleListView.setAdapter(this.peopleAdapter);
	}
	
	private void refreshTree() {
		int currentViewId = (this.pedigreeView.hasPersonNode()) ? this.pedigreeView.getPersonId() : this.previousPersonIndex;
		PersonNode person = this.pedigreeNode.getPerson(currentViewId);
		this.pedigreeView.setPersonNode(person);
	}
	
	private void startTreeModeWith(PersonNode person) {
		this.pedigreeView.setPersonNode(person);
		this.startTreeMode();
	}
	
	private void startTreeMode() {
		this.toggleView(true, this.pedigreeFrameLayout, this.peopleListView);
		this.treeMode = true;
		this.adjustOptionsMenu();
	}
	
	private void endTreeMode() {
		this.toggleView(true, this.peopleListView, this.pedigreeFrameLayout);
		this.treeMode = false;
		this.adjustOptionsMenu();
	}
	
	private void attemptGetPedigree(boolean obligatoryServerUpdate) {
		if (this.getPedigreeTask != null) {
			return;
		}
    	
    	if(this.app.getLoginManager().hasLoggedUser()) {
        	PedigreeActivity.this.toggleView(true, progressView, null);
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
				PedigreeActivity.this.refreshActivity();
			}
			else {
				String title = getResources().getString(R.string.alert_title_loading_data_failed);
				String message = this.error;
				int icon = R.drawable.ic_error;
				AlertDialog dialog = PedigreeActivity.this.getAlert(PedigreeActivity.this, message, title, icon);
				dialog.show();
			}
			
			PedigreeActivity.this.toggleView(false, progressView, null);
		}

		@Override
		protected void onCancelled() {
			getPedigreeTask = null;
			PedigreeActivity.this.toggleView(false, progressView, null);
		}
	}

	private class PeopleListActionModeCallback implements ActionMode.Callback {

    	private int selectedPosition;
    	private Person selectedPerson;
    	
    	@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
    		this.selectedPosition = PedigreeActivity.this.selectedPosition;
    		this.selectedPerson = (Person)peopleListView.getItemAtPosition(this.selectedPosition);
			mode.setTitle(this.selectedPerson.getDisplayName());
			mode.getMenuInflater().inflate(R.menu.pedigree_context_menu, menu);
			return true;
		}
    	
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			Person selectedPerson = (Person)peopleListView.getItemAtPosition(this.selectedPosition);
			
			switch(item.getItemId()) {
				case R.id.context_menu_view:
					// PedigreeActivity.this.attemptViewPedigree(selectedPerson);
					Toast.makeText(PedigreeActivity.this, "View", Toast.LENGTH_SHORT).show();
					mode.finish();
				break;
				case R.id.context_menu_edit:
					// PedigreeActivity.this.showEditPedigreeDialog(selectedPerson);
					Toast.makeText(PedigreeActivity.this, "Edit", Toast.LENGTH_SHORT).show();
					mode.finish();
				break;
				case R.id.context_menu_delete:
					// getPedigreeDeletionAlert(selectedPerson.getDisplayName()).show();
					Toast.makeText(PedigreeActivity.this, "View", Toast.LENGTH_SHORT).show();
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
			peopleListView.getChildAt(this.selectedPosition)
			.setBackgroundDrawable(peopleListView.getBackground());
			peopleListView.setItemChecked(this.selectedPosition, false);
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}
    }
	
	private class PersonItemLongClickListener implements AdapterView.OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			if(cabMode != null) {
				cabMode.finish();
			}
			PedigreeActivity.this.selectedPosition = position;
			PedigreeActivity.this.peopleListView.setItemChecked(position, true);
			view.setBackgroundResource(R.color.holo_blue_bright);
			cabMode = startActionMode(new PeopleListActionModeCallback());
			return true;
	    }
    }
	
	private class PersonItemClickListener implements AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if(cabMode != null) {
				cabMode.finish();
			}
			Person person = (Person)peopleListView.getItemAtPosition(position);
			startTreeModeWith(pedigreeNode.getPerson(person.getId()));
		}
    }

}
