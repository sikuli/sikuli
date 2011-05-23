import unittest
from sikuli import *
from java.io import FileNotFoundException

class TestVDict(unittest.TestCase):

   def setUp(self):
      self.dict = VDict()

   def testInit(self):
      assert len(self.dict) == 0

   def testFileNotFound(self):
      try:
         self.dict["not-exist-image"] = 1 # should throws FileNotFoundException
         self.fail("the image should not exist")
      except FileNotFoundException:
         pass

      try:
         print self.dict["not-exist-image"]  # should throws FileNotFoundException
         self.fail("the image should not exist")
      except FileNotFoundException:
         pass

   def testInsert(self):
      self.dict["test-res/1.png"] = 1
      self.dict["test-res/2a.png"] = 2
      self.dict["test-res/2b.png"] = 3
      dict2 = VDict()
      assert len(self.dict.keys()) == 3
      assert len(self.dict) == 3
      assert len(dict2.keys()) == 0


   def testLookup(self):
      self.dict["test-res/1.png"] = 1
      self.dict["test-res/2a.png"] = 2
      assert len(self.dict) == 3
      assert len(dict2.keys()) == 0


   def testLookup(self):
      self.dict["test-res/1.png"] = 1
      self.dict["test-res/2a.png"] = 2
      self.dict["test-res/2b.png"] = 3
      assert "test-res/1.png" in self.dict
      assert "test-res/2.png" in self.dict
      assert not ("test-res/big.png" in self.dict)

      vals = self.dict["test-res/2.png"]
      assert vals[0] == 2
      assert vals[1] == 3
      vals = self.dict["test-res/big.png"]
      assert len(vals) == 0
      assert len(self.dict.keys()) == 3


   def testLookup(self):
      self.dict["test-res/1.png"] = 1
      self.dict["test-res/2a.png"] = 2
      self.dict["test-res/2b.png"] = 3
      assert "test-res/1.png" in self.dict
      assert "test-res/2.png" in self.dict
      assert not ("test-res/big.png" in self.dict)

      vals = self.dict["test-res/2.png"]
      assert vals[0] == 2
      assert vals[1] == 3
      vals = self.dict["test-res/big.png"]
      assert len(vals) == 0
      assert len(self.dict.keys()) == 3

   def testDelete(self):
      self.dict["test-res/1.png"] = 1
      self.dict["test-res/2a.png"] = 2
      assert len(self.dict.keys()) == 2
      del self.dict["test-res/1.png"]
      assert len(self.dict.keys()) == 1
      del self.dict["test-res/2a.png"]
      assert len(self.dict.keys()) == 0

