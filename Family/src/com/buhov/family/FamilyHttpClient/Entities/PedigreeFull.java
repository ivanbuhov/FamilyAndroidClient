package com.buhov.family.FamilyHttpClient.Entities;

public class PedigreeFull {
	
	private int id;
	private String title;
	private int ownerId;
	private Person[] people;
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public int getOwnerId() {
		return this.ownerId;
	}
	
	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}

	public Person[] getPeople() {
		return this.people;
	}

	public void setPeople(Person[] people) {
		this.people = people;
	}
}
