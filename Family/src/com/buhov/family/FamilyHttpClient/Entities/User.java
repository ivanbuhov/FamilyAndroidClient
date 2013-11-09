package com.buhov.family.FamilyHttpClient.Entities;

public class User {
	private int id;
	private String username;
	private String authCode;
	
	public User(String username, String authCode) {
		this.username = username;
		this.authCode = authCode;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getUsername(){
		return this.username;
	}
	
	public String getAuthCode() {
		return this.authCode;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setUsername(String username){
		this.username = username;
	}
	
	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}
}
