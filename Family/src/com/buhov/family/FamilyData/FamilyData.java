package com.buhov.family.FamilyData;

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
	
	public FamilyData(Context context, FamilyDb db, FamilyHttpClient httpClient) {
		this.app = (FamilyApplication)context.getApplicationContext();
		this.db = db;
		this.httpClient = httpClient;
	}
	
	public User Register(UserDTO user) {
		try {
			User registeredUser = this.httpClient.Register(user);
			this.db.addUser(registeredUser);
			return registeredUser;
		}
		catch(FamilyServiceException e) {
			throw new FamilyDataException(e.getMessage());
		}
	}
	
	public User Login(UserDTO user) {
		User loggedUser;
		try {
			User inputUser = Utils.encryptUser(user);
			loggedUser = this.db.getUserOrDefault(inputUser);
			if(loggedUser == null) {
				loggedUser = this.httpClient.Login(user);
			}
			return loggedUser;
		}
		catch(FamilyServiceException e) {
			throw new FamilyDataException(e.getMessage());
		}
	}
	
	/*
	public Pedigree[] GetPedigrees(User user) {
		Pedigree[] pedigrees;
		if(this.app.isOnline()) {
			try {
				pedigrees = this.httpClient.GetPedigrees(user);
				this.db.Update(user.getId(), pedigrees);
				return pedigrees;
			} catch(FamilyServiceException e) {
				throw new FamilyDataException(e.getMessage());
			} catch(FamilyDbException e) {}
		}
		else {
			try {
				pedigrees = this.db.GetPedigrees(user);
			}
			catch(FamilyDbException e) {
				throw new FamilyDataException(e.getMessage());
			}
		}
		
		return pedigrees;
	}
	*/
}
