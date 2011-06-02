# Copyright 2010-2011, Sikuli.org
# Released under the MIT License.
from math import sqrt

def byDistanceTo(m):
   return lambda a,b: sqrt((a.x-m.x)**2+(a.y-m.y)**2) - sqrt((b.x-m.x)**2+(b.y-m.y)**2)

def byX(m):
   return m.x

def byY(m):
   return m.y
