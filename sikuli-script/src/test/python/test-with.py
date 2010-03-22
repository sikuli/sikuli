from __future__ import with_statement
from sikuli.Sikuli import *

with Region(400, 0, 200,200):
   print "local find"
   setThrowException(False)
   print wait("test-res/apple.png",0.5)
print "global find"
print wait("test-res/apple.png")
