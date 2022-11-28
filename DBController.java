package controller;

import java.sql.*;
import java.util.*;
import model.*;

public class DBController {	

	//declare Statement object
	Statement stmt;

	public DBController() throws Exception {
		//make connection
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/assignment1", "root", "");

		//Create a Statement object
		stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	}

	//method for insert new record
	public boolean insertNewRecord(Student stud, SubjectRegistration rg) throws Exception{
		int num1 = 0, num2 = 0;
		//execute SQL statement
		String sql = String.format("insert into student(studId, studName) values (%d,'%s')",stud.getStudId(), stud.getStudName());
		String sql2 = String.format("insert into subjectregistration(studId, sem, year, subId, subName) values (%d,%d,%d,'%s','%s')", 
				stud.getStudId(), rg.getSem(), rg.getYear(), rg.getSubId(), rg.getSubName());

		//process result
		if(!checkIdExist(stud)) {
			num1 = stmt.executeUpdate(sql);
			num2 = stmt.executeUpdate(sql2);
		}
		else {
			num1 = 1;
			num2 = stmt.executeUpdate(sql2);
		}
		return num1 + num2 == 2;
	}

	//method for delete record
	public boolean deleteRecord(Student stud, SubjectRegistration rg) throws Exception{
		String [] latestRecord = getLatestSubjectRecord(stud);

		//execute SQL statement
		String sql = String.format("delete from subjectregistration where studId = %d and sem = %d and year = %d",
				stud.getStudId(),rg.getSem(), rg.getYear()); 

		//process result
		int num = stmt.executeUpdate(sql);

		return num == latestRecord.length;
	}

	//method for modify record
	public boolean modifyRecord(Student stud, SubjectRegistration rg, String subId) throws Exception{
		int num = 0;
		String [] latestRecord = getLatestSubjectRecord(stud);
		//execute SQL statement
		String sql = String.format("update subjectregistration set subId ='%s', subName = '%s' "
				+ "where studId = %d and sem = %d and year = %d and subId = '%s'", 
				rg.getSubId(), rg.getSubName(), stud.getStudId(), rg.getSem(), rg.getYear(), subId);

		for(String temp: latestRecord) {
			if(!temp.substring(0, 6).equals(rg.getSubId())){
				//process result
				stmt.executeUpdate(sql);
				num++;
			}
			else
				num++;
		}
		return num == latestRecord.length ;
	}

	//method to display all records
	public String[] displayAllRecords() throws Exception{
		int row = 0, sem = 0, year = 0, studId = 0;
		String studName = null, subId = null, subName = null;
		String[] records = null, semester = null;
		Student[] studInfo = null;

		//get the all students' name
		studInfo = getAllStudentsName();

		//execute SQL statement
		String sql = String.format("select * from subjectregistration order by studId, year, sem");
		ResultSet rs = stmt.executeQuery(sql);

		//get the row number of results and instantiate array semester and records
		if(rs.last()) {
			row = rs.getRow();
			rs.beforeFirst();
		}
		semester = new String[row];
		records = new String[row];

		//get student ID, name, semester and subject registered for all students
		for(int cnt = 0; cnt < row && rs.next(); cnt ++) {
			studId = rs.getInt("studId");

			//get the student name
			for(int cnt2 = 0; cnt2 < studInfo.length; cnt2++) {
				if(studId == Integer.parseInt(studInfo[cnt2].toString().substring(0, 6)))
					studName = studInfo[cnt2].toString().substring(7);
			}
			year = rs.getInt("year");
			sem = rs.getInt("sem");
			semester[cnt] = Validation.formatSemester(sem, year);
			subId = rs.getString("subId");
			subName = rs.getString("subName");

			//format the all the output for display
			if(cnt == 0) {
				records[cnt] = String.format("%-10d %-18s %-15s %s %s", studId, studName, semester[cnt], subId, subName);
			}
			else if(cnt>0) {
				if(!semester[cnt].equals(semester[cnt-1]))
					records[cnt] = String.format("\n%-10d %-18s %-15s %s %s", studId, studName, semester[cnt], subId, subName);

				else {
					records[cnt] = String.format("%-10s %-18s %-15s %s %s", " ", " ", " ", subId, subName);
				}
			}
		}
		return records;
	}

