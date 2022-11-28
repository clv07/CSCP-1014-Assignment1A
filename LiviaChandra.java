package view;

import java.util.*;
import controller.*;
import model.*;

public class LiviaChandra {
	//declare scanner and DBController object
	Scanner scan = new Scanner(System.in);
	DBController db;

	public static void main(String[] args) throws Exception{
		LiviaChandra vw = new LiviaChandra();

		//display main menu with 5 options and let user choose any 1 option
		vw.chooseOption();
	}

	LiviaChandra() throws Exception {
		//create DBController object
		db = new DBController();
	}

	//method to display main menu
	void displayMainMenu() {
		System.out.println("Main Menu ");
		System.out.println("1. Add a new record");
		System.out.println("2. Delete a record");
		System.out.println("3. Modify a record");
		System.out.println("4. List all records ");
		System.out.println("5. Exit ");
	}

	//method for user to choose any 1 option from the 5 options in the main menu
	void chooseOption() throws Exception{
		int choice = 0;
		System.out.println("Welcome to Center of American Education (CAE) Student Subject Registration\n");
		do {
			System.out.println("Please select one option in the main menu to proceed.");

			//display main menu
			displayMainMenu();

			//prompt user enter choice
			System.out.print("Enter your choice: ");
			choice = scan.nextInt();

			//validate choice
			switch(choice) {
			case 1: addNewRecord();break;
			case 2: deleteRecord();break;
			case 3: modifyRecord();break;
			case 4: listAllRecords();break;
			case 5: System.out.println("End of the program. Thank you.");break;
			default: System.out.println("Invalid choice, please try again.\n");
			}
		}while(choice != 5);
	}

	//method for option 1 add new record
	void addNewRecord() throws Exception {
		int id = 0, sem = 0, year = 0, subCnt = 0;
		String name = "";
		String[] subChoice, subjectTaken = null;

		//display option 1: add a new record
		System.out.println("\nOption 1: Add a new record");

		//prompt user enter student ID 
		id = enterId();

		//instantiate Student object
		Student stud = new Student(id);	

		//validate student ID
		//ID exists, display student's name
		if(db.checkIdExist(stud)) {
			name = db.getName(stud);
			System.out.println("Student name: " + name);
		}

		//ID not exists, prompt user to enter student name
		else 
			name = enterName();

		//check if user has taken all 10 subjects
		subjectTaken = db.getSubId(stud);
		if(Validation.isAllSubjectTaken(stud))
			System.out.println("You have already registered for all 10 subjects.\n");

		else {
			//prompt user to enter semester and year
			year = enterYear();
			sem = selectSemester();

			//instantiate SubjectRegistration
			SubjectRegistration rg = new SubjectRegistration(sem, year, stud);

			//validate semester
			//semester exist
			if(db.isSemExist(stud, rg))
				System.out.println("You already registered for this semester.\n");

			//semester not exist
			else {
				//ask user number of subject selection
				do {
					System.out.print("Enter number of subject that you want to choose (at least 2 subjects): ");
					subCnt = scan.nextInt();

					//validate user choice for number of subject
					if(Validation.isValidSubCnt(stud, subCnt))
						System.out.printf("You already registered for %d subject, hence you can only choose for at most %d subjects.\n", 
								subjectTaken.length, 10 - subjectTaken.length);

					else if(Validation.isValidSubCnt2(stud, subCnt))
						System.out.printf("Please choose %d subjects because you just left one more subject to be taken "
								+ "if you choose this number of subject.\n", subCnt + 1);

					else if(Validation.isValidSubCnt(subCnt))
						System.out.println("Invalid number of subject. Please try again.");

				}while(Validation.isValidSubCnt(stud, subCnt) || Validation.isValidSubCnt(subCnt) || Validation.isValidSubCnt2(stud, subCnt));

				//prompt user to select subject
				subChoice = selectSubject(stud, subCnt);	

				//instantiate Student object and declare SubjectRegistration object
				Student stud2 = new Student(stud.getStudId(), name);
				SubjectRegistration rg2 = null;

				for(int cnt = 0; cnt < subChoice.length; cnt++) {

					//instantiate SubjectRegistration object
					rg2 = new SubjectRegistration(sem, year, subChoice[cnt].substring(0, 8), subChoice[cnt].substring(9), stud2);

					//insert new record into database, display message when record inserted successfully
					if(db.insertNewRecord(stud2, rg2) && cnt == subChoice.length -1 )
						System.out.printf("You registered %d subjects successfully for semester %s\n\n", subCnt, Validation.formatSemester(sem, year));
				}
			}
		}
	}

