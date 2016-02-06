import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.String;
/**
 *
 * @author Fawaz Alturbaq

 */ 
public class GEDFileReader {
  //Defining The known Tags
    private static String[] knownTags= {"INDI","NAME","SEX","BIRT","DEAT","FAMC","FAMS","FAM"
                ,"MARR","HUSB","WIFE","CHIL","DIV","DATE","HEAD","TRLR","NOTE"};
    
    public static void main(String[] args) {
        try{
        //Open the stream to read from the ged file
       java.io.FileReader reader = new java.io.FileReader("P01.ged");
       java.io.BufferedReader buffer = new java.io.BufferedReader(reader);
    String line = buffer.readLine();
    //checking if the file isn't empty
    if(line!=null)
    {
   
        
        //for each line
     do
     {
            //1. Print the line itself
         System.out.println("1. Line: "+ line);
         // Getting the level number and the tag
         String[] lineValues = line.split(" ");
         String level = lineValues[0],tag;
         //Exceptional case if the line level is 0 && it has value
         if(level.equals("0") && lineValues.length > 2)
         {
             tag = lineValues[2];
         }
         else
         {
             tag = lineValues[1];
         }
        
        //2. Printing the level number
        System.out.println("2. Level: "+ level);
        //Checking if the tag is known
        boolean knownTagFound = false;
        for(int i=0;i<knownTags.length;i++)
        {
            if(tag.equals(knownTags[i]))
            {
                knownTagFound = true;
                break;
            }
        }
        if(!knownTagFound)
        {
            tag = "Invalid tag";
        }
        //3. Printing the tag
        System.out.println("3. Tag: "+ tag +"\n");
        System.out.println("------------------------------------");
        
        //Read the next line
         line=buffer.readLine();

     }while(line!=null);
    }
   //Releasing the resources
    reader.close();
    buffer.close();
        }catch(java.io.IOException e)
        {
            System.out.println(e.getMessage());
        }
        
        
    }
    
}
