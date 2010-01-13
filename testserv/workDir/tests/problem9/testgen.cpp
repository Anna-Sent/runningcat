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

int nod(int a,int b)
{
    if (a==0) return b;
    return nod(b%a,a);
}


int main()
{
	srand(0);
	for(int i=0;i<30;)
	{
		int gcd=0;
		int a=((int)((double)rand())/((double)RAND_MAX)*10000.0)+1;
		int b=((int)((double)rand())/((double)RAND_MAX)*10000.0)+1;
		if (a>b) gcd= nod(b,a); else gcd= nod(a,b);
		if (gcd<20) continue;
		++i;
		fstream fin((string("in")+to_string(i)).c_str(),fstream::out);
		fin << a << " " << b<< "\n";
		fin.close();
		fstream fout(("out"+to_string(i)).c_str(),fstream::out);
		fout << gcd << "\n";
		fout.close();
	}
	return 0;
}

