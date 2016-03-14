import java.io.*;
import java.util.*;
import java.text.*;

public class GedTest {

	public static PrintWriter out;
	public static GedFileHandler handler;
	public static DateFormat dateFormat;

	public static void main(String[] args) throws IOException {
		String filePath = "";
		if (args.length > 0) {
			for (int i = 0; i < args.length; i++)
				filePath += args[i];
		} else {
			System.out.println("Error, no input file");
			return;
		}

		handler = new GedFileHandler(filePath);
		if (handler.constructRecords()) {
			out = new PrintWriter(new FileOutputStream("output.txt", false));
			dateFormat = new SimpleDateFormat("dd MMM yyyy");
			printAll();
			// ------------------Tests---------------------//
			listSingle();
			listMarried();
			listUpcomingBirthdays();
			listDeceased();
			CheckAgeLimit();
			US03BirthDeath();
			US10Marriage();
			divorceBeforeMarriage();
			// ---------------------------------------------//
			System.out.println("Done .. the result in output.txt");
			out.close();
		}
	}

	// Print all individuals and families
	public static void printAll() {
		Collections.sort(handler.indiRecords, new IndiRecordsComparator());
		Collections.sort(handler.famRecords, new FamRecordsComparator());

		out.println("Individuals in the File:");
		for (IndividualRecord temp : handler.indiRecords) {
			out.println("ID:" + temp.id + "   " + temp.name);
		}
		out.println("_____________________________________________________________________");
		out.println("Families in the File:");
		for (FamilyRecord temp : handler.famRecords) {
			out.println("Family ID:" + temp.familyId + "| Husband ID:" + temp.husbandId + " Name:"
					+ handler.getIndiById(temp.husbandId).name + "| Wife ID:" + temp.wifeId + " Name:"
					+ handler.getIndiById(temp.wifeId).name);
		}
	}

	// -----------------------------------------------------------//
	// --------------Insert Test Cases Below----------------------//
	// US31 List living single
	public static void listSingle() {
		out.println("------------ US31 List the Living Single in the family:-----------");
		for (IndividualRecord temp : handler.indiRecords) {
			if (temp.fams.size() == 0 && temp.deathDate2 == null)
				out.println("ID:" + temp.id + "   " + temp.name);
		}
	}

	// US07 Check the people who are more than 150 years old
	public static void CheckAgeLimit() {
		out.println("-----------Check Age Limit for Individual Here:-----------");
		// Developer note: Because we are on PC and Mac we need to use
		// + System.lineSeparator() to get an accurate new line
		// when using StringBuilder
		StringBuilder result = new StringBuilder();
		Calendar today = Calendar.getInstance();
		int todayYear = today.get(Calendar.YEAR);
		for (IndividualRecord temp : handler.indiRecords) {
			if (temp.birthDate2 != null) {
				Calendar birthdate = Calendar.getInstance();
				birthdate.setTime(temp.birthDate2);
				int birthYear = birthdate.get(Calendar.YEAR);
				if (todayYear - birthYear > 150) {
					int age = todayYear - birthYear;
					result.append("DOB is " + temp.birthDate);
					result.append("Name :" + temp.name + "   Age " + age);
					result.append("This individual age is more than 150 ");
				}
			}
		}
		out.print(result.toString());
	} // function CheckAgeLimit ends here

	// US03BirthDeath - To check to make sure person is not dead before birth
	public static void US03BirthDeath() {
		out.println("------US03 Check that death doesn't happen before birth:------");
		for (IndividualRecord temp : handler.indiRecords) {
			// Developer note: These temp.date DATE items need to check for null
			// pointer before
			// doing any comparison. Use .before or .after to compare items
			if (temp.deathDate2 != null && temp.birthDate2 != null) {
				if (temp.deathDate2.before(temp.birthDate2)) {
					out.println("ErrorUS03 : Check to make sure person is not dead before birth");
					out.println("Name:" + temp.name + " died before their birth! Birth: "
							+ dateFormat.format(temp.birthDate2) + " Death: " + dateFormat.format(temp.deathDate2));
				}
			}
		}
	} // -End US03BirthDeath

