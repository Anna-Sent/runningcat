#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#define MAX 2147483647
#define MIN -MAX-1
int gen_num(int a) {
	//srand(time(NULL));
	int b, overflow;
	do {
		b = rand()%10001-5000;
		overflow=0;
		if (a>0)
			overflow = b > MAX-a;
		else if (a<0)
			overflow = b < MIN-a;
	} while (overflow);
	return b;
}
/*int main() {
	int a;
	scanf("%d",&a);
	printf("%d\n",gen_num(a));
	return 0;
}*/

