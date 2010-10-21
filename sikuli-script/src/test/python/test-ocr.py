from sikuli.Sikuli import *

switchApp("System Preferences")
setAutoWaitTimeout(10)
click("System Preferences")
print "OCR text: ", find("test-res/sound.png").text() 
assert("Sound" in find("test-res/sound.png").text()) 
