# Copyright 2010-2011, Sikuli.org
# Released under the MIT License.
import unittest
from sikuli import *
import os
import random
import time
from java.lang import System

class TestEnv(unittest.TestCase):
   def testOS(self):
      if Env.getOS() == OS.MAC:
         assert System.getProperty("os.name").startswith("Mac OS X")
      elif Env.getOS() == OS.WINDOWS:
         assert System.getProperty("os.name").startswith("Windows")
      elif Env.getOS() == OS.LINUX:
         assert System.getProperty("os.name").find("Linux") >= 0
      assert Env.getOSVersion() != None

   #@unittest.skipIf(Env.getOS == OS.LINUX, "XVnc on Linux doesn't support caps lock")
   def testCapsLock(self):
      if Env.getOS() == OS.LINUX: # XVnc on Linux doesn't support caps lock
         print "skipped, XVnc on Linux doesn't support caps lock"
         return
      lock = Env.isLockOn(Key.CAPS_LOCK)
      assert (lock == True or lock == False)

   def testSikuliVersion(self):
      ver = Env.getSikuliVersion()
      assert ver != None

   def testMouseLocation(self):
      b = getBounds()
      Settings.MoveMouseDelay = 0
      for i in range(0,10):
         x = random.randint(b.getX(),b.getWidth())
         y = random.randint(b.getY(),b.getHeight())
         hover(Location(x,y))
         time.sleep(0.1)
         cursor = Env.getMouseLocation()
         assert cursor.x == x and cursor.y == y

