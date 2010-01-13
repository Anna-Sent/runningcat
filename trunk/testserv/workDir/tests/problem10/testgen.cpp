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

int main()
{
	srand(time(0));
	for(int i=1;i<31;++i)
	{
		int a=((int)((double)rand())/((double)RAND_MAX)*10000.0)+1;
		string ss = to_string(a);
		if (a%2==0) {
			int t=ss.size();
			for(int j=t-1;j>=0;--j)
			{
				fstream fin((string("in")+to_string(i)).c_str(),fstream::out);
				ss+=ss[j];
				fin << ss;
				fin.close();
				fstream fout(("out"+to_string(i)).c_str(),fstream::out);
				fout << "Yes";
				fout.close();
			}
		}
		else
		{
			fstream fin((string("in")+to_string(i)).c_str(),fstream::out);
			fin << ss;
			fin.close();
			fstream fout(("out"+to_string(i)).c_str(),fstream::out);
			fout << "No";
			fout.close();
		}
	}
	return 0;
}

