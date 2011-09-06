from __future__ import with_statement
import unittest
from org.sikuli.script import Region as JRegion
from sikuli import *

class TestJavaRegion(unittest.TestCase):

   def testToJythonRegion(self):
      jr = JRegion(0, 0, 100, 100)
      pyr = JRegion.toJythonRegion(jr)
      self.assertEqual(jr.x, pyr.x)
      self.assertEqual(jr.y, pyr.y)
      self.assertEqual(jr.w, pyr.w)
      self.assertEqual(jr.h, pyr.h)
      try:
         with jr:
            self.fail("should not happen: __exit__ is not defined in Java.")
      except AttributeError,e:
         pass

#   @unittest.skip("a known bug. with Region doesn't work in modules.")
#   def testWithRegion(self):
#      jr = JRegion(0, 0, 100, 100)
#      pyr = JRegion.toJythonRegion(jr)
#
#      with pyr:
#         roi = getROI()
#         self.assertEqual(jr.x, roi.x)
#         self.assertEqual(jr.y, roi.y)
#         self.assertEqual(jr.w, roi.width)
#         self.assertEqual(jr.h, roi.height)
#         pass # should not raise exception anymore

