package com.buhov.family.FamilyHttpClient.Entities;

public class PersonDTO {
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
	
	// Getters
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
	
	// Setters
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
	
	@Override
	public String toString() {
		return this.displayName.toString();
	}
	
}
