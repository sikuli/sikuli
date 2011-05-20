# Copyright 2010-2011, Sikuli.org
# Released under the MIT License.
from __future__ import with_statement
from sikuli.Sikuli import *

print Env.getOS(), Env.getOSVersion()
print "MAC?", Env.getOS() == OS.MAC
print "Mouse location:", Env.getMouseLocation()
print "Caps lock:", Env.isLockOn(Key.CAPS_LOCK)
print "Sikuli version:", Env.getSikuliVersion()
