package com.buhov.family.FamilyDb;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.buhov.family.FamilyHttpClient.Entities.Pedigree;
import com.buhov.family.FamilyHttpClient.Entities.PedigreeFull;
import com.buhov.family.FamilyHttpClient.Entities.Person;
import com.buhov.family.FamilyHttpClient.Entities.User;

public class FamilyDb {
	
	private FamilyDbHelper helper;
	
	public FamilyDb(FamilyDbHelper helper) {
		this.helper = helper;
	}
	
	public void update(int userId, Pedigree[] pedigrees) {
		SQLiteDatabase db = null;
		try {
			db = this.helper.getWritableDatabase();
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
		} finally {
			if(db != null) {
				db.close();
			}
		}
	}

	public void update(PedigreeFull pedigree) {
		SQLiteDatabase db = null;
		try {
			db = this.helper.getWritableDatabase();
			
			// Update the pedigree info
			ContentValues pedigreeValues = new ContentValues();
			pedigreeValues.put(FamilyDbHelper.C_ID, pedigree.getId());
			pedigreeValues.put(FamilyDbHelper.C_PEDIGREES_TITLE, pedigree.getTitle());
			pedigreeValues.put(FamilyDbHelper.C_PEDIGREES_OWNERID, pedigree.getOwnerId());
			db.insertWithOnConflict(FamilyDbHelper.TABLE_NAME_PEDIGREES, null, pedigreeValues, SQLiteDatabase.CONFLICT_REPLACE);
			
			// Delete all people from the current pedigree
			db.delete(FamilyDbHelper.TABLE_NAME_PEOPLE, FamilyDbHelper.C_PEOPLE_PEDIGREEID + "=" + pedigree.getId(), null);
			// Loop over the people and save them to the database
			ContentValues values = new ContentValues();
			for (Person person : pedigree.getPeople()) {
				// Insert into database
				values.clear();
				values.put(FamilyDbHelper.C_ID, person.getId());
				values.put(FamilyDbHelper.C_PEOPLE_DISPLAYNAME, person.getDisplayName());
				values.put(FamilyDbHelper.C_PEOPLE_FIRSTNAME, person.getFirstName());
				values.put(FamilyDbHelper.C_PEOPLE_MIDDLENAME, person.getMiddleName());
				values.put(FamilyDbHelper.C_PEOPLE_LASTNAME, person.getLastName());
				values.put(FamilyDbHelper.C_PEOPLE_NICKNAME, person.getNickname());
				values.put(FamilyDbHelper.C_PEOPLE_EMAIL, person.getEmail());
				values.put(FamilyDbHelper.C_PEOPLE_BIRTHDATE, person.getBirthDate());
				values.put(FamilyDbHelper.C_PEOPLE_ISALIVE, person.isAlive());
				values.put(FamilyDbHelper.C_PEOPLE_ISMALE, person.isMale());
				values.put(FamilyDbHelper.C_PEOPLE_ADDRESS, person.getAddress());
				values.put(FamilyDbHelper.C_PEOPLE_PROFESSION, person.getProfession());
				values.put(FamilyDbHelper.C_PEOPLE_PEDIGREEID, person.getPedigreeId());
				values.put(FamilyDbHelper.C_PEOPLE_FIRSTPARENTID, person.getFirstParentId());
				values.put(FamilyDbHelper.C_PEOPLE_SECONDPARENTID, person.getSecondParentId());
				values.put(FamilyDbHelper.C_PEOPLE_SPOUSEID, person.getSpouseId());

				db.insertOrThrow(FamilyDbHelper.TABLE_NAME_PEOPLE, null, values);
			}
		} finally {
			if(db != null) {
				db.close();
			}
		}
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
	
	public Pedigree[] getPedigrees(Integer ownerId) {
		SQLiteDatabase db = null;
		try {
			db = this.helper.getReadableDatabase();
			Cursor cursor = db.query(FamilyDbHelper.TABLE_NAME_PEDIGREES, new String[] { 
					FamilyDbHelper.C_ID,
					FamilyDbHelper.C_PEDIGREES_TITLE,
					FamilyDbHelper.C_PEDIGREES_OWNERID }, 
					FamilyDbHelper.C_PEDIGREES_OWNERID + "=?", 
					new String[] { ownerId.toString() }, null, null, null);
			Pedigree[] pedigrees = new Pedigree[cursor.getCount()];
			int currentIndex = 0;
			while(cursor.moveToNext()) {
				int pedigreeId = cursor.getInt(0);
				String pedigreeTitle = cursor.getString(1);
				int pedigreeOwnerId = cursor.getInt(2);
				pedigrees[currentIndex] = new Pedigree(pedigreeId, pedigreeTitle, pedigreeOwnerId);
				currentIndex++;
			}
			
			return pedigrees;
		} finally {
			if(db != null) {
				db.close();
			}
		}
	}
	
	public PedigreeFull getPedigree(Integer pedigreeId) {
		SQLiteDatabase db = null;
		try {
			db = this.helper.getReadableDatabase();
			PedigreeFull pedigree = new PedigreeFull();
			Cursor pedigreeCursor = db.query(FamilyDbHelper.TABLE_NAME_PEDIGREES, new String[] { 
					FamilyDbHelper.C_ID,
					FamilyDbHelper.C_PEDIGREES_TITLE,
					FamilyDbHelper.C_PEDIGREES_OWNERID },
					FamilyDbHelper.C_ID + "=?",
					new String[] { pedigreeId.toString() }, null, null, null);
			if(pedigreeCursor.moveToNext()) {
				pedigree.setId(pedigreeCursor.getInt(0));
				pedigree.setTitle(pedigreeCursor.getString(1));
				pedigree.setOwnerId(pedigreeCursor.getInt(2));
			}
			else {
				throw new FamilyDbException("No pedigree found.");
			}
			
			Cursor cursor = db.query(FamilyDbHelper.TABLE_NAME_PEOPLE, new String[] {
					FamilyDbHelper.C_ID,
					FamilyDbHelper.C_PEOPLE_DISPLAYNAME,
					FamilyDbHelper.C_PEOPLE_FIRSTNAME,
					FamilyDbHelper.C_PEOPLE_MIDDLENAME,
					FamilyDbHelper.C_PEOPLE_LASTNAME,
					FamilyDbHelper.C_PEOPLE_NICKNAME,
					FamilyDbHelper.C_PEOPLE_EMAIL,
					FamilyDbHelper.C_PEOPLE_BIRTHDATE,
					FamilyDbHelper.C_PEOPLE_ISALIVE,
					FamilyDbHelper.C_PEOPLE_ISMALE,
					FamilyDbHelper.C_PEOPLE_ADDRESS,
					FamilyDbHelper.C_PEOPLE_PROFESSION,
					FamilyDbHelper.C_PEOPLE_PEDIGREEID,
					FamilyDbHelper.C_PEOPLE_FIRSTPARENTID,
					FamilyDbHelper.C_PEOPLE_SECONDPARENTID,
					FamilyDbHelper.C_PEOPLE_SPOUSEID }, 
					FamilyDbHelper.C_PEOPLE_PEDIGREEID + "=?", 
					new String[] { pedigreeId.toString() }, null, null, null);
			Person[] people = new Person[cursor.getCount()];
			int currentIndex = 0;
			while(cursor.moveToNext()) {
				Person current = new Person();
				current.setId(cursor.getInt(0));
				current.setDisplayName(cursor.getString(1));
				current.setFirstName(cursor.getString(2));
				current.setMiddleName(cursor.getString(3));
				current.setLastName(cursor.getString(4));
				current.setNickname(cursor.getString(5));
				current.setEmail(cursor.getString(6));
				current.setBirthDate(cursor.getString(7));
				current.isAlive(cursor.getInt(8) > 0);
				current.isMale(cursor.getInt(9) > 0);
				current.setAddress(cursor.getString(10));
				current.setProfession(cursor.getString(11));
				current.setPedigreeId(cursor.getInt(12));
				current.setFirstParentId(cursor.getInt(13));
				current.setSecondParentId(cursor.getInt(14));
				current.setSpouseId(cursor.getInt(15));
				people[currentIndex] = current;
				currentIndex++;
			}
			
			pedigree.setPeople(people);
			return pedigree;
		} finally {
			if(db != null) {
				db.close();
			}
		}
	}
	
	public void deletePedigree(User user, Integer pedigreeId) {
		SQLiteDatabase db = null;
		try {
			db = this.helper.getWritableDatabase();
			// Delete all people from the pedigree
			db.delete(FamilyDbHelper.TABLE_NAME_PEOPLE, FamilyDbHelper.C_PEOPLE_PEDIGREEID + "=?", new String[] { pedigreeId.toString() });
			// Delete the pedigree
			db.delete(FamilyDbHelper.TABLE_NAME_PEDIGREES, FamilyDbHelper.C_ID + "=?", new String[] { pedigreeId.toString() });
		} finally {
			if(db != null) {
				db.close();
			}
		}
	}
}
