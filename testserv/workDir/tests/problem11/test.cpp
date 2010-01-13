#include <iostream>
using namespace std;
main(){
	int a,b,i;
	cin >> a >> b;
	i=a % 2 == 1?a:a+1;
	if (i<=b) { cout<<i;
		i+=2;
		for(;i<=b;i+=2) cout<<" "<< i;
	}
	return 0;
}
