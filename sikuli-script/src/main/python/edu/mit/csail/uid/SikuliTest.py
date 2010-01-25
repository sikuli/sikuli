from Sikuli import *
from junit.framework.Assert import *

def assertExist(pattern, region=None):
   si = getSikuliScript()
   if not region:
      assertTrue( find(pattern) != None)
   else:
      assertTrue( si._find(pattern, region) != None)

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



def helloSikuliTest():
   print "hello"
