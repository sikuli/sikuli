from __future__ import with_statement
from sikuli.Sikuli import *

count = 0
def addCount():
   global count
   count+=1
   print "count: " + str(count)

def changed(event):
   print "changed!: " + str(event)
   addCount()

def appeared(event):
   print "apple appeared " + str(event)
   addCount()

def vanished(event):
   print "apple vanished " + str(event)
   addCount()

with Region(0,0,200,200) as r1:
   onChange(changed)
   onAppear("test-res/apple.png", appeared)
   onVanish("test-res/apple.png", vanished)
   print "start observing on r1..."
   observe(background=True)
   addCount()

with Region(1000,0,280,100) as r2: 
   onChange(changed)   # watch the system clock
   print "start observing on r2..."
   observe(background=True)

wait(8)
print "stop observing..."
r1.stopObserver()
r2.stopObserver()
