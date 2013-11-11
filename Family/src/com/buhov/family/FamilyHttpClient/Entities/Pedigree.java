package com.buhov.family.FamilyHttpClient.Entities;

import java.util.List;

public class Pedigree {
	
	private int id;
	private String title;
	private int ownerId;
	private List<Person> people;
	
	public Pedigree(int id, String title, int ownerId) {
		this.id = id;
		this.title = title;
		this.ownerId = ownerId;
	}
	
	public int getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public int getOwnerId() {
		return this.ownerId;
	}
	
	public List<Person> getPeople() {
		return people;
	}
	
	@Override
	public String toString() {
		return this.title;
	}
}
