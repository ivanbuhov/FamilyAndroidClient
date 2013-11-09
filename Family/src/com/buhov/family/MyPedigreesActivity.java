package com.buhov.family;

import com.buhov.family.FamilyHttpClient.Entities.*;

import android.os.Bundle;
import android.view.*;

public class MyPedigreesActivity extends BaseActivity {

	private View contentView;
	private View progressView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pedigrees);
        this.progressView = findViewById(R.id.progress_my_pedigrees);
        this.contentView = findViewById(R.id.content_my_pedigrees);
        
        // Getting the pedigrees
        this.attemptGetMyPedigrees();
    }

    public void attemptGetMyPedigrees() {
    	MyPedigreesActivity.this.showProgress(true, contentView, progressView);
    	User currentUser = this.app.getLoggedUser();
    	//this.myPedigreesTask = new GetMyPedigreesTask();
    	//this.myPedigreesTask.execute(currentUser);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_pedigrees, menu);
        return true;
    }
    
}
