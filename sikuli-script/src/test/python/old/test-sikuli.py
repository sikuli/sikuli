# Copyright 2010-2011, Sikuli.org
# Released under the MIT License.
from sikuli.Sikuli import *

print "number of screens: " + str(Screen.getNumberScreens())
s = Screen()
print "default screen bound: " + str(s.getBounds())
print s.find("test-res/apple.png")
print "screen's last match: " +  str(s.getLastMatch())
r = Region(200,200,50,50)
r.setThrowException(False)
r.setAutoWaitTimeout(0.5)
print r.find("test-res/apple.png")
print "region's last match: " +  str(r.getLastMatch())
