# Imports the monkeyrunner modules used by this program
from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice
from org.sikuli.script import Finder
from org.sikuli.script import Settings

Settings.BundlePath = 'monkey-test1.sikuli/'

# Connects to the current device, returning a MonkeyDevice object
device = MonkeyRunner.waitForConnection()

def screenshot():
   filename = 'monkey-shot-tmp.png'
   result = device.takeSnapshot()
   result.writeToFile(filename, 'png')
   return filename

def find(img):
   f = Finder(screenshot())
   f.find(img)
   if f.hasNext():
      return f.next()
   return None

#device.press('KEYCODE_HOME','DOWN_AND_UP')
m = find("dial.png")
device.touch(m.x, m.y, 'DOWN_AND_UP')
device.type("123456789")
m = find("dial.png")
device.touch(m.x, m.y, 'DOWN_AND_UP')

