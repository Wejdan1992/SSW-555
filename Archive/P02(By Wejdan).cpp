
//  read GEDCOM file
//
//  Created by Wejdan Al-thobaiti on 1/30/16.
//  Copyright © 2016 Wejdan Al-thobaiti. All rights reserved.
//
#include<iostream>
#include<fstream>
#include<string>
using namespace std;

int main() {

	ifstream myFile("wejdanfamily.ged");
	string line,levelNo, tag;
	int flag=0;
	string validTag[] = { "INDI", "NAME", "SEX", "BIRT", "DEAT", "FAMC", "FAMS", "FAM", "MARR", "HUSB", "WIFE", "CHIL", "DIV", "DATE","HEAD", "TRLR", "NOTE" };
	if (myFile.is_open()) {
		while (!myFile.eof()) {


			myFile >> levelNo;
			myFile >> tag;
			getline(myFile, line);
			cout <<levelNo <<" "<< tag << line << endl;
			cout << "Level Number  " << levelNo << endl;
			flag = 0;
			for (int i = 0; i < 20; i++)
			{
				if (tag == validTag[i])
				{
					flag = 1;
					break;
				}
			}
			if (flag == 0)
				cout << "Tag is invalid"<<endl<<"\n";
			else
				cout << "Tag is valid"<<endl<<"\n";
		}
	}
	myFile.close();
	return 0;
}
