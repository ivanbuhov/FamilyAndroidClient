package com.buhov.family.FamilyHttpClient.Entities;

import java.sql.Date;

public class Person {
	private int id;
	private String displayName;
	private String firstName;
	private String middleName;
	private String lastName;
	private String nickname;
	private String email;
	private Date birthDate;
	private boolean isAlive;
	private boolean isMale;
	private String address;
	private String profession;
	private int pedigreeId;
	private int firstParentId;
	private int secondParentId;
	private int spouseId;
	
	public int getId() {
		return this.id;
	}
	public String getDisplayName() {
		return this.displayName;
	}
	public String getFirstName() {
		return this.firstName;
	}
	public String getMiddleName() {
		return this.middleName;
	}
	public String getLastName() {
		return this.lastName;
	}
	public String getNickname() {
		return this.nickname;
	}
	public String getEmail() {
		return this.email;
	}
	public Date getBirthDate() {
		return this.birthDate;
	}
	public boolean isAlive() {
		return this.isAlive;
	}
	public boolean isMale() {
		return this.isMale;
	}
	public String getAddress() {
		return this.address;
	}
	public String getProfession() {
		return this.profession;
	}
	public int getPedigreeId() {
		return this.pedigreeId;
	}
	public int getFirstParentId() {
		return this.firstParentId;
	}
	public int getSecondParentId() {
		return this.secondParentId;
	}
	public int getSpouseId() {
		return this.spouseId;
	}
}
