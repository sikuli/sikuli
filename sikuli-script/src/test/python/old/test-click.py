# Copyright 2010-2011, Sikuli.org
# Released under the MIT License.
from __future__ import with_statement
from sikuli.Sikuli import *

setBundlePath("test-res/")
with Region(0,0,100,100):
   click("apple.png",0)
with SCREEN:
   click(Pattern("about-this-mac.png").similar(0.6),0)