	//method for option 2 delete record
	void deleteRecord() throws Exception {
		int id = 0, year = 0, sem = 0, choice = 0, cnt = 1;
		String name = null, semester = null;

		//display option 2
		System.out.println("\nOption 2: Delete a record");

		//prompt user enter student ID 
		id = enterId();

		//instantiate Student object
		Student stud = new Student(id);

		//validate student ID
		//ID exists, display student's name and latest registration record	
		if(db.checkIdExist(stud)) {

			//display student's name
			name = db.getName(stud);
			System.out.println("Student name: " + name);

			//get semester of latest subject registration record
			year = db.findLatestYear(stud);
			sem = db.findLatestSem(stud, year);
			semester = Validation.formatSemester(sem, year);

			//display latest subject registration record for the student
			System.out.printf("Latest Subject Registration for Semester %s: \n", semester);
			for(String subject: db.getLatestSubjectRecord(stud))
				System.out.printf("%d. %s\n",cnt++, subject);

			//deletion of record
			do {
				//ask user to re-confirm deletion
				System.out.print("Do you confirm to delete this record? [1-yes/2-no]: ");
				choice = scan.nextInt();

				//validate user choice
				if(Validation.isValidConfirmDelete(choice))
					System.out.println("Invalid confirmation, please try again.");

				//delete record after confirmation
				else if(choice == 1) {

					//instantiate SubjectRegistration object
					SubjectRegistration rg = new SubjectRegistration(sem, year, stud);

					//prompt message inform record deleted successfully
					if(db.deleteRecord(stud, rg))
						System.out.printf("%s's subject registration record in semester %s has been deleted successfully.\n", name, semester);
				}

				//cancel confirmation to delete
				else
					System.out.println("Deletion has been cancelled.\n");

			}while(Validation.isValidConfirmDelete(choice));

		}

		//ID not exists, prompt user error message
		else 
			System.out.println("Student ID is not exist.\n");
		System.out.println();
	}

	//method for option 3 modify student latest record
	void modifyRecord() throws Exception{
		int id = 0,year = 0, sem = 0;
		String[] latestRecord, subChoice;

		//display option 3 
		System.out.println("\nOption 3: Modify a record");

		//prompt user enter student ID
		id = enterId();

		//instantiate Student object
		Student stud = new Student(id);

		//check id exist
		if(db.checkIdExist(stud)) {

			//check if all 10 subjects has been taken by student
			if(!Validation.isAllSubjectTaken(stud)) {

				if(Validation.isAllowModifySubject(stud)) {
					//prompt user enter latest year and semester of subject registration 
					System.out.println("Please enter year and semester for your latest subject registration.");
					year = enterYear();
					sem = selectSemester();

					//instantiate Subject Registration object
					SubjectRegistration rg = new SubjectRegistration(sem, year, stud);

					//validate semester
					//semester not found, prompt error message
					if(!db.isSemExist(stud, rg))
						System.out.println("Entered semester is not found.\n");

					//semester found, validate for latest semester
					else {
						//semester is latest, modify subject
						if(db.isSemLatest(stud, rg)) {

							//display all subject registration records for the student
							System.out.printf("\nAll subject registration record for %s\n", db.getName(stud));
							for(String temp: db.displayOneStudRecords(stud))
								System.out.println(temp);

							//get the latest record of student
							latestRecord = db.getLatestSubjectRecord(stud);

							//modify the latest subject registration record
							//prompt user to select new subjects for latest subject registration
							System.out.printf("\nPlease choose %d subjects for semester %s", latestRecord.length, Validation.formatSemester(sem, year));
							subChoice = selectSubject(stud, latestRecord.length);	

							//declare SubjectRegistration object
							SubjectRegistration sg = null;

							for(int cnt = 0; cnt < subChoice.length ; cnt++) {

								//instantiate SubjectRegistration object
								sg = new SubjectRegistration(sem, year, subChoice[cnt].substring(0, 8), subChoice[cnt].substring(9), stud);

								//prompt message inform user if data updated successfully
								if(db.modifyRecord(stud, sg, latestRecord[cnt].substring(0, 8)) && cnt == subChoice.length -1 )
									System.out.printf("\nSubjects updated successfully for semester %s\n\n", Validation.formatSemester(sem, year));
							}
						}	

						//semester is not latest, prompt error message
						else
							System.out.println("Entered semester is not the latest.\n");
					}
				}
				else
					System.out.println("Student is not allowed to modify subject because most of the subjects have been selected.\n");
			}
			//student taken all 10 subjects
			else
				System.out.println("You have already taken all 10 subjects, hence record cannot be changed.\n");
		}
		else
			System.out.println("Entered id is not existed.\n");
	}

