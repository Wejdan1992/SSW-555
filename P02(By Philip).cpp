#include <fstream>
#include <iostream>
#include <string>

using namespace std;
int main()
{
	//The logic was heavily flawed from conception
	//This is hacky at best, there is a lot of strict situations that need to be considered
	ifstream inputFS("C:\\Users\\Phil\\Documents\\Visual Studio 2015\\Projects\\FileIO_Parsing\\GEDCOM.txt", ios::in);
	if (!inputFS.is_open())
	{
		cout << "Could not open file!" << endl;
		return 0;
	}

		string wordParse;
		bool validTag = false;
		while (inputFS >> wordParse)
		{
			if (wordParse == "0")
			{
				//level 0 seems to be weird and mixed orders
				inputFS >> wordParse;
				if (wordParse == "NOTE")
				{
					cout << wordParse << endl;
					while (inputFS >> wordParse && (wordParse != "0" && wordParse != "1" && wordParse != "2"))
					{
						cout << wordParse << " ";
					}
					cout << endl;
				}
				else
				{
					cout << wordParse << endl;
					inputFS >> wordParse;
					cout << wordParse << endl;
				}
			}
			if (wordParse == "1" || wordParse == "2") //hit level identifier
			{
				cout << wordParse << endl;  //print the actual identifier

				inputFS >> wordParse; // move to tag
				if (wordParse == "INDI" || wordParse == "SEX" || wordParse == "FAMC" || wordParse == "FAMS" || wordParse == "FAM" || wordParse == "MARR" || wordParse == "HUSB" || wordParse == "WIFE" || wordParse == "CHIL" || wordParse == "DATE" || wordParse == "HEAD" || wordParse == "TRLR")
				{
					validTag = true;
					cout << wordParse << endl;
					//This check for the upcoming tag is the problem.....
					while (inputFS >> wordParse && (wordParse != "0" && wordParse != "1" && wordParse != "2"))
					{
						cout << wordParse << " ";
					}
					cout << endl;
				}
				else if (wordParse == "BIRT" || wordParse == "DEAT" || wordParse == "DIV")
				{
					validTag = true;
					cout << wordParse << endl;
				}
				else if (!validTag)
				{
					/*/old code ...this could be FAM, INDI and it's structure is different
					inputFS >> wordParse;
					string reserveWord = wordParse;
					inputFS >> wordParse;
					if (wordParse == "FAM")
					{
						cout << reserveWord << endl;
						cout << wordParse << endl;
					}
					else if (wordParse == "INDI")
					{
						cout << reserveWord << endl;
						cout << wordParse << endl;
					}
					else
					/*/
						cout << "invalid tag" << wordParse << endl;
				}
				validTag = false;
		
			}

		}
		
	return 0;
}
