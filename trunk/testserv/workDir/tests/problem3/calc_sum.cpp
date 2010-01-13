#include <stdio.h>
int main() {
	int n,a,s=0;
	scanf("%d",&n);
	for (int i=0;i<n;++i) {
		for (int j=0;j<n;++j) {
			scanf("%d",&a);
			s += a;
		}
	}
	printf("%d\n",s);
	return 0;
}
