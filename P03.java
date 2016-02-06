
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

 enum Tag{
 	INDI,
	NAME,
	SEX,
	BIRT,
	DEAT,
	FAMC,
	FAMS,
	FAM,
	MARR,
	HUSB,
	WIFE,
	CHIL,
	DIV,
	DATE,
	HEAD,
	TRLR,
	NOTE
}

 class Line
{
	private String value,tag;
	private int level;
	public Line(int level,String value,String tag)
	{
		this.level = level;
		this.value = value;
		this.tag = tag;
	}
	public Line(int level,String tag)
	{
		this.level = level;
		this.tag = tag;
		this.value = "no value";
	}
	private Line (String line)
	{
		
		String[] lineValues = line.split(" ");
		this.level = Integer.parseInt(lineValues[0]);
		if(level == 0)
		{

			this.tag = lineValues[lineValues.length - 1];
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
			
			this.tag = lineValues[1];
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
		if(!tagIsValid(this.tag))
		{
			this.tag = "invalid tag";
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
	public String getTag()
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
 class Record
{
	public String type;
	public List<Line> lines = new ArrayList<Line>();
	public Record(String type)
	{
		this.type = type;
	}


}


  class  GedFileHandler
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

		public  void constructRecords() throws IOException
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
		
	}
	 private void constructIndiRecords ()
	{
	for(int i = 0;i< recordsNumber; i++)
	{
		if(records.get(i).type.equals("INDI"))
		{
			Record thisRecord = records.get(i);
			String name=null,
			 sex=null,
			 id=null,
			 birthDate=null,
			 deathDate=null,
			 famc=null;
			List<String> fams = new ArrayList<String>();
			for (int j=0; j< thisRecord.lines.size();j++)
			{
                            if(thisRecord.lines.get(j).getTag()!=null)
				switch(thisRecord.lines.get(j).getTag())
				{
					case "INDI":
					id = thisRecord.lines.get(j).getValue();
					break;

					case "NAME":
					name = thisRecord.lines.get(j).getValue();
					break;

					case "SEX":
					sex = thisRecord.lines.get(j).getValue();
					break;

					case "BIRT":
					birthDate = thisRecord.lines.get(j+1).getValue();
					break;

					case "DEAT":
					birthDate = thisRecord.lines.get(j+1).getValue();
					break;

					case "FAMC":
					famc = thisRecord.lines.get(j).getValue();
					break;

					case "FAMS":
					fams.add(thisRecord.lines.get(j).getValue());
					break;

					default:
					break;

				}
			}
                        this.indiRecords.add(new IndividualRecord(id,name,sex,birthDate,fams,famc));
			this.indiRecordsNumber++;
		}
	}   
	}
	private  void constructFamRecords ()
	{

	    for(int i = 0;i< recordsNumber; i++)
	{
		if(records.get(i).type.equals("FAM"))
		{
			Record thisRecord = records.get(i);
			String familyId=null,
			husbandId=null,
			wifeId=null,
			marriageDate=null,
			divorceDate=null;
			List <String> childerenList = new ArrayList<String>();
			for (int j=0; j< thisRecord.lines.size();j++)
			{
                             if(thisRecord.lines.get(j).getTag()!=null)
				switch(thisRecord.lines.get(j).getTag())
				{
					case "FAM":
					familyId = thisRecord.lines.get(j).getValue();
					break;

					case "HUSB":
					husbandId = thisRecord.lines.get(j).getValue();
					break;

					case "WIFE":
					wifeId = thisRecord.lines.get(j).getValue();
					break;

					case "MARR":
					marriageDate = thisRecord.lines.get(j+1).getValue();
					break;

					case "DIV":
					divorceDate = thisRecord.lines.get(j+1).getValue();
					break;

					case "CHIL":
					childerenList.add(thisRecord.lines.get(j).getValue());
					break;


					default:
					break;

				}
			}
			this.famRecords.add(new FamilyRecord(familyId,husbandId,wifeId,childerenList,marriageDate,divorceDate));
			this.famRecordsNumber++;
		}
	}   
	}


}

 class IndividualRecord 
{

	String name;
	String sex;
	String id;
	String birthDate;
	String deathDate;
	List<String> fams;
	String famc;


	public IndividualRecord(String id, String name, String sex, String birthDate, List fams, String famc)
	{ 
		this.id=id;
		this.name=name;
		this.sex=sex;
		this.birthDate=birthDate;
		this.fams =fams;
		this.famc=famc;

	}
}
 class FamilyRecord
 {
	String familyId;
	String husbandId;
	String wifeId;
	String marriageDate,divorceDate;
	List <String> childerenList;
	
	public FamilyRecord (String familyId, String husbandId, String wifeId,List childerenList, String marriageDate, String divorceDate)
	{
		this.familyId=familyId;
		this.husbandId=husbandId;
		this.wifeId=wifeId;
		this.childerenList = childerenList;
		this.marriageDate = marriageDate;
		this.divorceDate = divorceDate;
	}
}
	
public class P03  
{
	

  
  
	public static void main(String[] args) throws IOException 
	{

		       
		    
	    	String filePath= "P01.ged";
	    	GedFileHandler handler =new GedFileHandler(filePath);
	    	handler.constructRecords();
	    	System.out.println(handler.recordsNumber);
	    	System.out.println(handler.indiRecordsNumber);
       		System.out.println(handler.famRecordsNumber);
       		for(IndividualRecord temp : handler.indiRecords)
       		{
       			System.out.println(temp.id+ "   "+temp.name);
       		}
       		for(FamilyRecord temp : handler.famRecords)
       		{
       			System.out.println(temp.familyId+ "   "+temp.husbandId+ "  " +temp.wifeId);
       		}
       		// for(Record rec : handler.records)
       		// {
       		// 	//System.out.println("Printing record");
       		// for(Line temp : rec.lines)
       		// {
       		// 	System.out.println("Line level:" + temp.getLevel());
       		// 	System.out.println("Line tag:" + temp.getTag());
       		// 	System.out.println("Line value:" + temp.getValue());
       		// 	System.out.println("_______________________________");
       		// }
       	}




     }
 
 
    	    
           
