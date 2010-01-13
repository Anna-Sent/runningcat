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
		int b=((int)((double)rand())/((double)RAND_MAX)*100.0)+1;
		fstream fin((string("in")+to_string(i)).c_str(),fstream::out);
		fin << a << " " << (b+a);
		fin.close();
		fstream fout(("out"+to_string(i)).c_str(),fstream::out);
		if (a%2==0)a+=1;
		fout << a;
		for(int i=a+2;i<=a+b;i+=2)
		{
			if (i%2==1) fout << " " << i;
		}
		fout.close();
	}
	return 0;
}

