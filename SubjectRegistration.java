package model;

import controller.Validation;

public class SubjectRegistration {
	private int sem;
	private int year;
	private String subId;
	private String subName;
	private Student stud; 

	//SubjectRegistration constructor
	public SubjectRegistration(int sem, int year, Student stud) {
		setSem(sem);
		setYear(year);
		this.stud = stud;
	}

	//SubjectRegistration constructor
	public SubjectRegistration(int sem, int year, String subId, String subName, Student stud) {
		setSem(sem);
		setYear(year);
		setSubId(subId);
		setSubName(subName);
		this.stud = stud;
	}

	//to String overriding method
	public String toString() {
		return String.format("Semester: %d/%d\nSubject Id: %s\nSubject Name: %s", sem, year, subId, subName);
	}

	//getter for semester
	public int getSem() {
		return sem;
	}

	//setter for semester
	public void setSem(int sem) {
		if(sem == 1 || sem == 5 || sem == 8)
			this.sem = sem;
	}

	//getter for year
	public int getYear() {
		return year;
	}

	//setter for year
	public void setYear(int year) {
		if(!Validation.isValidYear(year))
			this.year = year;
	}

	//getter for subject ID
	public String getSubId() {
		return subId;
	}

	//setter for subject ID
	public void setSubId(String subId) {
		String [] subIdList = Subject.SUB_ID;
		for(int cnt = 0; cnt < subIdList.length; cnt++ ) {
			if(subId.equals(subIdList[cnt]))
				this.subId = subId;
		}
	}

	//getter for subject name
	public String getSubName() {
		return subName;
	}

	//setter for subject name
	public void setSubName(String subName) {
		String [] subNameList = Subject.SUB_NAME;
		for(int cnt = 0; cnt < subNameList.length; cnt++ ) {
			if(subName.equals(subNameList[cnt]))
				this.subName = subName;
		}
	}


}