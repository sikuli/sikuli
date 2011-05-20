# Copyright 2010-2011, Sikuli.org
# Released under the MIT License.
from __future__ import with_statement
from sikuli import *

pref = App("System Preferences").open()
setAutoWaitTimeout(10)
#click("System Preferences")
#VisionProxy.setDebug(DebugCategories.OCR,1)
with Region(pref.window()):
   print "OCR text: ", find("test-res/sound.png").text() 
   assert("Sound" in find("test-res/sound.png").text()) 
   click("Sound")
