import unittest
from sikuli import *

class TestBasic(unittest.TestCase):
   def testImport(self):
      from sikuli import Region

   def testGlobalFunction(self):
      assert SCREEN != None
      assert getBounds() != None

   def testBasicClass(self):
      assert Region(0,0,100,100) != None
      assert Screen() != None
      assert Key.ENTER != None
      assert FOREVER != None