	// US10Marriage - To check to make sure person is older than 14 years old to
	// be married
	public static void US10Marriage() {
		out.println("------US10 Check Marriage is after 14 years old:------");
		// Developer note: Because we are on PC and Mac we need to use
		// + System.lineSeparator() to get an accurate new line
		// when using StringBuilder
		StringBuilder result = new StringBuilder();
		Calendar today = Calendar.getInstance();
		int todayYear = today.get(Calendar.YEAR);
		int todayMonth = today.get(Calendar.MONTH);
		int todayDay = today.get(Calendar.DAY_OF_MONTH);
		for (IndividualRecord temp : handler.indiRecords) {
			if (temp.fams != null && temp.birthDate2 != null) { // ignore all
																// people not
																// married
				Calendar birthdate = Calendar.getInstance();
				birthdate.setTime(temp.birthDate2);
				int birthYear = birthdate.get(Calendar.YEAR);
				int birthMonth = birthdate.get(Calendar.MONTH);
				int birthDay = birthdate.get(Calendar.DAY_OF_MONTH);
				if (todayYear - birthYear < 14 || (todayYear - birthYear == 14 && todayMonth < birthMonth)
						|| (todayYear - birthYear == 14 && todayMonth == birthMonth && todayDay < birthDay)) {
					result.append("ErrorUS03 : This individual is less than 14 years old and married"
							+ System.lineSeparator());
					result.append("Name:" + temp.name + System.lineSeparator());
				}
			}
		}
		out.print(result.toString());
	} // -End US10Marriage

	// US01 Dates before current date Dates (birth, marriage, divorce, death)
	// should not be after the current date
	public static void US01DateAfterCurrent() {
		out.println("------US01 Check that Dates" + "(birth, marriage, divorce, death)"
				+ "should not be after the current date:------");
		StringBuilder result = new StringBuilder();
		Calendar today = Calendar.getInstance();
		int todayYear = today.get(Calendar.YEAR);
		int todayMonth = today.get(Calendar.MONTH);
		int todayDay = today.get(Calendar.DAY_OF_MONTH);
		for (IndividualRecord temp : handler.indiRecords) {
			if (temp.birthDate2 != null) {
				Calendar checkDate = Calendar.getInstance();
				checkDate.setTime(temp.birthDate2);
				int checkYear = checkDate.get(Calendar.YEAR);
				int checkMonth = checkDate.get(Calendar.MONTH);
				int checkDay = checkDate.get(Calendar.DAY_OF_MONTH);
				if (todayYear > checkYear || (todayYear == checkYear && todayMonth > checkMonth)
						|| (todayYear == checkYear && todayMonth == checkMonth && todayDay > checkDay)) {
					result.append("ErrorUS01 : Birthday occurs after Today" + System.lineSeparator());
					result.append("Name: " + temp.name + "Birthdate " + temp.birthDate2 + System.lineSeparator());
				}
			}
			if (temp.deathDate2 != null) {
				Calendar checkDate = Calendar.getInstance();
				checkDate.setTime(temp.birthDate2);
				int checkYear = checkDate.get(Calendar.YEAR);
				int checkMonth = checkDate.get(Calendar.MONTH);
				int checkDay = checkDate.get(Calendar.DAY_OF_MONTH);
				if (todayYear > checkYear || (todayYear == checkYear && todayMonth > checkMonth)
						|| (todayYear == checkYear && todayMonth == checkMonth && todayDay > checkDay)) {
					result.append("ErrorUS01 : Deathday occurs after Today" + System.lineSeparator());
					result.append("Name: " + temp.name + "Deathday " + temp.deathDate2 + System.lineSeparator());
				}
			}
		}
		for (FamilyRecord temp : handler.famRecords) {
			if (temp.marriageDate2 != null) {
				Calendar checkDate = Calendar.getInstance();
				checkDate.setTime(temp.marriageDate2);
				int checkYear = checkDate.get(Calendar.YEAR);
				int checkMonth = checkDate.get(Calendar.MONTH);
				int checkDay = checkDate.get(Calendar.DAY_OF_MONTH);
				if (todayYear > checkYear || (todayYear == checkYear && todayMonth > checkMonth)
						|| (todayYear == checkYear && todayMonth == checkMonth && todayDay > checkDay)) {
					result.append("ErrorUS01 : Marriage date occurs after Today" + System.lineSeparator());
					result.append("Name: " + handler.getIndiById(temp.husbandId).name + " and "
							+ handler.getIndiById(temp.wifeId).name + " married " + temp.marriageDate2
							+ System.lineSeparator());
				}
			}
			if (temp.divorceDate2 != null) {
				Calendar checkDate = Calendar.getInstance();
				checkDate.setTime(temp.divorceDate2);
				int checkYear = checkDate.get(Calendar.YEAR);
				int checkMonth = checkDate.get(Calendar.MONTH);
				int checkDay = checkDate.get(Calendar.DAY_OF_MONTH);
				if (todayYear > checkYear || (todayYear == checkYear && todayMonth > checkMonth)
						|| (todayYear == checkYear && todayMonth == checkMonth && todayDay > checkDay)) {
					result.append("ErrorUS01 : Divorce date occurs after Today" + System.lineSeparator());
					result.append("Name: " + handler.getIndiById(temp.husbandId).name + " and "
							+ handler.getIndiById(temp.wifeId).name + " divorced " + temp.divorceDate2
							+ System.lineSeparator());
				}
			}
		}
		out.print(result.toString());
	} // -End US01DateAfterCurrent

