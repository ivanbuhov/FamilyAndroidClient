package com.buhov.family.FamilyData;

import java.util.Date;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.buhov.family.FamilyApplication;
import com.buhov.family.MyPedigreesActivity;
import com.buhov.family.R;
import com.buhov.family.LoginActivity;
import com.buhov.family.Utils;
import com.buhov.family.FamilyDb.FamilyDb;
import com.buhov.family.FamilyDb.FamilyDbException;
import com.buhov.family.FamilyDb.FamilyDbHelper;
import com.buhov.family.FamilyHttpClient.FamilyHttpClient;
import com.buhov.family.FamilyHttpClient.FamilyServiceException;
import com.buhov.family.FamilyHttpClient.Entities.Pedigree;
import com.buhov.family.FamilyHttpClient.Entities.User;
import com.buhov.family.FamilyHttpClient.Entities.UserDTO;

public class FamilyData {
	
	private FamilyApplication app;
	private FamilyDb db;
	private FamilyHttpClient httpClient;
	private UpdatesLogger updateLogger;
	
	public FamilyData(Context context, FamilyDb db, FamilyHttpClient httpClient) {
		this.app = (FamilyApplication)context.getApplicationContext();
		this.db = db;
		this.httpClient = httpClient;
		this.updateLogger = new UpdatesLogger();
	}
	
	public User register(UserDTO user) {
		try {
			User registeredUser = this.httpClient.register(user);
			this.db.addUser(registeredUser);
			return registeredUser;
		}
		catch(FamilyServiceException e) {
			throw new FamilyDataException(e.getMessage());
		}
	}
	
	public User login(UserDTO user) {
		User loggedUser;
		try {
			User inputUser = Utils.encryptUser(user);
			loggedUser = this.db.getUserOrDefault(inputUser);
			if(loggedUser == null) {
				loggedUser = this.httpClient.login(user);
				this.db.addUser(loggedUser);
			}
			return loggedUser;
		}
		catch(FamilyServiceException e) {
			throw new FamilyDataException(e.getMessage());
		}
	}
	
	public Pedigree[] getPedigrees(User user) {
		Pedigree[] pedigrees;
		
		try {
			if(this.updateLogger.shouldPedigreeListBeUpdated(user.getId()) && this.app.isNetworkConnected()) {
				pedigrees = this.httpClient.getPedigrees(user);
				this.db.update(user.getId(), pedigrees);
				this.updateLogger.pedigreeListUpdated(user.getId());
				return pedigrees;
			}
			else {
				pedigrees = this.db.getPedigrees(user.getId());
			}
		} catch(FamilyServiceException e) {
			throw new FamilyDataException(e.getMessage());
		} catch(FamilyDbException e) {
			throw new FamilyDataException("An error ocured while fetching the data from the database. Please try again.");
		}
		
		return pedigrees;
	}
	
	private class UpdatesLogger {

		private HashMap<Integer, Long> pedigreeLists; 
		private HashMap<Integer, Long> personLists;

		public UpdatesLogger() {
			this.pedigreeLists = new HashMap<Integer, Long>();
			this.personLists = new HashMap<Integer, Long>();
		}

		private Boolean shouldPedigreeListBeUpdated(Integer ownerId) {
			if(this.pedigreeLists.containsKey(ownerId)) {
				long maxSecondsWithoutUpdate = PreferenceManager.getDefaultSharedPreferences(app).getLong(
						app.getResources().getString(R.string.pref_key_update_if_not_updated_more_than), 600);
				
				long lastUpdated = this.pedigreeLists.get(ownerId);
				long now = (new Date()).getTime();
				long secondsElapsed = (now - lastUpdated) / 1000;
				if(secondsElapsed < maxSecondsWithoutUpdate) {
					return false;
				}
			}
			
			return true;
		}

		private Boolean shouldPersonListBeUpdated(Integer pedigreeId) {
			if(this.personLists.containsKey(pedigreeId)) {
				long maxSecondsWithoutUpdate = PreferenceManager.getDefaultSharedPreferences(app).getLong(
						app.getResources().getString(R.string.pref_key_update_if_not_updated_more_than), 600);
				
				long lastUpdated = this.personLists.get(pedigreeId);
				long now = (new Date()).getTime();
				long secondsElapsed = (now - lastUpdated) / 1000;
				if(secondsElapsed < maxSecondsWithoutUpdate) {
					return false;
				}
			}
			
			return true;
		}

		private void pedigreeListUpdated(Integer ownerId) {
			long now = (new Date()).getTime();
			this.pedigreeLists.put(ownerId, now);
		}

		private void personListUpdated(Integer pedigreeId) {
			long now = (new Date()).getTime();
			this.personLists.put(pedigreeId, now);
		}
	}
}
