# Copyright 2010-2011, Sikuli.org
# Released under the MIT License.
from sikuli.Sikuli import *

keyDown(Key.CMD + Key.ALT + Key.ESC)
keyUp(Key.ALT + Key.ESC)
wait(2)
keyDown("w")
keyUp()
