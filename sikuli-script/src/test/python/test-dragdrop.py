from sikuli.Sikuli import *


switchApp("System Preferences.app")
m = find(Pattern("test-res/sound-thumb.png").similar(0.5))
dragDrop(m, [m.x-200, m.y])
drag(m)
dropAt(Location(0,0))
