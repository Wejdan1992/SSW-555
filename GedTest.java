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
			// Lists below
			US29ListDeceased();
			US30ListMarried();
			US38ListUpcomingBirthdays();
			OrderedSiblingList();
<<<<<<< HEAD
			 ListRecentBirth();
                          ListRecentDeath();
                          ListOrphans();
=======
			ListRecentBirth();
            ListRecentDeath();
>>>>>>> origin/master

			// Checks below
			US01DateAfterCurrent();
			US02BirthBeforeMarriage();
			US03BirthDeath();
			US04DivorceBeforeMarriage();
			US05MarriageBeforeDeath();
			US07CheckAgeLimit();
			US09BirthBeforeParentsDeath();
			US10Marriage();
			US11NoBigamy();
			US14LessThan5BirthsAtOnce();
			US12parentsNotTooOld();
			US15fewerThan15Siblings();
			CheckHusbandMale();
			CheckWifeFemale();
			checkMaleLastNames();
			checkSiblingsMarriage();

			// ---------------------------------------------//
			System.out.println("Done .. the result in output.txt");
			out.close();
		} else {
			System.out.println("Error, wrong file structure..");
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
	// owner: Wejdan
	public static void listSingle() {
		out.println("------------ US31 List the Living Single in the family:-----------");
		for (IndividualRecord temp : handler.indiRecords) {
			if (temp.fams.size() == 0 && temp.deathDate2 == null)
				out.println("ID:" + temp.id + "   " + temp.name);
		}
	}

	// US07 Check the people who are more than 150 years old
	// owner: Wejdan
	public static void US07CheckAgeLimit() {
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
					result.append("DOB is " + dateFormat.format(temp.birthDate2) + System.lineSeparator());
					result.append("Name :" + temp.name + "   Age " + age + System.lineSeparator());
					result.append("This individual age is more than 150 " + System.lineSeparator());
				}
			}
		}
		out.print(result.toString());
	} // -End US07 CheckAgeLimit

	// US03BirthDeath - To check to make sure person is not dead before birth
	// owner: Philip
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
	// owner: Philip
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
			if (temp.fams.size() > 0 && temp.birthDate2 != null) { // ignore all
																	// people
																	// not
																	// married
				Calendar birthdate = Calendar.getInstance();
				birthdate.setTime(temp.birthDate2);
				int birthYear = birthdate.get(Calendar.YEAR);
				int birthMonth = birthdate.get(Calendar.MONTH);
				int birthDay = birthdate.get(Calendar.DAY_OF_MONTH);
				if (todayYear - birthYear < 14 || (todayYear - birthYear == 14 && todayMonth < birthMonth)
					|| (todayYear - birthYear == 14 && todayMonth == birthMonth && todayDay < birthDay)) {
					result.append("ErrorUS10 : This individual is less than 14 years old and married"
						+ System.lineSeparator());
				result.append("Name:" + temp.name + " Birth: " + dateFormat.format(temp.birthDate2)
					+ System.lineSeparator());
			}
		}
	}
	out.print(result.toString());
	} // -End US10Marriage

	// US01 Dates before current date Dates (birth, marriage, divorce, death)
	// should not be after the current date
	// owner: Philip
	public static void US01DateAfterCurrent() {
		out.println("---------------------------------------------------------------------------------------------");
		out.println("------US01 Check that Dates (birth, marriage, divorce, death) should not be after the current date:------");
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
				if (todayYear < checkYear || (todayYear == checkYear && todayMonth < checkMonth)
					|| (todayYear == checkYear && todayMonth == checkMonth && todayDay < checkDay)) {
					result.append("ErrorUS01 : Birthday occurs after Today" + System.lineSeparator());
				result.append("Name: " + temp.name + "Birthdate " + dateFormat.format(temp.birthDate2)
					+ System.lineSeparator());
			}
		}
		if (temp.deathDate2 != null) {
			Calendar checkDate = Calendar.getInstance();
			checkDate.setTime(temp.birthDate2);
			int checkYear = checkDate.get(Calendar.YEAR);
			int checkMonth = checkDate.get(Calendar.MONTH);
			int checkDay = checkDate.get(Calendar.DAY_OF_MONTH);
			if (todayYear < checkYear || (todayYear == checkYear && todayMonth < checkMonth)
				|| (todayYear == checkYear && todayMonth == checkMonth && todayDay < checkDay)) {
				result.append("ErrorUS01 : Deathday occurs after Today" + System.lineSeparator());
			result.append("Name: " + temp.name + "Deathday " + dateFormat.format(temp.deathDate2)
				+ System.lineSeparator());
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
		if (todayYear < checkYear || (todayYear == checkYear && todayMonth < checkMonth)
			|| (todayYear == checkYear && todayMonth == checkMonth && todayDay < checkDay)) {
			result.append("ErrorUS01 : Marriage date occurs after Today" + System.lineSeparator());
		result.append("Name: " + handler.getIndiById(temp.husbandId).name + " and "
			+ handler.getIndiById(temp.wifeId).name + " married "
			+ dateFormat.format(temp.marriageDate2) + System.lineSeparator());
	}
}
if (temp.divorceDate2 != null) {
	Calendar checkDate = Calendar.getInstance();
	checkDate.setTime(temp.divorceDate2);
	int checkYear = checkDate.get(Calendar.YEAR);
	int checkMonth = checkDate.get(Calendar.MONTH);
	int checkDay = checkDate.get(Calendar.DAY_OF_MONTH);
	if (todayYear < checkYear || (todayYear == checkYear && todayMonth < checkMonth)
		|| (todayYear == checkYear && todayMonth == checkMonth && todayDay < checkDay)) {
		result.append("ErrorUS01 : Divorce date occurs after Today" + System.lineSeparator());
	result.append("Name: " + handler.getIndiById(temp.husbandId).name + " and "
		+ handler.getIndiById(temp.wifeId).name + " divorced "
		+ dateFormat.format(temp.divorceDate2) + System.lineSeparator());
}
}
}
out.print(result.toString());
	} // -End US01DateAfterCurrent

	// US02 Birth before marriage Birth should occur before marriage of an
	// individual
	// owner: Philip
	public static void US02BirthBeforeMarriage() {
		out.println("------US02 Check that birth should occur before marriage:------");
		for (FamilyRecord temp : handler.famRecords) {
			if (handler.getIndiById(temp.husbandId).birthDate2 != null) {
				if (handler.getIndiById(temp.husbandId).birthDate2.after(temp.marriageDate2)) {
					out.println("Name:" + handler.getIndiById(temp.husbandId).name + " is born after marriage! Birth: "
						+ dateFormat.format(handler.getIndiById(temp.husbandId).birthDate2) + " Marriage: "
						+ dateFormat.format(temp.marriageDate2));
				}
			}
			if (handler.getIndiById(temp.wifeId).birthDate2 != null) {
				if (handler.getIndiById(temp.wifeId).birthDate2.after(temp.marriageDate2)) {
					out.println("Name:" + handler.getIndiById(temp.wifeId).name + " is born after marriage! Birth: "
						+ dateFormat.format(handler.getIndiById(temp.wifeId).birthDate2) + " Marriage: "
						+ dateFormat.format(temp.marriageDate2));
				}
			}
		}
	} // -End US02BirthBeforeMarriage
	
	//US05	Marriage before death
	// owner: Philip
	public static void US05MarriageBeforeDeath() {
		out.println("------US05 Check that marriage should occur before death:------");
		for (FamilyRecord temp : handler.famRecords) {
			if (handler.getIndiById(temp.husbandId).deathDate2 != null) {
				if (handler.getIndiById(temp.husbandId).deathDate2.before(temp.marriageDate2)) {
					out.println("ErrorUS09 : Name:" + handler.getIndiById(temp.husbandId).name + " is married after death! Death date: "
						+ dateFormat.format(handler.getIndiById(temp.husbandId).deathDate2) + " Marriage: "
						+ dateFormat.format(temp.marriageDate2));
				}
			}
			if (handler.getIndiById(temp.wifeId).deathDate2 != null) {
				if (handler.getIndiById(temp.wifeId).deathDate2.before(temp.marriageDate2)) {
					out.println("ErrorUS05 : Name:" + handler.getIndiById(temp.wifeId).name + " is married after death! Death date: "
						+ dateFormat.format(handler.getIndiById(temp.wifeId).deathDate2) + " Marriage: "
						+ dateFormat.format(temp.marriageDate2));
				}
			}
		}
	} // -End US05MarriageBeforeDeath
	//US09	Birth before death of parents
	//Child should be born before death of mother and before 9 months after death of father
	// owner: Philip
	public static void US09BirthBeforeParentsDeath() {
		out.println("------US09 Check that birth happens before parents death:------");
		StringBuilder result = new StringBuilder();
		for (FamilyRecord temp : handler.famRecords) {
			IndividualRecord father = handler.getIndiById(temp.husbandId);
			IndividualRecord mother = handler.getIndiById(temp.wifeId);
			if (father.deathDate2 != null) {
				for (String childId : temp.childerenList) {
					IndividualRecord child = handler.getIndiByIdString(childId);
					if (child.birthDate2 != null) {
						Calendar fatherDeath = Calendar.getInstance();
						fatherDeath.setTime(father.deathDate2);
						Calendar childBirth = Calendar.getInstance();
						childBirth.setTime(child.birthDate2);
						int diffYears = fatherDeath.get(Calendar.YEAR) - childBirth.get(Calendar.YEAR);
						int diffMonths = fatherDeath.get(Calendar.MONTH) - childBirth.get(Calendar.MONTH);
						diffMonths = diffMonths + (diffYears * 12);
						if (diffMonths < 9) {
							result.append("ErrorUS09 : Father's Name: " + father.name + " died: " + dateFormat.format(father.deathDate2) 
								+ " Child's Name: " + child.name + " birth: " + dateFormat.format(child.birthDate2)
								+ System.lineSeparator());
						}
					}
				}
			}
			if (mother.deathDate2 != null) {
				for (String childId : temp.childerenList) {
					IndividualRecord child = handler.getIndiByIdString(childId);
					if (child.birthDate2 != null) {
						Calendar motherDeath = Calendar.getInstance();
						motherDeath.setTime(mother.deathDate2);
						Calendar childBirth = Calendar.getInstance();
						childBirth.setTime(child.birthDate2);
						int diffYears = motherDeath.get(Calendar.YEAR) - childBirth.get(Calendar.YEAR);
						int diffMonths = motherDeath.get(Calendar.MONTH) - childBirth.get(Calendar.MONTH);
						int diffDays = motherDeath.get(Calendar.DAY_OF_MONTH) - childBirth.get(Calendar.DAY_OF_MONTH); 
						diffDays = (diffMonths + (diffYears * 12) * 30) + diffDays;
						if (diffDays < 1) {
							result.append("ErrorUS09 : Mother's Name: " + mother.name + " died: " + dateFormat.format(mother.deathDate2) 
								+ " Child's Name: " + child.name + " birth: " + dateFormat.format(child.birthDate2)
								+ System.lineSeparator());
						}
					}
				}
			}

		}
		out.println(result.toString());
	} // -End US09BirthBeforeParentsDeath
	
	// US11 No Bigamy
	// Marriage should not occur during marriage to another spouse
	// owner: Philip
	public static void US11NoBigamy() {
		out.println("------US11 Check that no more than one marriage occurs at the same time:------");
		for (FamilyRecord temp : handler.famRecords) {
			if (handler.getIndiById(temp.husbandId).fams.size() > 1 
					&& temp.marriageDate2 != null && temp.divorceDate2 == null) {//husband
				out.println(handler.getIndiById(temp.husbandId).name + "is in multiple marriages with " + handler.getIndiById(temp.wifeId).name );
			}
			if (handler.getIndiById(temp.wifeId).fams.size() > 1 
					&& temp.marriageDate2 != null && temp.divorceDate2 == null) {//wife
				out.println(handler.getIndiById(temp.wifeId).name + "is in multiple marriages with " + handler.getIndiById(temp.husbandId).name );
			}
		}
	} // -End US11NoBigamy
	
	// US14 LessThan5BirthsAtOnce
	// No more than five siblings should be born at the same time
	// owner: Philip
	public static void US14LessThan5BirthsAtOnce() {
		out.println("------US14 Check that no more than 5 children born at once:------");
		for (FamilyRecord temp : handler.famRecords) {
			if (temp.childerenList.size() > 5) {
				for(int i = 0; i < temp.childerenList.size(); i++){
					IndividualRecord child = handler.getIndiByIdString(temp.childerenList.get(i));
					Date checkDate = child.birthDate2;
					int count = 1;
					for(int j = 0; j < temp.childerenList.size(); j++){
						IndividualRecord compareChild = handler.getIndiByIdString(temp.childerenList.get(j));
						if(i != j && compareChild.birthDate2.compareTo(checkDate) == 0){
							count++;
						}
					}
					if(count > 5 && i + 1 == temp.childerenList.size()){ //
						out.println("Husband: " + handler.getIndiById(temp.husbandId).name 
									+ "and Wife: " + handler.getIndiById(temp.wifeId).name
							+ "have " + count + " children with same birthdates");
					}
				}
			}
		}
	} // -End US14LessThan5BirthsAtOnce
	
	// US04
	// owner: Fawaz
	public static void US04DivorceBeforeMarriage() {
		out.println("------Check if there's a divorce before marriage:------");
		int count = 0;
		StringBuilder result = new StringBuilder();
		for (FamilyRecord temp : handler.famRecords) {
			// Check if there's a marriage and divorce date
			if (temp.marriageDate2 != null && temp.divorceDate2 != null) {
				if (temp.marriageDate2.after(temp.divorceDate2)) {
					count++;
					result.append("Case " + count + System.lineSeparator());
					result.append("Husband:" + handler.getIndiById(temp.husbandId).name + " | Wife:"
						+ handler.getIndiById(temp.wifeId).name + System.lineSeparator());
					result.append("Marriage Date:" + dateFormat.format(temp.marriageDate2) + " | Divorce Date:"
						+ dateFormat.format(temp.divorceDate2) + System.lineSeparator());

				}
			}
		}
		out.println(count + "  Divorce(s) before marriage(s) found:");
		out.print(result.toString());
	}

	// US38
	// owner: Fawaz
	public static void US38ListUpcomingBirthdays() {
		out.println("------List all upcoming birthdays:------");
		Calendar today = Calendar.getInstance();
		int today_Month = today.get(Calendar.MONTH);
		int today_dayOfMonth = today.get(Calendar.DAY_OF_MONTH);
		int count = 1;
		for (IndividualRecord temp : handler.indiRecords) {
			// Listing only living people
			if (temp.deathDate2 == null && temp.birthDate2 != null) {
				Calendar birthDay = Calendar.getInstance();
				birthDay.setTime(temp.birthDate2);
				int bd_Month = birthDay.get(Calendar.MONTH);
				int bd_dayOfMonth = birthDay.get(Calendar.DAY_OF_MONTH);
				if (today_Month == bd_Month && today_dayOfMonth <= bd_dayOfMonth) {
					out.println(count + ". Name:" + temp.name + " | Birth Day:" + dateFormat.format(temp.birthDate2));
					count++;

				} else if (bd_Month - today_Month == 1 && bd_dayOfMonth <= today_dayOfMonth) {
					out.println(count + ". Name:" + temp.name + " | Birth Day:" + dateFormat.format(temp.birthDate2));
					count++;
				}
			}
		}
	}

	// US30 list living married
	// owner: Wejdan
	public static void US30ListMarried() {
		out.println("--------------List the Living Married in the Family:-------------");
		for (IndividualRecord temp : handler.indiRecords) {
			if (temp.fams.size() != 0 && temp.deathDate2 == null)
				out.println("ID:" + temp.id + "   " + temp.name);
		}
	}

	// US29 list Deceased people
	// owner: Wejdan
	public static void US29ListDeceased() {
		out.println("--------------List the Deceased in the Family :-------------");
		for (IndividualRecord temp : handler.indiRecords) {
			if (temp.deathDate2 != null)
				out.println("ID:" + temp.id + "   " + temp.name);
		}
	}

	// US 12 - Check the difference in age between parents and their childeren
	// owner: Fawaz
	public static void US12parentsNotTooOld() {
		StringBuilder result = new StringBuilder();
		int count = 0;
		out.println("----US 12Check the difference in age between parents and childeren :----");
		for (FamilyRecord temp : handler.famRecords) {
			IndividualRecord father = handler.getIndiById(temp.husbandId);
			IndividualRecord mother = handler.getIndiById(temp.wifeId);
			if (father != null && mother != null && father.birthDate2 != null && mother.birthDate2 != null) {
				Date today = new Date();
				int husbandAge = compare(today, father.birthDate2);
				int wifeAge = compare(today, mother.birthDate2);
				for (String childId : temp.childerenList) {

					IndividualRecord child = handler.getIndiByIdString(childId);
					if (child != null && child.birthDate2 != null) {
						int childAge = compare(today, child.birthDate2);
						if ((husbandAge - childAge) >= 80) {
							count++;
							result.append("Case " + count + ": Father is " + (husbandAge - childAge)
								+ " older than his child" + "| Father's Name:"
								+ handler.getIndiById(temp.husbandId).name + "| Father's ID:" + temp.husbandId
								+ " || Child's Name:" + handler.getIndiByIdString(childId).name + " | Child's ID:"
								+ childId + System.lineSeparator() + System.lineSeparator());
						}
						if ((wifeAge - childAge) >= 60) {
							count++;
							result.append("Case " + count + ": Mother is " + (wifeAge - childAge)
								+ " older than her child" + "| Mother's Name:"
								+ handler.getIndiById(temp.wifeId).name + "| Mother's ID:" + temp.wifeId
								+ " || Child's Name:" + handler.getIndiByIdString(childId).name + " | Child's ID:"
								+ childId + System.lineSeparator() + System.lineSeparator());
						}
					}
				}
			}

		}
		out.println(count + " Cases(s) Founded:" + System.lineSeparator());
		out.println(result.toString());

	}

	// US15
	// owner: Fawaz
	public static void US15fewerThan15Siblings() {
		out.println("------US15 Check if the number of siblings is less than 15 :---------");
		int count = 0;
		StringBuilder result = new StringBuilder();
		for (FamilyRecord temp : handler.famRecords) {

			if (temp.childerenList.size() >= 15) {
				count++;
				result.append("Case " + count + ": Family ID:" + temp.familyId);
			}
		}
		out.println(count + " Cases(s) Founded:" + System.lineSeparator());
		out.println(result.toString());
	}

	////////////////////////////////////////////////////////
	public static void CheckHusbandMale() {
		out.println(" *********************** Check the Husband Gender ***************");

		for (FamilyRecord ftemp : handler.famRecords) {

			for (IndividualRecord Itemp : handler.indiRecords)

			{

				if (ftemp.husbandId == Itemp.id && Itemp.sex.equalsIgnoreCase("F")) {

					out.println("Husband with Id " + ftemp.husbandId + " is not a male ");
					break;
				}

			}

		}

	}

	public static void CheckWifeFemale() {
		out.println(" *********************** Check the Wife Gender ******************");

		for (FamilyRecord ftemp : handler.famRecords) {

			for (IndividualRecord Itemp : handler.indiRecords)

			{

				if (ftemp.wifeId == Itemp.id && Itemp.sex.equalsIgnoreCase("M")) {

					out.println("Wife with Id " + ftemp.wifeId + " is not a fmale ");
					break;
				}

			}

		}

	}

	public static void OrderedSiblingList() {

		List<String> doneIds = new ArrayList<>();
		List<IndividualRecord> sortedRecords = new ArrayList<>();
		List<Integer> sortedages = new ArrayList<>();
		int idsCount = 0;
		out.println("------------------------------------------------------------------------------------");
		out.println("\n************(--Ordered Sibling list will be printed for each family--)************");
		for (int i = 0; i < handler.indiRecords.size(); i++) {

			for (int j = 0; j < handler.indiRecords.size(); j++) {
				if (handler.indiRecords.get(i).famc != null && handler.indiRecords.get(j).famc != null) {
					if (handler.indiRecords.get(i).famc.equals(handler.indiRecords.get(j).famc)) {

						if (checkIfIdalreadyExist(handler.indiRecords.get(i).famc, doneIds) == true) {
							String s = handler.indiRecords.get(j).birthDate;
							String[] tokens = s.split(" ");
							int test = 0;
							for (String t : tokens) {

								if (test == 2) {
									int year = Integer.parseInt(t);
									int age = 2016 - year;

									sortedages.add(age);
								}
								test++;
							} // for loop ends here

							if (handler.indiRecords.get(i).name != handler.indiRecords.get(j).name)
								out.println(" ");
							sortedRecords.add(handler.indiRecords.get(j));

						} // if checkIdalreadyExist ends here

					}

				}

			}

			if (sortedRecords.size() > 0) {

				for (int g = 0; g < sortedages.size(); g++) {
					for (int f = 0; f < sortedages.size(); f++) {

						if ((f + 1) < sortedages.size()) {
							if (sortedages.get(f) < sortedages.get(f + 1)) {

								int tempage = sortedages.get(f);
								sortedages.set(f, sortedages.get(f + 1));
								sortedages.set((f + 1), tempage);

								IndividualRecord temprecord = sortedRecords.get(f);
								sortedRecords.set(f, sortedRecords.get(f + 1));
								sortedRecords.set((f + 1), temprecord);
							}

						} // 2nd for loop ends here
					}
				}

				out.println("*--------The Children in the family ID:  " + sortedRecords.get(0).famc
					+ "  are listed below according to there age:\n");
				for (int s = 0; s < sortedRecords.size(); s++) {

					out.println(sortedRecords.get(s).name + " with id " + sortedRecords.get(s).id + " is "
						+ sortedages.get(s) + "  " + "years old");

				}

				sortedages.clear();
				sortedRecords.clear();

			}

			if (handler.indiRecords.get(i).famc != null) {

				if (checkIfIdalreadyExist(handler.indiRecords.get(i).famc, doneIds) == true) {
					doneIds.add(handler.indiRecords.get(i).famc);
				}
			}

		}

	}
	//US16
	//owner:Fawaz
	public static void checkMaleLastNames()
	{
		out.println("----------------Checking Male Last Names---------");
		int count =0;
		StringBuilder result = new StringBuilder();

		for(FamilyRecord temp : handler.famRecords)
		{
			String fullName = handler.getIndiById(temp.husbandId).name;
			String[] firstAndLast = fullName.split(" ");
			if(firstAndLast.length > 1)
			{
				for(String child:temp.childerenList)
				{
					IndividualRecord childRecord = handler.getIndiByIdString(child);
					if(childRecord.sex.equals("M"))
					{
						String childFullName = childRecord.name;
						String[] childFirstAndLast = childFullName.split(" ");
						if(childFirstAndLast.length > 1)
						{
							if(!firstAndLast[1].equals(childFirstAndLast[1]))
							{
								count++;
								result.append("Case "+count+": Family's Last Name:"+firstAndLast[1]+" | Male Child Full Name: "+childFullName+System.lineSeparator());
							}
						}
					}
				}
			}
		}
		out.println(count+ " Cases Found:");
		out.println(result.toString());
	}
	public static void checkSiblingsMarriage()
	{
		out.println("--------Check Marriage between siblings-------");
		int count =0;
		StringBuilder result=new StringBuilder();
		for(FamilyRecord temp:handler.famRecords)
		{
			IndividualRecord husband = handler.getIndiById(temp.husbandId);
			IndividualRecord wife = handler.getIndiById(temp.wifeId);
			if(husband.famc != null && wife.famc !=null)
			{
				if(husband.famc.equals(wife.famc))
				{
					count++;
					result.append("Case "+count+": Husband:"+husband.name+"| Wife:"+wife.name+System.lineSeparator());
				}
			}
		}
		out.println(count+ " Cases Found:");
		out.println(result.toString());
	}

	public static boolean checkIfIdalreadyExist(String newId, List<String> completeList) {

		for (int x = 0; x < completeList.size(); x++) {
			if (newId.equals(completeList.get(x))) {

				return false;
			}

		}

		return true;
	}// //OrderedSiblingList ends here
	
	
	    
    public static void ListRecentBirth() {
        out.println("---------------------------------------------------------------------------------------");
        out.println("------List all recent births that occur during the last 30 days:------");
        Calendar today = Calendar.getInstance();
        int today_Month = today.get(Calendar.MONTH);
        int today_dayOfMonth = today.get(Calendar.DAY_OF_MONTH);
        int today_Year =today.get(Calendar.YEAR);
        int count = 1;
        for (IndividualRecord temp : handler.indiRecords) {
            
            if (temp.deathDate2 == null && temp.birthDate2 != null) {
                Calendar birthDay = Calendar.getInstance();
                birthDay.setTime(temp.birthDate2);
                int bd_Month = birthDay.get(Calendar.MONTH);
                int bd_dayOfMonth = birthDay.get(Calendar.DAY_OF_MONTH);
                int bd_Year=birthDay.get(Calendar.YEAR);
                if (today_Year==bd_Year && today_Month == bd_Month && today_dayOfMonth >= bd_dayOfMonth)
                {
                    out.println(
                                count + ". Name:" + temp.name + " | Birth Day:" + dateFormat.format(temp.birthDate2));
                    count++;
                    
                } else if (today_Year==bd_Year&&today_Month - bd_Month == 1 && today_dayOfMonth <= bd_dayOfMonth ) {
                    out.println(count + ". Name:" + temp.name + " | Birth Day:" + dateFormat.format(temp.birthDate2));
                    count++;
                }
            }
        }
    }
    
    
    public static void ListRecentDeath() {
        out.println("---------------------------------------------------------------------------------------");
        out.println("------List all recent death cases that occur during the last 30 days:------");
        Calendar today = Calendar.getInstance();
        int today_Month = today.get(Calendar.MONTH);
        int today_dayOfMonth = today.get(Calendar.DAY_OF_MONTH);
        int today_Year =today.get(Calendar.YEAR);
        int count = 1;
        for (IndividualRecord temp : handler.indiRecords) {
            
            if (temp.deathDate2 != null) {
                Calendar deathDate = Calendar.getInstance();
                deathDate.setTime(temp.deathDate2);
                int death_Month = deathDate.get(Calendar.MONTH);
                int death_dayOfMonth = deathDate.get(Calendar.DAY_OF_MONTH);
                int death_Year=deathDate.get(Calendar.YEAR);
                if (today_Year== death_Year && today_Month == death_Month && today_dayOfMonth >=  death_dayOfMonth)
                {
                    out.println(
                                count + ". Name:" + temp.name + " | Death Day:" + dateFormat.format(temp.deathDate2));
                    count++;
                    
                } else if (today_Year==death_Year&&today_Month - death_Month == 1 && today_dayOfMonth <= death_dayOfMonth ) {
                    out.println(count + ". Name:" + temp.name + " | Death Day:" + dateFormat.format(temp.deathDate2));
                    count++;
                }
            }
        }
    }
    //owner:Fawaz
    //US33
    public static void ListOrphans()
    {
    	out.println("----------------List All Orphans-------------------");
    	StringBuilder result=new StringBuilder();
    	int count=0;
    	for(FamilyRecord temp:handler.famRecords)
    	{
    		IndividualRecord father = handler.getIndiById(temp.husbandId);
    		IndividualRecord mother = handler.getIndiById(temp.wifeId);
    		if(father.deathDate2!=null&&mother.deathDate2!=null)//both parents are dead
    		{
    			for(String childId:temp.childerenList)
    			{
    				IndividualRecord child = handler.getIndiByIdString(childId);
    				int childAge = compare(new Date(),child.birthDate2);
    				if(childAge<18)
    				{
    					count++;
    					result.append("Case "+count+": Child's Name:"+child.name+"| Child's ID:"+child.id+System.lineSeparator());
    				}

    			}
    		}
    	}
    	out.println(count+" Cases Founded:");
    	out.println(result.toString());
    }


	// ------------------End of Test Cases--------------//
	// -------------------------------------------------//

	// Helper methods
	private static int compare(Date d1, Date d2) {
		Calendar date1 = Calendar.getInstance();
		date1.setTime(d1);
		Calendar date2 = Calendar.getInstance();
		date2.setTime(d2);
		int diff = date1.get(Calendar.YEAR) - date2.get(Calendar.YEAR);
		if (date1.get(Calendar.MONTH) < date2.get(Calendar.MONTH)) {
			diff--;
		} else if (date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH)) {
			if (date1.get(Calendar.DAY_OF_MONTH) < date2.get(Calendar.DAY_OF_MONTH)) {
				diff--;
			}
		}
		return diff;
	}

}// public class GedTest ends here