	//method to display records of one student
	public String[] displayOneStudRecords(Student stud) throws Exception{
		int year = 0, sem = 0, row = 0;
		String subId = null, subName = null;
		String [] record = null, semester = null;

		//execute sql statement
		String sql = String.format("select year, sem, subId, subName from subjectregistration where studId = %d order by year, sem", stud.getStudId());

		//process result
		ResultSet rs = stmt.executeQuery(sql);

		//get number of records and instantiate record and semester array
		if(rs.last()) {
			row = rs.getRow();
			rs.beforeFirst();
		}
		record = new String[row];
		semester = new String[row];

		//format the output for displaying purpose
		for(int cnt = 0; cnt < row && rs.next(); cnt++) {
			year = rs.getInt("year");
			sem = rs.getInt("sem");
			semester[cnt] = Validation.formatSemester(sem, year);
			subId = rs.getString("subId");
			subName = rs.getString("subName");
			if(cnt == 0) {
				record[cnt] = String.format("Semester %s\n%s %s", semester[cnt], subId, subName);
			}

			else if(cnt>0) {
				if(!semester[cnt].equals(semester[cnt-1])) {
					record[cnt] = String.format("\nSemester %s\n%s %s", semester[cnt],subId, subName);
				}
				else {
					record[cnt] = String.format("%s %s",subId, subName);
				}
			}
		}
		return record;
	}

	//method to get all students' name from database
	Student[] getAllStudentsName()throws Exception{
		int row = 0, studId = 0;
		String studName = null;
		Student[] stud;

		//execute sql statement
		String sql = String.format("select * from student");

		//process result
		ResultSet rs = stmt.executeQuery(sql);

		//get the row number and instantiate stud array
		if(rs.last()) {
			row = rs.getRow();
			rs.beforeFirst();
		}
		stud = new Student[row];

		//get the student ID and student name for all of the students 
		for(int cnt = 0; cnt < row && rs.next(); cnt++) {
			studId = rs.getInt("studId");
			studName = rs.getString("studName");
			stud[cnt] = new Student(studId, studName);
		}
		return stud;
	}

	//method to check whether the student ID exist
	public boolean checkIdExist(Student stud) throws Exception{
		int dbId = 0;
		boolean valid = false;

		//get the student ID
		int id = stud.getStudId();

		//execute sql statement
		String sql = String.format("select studId from student where studId = %d", id);

		//process result
		ResultSet rs = stmt.executeQuery(sql);

		//check whether student ID exist
		while(rs.next()) {
			dbId = rs.getInt("studId");
			if (id == dbId)
				valid = true;
		}
		return valid;
	}

	//method to get the student name if the student ID exist
	public String getName(Student stud) throws Exception{
		String name = "";

		//get student ID
		int id = stud.getStudId();

		//execute sql statement
		String sql = String.format("select studName from student where studId = %d", id);

		//process result
		ResultSet rs = stmt.executeQuery(sql);

		//get student name
		while(rs.next()) {
			name = rs.getString("studName");
		}
		return name;
	}

	//method to get all the subjects registered by a student
	public String[] getSubId(Student stud) throws Exception{
		String subId[] = null;
		int row = 0;

		//execute sql statement
		String sql = String.format("select subId from subjectregistration where studId = %d", stud.getStudId());

		//process result
		ResultSet rs = stmt.executeQuery(sql);

		//get row number and instantiate subId array
		if(rs.last()) {
			row = rs.getRow();
			subId = new String[row];
			rs.beforeFirst();
		}

		//get student's record for all registered subject
		for(int cnt = 0; cnt < row && rs.next(); cnt++) {
			subId[cnt] = rs.getString("subId");
		}

		//return subId array
		return subId;
	}

