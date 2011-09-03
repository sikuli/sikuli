from __future__ import with_statement
import unittest
from sikuli import *

class TestApp(unittest.TestCase):

   def setUp(self):
      if Env.isWindows():
         self.app = "notepad"
      elif Env.isMac():
         self.app = "TextEdit.app"
      elif Env.isLinux():
         self.app = "xeyes"
      self.app = App(self.app).open()

   def tearDown(self):
      self.app.close()

   def testAppWindow(self):
      win = None
      t = 0
      while win == None and t < 10:
         win = self.app.window()
         t += 1
         wait(1)
      self.assertTrue(win != None)
      with win:
         pass  # __enter__ and __exit__ need to exist
