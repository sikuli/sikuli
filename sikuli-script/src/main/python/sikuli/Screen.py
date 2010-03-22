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
      JRegion.__init__(self, x, y, w, h)

   @classmethod
   def getNumberScreens(cls):
      return JScreen.getNumberScreens()

   @classmethod
   def getBounds(cls, screen_id):
      return JScreen.getBounds(screen_id)

   def getBounds(self):
      return JScreen().getBounds()

   def _exposeAllMethods(self):
      exclude_list = [ 'classDictInit', 'clone', 'equals', 'finalize', 
                       'getClass', 'hashCode', 'notify', 'notifyAll', 
                       'super__wait', 'toGlobalCoord', 'toString' ]
      for name in dir(self):
         if inspect.ismethod(getattr(self,name)) \
          and name[0] != '_' and not name in exclude_list:
            if DEBUG: print "expose " + name
            __main__.__dict__[name] = eval("self."+name)

