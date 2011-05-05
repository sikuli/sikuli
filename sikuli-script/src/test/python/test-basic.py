# Copyright 2010-2011, Sikuli.org
# Released under the MIT License.
from __future__ import with_statement
from sikuli.Sikuli import *


if Key.ENTER: print "Key exists"

r = Region(0,0,100,100)
if r: print "Region exists"

s = Screen()
if s: print "Screen exists"

if getBounds(): print "global functions exposed"

v = VDict()
v["test-res/apple.png"]="apple"
if v["test-res/apple.png"][0] == "apple": 
   print "VDict works"

setThrowException(True)
if exists(Pattern("test-res/apple.png").similar(0.9)): 
   print "apple exists " + str(SCREEN.getLastMatch())
else: # should not throw exception
   print "apple doesn not exist"

