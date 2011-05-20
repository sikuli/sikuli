# Copyright 2010-2011, Sikuli.org
# Released under the MIT License.
from __future__ import with_statement
from sikuli.Sikuli import *


def changed(event):
   print "screen changed! " + str(event)
   event.region.stopObserver()

n = getNumberScreens()
print "number of screens: %d" % n
screens = []
for i in range(n):
   screens += [Screen(i)]
   print "screen(%d): %s" % (i, str(screens[i].getBounds()))

if n > 1:
   #print "apple on screen(0)? " + str(screens[0].exists("test-res/apple.png"))
   #print "apple on screen(1)? " + str(screens[1].exists("test-res/apple.png"))

   popup("select a region on Screen(1)")
   r = screens[1].selectRegion()
   print "select: " + str(r.getROI())
   with r:
      onChange(changed)
      observe(background=True)

   popup("select a region on Screen(0)")
   r2 = screens[0].selectRegion()
   with r2:
      onChange(changed)
      observe(background=False)
