#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#define MAX 2147483647
#define MIN -MAX-1
int main() {
	srand(time(NULL));
	int a=rand(), b, overflow;
	do {
		b = rand();
		overflow=0;
		if (a>0)
			overflow = b > MAX-a;
		else if (a<0)
			overflow = b < MIN-a;
	} while (overflow!=0);
	printf("%d %d\n",a,b);
	return 0;
}