	//method for option 4 list all records
	void listAllRecords() throws Exception{
		db = new DBController();

		//display option 4: List all records
		System.out.println("\nOption 4: List All Records");
		System.out.printf("\n%37s\n","Student Registration Listing");
		System.out.printf("%-10s %-18s %-15s %s\n", "Id", "Name","Semester","Subjects");

		//get all student subject registration records from database and display
		for(String temp: db.displayAllRecords()) {
			System.out.println(temp);
		}
		System.out.printf("\n%37s\n", "------------------------------------End of the list------------------------------------");
		System.out.println();
	}

	//method for user enter student id
	int enterId() {
		int id = 0;
		do {
			//prompt user enter student ID
			System.out.print("Enter your student ID: ");
			id = scan.nextInt();
			scan.nextLine();

			//validate entered student ID
			if(!Validation.isValidId(id))
				System.out.println("The student ID is a 6 digits value. Please try again.");
		}while(!Validation.isValidId(id));

		//return student object
		return id;
	}

	//method for user enter student name
	String enterName() {
		String name = null;
		do {
			//prompt user to enter name
			System.out.print("Enter your name: ");
			name = scan.nextLine(); 

			//validate user entered name
			if(!Validation.isValidName(name))
				System.out.println("Student name can only contain letters, dash (-), and spaces. Please try again.");

		}while(!Validation.isValidName(name));

		return name;
	}

	//method for user enter year
	int enterYear() {
		int year = 0;
		do {
			//prompt user enter year
			System.out.print("Enter year [e.g. 2021] : ");
			year = scan.nextInt();

			//validate year
			if(Validation.isValidYear(year))
				System.out.println("Invalid year. Please try again.");

		}while(Validation.isValidYear(year));

		return year;
	}

	//method for user select semester
	int selectSemester() {
		int sem = 0;
		do {
			//prompt user choose semester
			System.out.print("Select semester [1-January / 2-May / 3-August]: ");
			sem = scan.nextInt();

			//validate semester
			if(!Validation.isValidSem(sem))
				System.out.println("Invalid semester. Please try again.");

		}while(!Validation.isValidSem(sem));

		//1-January semester, 2-May semester, 3-August semester
		switch (sem) {
		case 2: sem = 5;break;
		case 3: sem = 8;break;
		}
		return sem;
	}

	//method for user select subjects
	String [] selectSubject(Student stud, int subCnt) throws Exception{
		int [] subject = new int[subCnt];

		//display all 10 subjects
		System.out.println("\nSubject Listing: ");
		for(String temp: Validation.displaySubjectLists())
			System.out.println(temp);

		//prompt user to select subjects
		System.out.println("\nPlease enter 1-10 for subject selection.");
		for(int cnt = 0; cnt < subCnt; cnt++) {
			do {
				System.out.printf("Subject selection %d: ",cnt + 1);
				subject[cnt] = scan.nextInt();

				//validate user choice
				if(Validation.isValidSubChoice(subject[cnt]))
					System.out.println("You can only choose from 1 to 10. Please try again");

				//validate subject selection
				if(Validation.isValidSubjectSelection(subCnt, subject, subject))
					System.out.println("Subject already selected. Please select other subjects.");

				//check subject taken in other semester
				else if(Validation.checkSubjectTakenBefore(stud , subject[cnt]))
					System.out.println("Subject already taken in other semester. Please try again.");

			}while(Validation.isValidSubChoice(subject[cnt]) ||
					Validation.checkSubjectTakenBefore(stud, subject[cnt]) ||
					Validation.isValidSubjectSelection(subCnt, subject, subject));
		}

		//return subject choices
		return Validation.getStudSubChoice(subject);
	}
}

