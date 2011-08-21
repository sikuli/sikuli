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
      self.assertTrue(Env.addHotkey(KeyEvent.VK_F1, 0, pressed))

   def testAddHotkeyReal(self):
      f = JFrame("hello")
      global not_pressed
      not_pressed = True
      Env.addHotkey(KeyEvent.VK_F1, 0, pressed)
      self.assertTrue(not_pressed)
      count = 0
      while not_pressed and count < WAIT_TIME:
         count += 1
         wait(1)
         keyDown(Key.F1)
      self.assertFalse(not_pressed) 

   def testRemoveHotkey(self):
      self.assertFalse(Env.removeHotkey(KeyEvent.VK_F7, 0))
      self.assertTrue(Env.addHotkey(KeyEvent.VK_F7, 0, pressed))
      self.assertTrue(Env.removeHotkey(KeyEvent.VK_F7, 0))
