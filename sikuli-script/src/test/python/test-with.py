from __future__ import with_statement
from sikuli.Sikuli import *

print "global find"
assert(wait("test-res/apple.png",0.5)!=None)
with Region(400, 0, 200,200):
   print "local find"
   setThrowException(False)
   assert(wait("test-res/apple.png",0.5)==None)
   with Region(0, 0, 100,100):
      print "local find 2"
      setThrowException(True)
      assert(wait("test-res/apple.png",0.5)!=None)
      print "exit 2"
   print "exit 1"
print "global find"
assert(wait("test-res/apple.png",0.5)!=None)
