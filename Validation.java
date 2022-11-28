package controller;

import java.util.regex.Pattern;
import model.*;

public class Validation {
	public static boolean isValidId(int id) {
		//student id can only contain 6 digit
		return (Integer.toString(id).length() == 6);
	}

	public static boolean isValidName(String name) {
		//student name can only contain letter, dash(-) and white space
		return (Pattern.matches("[A-Za-z\s\\-]*", name));
	}

	public static boolean isValidSem(int sem){
		//user can only choose 1, 2, or 3 for semester
		return (sem>=1 && sem<=3);
	}

	public static boolean isValidYear(int year) {
		//validate user entered year
		return (Integer.toString(year).length() != 4 
				|| (year/1000 != 1 && year/1000 != 2)
				||(year/1000 == 2 && year/100%10 != 0)) ;
	}

	public static String[] displaySubjectLists() {
		String[] subId = Subject.SUB_ID;
		String [] subName = Subject.SUB_NAME;
		String [] subject = new String[10];
		
		//display all 10 subjects for student to choose
		for(int cnt=0; cnt<10; cnt++) {
			String temp = String.format("%d. %s %s", cnt + 1, subId[cnt], subName[cnt]);
			subject[cnt] = temp;
		}
		return subject;
	}

	public static boolean isValidSubChoice(int choice) {
		//validate user choice for selecting subject
		return choice < 1 || choice > 10;
	}
	
	public static boolean isValidSubCnt (int subCnt) {
		//validate user choice of number of subject
		return (subCnt < 2 || subCnt > 10);
	}

	public static boolean isValidSubCnt (Student stud, int subCnt) throws Exception{
		//validate user choice for number of subject
		DBController db = new DBController();
		String subTaken[] = db.getSubId(stud);
		return (subTaken != null && (10 - subTaken.length) < subCnt);
	}

	public static boolean isValidSubCnt2(Student stud, int subCnt)throws Exception{
		//validate user choice for number of subject
		DBController db = new DBController();
		String subTaken[] = db.getSubId(stud);
		return (subTaken != null && subTaken.length + subCnt == 9);
	}
	
	public static String[] getStudSubChoice (int[] selection) {
		String [] subChoice = new String[selection.length];
		String [] subId = Subject.SUB_ID;
		String [] subName = Subject.SUB_NAME;
		for(int cnt = 0; cnt < subChoice.length; cnt++) {
			subChoice[cnt] = subId[selection[cnt] - 1] + " " + subName[selection[cnt]-1];
		}
		return subChoice;
	}

	public static boolean isValidConfirmDelete(int choice) {
		//validate user confirmation to delete
		return choice!=1 && choice !=2;
	}

	public static String formatSemester(int sem, int year) {
		String strYr = null;
		
		//format semester and year into MM/YY
		strYr = Integer.toString(year%100);
		if(strYr.length() == 1)
			strYr = "0" + strYr;
		return String.format("0%d/%s", sem, strYr); 
	}

	public static boolean isValidSubjectSelection(int subCnt, int[] subChoice1, int[] subChoice2) {
		boolean valid = false;
		//validate user entering for subject selection
		for(int cnt = 0; cnt < subCnt; cnt++) {
			for(int cnt2 = 0; cnt2 < subCnt  && subChoice1[cnt] != 0; cnt2++) {
				if(cnt != cnt2 && subChoice1[cnt] == subChoice2[cnt2]) {
					valid = true;
				}
			}
		}
		return valid;
	}

	//method to check subject taken before in previous semester
	public static boolean checkSubjectTakenBefore(Student stud, int subChoice) throws Exception {
		DBController db = new DBController();
		boolean valid = false;
		String subId[] = Subject.SUB_ID;
		String subTaken[] = db.getSubId(stud);
		
		if(subTaken != null) {
			for(int cnt = 0; cnt < subTaken.length; cnt++) {
				if (subId[subChoice - 1].equalsIgnoreCase(subTaken[cnt])) {
					valid = true;
					break;
				}
			}
		}
		return valid;
	}
	
	public static boolean isAllSubjectTaken(Student stud) throws Exception{
		//check if user has taken all subjects
		DBController db = new DBController();
		String subTaken[] = db.getSubId(stud);
		return (subTaken != null && subTaken.length == 10);
	}
	
	public static boolean isAllowModifySubject(Student stud)throws Exception{
		//check whether student are still able to modify the subjects
		DBController db = new DBController();
		String subTaken[] = db.getSubId(stud);
		String latestRecord[] = db.getLatestSubjectRecord(stud);
		return(10-subTaken.length >= latestRecord.length);
	}

}
