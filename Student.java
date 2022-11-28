package model;

import controller.Validation;

public class Student {
	private int studId;
	private String studName;

	//Student constructor
	public Student(int studId) {
		setStudId(studId);
	}
	
	//Student constructor
	public Student(int studId, String studName) {
		setStudId(studId);
		setStudName(studName);
	}

	//to String method
	public String toString() {
		return String.format("%d %s", studId, studName);
	}
	
	//getter for student ID
	public int getStudId() {
		return studId;
	}
	
	//setter for student ID
	public void setStudId(int studId) {
		if(Validation.isValidId(studId))
			this.studId = studId;	
	}
	
	//getter for student name
	public String getStudName() {
		return studName;
	}
	
	//setter for student name
	public void setStudName(String studName) {
		if(Validation.isValidName(studName))
			this.studName = studName;
	}	
}
