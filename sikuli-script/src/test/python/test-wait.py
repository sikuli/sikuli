from __future__ import with_statement
from sikuli.Sikuli import *

with Region(10, 0, 200,200) as r:
   p = Pattern("test-res/apple.png").targetOffset(30,5)
   m = r.wait(p)
   print "center: " + str(r.lastMatch.getCenter())
   print "target(+30,+5): " + str(r.lastMatch.getTarget())
   click(m)
   apple= Pattern("test-res/apple.png").similar(0.99)
   wait(0.5)
   click(apple)
   p = Pattern("test-res/about-this-mac.png").similar(0.99)
   assert( find(p) != None)
   wait(0.5)
   click(apple)
   assert( waitVanish(p, 2) == True)


apple= Pattern("test-res/apple.png").similar(0.99)
print "ctrl-click below the apple icon"
apple.targetOffset(0,30)
click(apple, KEY_CTRL)
