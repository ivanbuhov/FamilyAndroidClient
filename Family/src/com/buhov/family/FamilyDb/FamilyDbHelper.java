package com.buhov.family.FamilyDb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class FamilyDbHelper extends SQLiteOpenHelper {

	static final String DB_NAME = "family.db";
	static final int DB_VERSION = 1;
	
	static final String TABLE_NAME_USERS = "users";
	static final String TABLE_NAME_PEDIGREES = "pedigrees";
	static final String TABLE_NAME_PEOPLE = "people";
	
	static final String C_ID = BaseColumns._ID;
	static final String C_USERS_USERNAME = "username";
	static final String C_USERS_AUTHCODE = "authcode";
	
	static final String C_PEDIGREES_TITLE = "title";
	static final String C_PEDIGREES_OWNERID = "ownerId";
	
	static final String C_PEOPLE_DISPLAYNAME = "displayName";
	static final String C_PEOPLE_FIRSTNAME = "firstName";
	static final String C_PEOPLE_MIDDLENAME = "middleName";
	static final String C_PEOPLE_LASTNAME = "lastName";
	static final String C_PEOPLE_NICKNAME = "nickname";
	static final String C_PEOPLE_EMAIL = "email";
	static final String C_PEOPLE_BIRTHDATE = "birthdate";
	static final String C_PEOPLE_ISALIVE = "isAlive";
	static final String C_PEOPLE_ISMALE = "isMale";
	static final String C_PEOPLE_ADDRESS = "address";
	static final String C_PEOPLE_PROFESSION = "profession";
	static final String C_PEOPLE_PEDIGREEID = "pedigreeId";
	static final String C_PEOPLE_FIRSTPARENTID = "firstParentId";
	static final String C_PEOPLE_SECONDPARENTID = "secondParentId";
	static final String C_PEOPLE_SPOUSEID = "spouseId";
	
	public FamilyDbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		String usersTableSql = "create table " + TABLE_NAME_USERS + " ( "
				+ C_ID + " INT PRIMARY KEY NOT NULL, "
				+ C_USERS_USERNAME + " NVARCHAR(30), "
				+ C_USERS_AUTHCODE + " NCHAR(40) ); ";
		
		String pedigreesTableSql = "create table " + TABLE_NAME_PEDIGREES + " ( "
				+ C_ID + " INT PRIMARY KEY NOT NULL, "
				+ C_PEDIGREES_TITLE + " NVARCHAR(30), "
				+ C_PEDIGREES_OWNERID + " INT NOT NULL ); ";
		
		String peopleTableSql = "create table " + TABLE_NAME_PEOPLE + " ( "
				+ C_ID + " INT PRIMARY KEY NOT NULL, "
				+ C_PEOPLE_DISPLAYNAME + " NVARCHAR(30), "
				+ C_PEOPLE_FIRSTNAME + " NVARCHAR(30), "
				+ C_PEOPLE_MIDDLENAME + " NVARCHAR(30), "
				+ C_PEOPLE_LASTNAME + " NVARCHAR(30), "
				+ C_PEOPLE_NICKNAME + " NVARCHAR(30), "
				+ C_PEOPLE_EMAIL + " NVARCHAR(50), "
				+ C_PEOPLE_BIRTHDATE + " DATE, "
				+ C_PEOPLE_ISALIVE + " BOOLEAN, "
				+ C_PEOPLE_ISMALE + " BOOLEAN, "
				+ C_PEOPLE_ADDRESS + " NVARCHAR(100), "
				+ C_PEOPLE_PROFESSION + " NVARCHAR(50), "
				+ C_PEOPLE_PEDIGREEID + " INT, "
				+ C_PEOPLE_FIRSTPARENTID + " INT, "
				+ C_PEOPLE_SECONDPARENTID + " INT, "
				+ C_PEOPLE_SPOUSEID + " INT ); ";
		
		database.execSQL(usersTableSql);
		database.execSQL(pedigreesTableSql);
		database.execSQL(peopleTableSql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		String dropSQL = "DROP TABLE IF EXISTS " + TABLE_NAME_PEOPLE + "; "
				+ "DROP TABLE IF EXISTS " + TABLE_NAME_PEDIGREES + "; "
				+ "DROP TABLE IF EXISTS " + TABLE_NAME_USERS + "; ";
		
		database.execSQL(dropSQL);
		this.onCreate(database);
		
	}
}
