from sikuli.Sikuli import *
#from edu.mit.csail.uid import VisionProxy
#from edu.mit.csail.uid import DebugCategories

switchApp("System Preferences")
setAutoWaitTimeout(10)
#click("System Preferences")
#VisionProxy.setDebug(DebugCategories.OCR,1)
print "OCR text: ", find("test-res/sound.png").text() 
assert("Sound" in find("test-res/sound.png").text()) 
