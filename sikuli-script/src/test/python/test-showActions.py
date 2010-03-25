from __future__ import with_statement
from sikuli.Sikuli import *


setShowActions(True)
setBundlePath("test-res")
switchApp("System Preferences.app")
click("show-all.png")
click("sound.png")
thumb = find(Pattern("sound-thumb.png").similar(0.5))
dragDrop(thumb, thumb.getCenter().left(100))
