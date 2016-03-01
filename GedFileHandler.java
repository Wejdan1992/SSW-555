	import java.util.*;
	import java.io.*;
	import java.text.*;
	/*Helper classes are
	1. Line - To store all the info line by line
	2. Record - To store all the records. It consists of Line objects
	3. Individual Record - To store all the data about indivdials using the Record class
	4. Family Record -To store all the data about Families using the Record class
	*/

	//Known Tags
	enum  Tag{
		INDI,
		NAME,
		SEX,
		BIRT,
		DEAT,
		FAMS,
		FAMC,
		FAM,
		MARR,
		HUSB,
		WIFE,
		CHIL,
		DIV,
		DATE,
		HEAD,
		TRLR,
		NOTE,
		INVALIDTAG,
	}

	// This class will take a line and parse it into 1.level 2.value 3. tag
	class Line
	{
		private String value;
		private Tag tag;
		private int level;
		public Line(int level,String value,Tag tag)
		{
			this.level = level;
			this.value = value;
			this.tag = tag;
		}
		public Line(int level,Tag tag)
		{
			this.level = level;
			this.tag = tag;
			this.value = "no value";
		}
		private Line (String line)
		{
			
			String[] lineValues = line.split(" ");
			if(lineValues.length > 1)
			{
				this.level = Integer.parseInt(lineValues[0]);
				if(level == 0)
				{

					this.tag = Tag.valueOf(lineValues[lineValues.length - 1]);
					if(lineValues.length > 2)
					{
						this.value=lineValues[1];
						for(int i=2;i<lineValues.length-1;i++)
						{
							this.value+=" ";
							this.value+=lineValues[i];
						}
					}
					else
						this.value = "no value";

				}
				else
				{
					try
					{
						this.tag = Tag.valueOf(lineValues[1]);
					}catch(IllegalArgumentException e)
					{
						this.tag = Tag.INVALIDTAG;
					}
					catch(NullPointerException e)
					{
						this.tag = Tag.INVALIDTAG;
					}
					if(lineValues.length > 2)
					{
						this.value = lineValues[2];
						for(int i=3;i<lineValues.length;i++)
						{
							this.value+=" ";
							this.value+=lineValues[i];
						}
					}
					else this.value = "no value";
				}

			}

		}
		public static Line lineFactory(String line)
		{
			if(line != null)
				return new Line(line);
			else
				return null;
		}
		public int getLevel()
		{
			return this.level;
		}
		public String getValue()
		{
			if(value!=null)
			{
				return this.value;
			}
			else return "No Value";
		}
		public Tag getTag()
		{
			return this.tag;
		}
		boolean tagIsValid(String value) 
		{
			for (Tag tag : Tag.values()) 
			{
				if (tag.name().equals(value))
				{
					return true;
				}
			}

			return false;
		}
	}

	//This class for structuring the data and store the lines into records
	class Record
	{
		public Tag type;
		public List<Line> lines = new ArrayList<Line>();
		public Record(Tag type)
		{
			this.type = type;
		}


	}

	//This's the main class which will handle the ged files. Its functions are:
	//1. construct the records using the Line objects
	//2. construct the individual records using Record objects
	//3. construct the families records using Record objects
	public class  GedFileHandler
	{
		List<Record> records = new ArrayList<Record>();
		List<IndividualRecord> indiRecords = new ArrayList<IndividualRecord>();
		List<FamilyRecord> famRecords = new ArrayList<FamilyRecord>();
		int recordsNumber = 0;
		int indiRecordsNumber = 0;
		int famRecordsNumber = 0 ;
		String gedFileName;

		public GedFileHandler(String gedFileName)
		{
			this.gedFileName = gedFileName;
		}
		public  IndividualRecord getIndiById(int id)
		{
			for(IndividualRecord temp: indiRecords)
			{
				if(temp.id == id)
				{
					return temp;
				}
			}
			return null;
		}

		public  boolean constructRecords() throws IOException
		{
			try
			{
				java.io.FileReader reader = new java.io.FileReader(gedFileName);
				java.io.BufferedReader buffer = new java.io.BufferedReader(reader);
				Line line = Line.lineFactory(buffer.readLine());
				do
				{
					if(line.getLevel() == 0)
					{
						records.add(new Record(line.getTag()));	
						records.get(recordsNumber).lines.add(line);

						line = Line.lineFactory(buffer.readLine());
						do
						{
							records.get(recordsNumber).lines.add(line);
							line = Line.lineFactory(buffer.readLine());
						}while( line != null && line.getLevel() != 0 );
						recordsNumber++;
					}
					else
						line = Line.lineFactory(buffer.readLine());
				}while(line!=null);

				constructIndiRecords();
				constructFamRecords();
				reader.close();
				buffer.close();
				return true;
			}
			catch(java.io.FileNotFoundException e)
			{
				System.out.println("Error, no such file");
				return false;
			}
			catch(java.io.IOException e)
			{
				System.out.println("Error, couldn't open the file");
				return false;
			}
			
		}
		private void constructIndiRecords ()
		{
			for(int i = 0;i< recordsNumber; i++)
			{
				if(records.get(i).type == Tag.INDI)
				{
					Record thisRecord = records.get(i);
					int id = -1;
					String name=null,
					sex=null,
					birthDate=null,
					deathDate=null,
					famc=null;
					Date birthDate2 = null,deathDate2 = null;
					List<String> fams = new ArrayList<String>();


					DateFormat format = new SimpleDateFormat("dd MMM yyyy");
					for (int j=0; j< thisRecord.lines.size();j++)
					{
						if(thisRecord.lines.get(j).getTag()!=null)
						{
							switch(thisRecord.lines.get(j).getTag())
							{
								case INDI:
								id = Integer.parseInt(thisRecord.lines.get(j).getValue().replaceAll("@","").replace("I",""));
								break;

								case NAME:
								name = thisRecord.lines.get(j).getValue();
								break;

								case SEX:
								sex = thisRecord.lines.get(j).getValue();
								break;

								case BIRT:
								birthDate = thisRecord.lines.get(j+1).getValue();
								try
								{
									birthDate2 = format.parse(thisRecord.lines.get(j+1).getValue());
								}catch(ParseException e)
								{}
								break;

								case DEAT:
								deathDate = thisRecord.lines.get(j+1).getValue();
								try{
									deathDate2 = format.parse(thisRecord.lines.get(j+1).getValue());
								}catch(ParseException e)
								{}
								break;

								case FAMS:
								fams.add(thisRecord.lines.get(j).getValue());	
								break;

								case FAMC:
								famc = thisRecord.lines.get(j).getValue();
								break;

								default:
								break;

							}
						}
					}
					this.indiRecords.add(new IndividualRecord(id,name,sex,birthDate,deathDate,birthDate2,deathDate2,fams,famc));
					this.indiRecordsNumber++;
				}
			}   
		}
		private  void constructFamRecords ()
		{

			for(int i = 0;i< recordsNumber; i++)
			{
				if(records.get(i).type == Tag.FAM)
				{
					Record thisRecord = records.get(i);
					int familyId=-1,husbandId=-1,wifeId=-1;
					String
					marriageDate=null,
					divorceDate=null;
					Date marriageDate2 = null, divorceDate2 = null;
					List <String> childerenList = new ArrayList<String>();

					DateFormat format = new SimpleDateFormat("dd MMM yyyy");
					for (int j=0; j< thisRecord.lines.size();j++)
					{
						if(thisRecord.lines.get(j).getTag()!=null)
						{
							switch(thisRecord.lines.get(j).getTag())
							{
								case FAM:
								familyId = Integer.parseInt(thisRecord.lines.get(j).getValue().replaceAll("@","").replace("F",""));
								break;

								case HUSB:
								husbandId = Integer.parseInt(thisRecord.lines.get(j).getValue().replaceAll("@","").replace("I",""));
								break;

								case WIFE:
								wifeId = Integer.parseInt(thisRecord.lines.get(j).getValue().replaceAll("@","").replace("I",""));
								break;

								case MARR:
								marriageDate = thisRecord.lines.get(j+1).getValue();
								try
								{
									marriageDate2 = format.parse(thisRecord.lines.get(j+1).getValue());
								}catch(ParseException e)
								{}
								break;

								case DIV:
								divorceDate = thisRecord.lines.get(j+1).getValue();
								try
								{
									divorceDate2 = format.parse(thisRecord.lines.get(j+1).getValue());
								}catch(ParseException e)
								{}
								break;

								case CHIL:
								childerenList.add(thisRecord.lines.get(j).getValue());
								break;


								default:
								break;

							}
						}
					}
					this.famRecords.add(new FamilyRecord(familyId,husbandId,wifeId,childerenList,marriageDate,divorceDate,marriageDate2,divorceDate2));
					this.famRecordsNumber++;
				}
			}   
		}



	}

	class IndividualRecord 
	{

		String name;
		String sex;
		int id;
		String birthDate;
		String deathDate;
		Date birthDate2,deathDate2;
		List<String> fams;
		String famc;


		public IndividualRecord(int id, String name, String sex, String birthDate, String deathDate,Date birthDate2,Date deathDate2, List<String> fams, String famc)
		{ 
			this.id=id;
			this.name=name;
			this.sex=sex;
			this.birthDate=birthDate;
			this.deathDate=deathDate;
			this.birthDate2=birthDate2;
			this.deathDate2=deathDate2;
			this.fams=fams;
			this.famc=famc;

		}
		
	}
	//To sort the list of individual records based on their ids
	class IndiRecordsComparator implements Comparator<IndividualRecord>
	{
		@Override
		public int compare(IndividualRecord indi1,IndividualRecord indi2)
		{
			return indi1.id - indi2.id;
		}
	}
	class FamilyRecord
	{
		int familyId,husbandId,wifeId;
		String marriageDate,divorceDate;
		Date marriageDate2,divorceDate2;
		List <String> childerenList;
		
		public FamilyRecord (int familyId, int husbandId, int wifeId,List<String> childerenList, String marriageDate, String divorceDate,Date marriageDate2,Date divorceDate2)
		{
			this.familyId=familyId;
			this.husbandId=husbandId;
			this.wifeId=wifeId;
			this.childerenList = childerenList;
			this.marriageDate = marriageDate;
			this.divorceDate = divorceDate;
			this.marriageDate2 = marriageDate2;
			this.divorceDate2 = divorceDate2;
		}
	}
	//To sort the list of families records based on their ids
	class FamRecordsComparator implements Comparator<FamilyRecord>
	{
		@Override
		public int compare(FamilyRecord fam1,FamilyRecord fam2)
		{
			return fam1.familyId - fam2.familyId;
		}
	}