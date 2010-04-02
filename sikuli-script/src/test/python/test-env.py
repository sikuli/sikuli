from __future__ import with_statement
from sikuli.Sikuli import *

print Env.getOS(), Env.getOSVersion()
print "MAC?", Env.getOS() == OS.MAC
print Env.getMouseLocation()
