from __future__ import with_statement
from sikuli import *

r = SCREEN.createRegion(Region(1,2,3,4))
with r:
   assert(r.x==1)
   assert(r.y==2)
   pass # only Python Region would work
#r.find

