# Copyright 2010-2011, Sikuli.org
# Released under the MIT License.
from org.sikuli.script import Screen as JScreen
import inspect
import __main__
import __builtin__
import sys

from Region import *
from java.awt import Rectangle

DEBUG=False

class Screen(Region):

   def __init__(self, id=None):
      if id != None:
         r = JScreen.getBounds(id)
      else:
         r = JScreen().getBounds()
      (x, y, w, h) = (int(r.getX()), int(r.getY()), \
                      int(r.getWidth()), int(r.getHeight()))
      Region.__init__(self, x, y, w, h)
      self.id = int(id) if id else 0

   def __int__(self):
       return self.id

   # iterates over screens - may be useful to search for something in each screen
   def __iter__(self):
      return iter(self.__class__(i) for i in range(len(self)))
   
   # returns whether screen has a valid id number
   def __bool__(self):
       return self.id >= 0 and self.id < len(self)
    
   # shorthand for getting the number of screens
   def __len__(self):
       return JScreen.getNumberScreens() 
   
   def __contains__(self, other):
       ''' Checks whether other is (completely) within the given screen 
       
       Arguments:
           other (Tuple|List|Region|Location) - the item to check
       
       Returns:
           bool
       '''
       from collections import Iterable
       x, y = getattr(other, 'x', 0), getattr(other, 'y', 0)
       w, h = getattr(other, 'w', 0), getattr(other, 'h', 0)

       if isinstance(other, Iterable) and not isinstance(self.__class__):
           if len(other) == 2:
              x, y = other
           elif len(other) == 4:
              x, y, w, h = other
       elif isinstance(other, Rectangle):
           x, y, w, h = (other.getX(), other.getY(), 
                         other.getWidth(), other.getHeight())
       within = (x >= self.x and x+w <= self.x+self.w and 
                 y >= self.y and y+h <= self.y+self.h)

       screen = other.getScreen() if (getattr(other, 'getScreen', None) and 
                                      callable(other.getScreen)) else None
       if not screen:
           return within
       else:
           # cheat for screen compare as cannot guarantee int/hash methods work
           # this is probably not needed as Regions map to screens based on coordinates 
           return within and repr(screen) == repr(self) 
   
   def __hash__(self):
       return self.id
   
   def __cmp__(self, other):
       return int(self) - int(other)
    
   def __eq__(self, other):
       return hash(self) == hash(other)

   def __ne__(self, other):
       return not self.__eq__(other)
            
   @classmethod
   def getNumberScreens(cls):
      return JScreen.getNumberScreens()

   def getBounds(self):
      return self.getScreen().getBounds()

   def selectRegion(self, msg=None):
      if msg:
         r = self.getScreen().selectRegion(msg)
      else:
         r = self.getScreen().selectRegion()
      if r:
         return Region(r)
      else:
         return None

   def showRegion(self, region):
      self.getScreen().showRegion(region)

   ##
   # Enters the screen-capture mode asking the user to capture a region of 
   # the screen if no arguments are given.
   # If any arguments are specified, capture() automatically captures the given
   # region of the screen.
   # @param *args The args can be 4 integers: x, y, w, and h, a <a href="org/sikuli/script/Match.html">Match</a> object or a {@link #Region} object.
   # @return The path to the captured image.
   #
   def capture(self, *args):
      scr = self.getScreen()
      if len(args) == 0:
         simg = scr.userCapture()
         if simg: 
            return simg.getFilename()
         else:
            return None
      elif len(args) == 1:
         if __builtin__.type(args[0]) is types.StringType or __builtin__.type(args[0]) is types.UnicodeType:
            simg = scr.userCapture(args[0])
            if simg:
               return simg.getFilename()
            else:
               return None
         else:
            return scr.capture(args[0]).getFilename()
      elif len(args) == 4:
         return scr.capture(args[0], args[1], args[2], args[3]).getFilename()
      else:
         return None

   def toString(self):
      return self.getScreen().toString()

   def _exposeAllMethods(self, mod):
      exclude_list = [ 'class', 'classDictInit', 'clone', 'equals', 'finalize', 
                       'getClass', 'hashCode', 'notify', 'notifyAll', 
                       'toGlobalCoord', 'toString',
                       'capture', 'selectRegion']
      dict = sys.modules[mod].__dict__
      for name in dir(self):
         if inspect.ismethod(getattr(self,name)) \
          and name[0] != '_' and name[:7] != 'super__' and \
          not name in exclude_list:
            if DEBUG: print "expose " + name
            dict[name] = eval("self."+name)
            #__main__.__dict__[name] = eval("self."+name)
  