# Copyright 2010-2011, Sikuli.org
# Released under the MIT License.
from __future__ import with_statement
from sikuli.Sikuli import *

r = Region(300,300,100,100)
print "region at (300,300) w100 x h100"
print "nearby: ", r.nearby()
print "left: ", r.left()
print "above: ", r.above()
print "right: ", r.right()
print "below: ", r.below()

r = Region(0,0,100,100)
print "region at (0,0) w100 x h100"
print "nearby: ", r.nearby()
