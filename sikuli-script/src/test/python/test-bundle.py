from __future__ import with_statement
from sikuli.Sikuli import *
import os
from java.lang import System

setThrowException(True)
setAutoWaitTimeout(0.3)
print "(before setting bundle path) can't find the image: "
try:
   find("apple.png")
except FindFailed,e:
   print "got exception" , e
setBundlePath("test-res/")
m = find("apple.png")
print "(after setting bundle path) found: " + str(m) 
assert( m != None )

setBundlePath("/")
System.setProperty("SIKULI_IMAGE_PATH", "test-res/")
m = find("apple.png")
assert( m != None )