	// US02 Birth before marriage Birth should occur before marriage of an
	// individual
	public static void US02BirthBeforeMarriage() {
		out.println("------US02 Check that birth should occur before marriage:------");
		for (FamilyRecord temp : handler.famRecords) {
			if (handler.getIndiById(temp.husbandId).birthDate2 != null) {
				if (handler.getIndiById(temp.husbandId).birthDate2.after(temp.marriageDate2)) {
					out.println("ErrorUS02 : Birth occured after marriage");
					out.println("Name:" + handler.getIndiById(temp.husbandId).name + " is born after marriage! Birth: "
							+ dateFormat.format(handler.getIndiById(temp.husbandId).birthDate2) + " Marriage: "
							+ dateFormat.format(temp.marriageDate2));
				}
			}
			if (handler.getIndiById(temp.wifeId).birthDate2 != null) {
				if (handler.getIndiById(temp.wifeId).birthDate2.after(temp.marriageDate2)) {
					out.println("ErrorUS02 : Birth occured after marriage");
					out.println("Name:" + handler.getIndiById(temp.wifeId).name + " is born after marriage! Birth: "
							+ dateFormat.format(handler.getIndiById(temp.wifeId).birthDate2) + " Marriage: "
							+ dateFormat.format(temp.marriageDate2));
				}
			}
		}
	} // -End US02BirthBeforeMarriage
		
	// US04
	public static void divorceBeforeMarriage() {
		out.println("------Check if there's a divorce before marriage:------");
		int count = 0;
		StringBuilder result = new StringBuilder();
		for (FamilyRecord temp : handler.famRecords) {
			// Check if there's a marriage and divorce date
			if (temp.marriageDate2 != null && temp.divorceDate2 != null) {
				if (temp.marriageDate2.after(temp.divorceDate2)) {
					result.append("Case " + count + System.lineSeparator());
					result.append("Husband:" + handler.getIndiById(temp.husbandId).name + " | Wife:"
							+ handler.getIndiById(temp.wifeId).name + System.lineSeparator());
					result.append("Marriage Date:" + dateFormat.format(temp.marriageDate2) + " | Divorce Date:"
							+ dateFormat.format(temp.divorceDate2) + System.lineSeparator());
					count++;
				}
			}
		}
		out.println(count + "  Divorce(s) before marriage(s) found:");
		out.print(result.toString());
	}

	// US38
	public static void listUpcomingBirthdays() {
		out.println("------List all upcoming birthdays:------");
		Calendar today = Calendar.getInstance();
		int today_Month = today.get(Calendar.MONTH);
		int today_dayOfMonth = today.get(Calendar.DAY_OF_MONTH);
		int count = 1;
		for (IndividualRecord temp : handler.indiRecords) {
			// Listing only living people
			if (temp.deathDate2 == null) {
				Calendar birthDay = Calendar.getInstance();
				birthDay.setTime(temp.birthDate2);
				int bd_Month = birthDay.get(Calendar.MONTH);
				int bd_dayOfMonth = birthDay.get(Calendar.DAY_OF_MONTH);
				if (today_Month == bd_Month) {
					if (Math.abs(today_dayOfMonth - bd_dayOfMonth) <= 30) {
						out.println(
								count + ". Name:" + temp.name + " | Birth Day:" + dateFormat.format(temp.birthDate2));
						count++;
					}
				} else if (bd_Month - today_Month == 1 && bd_dayOfMonth <= today_dayOfMonth) {
					out.println(count + ". Name:" + temp.name + " | Birth Day:" + dateFormat.format(temp.birthDate2));
					count++;
				}
			}
		}
	}

	// US30 list living married
	public static void listMarried() {
		out.println("--------------List the Living Married in the Family:-------------");
		for (IndividualRecord temp : handler.indiRecords) {
			if (temp.fams.size() != 0 && temp.deathDate2 == null)
				out.println("ID:" + temp.id + "   " + temp.name);
		}
	}

	// US29 list Deceased people
	public static void listDeceased() {
		out.println("--------------List the Deceased in the Family :-------------");
		for (IndividualRecord temp : handler.indiRecords) {
			if (temp.deathDate2 != null)
				out.println("ID:" + temp.id + "   " + temp.name);
		}
	}

	// ------------------End of Test Cases--------------//
	// -------------------------------------------------//

}// public class GedTest ends here
