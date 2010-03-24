from sikuli.Sikuli import *

r = Region(10, 0, 200,200)
p = Pattern("test-res/apple.png").targetOffset(30,5)
m = r.wait(p)
click(m)
print "center: " + str(r.lastMatch.getCenter())
print "target(+30,+5): " + str(r.lastMatch.getTarget())

print "ctrl-click below the apple icon"
p.targetOffset(0,30)
click(p, KEY_CTRL)

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
