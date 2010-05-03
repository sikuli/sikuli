from __future__ import with_statement
from sikuli.Sikuli import *

print wait("test-res/apple.png",0)
assert( waitVanish("test-res/about-this-mac.png", 0) == True)
with Region(10, 0, 200,200) as r:
   p = Pattern("test-res/apple.png").targetOffset(30,5)
   r.setAutoWaitTimeout(1)
   m = r.wait(p)
   print m
   print "center: " + str(r.getLastMatch().getCenter())
   print "target(+30,+5): " + str(r.getLastMatch().getTarget())
   print "click 1"
   click(m)
   wait(1)
   click(m)
   apple = find(Pattern("test-res/apple.png").similar(0.96))
   click(apple)
   wait(1)
   p = Pattern("test-res/about-this-mac.png").similar(0.8)
   assert( find(p) != None)
   wait(1)
   click(apple)
   assert( waitVanish(p, 2) == True)


apple= Pattern("test-res/apple.png").similar(0.96)
print "ctrl-click below the apple icon"
apple.targetOffset(0,30)
click(apple, KEY_CTRL)
