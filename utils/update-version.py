import sys, re, os
file = sys.argv[1]
ver = sys.argv[2]
pattern = re.compile("(SikuliVersion = )\".*\"(;)")

#print file, ver
f = open(file, 'r')
output = open(file+".tmp", 'w')

for line in f.xreadlines():
   output.write(pattern.sub(r'\1"'+ver+r'"\2', line))

output.close()
f.close()
os.rename(file+".tmp", file)
