from __future__ import with_statement
from sikuli.Sikuli import *

m = findAll("test-res/apple.png")
for x in m:
   print "match: " + str(x)
m.destroy()
print "last match: " + str(screen.lastMatch)
print "last match: " + str(screen.lastMatches)
with findAll("test-res/apple.png") as m:
   print list(m)
