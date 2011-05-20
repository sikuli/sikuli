# Copyright 2010-2011, Sikuli.org
# Released under the MIT License.
from sikuli.Sikuli import *


switchApp("System Preferences.app")
click("test-res/sound.png")
m = find(Pattern("test-res/sound-thumb.png").similar(0.5))
dragDrop(m, m.getCenter().left(200))
drag(m)
dropAt(m.getCenter().offset(200,10))
