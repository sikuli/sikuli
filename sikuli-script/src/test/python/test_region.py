import unittest
from sikuli import *
from org.sikuli.script import JButtons

class TestRegion(unittest.TestCase):

   @classmethod
   def setUpClass(cls):
      cls.r = Region(0,0,100,100)
      cls.r.setThrowException(False)
      cls.r.setAutoWaitTimeout(0)
      cls.img = "test-res/network.png"

   @classmethod
   def tearDownClass(cls):
      pass

   def testFind(self):
      self.r.find(self.img)

   def testFindAll(self):
      self.r.findAll(self.img)

   def testWait(self):
      self.r.wait(0.1)
      self.r.wait(self.img)
      self.r.wait(self.img, 0.1)
      try:
         self.r.wait(self.img, 1, "blah")
         self.fail("wait() should not take so many arguments.")
      except TypeError,e:
         assert e != None

   def testWaitVanish(self):
      self.r.waitVanish(self.img)
      self.r.waitVanish(self.img, 0.1)

   def testExists(self):
      self.r.exists(self.img)
      self.r.exists(self.img, 0.1)

   def testClick(self):
      self.r.click(self.img)
      self.r.click(self.img, KEY_CTRL)

   def testDoubleClick(self):
      self.r.doubleClick(self.img)
      self.r.doubleClick(self.img, KEY_CTRL)

   def testRightClick(self):
      self.r.rightClick(self.img)
      self.r.rightClick(self.img, KEY_CTRL)

   def testHover(self):
      self.r.hover(self.img)

   def testType(self):
      self.r.type("a string")
      self.r.type(self.img, "a string")
      self.r.type(self.img, "a string", KEY_SHIFT)
