#include <iostream>
#include <fstream>
#include <algorithm>
#include <sstream>

template <class T>
inline std::string to_string (const T& t)
{
	std::stringstream ss;
	ss << t;
	return ss.str();
}

int a[100000];
using namespace std;
int main()
{
	srand(0);
	for(int i=1;i<30;++i)
	{
		for(int j=0;j<i;++j)
		{
			a[j]=((int)((double)rand())/((double)RAND_MAX)*1000.0);
		}
		fstream fin((string("in")+to_string(i)).c_str(),fstream::out);
		fin << i << "\n";
		for(int j=0;j<i;++j)
			fin << a[j] << " ";
		fin << "\n";
		fin.close();
		sort(&a[0],&a[i]);
		fstream fout(("out"+to_string(i)).c_str(),fstream::out);
		for(int j=0;j<i;++j)
			fout << a[j] << " ";
		fout.close();
	}
	fstream fin("in30",fstream::out);
	fstream fout("out30",fstream::out);
	fin << 30000 << "\n";
	for(int i=40000;i>10000;--i)
	{
		fin << i << " ";
	}
	fin << "\n";
	for(int i=10001;i<=40000;++i)
	{
		fout << i << " ";
	}
	fout << "\n";
	return 0;
}

