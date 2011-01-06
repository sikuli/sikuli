from __future__ import with_statement
from sikuli.Sikuli import *

count = 0
def addCount():
   global count
   count+=1
   print "count: " + str(count)

def changed(event):
   print event.type == SikuliEvent.Type.CHANGE
   print "changed!: " + str(event)
   for ch in event.changes:
      ch.highlight()
   sleep(1)
   for ch in event.changes:
      ch.highlight()
   addCount()

def appeared(event):
   print "apple appeared " + str(event)
   addCount()

def vanished(event):
   print "apple vanished " + str(event)
   addCount()

with selectRegion("select an animated region") as r3: 
   onChange(30, changed)   
   print "start observing on r3..."
   observe(background=True)

with Region(0,0,200,200) as r1:
   onChange(changed)
   onAppear(Pattern("test-res/apple.png"), appeared)
   onVanish(Pattern("test-res/apple.png"), vanished)
   print "start observing on r1..."
   observe(time=FOREVER, background=True)
   addCount()

b = getBounds()
with Region(b.width-280,0,280,100) as r2: 
   onChange(10, changed)   # watch the system clock
   print "start observing on r2..."
   observe(background=True)



wait(30)
print "stop observing..."
r1.stopObserver()
r2.stopObserver()
r3.stopObserver()
