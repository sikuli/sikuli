from python.edu.mit.csail.uid.Sikuli import *

#str = input("please input..")
#popup(str)

print find("test-res/apple.png")

img = capture()
d = VDict()
d[img] = "yes"
for i in xrange(1):
   img = capture()
   val = d[img]
   if val:
      popup(val[0])
   else:
      popup("no")

print "all test cases passed."
