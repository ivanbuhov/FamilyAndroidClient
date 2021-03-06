package com.buhov.family.FamilyHttpClient.Entities;

public class Person {
	private int id;
	private String displayName;
	private String firstName;
	private String middleName;
	private String lastName;
	private String nickname;
	private String email;
	private String birthDate;
	private boolean isAlive;
	private boolean isMale;
	private String address;
	private String profession;
	private int pedigreeId;
	private int firstParentId;
	private int secondParentId;
	private int spouseId;
	
	// Getters
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
	public String getBirthDate() {
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
	
	// Setters
	public void setId(int id) {
		this.id = id;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}
	public void isAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}
	public void isMale(boolean isMale) {
		this.isMale = isMale;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setProfession(String profession) {
		this.profession = profession;
	}
	public void setPedigreeId(int pedigreeId) {
		this.pedigreeId = pedigreeId;
	}
	public void setFirstParentId(int firstParentId) {
		this.firstParentId = firstParentId;
	}
	public void setSecondParentId(int secondParentId) {
		this.secondParentId = secondParentId;
	}
	public void setSpouseId(int spouseId) {
		this.spouseId = spouseId;
	}
	
	@Override
	public String toString() {
		return this.displayName.toString();
	}
	
}
