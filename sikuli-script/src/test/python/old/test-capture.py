# Copyright 2010-2011, Sikuli.org
# Released under the MIT License.
from __future__ import with_statement
from sikuli.Sikuli import *


assert(selectRegion("cancel this selection") == None)
assert(selectRegion("customized selectRegion message") != None)
assert(capture("customized capture message") != None)
print Rectangle(0,0,100,100)
assert(capture(0, 0, 100, 100) != None)
assert(capture(Rectangle(0,0,100,100)) != None)
assert(capture(Region(0,0,100,100)) != None)
popup("please select a region")
assert(capture() != None)


