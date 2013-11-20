package com.buhov.family.FamilyData;

import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.preference.PreferenceManager;

import com.buhov.family.FamilyApplication;
import com.buhov.family.R;
import com.buhov.family.Utils;
import com.buhov.family.FamilyDb.FamilyDb;
import com.buhov.family.FamilyDb.FamilyDbException;
import com.buhov.family.FamilyHttpClient.FamilyHttpClient;
import com.buhov.family.FamilyHttpClient.FamilyServiceException;
import com.buhov.family.FamilyHttpClient.Entities.Pedigree;
import com.buhov.family.FamilyHttpClient.Entities.PedigreeDTO;
import com.buhov.family.FamilyHttpClient.Entities.PedigreeFull;
import com.buhov.family.FamilyHttpClient.Entities.PedigreeNew;
import com.buhov.family.FamilyHttpClient.Entities.PersonDTO;
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
	
	public Pedigree[] getPedigrees(User user, boolean obligatoryServerUpdate) {
		Pedigree[] pedigrees;
		
		try {
			if(obligatoryServerUpdate || (this.updateLogger.shouldPedigreeListBeUpdated(user.getId()) && this.app.isNetworkConnected())) {
				pedigrees = this.httpClient.getPedigrees(user);
				this.db.update(user.getId(), pedigrees);
				this.updateLogger.pedigreeListUpdated(user.getId());
			}
			else {
				pedigrees = this.db.getPedigrees(user.getId());
			}
		} 
		catch(FamilyServiceException e) {
			throw new FamilyDataException(e.getMessage());
		} 
		catch(FamilyDbException e) {
			throw new FamilyDataException("An error ocured while fetching the data from the database. Please try again.");
		}
		
		return pedigrees;
	}
	
	public PedigreeFull getPedigree(User user, int pedigreeId, boolean obligatoryServerUpdate) {
		PedigreeFull pedigree;
		
		try {
			if(obligatoryServerUpdate || (this.updateLogger.shouldPedigreeBeUpdated(pedigreeId) && this.app.isNetworkConnected())) {
				pedigree = this.httpClient.getPedigree(user, pedigreeId);
				this.db.update(pedigree);
				this.updateLogger.pedigreeUpdated(pedigreeId);
			}
			else {
				pedigree = this.db.getPedigree(pedigreeId);
			}
		} 
		catch(FamilyServiceException e) {
			throw new FamilyDataException(e.getMessage());
		} 
		catch(FamilyDbException e) {
			throw new FamilyDataException("An error ocured while fetching the data from the database. Please try again.");
		}
		
		return pedigree;
	}
	
	public Pedigree[] addPedigree(User user, PedigreeDTO newPedigree) {
		try {
			Pedigree[] pedigrees = this.httpClient.addPedigree(user, newPedigree);
			this.db.update(user.getId(), pedigrees);
			return pedigrees;
		}
		catch(FamilyServiceException e) {
			throw new FamilyDataException(e.getMessage());
		}
		catch(FamilyDbException e) {
			throw new FamilyDataException(e.getMessage());
		}
	}
	
	public PedigreeFull addParent(User user, int personId, PersonDTO personDTO) {
		try {
			PedigreeFull pedigree = this.httpClient.addParent(user, personId, personDTO);
			this.db.update(pedigree);
			return pedigree;
		}
		catch(FamilyServiceException e) {
			throw new FamilyDataException(e.getMessage());
		}
		catch(FamilyDbException e) {
			throw new FamilyDataException(e.getMessage());
		}
	}
	
	public PedigreeFull addChild(User user, int personId, PersonDTO personDTO) {
		try {
			PedigreeFull pedigree = this.httpClient.addChild(user, personId, personDTO);
			this.db.update(pedigree);
			return pedigree;
		}
		catch(FamilyServiceException e) {
			throw new FamilyDataException(e.getMessage());
		}
		catch(FamilyDbException e) {
			throw new FamilyDataException(e.getMessage());
		}
	}
	
	public PedigreeFull addSpouse(User user, int personId, PersonDTO personDTO) {
		try {
			PedigreeFull pedigree = this.httpClient.addSpouse(user, personId, personDTO);
			this.db.update(pedigree);
			return pedigree;
		}
		catch(FamilyServiceException e) {
			throw new FamilyDataException(e.getMessage());
		}
		catch(FamilyDbException e) {
			throw new FamilyDataException(e.getMessage());
		}
	}
	
	public Pedigree[] deletePedigree(User user, int pedigreeId) {
		try {
			Pedigree[] pedigrees = this.httpClient.deletePedigree(user, pedigreeId);
			this.db.update(user.getId(), pedigrees);
			return pedigrees;
		}
		catch(FamilyServiceException e) {
			throw new FamilyDataException(e.getMessage());
		}
		catch(FamilyDbException e) {
			throw new FamilyDataException(e.getMessage());
		}
	}
	
	public PedigreeFull deletePerson(User user, Integer personId) {
		try {
			PedigreeFull pedigree = this.httpClient.deletePerson(user, personId);
			this.db.update(pedigree);
			return pedigree;
		}
		catch(FamilyServiceException e) {
			throw new FamilyDataException(e.getMessage());
		}
		catch(FamilyDbException e) {
			throw new FamilyDataException(e.getMessage());
		}
	}
	
	public Pedigree[] updatePedigree(User user, PedigreeNew pedigreeNew) {
		try {
			Pedigree[] pedigrees = this.httpClient.updatePedigree(user, pedigreeNew);
			this.db.update(user.getId(), pedigrees);
			return pedigrees;
		}
		catch(FamilyServiceException e) {
			throw new FamilyDataException(e.getMessage());
		}
		catch(FamilyDbException e) {
			throw new FamilyDataException(e.getMessage());
		}
	}
	
	public PedigreeFull updatePerson(User user, int personId, PersonDTO personInfo) {
		try {
			PedigreeFull pedigree = this.httpClient.updatePerson(user, personId, personInfo);
			this.db.update(pedigree);
			return pedigree;
		}
		catch(FamilyServiceException e) {
			throw new FamilyDataException(e.getMessage());
		}
		catch(FamilyDbException e) {
			throw new FamilyDataException(e.getMessage());
		}
	}
	
	private class UpdatesLogger {

		private HashMap<Integer, Long> pedigreeLists; 
		private HashMap<Integer, Long> pedigrees;

		public UpdatesLogger() {
			this.pedigreeLists = new HashMap<Integer, Long>();
			this.pedigrees = new HashMap<Integer, Long>();
		}

		private Boolean shouldPedigreeListBeUpdated(Integer ownerId) {
			if(this.pedigreeLists.containsKey(ownerId)) {
				String prefKey = app.getResources().getString(R.string.pref_key_update_if_not_updated_more_than);
				String maxSecondsWithoutUpdateAsString = PreferenceManager.getDefaultSharedPreferences(app).getString(prefKey, "600");
				long maxSecondsWithoutUpdate = Long.parseLong(maxSecondsWithoutUpdateAsString);
				
				long lastUpdated = this.pedigreeLists.get(ownerId);
				long now = (new Date()).getTime();
				long secondsElapsed = (now - lastUpdated) / 1000;
				if(secondsElapsed < maxSecondsWithoutUpdate) {
					return false;
				}
			}
			
			return true;
		}

		private Boolean shouldPedigreeBeUpdated(Integer pedigreeId) {
			if(this.pedigrees.containsKey(pedigreeId)) {
				String prefKey = app.getResources().getString(R.string.pref_key_update_if_not_updated_more_than);
				String maxSecondsWithoutUpdateAsString = PreferenceManager.getDefaultSharedPreferences(app).getString(prefKey, "600");
				long maxSecondsWithoutUpdate = Long.parseLong(maxSecondsWithoutUpdateAsString);
				
				long lastUpdated = this.pedigrees.get(pedigreeId);
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

		private void pedigreeUpdated(Integer pedigreeId) {
			long now = (new Date()).getTime();
			this.pedigrees.put(pedigreeId, now);
		}
	}

}
