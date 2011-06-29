import unittest
#from sikuli import *
from org.sikuli.script.android import AndroidScreen

class TestAndroidBasic(unittest.TestCase):

   @classmethod
   def setUpClass(cls):
      cls.s = AndroidScreen()
      assert cls.s != None

   def testBounds(self):
      bounds = self.s.getBounds()
      assert(bounds.x == 0)
      assert(bounds.y == 0)
      assert(bounds.width > 0)
      assert(bounds.height > 0)

   def testCapture(self):
      img = self.s.capture()
      assert img != None

