import unittest
from sikuli import *
from org.sikuli.script import JButtons

class TestFind(unittest.TestCase):

   @classmethod
   def setUpClass(cls):
      cls.btns = JButtons()

   @classmethod
   def tearDownClass(cls):
      cls.btns.dispose() 

   def _tryFindWrongPath(self):
      setThrowException(True)
      setAutoWaitTimeout(0.1)
      try:
         find("network.png")
         self.fail("the image shouldn't exist")
      except FindFailed,e:
         assert e != None

   def testBundlePath(self):
      self._tryFindWrongPath()
      setBundlePath("test-res/")
      assert find("network.png") != None

   def testSikuliImagePath(self):
      from java.lang import System
      setBundlePath("/")
      self._tryFindWrongPath()
      System.setProperty("SIKULI_IMAGE_PATH", "test-res")
      assert find("network.png") != None


