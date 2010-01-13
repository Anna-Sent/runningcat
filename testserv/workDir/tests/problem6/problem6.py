#bad solution:
def joseph_recursion(n, k):
	if n < 1 or k < 1:
		return None

	if n == 1:
		return 1
	p = k % n
	li = range(1, n+1)
	li = li[p:] + li[:(p-1)] #del li[p-1]
	r = joseph(n-1, k)
	return li[r-1]
#good solution:
def joseph(n, k):
	if n < 1 or k < 1:
		return None

	i = 2
	r = 1
	while i <= n:
		p = k % i
		r = r + p
		if r > i:
			r = r - i
		i = i + 1
	return r
#main
if __name__ == "__main__":
	#print joseph(7, 2)
	filein = "in"
	fileout = "out"
	s = raw_input("Enter i: ")
	i = int(s)
	while True:
		args = raw_input("Enter arguments: ")
		if args != "-1":
			FILE = open(filein + str(i), "w")
			FILE.write(args)
			FILE.close()
			a = args.split()
			ret = joseph(int(a[0]), int(a[1]))
			FILE = open(fileout + str(i), "w")
			FILE.write(str(ret))
			print 'written %d' % ret
			FILE.close()
			i = i + 1
		else:
			break

