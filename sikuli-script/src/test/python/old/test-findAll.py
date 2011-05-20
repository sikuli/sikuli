# Copyright 2010-2011, Sikuli.org
# Released under the MIT License.
from __future__ import with_statement
from sikuli.Sikuli import *

m = findAll("test-res/apple.png")
for x in m:
   print "match: " + str(x)
m.destroy()
assert getLastMatch() == None
assert SCREEN.getLastMatch() == None
assert SCREEN.getLastMatches() != None
with findAll("test-res/apple.png") as m:
   print list(m)
