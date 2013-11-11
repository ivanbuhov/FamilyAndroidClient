package com.buhov.family.FamilyDb;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.buhov.family.FamilyHttpClient.Entities.Pedigree;
import com.buhov.family.FamilyHttpClient.Entities.User;

public class FamilyDb {
	
	private FamilyDbHelper helper;
	
	public FamilyDb(FamilyDbHelper helper) {
		this.helper = helper;
	}
	
	public void Update(int userId, Pedigree[] pedigrees) {
		SQLiteDatabase db = this.helper.getWritableDatabase();
		
		// Delete all pedigrees from the current user
		db.delete(FamilyDbHelper.TABLE_NAME_PEDIGREES, FamilyDbHelper.C_PEDIGREES_OWNERID + "=" + userId, null);
		// Loop over the pedigrees and save them to the database
		ContentValues values = new ContentValues();
		for (Pedigree pedigree : pedigrees) {
			// Insert into database
			values.clear();
			values.put(FamilyDbHelper.C_ID, pedigree.getId());
			values.put(FamilyDbHelper.C_PEDIGREES_TITLE, pedigree.getTitle());
			values.put(FamilyDbHelper.C_PEDIGREES_OWNERID, pedigree.getOwnerId());
			db.insertOrThrow(FamilyDbHelper.TABLE_NAME_PEDIGREES, null, values);
		}
		
		db.close();
	}

	public void addUser(User user) {
		SQLiteDatabase db = null;
		try {
			db = this.helper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(FamilyDbHelper.C_ID, user.getId());
			values.put(FamilyDbHelper.C_USERS_USERNAME, user.getUsername());
			values.put(FamilyDbHelper.C_USERS_AUTHCODE, user.getAuthCode());
			db.insertOrThrow(FamilyDbHelper.TABLE_NAME_USERS, null, values);
		} finally {
			if(db != null) {
				db.close();
			}
		}
	}
	
	public User getUserOrDefault(User user) {
		SQLiteDatabase db = null;
		try {
			db = this.helper.getReadableDatabase();
			Cursor cursor = db.query(FamilyDbHelper.TABLE_NAME_USERS, new String[] { 
					FamilyDbHelper.C_ID,
					FamilyDbHelper.C_USERS_USERNAME,
					FamilyDbHelper.C_USERS_AUTHCODE }, 
					FamilyDbHelper.C_USERS_USERNAME + "=? AND " + FamilyDbHelper.C_USERS_AUTHCODE + "=?", 
					new String[] { user.getUsername(), user.getAuthCode() }, null, null, null);
			if(cursor.moveToNext()) {
				int id = cursor.getInt(0);
				String username = cursor.getString(1);
				String authCode = cursor.getString(2);
				User result = new User(username, authCode);
				result.setId(id);
				return result;
			}
			
			return null;
		} finally {
			if(db != null) {
				db.close();
			}
		}
	}

}
