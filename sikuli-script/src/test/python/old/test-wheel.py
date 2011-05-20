# Copyright 2010-2011, Sikuli.org
# Released under the MIT License.
from sikuli.Sikuli import *

print "wheel up..."
wheel(WHEEL_UP, 10)
sleep(1)
print "wheel down..."
wheel(None, WHEEL_DOWN, 10)
sleep(1)

