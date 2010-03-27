from __future__ import with_statement
from sikuli.Sikuli import *

def changed(event):
   print "changed!: " + str(event)

def appeared(event):
   print "apple appeared " + str(event)

def vanished(event):
   print "apple vanished " + str(event)

with Region(0,0,300,300):
   onChange(changed)
   onAppear("test-res/apple.png", appeared)
   onVanish("test-res/apple.png", vanished)
   observe(5)
