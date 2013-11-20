package com.buhov.family.FamilyHttpClient.Entities;

public class Date {
	private int day;
	private int month;
	private int year;
	
	public Date(int year, int month, int day) {
		this.year = year;
		this.month = month;
		this.day = day;
	}
	
	public int getDay() {
		return this.day;
	}
	
	public int getMonth() {
		return this.month;
	}
	
	public int getYear() {
		return this.year;
	}
	
	public void setDay(int day) {
		this.day = day;
	}
	
	public void setMonth(int month) {
		this.month = month;
	}
	
	public void setYear(int year) {
		this.year = year;
	}
	
	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		if(this.day < 10) {
			output.append("0");
		}
		output.append(this.day + ".");
		if(this.month < 10) {
			output.append("0");
		}
		output.append(this.month + ".");
		output.append(this.year);
		return output.toString();
	}
}
