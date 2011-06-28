import unittest
from sikuli import *

class TestKey(unittest.TestCase):

   def testCommonKeys(self):
      assert(Key.ENTER == "\n")
      assert(Key.BACKSPACE == "\b")
      assert(Key.TAB == "\t")

   def testModifiers(self):
      assert(KEY_CTRL != None)

   def testNewModifiers(self):
      assert(KeyModifier.CTRL == KEY_CTRL)
      assert(KeyModifier.SHIFT == KEY_SHIFT)
      assert(KeyModifier.META == KEY_META)
