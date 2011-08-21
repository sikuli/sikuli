import unittest
from sikuli import *
from java.awt.event import KeyEvent
from javax.swing import JFrame

not_pressed = True
WAIT_TIME = 4

def pressed(event):
   global not_pressed
   not_pressed = False
   print "hotkey pressed! %d %d" %(event.modifiers,event.keyCode)

class TestHotkey(unittest.TestCase):
   def testAddHotkey(self):
      self.assertTrue(Env.addHotkey(Key.F6, 0, pressed))

   def testAddHotkeyReal(self):
      #f = JFrame("hello")
      global not_pressed
      Env.addHotkey(Key.F6, 0, pressed)
      self.assertTrue(not_pressed)
      count = 0
      while not_pressed and count < WAIT_TIME:
         count += 1
         wait(1)
         keyDown(Key.F6)
         keyUp(Key.F6)
      self.assertFalse(not_pressed) 
      #f.dispose()

   def testRemoveHotkey(self):
      self.assertFalse(Env.removeHotkey(Key.F7, 0))
      self.assertTrue(Env.addHotkey(Key.F7, 0, pressed))
      self.assertTrue(Env.removeHotkey(Key.F7, 0))


   def setUp(self):
      global not_pressed
      not_pressed = True

   @classmethod
   def tearDownClass(self):
      print "clean up"
      Env.cleanUp()

