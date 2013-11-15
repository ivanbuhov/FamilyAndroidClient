package com.buhov.family.FamilyHttpClient.Entities;

public class PedigreeNew {
	
	private int id;
	private String title;
	
	public PedigreeNew(int id, String title) {
		this.id = id;
		this.title = title;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public int getId() {
		return this.id;
	}
}
