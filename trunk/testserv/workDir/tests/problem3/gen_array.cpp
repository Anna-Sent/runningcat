#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include "gen_num.cpp"
int main() {
	srand(time(NULL));
	int n, s=0;
	while ((n = rand()%1001)==0);
	printf("%d\n",n);
	for (int i=0;i<n;++i) {
		for (int j=0;j<n;++j) {
			int b=gen_num(s);
			s += b;
			printf("%d%s",b,(j==n-1)?"":" ");
		}
		printf("%s",(i==n-1)?"":"\n");
	}
	return 0;
}
