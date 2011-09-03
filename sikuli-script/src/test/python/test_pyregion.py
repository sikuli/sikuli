from __future__ import with_statement
import unittest
from org.sikuli.script import Region as JRegion
from sikuli import *

class TestJavaRegion(unittest.TestCase):

   def testToJythonRegion(self):
      jr = JRegion(0, 0, 100, 100)
      pyr = JRegion.toJythonRegion(jr)
      try:
         with jr:
            self.fail("should not happen: __exit__ is not defined in Java.")
      except AttributeError,e:
         pass

      with pyr:
         pass # should not raise exception anymore

