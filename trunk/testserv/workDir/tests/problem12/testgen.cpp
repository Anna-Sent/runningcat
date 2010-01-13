#include <iostream>
#include <fstream>
#include <algorithm>
#include <sstream>
using namespace std;

template <class T>
inline std::string to_string (const T& t)
{
	std::stringstream ss;
	ss << t;
	return ss.str();
}

string sum(string a1,string a2)
{
	a1="0"+a1;
	a2="0"+a2;
	while(a1.size()<a2.size()) a1="0"+a1;
	while(a1.size()>a2.size()) a2="0"+a2;
	string res="";
	int sp=0;
	for(int i=a1.size()-1;i>=0;--i)
	{
		int ss = a1[i]+a2[i]-2*'0'+sp;
		char c[2];c[0]=((char)'0'+ss%10);c[1]=0;
		res=string(c)+res;
		sp=ss/10;
	}
	while (res[0]=='0') res.erase(0,1);
	return res;
}

int main()
{
	srand(time(0));
	for(int i=1;i<31;++i)
	{
		string ss1,ss2;
		for(int k=0;k<30;++k)
		{
			int a=((int)((double)rand())/((double)RAND_MAX)*10000.0)+1;
			ss1 += to_string(a);
		}
		for(int k=0;k<30;++k)
		{
			int a=((int)((double)rand())/((double)RAND_MAX)*10000.0)+1;
			ss2 += to_string(a);
		}
		fstream fin((string("in")+to_string(i)).c_str(),fstream::out);
		fin << ss1.size() << " " << ss2.size() << "\n";
		fin << ss1 << "\n" << ss2;
		fin.close();
		fstream fout(("out"+to_string(i)).c_str(),fstream::out);
		fout << sum(ss1,ss2);
		fout.close();
	}
	return 0;
}

