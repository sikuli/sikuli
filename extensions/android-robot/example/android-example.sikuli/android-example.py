import unittest
#import os
from org.sikuli.script.android import AndroidScreen
from org.sikuli.script import Settings

print "set up android connection"
scr = AndroidScreen() # compatiable with Sikuli Screen/Region
assert scr != None
dev = scr.getRobot().getDevice() # Android Monkey device

#Settings.BundlePath = os.getcwd() + "/test-res/"
Settings.MoveMouseDelay = 0
Settings.AutoWaitTimeout = 10


class TestAndroidBasic(unittest.TestCase):

   def testBounds(self):
      global scr, dev
      bounds = scr.getBounds()
      assert(bounds.x == 0)
      assert(bounds.y == 0)
      assert(bounds.width > 0)
      assert(bounds.height > 0)

   def testCapture(self):
      global scr, dev
      img = scr.capture()
      assert img != None

   def testDrag(self):
      global scr, dev
      if not scr.exists("lock.png"):
         dev.press('KEYCODE_POWER', 'DOWN_AND_UP')
      scr.dragDrop("lock.png", "speaker.png")

   def testType(self):
      global scr, dev
      dev.press('KEYCODE_HOME', 'DOWN_AND_UP')
      scr.click("google.png")
      scr.type("sikuli")
      assert scr.exists("search-sikuli.png")

if __name__ == '__main__':
   unittest.main()
