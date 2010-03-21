##
# This module provides a Jython interface of Sikuli Test
##
from Sikuli import *
from junit.framework.Assert import *

##
# Asserts that the given <i>pattern</i> appears on the screen (if no <i>region</i> is given) or in the given <i>region</i>.
# @param pattern The file name of an image, which can be an absolute path or a relative path to file in the source bundle (.sikuli). It also can be a {@link #Pattern} object.
# @param region This can be a file name of an image.
# @exception AssertionError if the pattern can't be found.
def assertExist(pattern, region=None):
   si = getSikuliScript()
   if not region:
      assertTrue( find(pattern) != None)
   else:
      assertTrue( si._find(pattern, region) != None)

##
# Asserts that the given <i>pattern</i> doesn't appear on the screen (if no <i>region</i> is given) or in the given <i>region</i>.
# @param pattern The file name of an image, which can be an absolute path or a relative path to file in the source bundle (.sikuli). It also can be a {@link #Pattern} object.
# @param region This can be a file name of an image.
# @exception AssertionError if the pattern can be found.
def assertNotExist(pattern, region=None):
   si = getSikuliScript()
   #FIXME: region is not supported yet
   if si.getAutoWaitTimeout() != 0:
      untilNotExist(pattern)
      return
   if not region:
      assertTrue( find_without_wait(pattern) == None)
   else:
      assertTrue( si._find(pattern, region) == None)