	//method to check whether the student had registered for a particular semester
	public boolean isSemExist(Student stud, SubjectRegistration rg) throws Exception{
		boolean exist = false;
		//execute SQL statement
		String sql = String.format("select sem, year from subjectregistration where studId = %d", stud.getStudId());

		//process result
		ResultSet rs = stmt.executeQuery(sql);

		//get semester and year
		while(rs.next()) {
			int dbSem = rs.getInt("sem");
			int dbYear = rs.getInt("year");

			//check whether the semester is exist
			if(dbSem == rg.getSem() && dbYear == rg.getYear()) {
				exist = true;
				break;
			}
		}
		return exist;
	}

	//method to check whether user entered semester is the latest semester
	public boolean isSemLatest(Student stud, SubjectRegistration rg)throws Exception{

		//find latest year and semester
		int dbYear = findLatestYear(stud);
		int dbSem = findLatestSem(stud, dbYear);

		//validation for latest year and semester
		return (rg.getSem() == dbSem && rg.getYear() == dbYear);
	}

	//method to find the latest year for the subject registration record of a student
	public int findLatestYear(Student stud) throws Exception{
		int row = 0;
		int [] year;

		//get student ID
		int id = stud.getStudId();

		//execute SQL statement to find latest year
		String sql = String.format("select year from subjectregistration where studId = %d", id);

		//process result
		ResultSet rs = stmt.executeQuery(sql);

		//get row number and instantiate array year
		if(rs.last()) {
			row = rs.getRow();
			rs.beforeFirst();
		}

		if(row != 0) {
			year = new int[row];

			//get all the year of subject registration for a student
			for(int cnt = 0; cnt < row && rs.next(); cnt++) {
				year[cnt] = rs.getInt("year");
			}

			//sort the value in year array to find year of latest record
			Arrays.sort(year);
		}

		else {
			row = 1;
			year = new int[row];
		}
		//return the latest year of subject registration record for the student
		return year[row-1];
	}

	//method to find the latest semester for the subject registration record of a student
	public int findLatestSem(Student stud, int year) throws Exception{
		int [] sem;
		int row = 0;

		//get year of latest student record
		year = findLatestYear(stud);

		if(year != 0) {
			//execute SQL statement to find latest semester
			String sql = String.format("select sem from subjectregistration where studId = %d and year = %d", stud.getStudId(), year);

			//process result
			ResultSet rs = stmt.executeQuery(sql);

			//get row number and instantiate array sem
			if(rs.last()) {
				row = rs.getRow();
				rs.beforeFirst();
			}
			sem = new int[row];

			//get sem from database
			for(int cnt = 0; cnt< row && rs.next(); cnt++) {
				sem[cnt] = rs.getInt("sem");
			}

			//sort sem array to find latest semester
			Arrays.sort(sem);
		}

		else {
			row = 1;
			sem = new int[row];
		}

		//return latest semester of subject registration record for the student
		return sem[row-1];
	}

	//method to get the latest subject registration record of a student
	public String[] getLatestSubjectRecord(Student stud)throws Exception{
		int row = 0, year = 0, sem = 0;
		String [] subject;

		//find latest year and semester of subject registration record for a student
		year = findLatestYear(stud);
		sem = findLatestSem(stud, year);

		if(year !=0 && sem != 0) {
			//execute SQL statement
			String sql = String.format("select subId,subName from subjectregistration where studId = '%s' and year = %d and sem = %d", stud.getStudId(), year, sem);

			//process result
			ResultSet rs = stmt.executeQuery(sql);

			//get row number and instantiate array subject
			if(rs.last()) {
				row = rs.getRow();
				rs.beforeFirst();
			}
			subject = new String [row];

			//get subjects registered in the latest semester from database
			for(int cnt = 0; cnt<row && rs.next(); cnt++) {
				subject[cnt] = rs.getString("subId") + " " + rs.getString("subName");
			}
		}

		else {
			row = 1;
			subject = new String[row];
		}
		return subject; 
	}

}


