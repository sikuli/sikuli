from __future__ import with_statement
from sikuli.Sikuli import *

if getNumberScreens()>1:
   s = Screen(1)
else:
   s = SCREEN

with s:
   setShowActions(True)
   setBundlePath("test-res")
   app = App("System Preferences.app")
   app.open()
   win = app.window()
   win.highlight()
   click("show-all.png")
   win.highlight()
   click("sound.png")
   thumb = find(Pattern("sound-thumb.png").similar(0.5))
   dragDrop(thumb, thumb.getCenter().left(100))
exit(0)
