from edu.mit.csail.uid import Screen as JScreen
import inspect
import __main__

from Region import *

DEBUG=False

class Screen(Region):
   def __init__(self, id=None):
      if id != None:
         r = JScreen.getBounds(id)
      else:
         r = self.getBounds()
      (x, y, w, h) = (int(r.getX()), int(r.getY()), \
                      int(r.getWidth()), int(r.getHeight()))
      Region.__init__(self, x, y, w, h)

   @classmethod
   def getNumberScreens(cls):
      return JScreen.getNumberScreens()

   @classmethod
   def getBounds(cls, screen_id):
      return JScreen.getBounds(screen_id)

   def getBounds(self):
      return JScreen().getBounds()

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
         return scr.userCapture().getFilename()
      else:
         if isinstance(args[0],JRegion):
            r = args[0]
            return scr.capture(r.x, r.y, r.w, r.h).getFilename()
         elif len(args) == 4:
            return scr.capture(args[0], args[1], args[2], args[3]).getFilename()
         else:
            return None

   def _exposeAllMethods(self):
      exclude_list = [ 'classDictInit', 'clone', 'equals', 'finalize', 
                       'getClass', 'hashCode', 'notify', 'notifyAll', 
                       'super__wait', 'toGlobalCoord', 'toString' ]
      for name in dir(self):
         if inspect.ismethod(getattr(self,name)) \
          and name[0] != '_' and not name in exclude_list:
            if DEBUG: print "expose " + name
            __main__.__dict__[name] = eval("self."+name)

