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
