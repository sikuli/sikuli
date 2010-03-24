from sikuli.Sikuli import *

print "number of screens: " + str(Screen.getNumberScreens())
s = Screen()
print "default screen bound: " + str(s.getBounds())
print s.find("test-res/apple.png")
print "screen's last match: " +  str(s.lastMatch)
r = Region(200,200,50,50)
r.setThrowException(False)
r.setAutoWaitTimeout(0.5)
print r.find("test-res/apple.png")
print "region's last match: " +  str(r.lastMatch)
