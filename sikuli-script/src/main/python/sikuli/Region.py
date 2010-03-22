from edu.mit.csail.uid import Region as JRegion
import __main__

class Region(JRegion):
   # override all global sikuli functions by Region's methods.
   def __enter__(self):
      for name in dir(self):
         if inspect.ismethod(getattr(self,name)) and __main__.__dict__.has_key(name):
            __main__.__dict__["__old_"+name] = __main__.__dict__[name]
            __main__.__dict__[name] = eval("self."+name)

   def __exit__(self, type, value, traceback):
      for name in dir(__main__):
         if name[0:6] == "__old_":
            orig_name = name[6:]
            __main__.__dict__[orig_name] = __main__.__dict__[name]
            del __main__.__dict__[name]


   def wait(self, target, timeout=3):
      print self, target, timeout
      return JRegion.wait(self, target, timeout)
