from edu.mit.csail.uid import Region as JRegion
import inspect
import __main__

class Region(JRegion):
   # override all global sikuli functions by Region's methods.
   def __enter__(self):
      self._global_funcs = {}
      for name in dir(self):
         if inspect.ismethod(getattr(self,name)) and __main__.__dict__.has_key(name):
            self._global_funcs[name] = __main__.__dict__[name]
            #print "save " + name + " :" + str(__main__.__dict__[name])
            __main__.__dict__[name] = eval("self."+name)

   def __exit__(self, type, value, traceback):
      for name in self._global_funcs.keys():
         #print "restore " + name + " :" + str(self._global_funcs[name])
         __main__.__dict__[name] = self._global_funcs[name]


   def wait(self, target, timeout=3):
      return JRegion.wait(self, target, timeout)
   
   ##
   # Sets the flag of throwing exceptions if {@link #find find()} fails. <br/>
   # Setting this flag to <i>True</i> enables all methods that use 
   # find() throws an exception if the find()
   # can not find anything similar on the screen.
   # Once the flag is set to <i>False</i>, all methods that use find()
   # just return <i>None</i> if nothing is found. <br/>
   # The default value of thie flag is <i>True</i>.
   #
   def setThrowException(self, flag):
      return JRegion.setThrowException(self, flag)

   ##
   # Sets the maximum waiting time in seconds for {@link #find find()}. <br/>
   # Setting this time to a non-zero value enables all methods that use find()
   # wait the appearing of the given image pattern until the specified amount of
   # time has elapsed. <br/>
   # The default timeout is <i>3.0 sec</i>.
   #
   def setAutoWaitTimeout(self, sec):
      return JRegion.setAutoWaitTimeout(self, sec)

