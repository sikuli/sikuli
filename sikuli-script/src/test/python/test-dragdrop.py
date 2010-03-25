from sikuli.Sikuli import *


switchApp("System Preferences.app")
m = find(Pattern("test-res/sound-thumb.png").similar(0.5))
dragDrop(m, m.getCenter().left(200))
drag(m)
dropAt(m.getCenter().offset(200,10))
