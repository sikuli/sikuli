from edu.mit.csail.uid import Screen as JScreen
import inspect
import __main__

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
   # @param *args The args can be 4 integers: x, y, w, and h, a <a href="edu/mit/csail/uid/Match.html">Match</a> object or a {@link #Region} object.
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
         if __builtin__.type(args[0]) is types.StringType:
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

   def _exposeAllMethods(self):
      exclude_list = [ 'class', 'classDictInit', 'clone', 'equals', 'finalize', 
                       'getClass', 'hashCode', 'notify', 'notifyAll', 
                       'toGlobalCoord', 'toString',
                       'capture', 'selectRegion']
      for name in dir(self):
         if inspect.ismethod(getattr(self,name)) \
          and name[0] != '_' and name[:7] != 'super__' and \
          not name in exclude_list:
            if DEBUG: print "expose " + name
            #exec("__main__.%s = self.%s" %(name, name))
            __main__.__dict__[name] = eval("self."+name)

