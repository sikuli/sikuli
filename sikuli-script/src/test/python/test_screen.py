# -*- coding: utf-8 -*-
import unittest
from sikuli import *
from org.sikuli.script import JButtons

class TestScreen(unittest.TestCase):

   @classmethod
   def setUpClass(cls):
      pass

   @classmethod
   def tearDownClass(cls):
      pass

   def testCapture(self):
      simg = SCREEN.capture(0, 0, 100, 100)
      #simg = SCREEN.capture("message")

   def testContains(self):
       screen = Screen()
       w, h = screen.w//2, screen.h//2
       x, y = screen.x + w, screen.y + h
       
       self.assertIn((x, y), screen)
       self.assertIn(Location(x, y) in screen)
       self.assertIn((x, y, w, h) in screen)
       self.assertIn(Region(x, y, w, h) in screen)
       self.assertFalse((-1, -1) in screen)
       
       # test overlap case
       overlap = (-1, y, screen.w, h)
       self.assertFalse(overlap in screen, 
                        "Overlapping region {} is not completely within {}"
                        "".format(overlap, screen))
       
   def testInt(self):
       self.assertEqual(int(Screen()), 0, 
                        "Screen should be default screen with an id of 0")
       
   def testBool(self):
       self.assertTrue(bool(Screen()))
       self.assertFalse(bool(Screen(-1)))
