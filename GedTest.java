import java.io.*;
import java.util.*;
import java.text.*;
import java.lang.*;

public class GedTest  
{

  public static PrintWriter out;
  public static GedFileHandler handler;
  public static DateFormat dateFormat;
  public static void main(String[] args) throws IOException 
  {
    String filePath="";
    if(args.length > 0)
    {
      for(int i=0;i<args.length;i++)
        filePath += args[i];
    }
    else
    {
      System.out.println("Error, no input file");
      return;
    }

    handler =new GedFileHandler(filePath);
    if(handler.constructRecords())
    {
      out = new PrintWriter(new FileOutputStream("output.txt",false));
      dateFormat = new SimpleDateFormat("dd MMM yyyy");
      printAll();
        //------------------Tests---------------------//
      ListSingle();
      CheckAgeLimit();
      US03BirthDeath();
      US10Marriage();
      divorceBeforeMarriage();
        //---------------------------------------------//
      System.out.println("Done .. the result in output.txt");
      out.close();

    }


  }


        //Print all individuals and families
  public static void printAll()
  {
   Collections.sort(handler.indiRecords,new IndiRecordsComparator());
   Collections.sort(handler.famRecords,new FamRecordsComparator());

   out.println("Individuals in the File:");
   for(IndividualRecord temp : handler.indiRecords)
   {
    out.println("ID:"+temp.id+ "   "+temp.name);
  }
  out.println("_____________________________________________________________________");
  out.println("Families in the File:");
  for(FamilyRecord temp : handler.famRecords)
  {
    out.println("Family ID:"+temp.familyId+ 
      "| Husband ID:"+temp.husbandId+" Name:"+handler.getIndiById(temp.husbandId).name+
      "| Wife ID:" +temp.wifeId+" Name:"+handler.getIndiById(temp.wifeId).name);
  }
}


     //--------------------------------------------------------------------------------------//
    //-------------------------------------Test Cases ---------------------------------------//
    // to list living single
public static void ListSingle()
{
  out.println("--------------Living Single in the family are:-------------");
  for(IndividualRecord temp : handler.indiRecords)
  {
    if(temp.fams.size()==0 && temp.deathDate== null)
      out.println("ID:"+temp.id+ "   "+temp.name);
  }
}

    //  to check the people who are more than 150 years old
public static void CheckAgeLimit()
{

  out.println("--------------Check Age Limit for Individual Here:-------------");
  for(IndividualRecord temp : handler.indiRecords)
  {

    String s= temp.birthDate;
    if(s!=null)
    {
      String[] tokens = s.split(" ");
      
      int test=0;
      for (String t : tokens){
        if(test==2){
          int year = Integer.parseInt(t);
          int age = 2016 - year;
              //System.out.println("DOB is " + temp.birthDate);
              //System.out.println("Name :"+temp.name+ "   Age "+ age);
          if(age >150){
           out.println("DOB is " + temp.birthDate);
           out.println("Name :"+temp.name+ "   Age "+ age);
           out.println("This individual age is more than 150 ");
         }
       }
       test++;
          } // for loop ends here
        }
      }
    } // function CheckAgeLimit ends here 

    //  to check to make sure person is not dead before birth
    public static void US03BirthDeath()
    {
      out.println("------Check that death doesn't happen before birth:------");
      for(IndividualRecord temp : handler.indiRecords){
          if (temp.deathDate != null){ //ignore all people without death dates or get exception error
            String sb = temp.birthDate;
            String[] tokensb = sb.split(" ");
            int birthYear = 0;
            int test = 0;
            for (String t : tokensb){
              if(test==2)
              {
                birthYear = Integer.parseInt(t);
              }
              test++;
          } // for loop ends here

          String sd = temp.deathDate;
          String[] tokensd = sd.split(" ");
          int deathYear = 0;
          test = 0;
          for (String t : tokensd){
            if(test==2){
              deathYear = Integer.parseInt(t);
            }
            test++;
          } // for loop ends here
          if (deathYear < birthYear){
            out.println("ErrorUS03: Death happens before birth"); 
            out.println("ErrorUS03: Name " + temp.name); 
          }
        }
        } // Indi for loop ends here
    } // function US10Marriage ends here 
    
    //  to check to make sure person is older than 14 years old to be married
    public static void US10Marriage()
    {
      out.println("------Check Marriage is after 14 years old:------");
      for(IndividualRecord temp : handler.indiRecords){
          if(temp.fams != null && temp.birthDate!=null){ //ignore all people not married or get exception error
            String sb = temp.birthDate;
            String[] tokens = sb.split(" ");
            int test = 0; 
            for (String t : tokens)
            {
              if(test == 2){
                int year = Integer.parseInt(t);
                int age = 2016 - year;
                if(age <= 14){
                  out.println("ErrorUS10: This individual is less than 14 years old and married");
                  out.println("ErrorUS10: Name " + temp.name);
                }
              }
              test++;
        } // for loop ends here
      }
        } // Indi for loop ends here
    } // function US10Marriage ends here

    public static void divorceBeforeMarriage()
    {
      out.println("------Check if there's a divorce before marriage:------");
      int count = 1;
      StringBuilder result = new StringBuilder();
      for(FamilyRecord temp : handler.famRecords)
      {
        //Checl if there's a marriage and divorce date
        if(temp.marriageDate2 != null && temp.divorceDate2 != null)
        {
          if(temp.marriageDate2.after(temp.divorceDate2))
          {
            result.append("Case "+count+":\n");
            result.append("Husband:"+  handler.getIndiById(temp.husbandId).name+ " | Wife:"+handler.getIndiById(temp.wifeId).name+"\n");
            result.append("Marriage Date:"+dateFormat.format(temp.marriageDate2)+" | Divorce Date:"+ dateFormat.format(temp.divorceDate2)+"\n");
            result.append("-----------------------------\n");
            count++;
          }
        }
      }
      out.println(count-1+"  Divorce(s) before marriage(s) found:");
      out.print(result.toString());
    }

    //------------------------------------------End of Test Cases -------------------------------// 
    //-------------------------------------------------------------------------------------------//     
    
}// public class GedTest ends here





