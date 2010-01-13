def num(b, n):
	if n == 0:
		return "0"
	s = ""
	while n != 0:
		r = n % b
		if r < 10:
			s = str(r) + s
		else:
			s = chr(r - 10 + ord('A')) + s
		n = n / b
	return s;

if __name__ == "__main__":
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
                        ret = num(int(a[0]), int(a[1]))
                        FILE = open(fileout + str(i), "w")
                        FILE.write(ret)
                        print 'written %s' % ret
			FILE.close()
                        i = i + 1
                else:
                        break
