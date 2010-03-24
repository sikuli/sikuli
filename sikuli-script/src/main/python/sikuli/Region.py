from edu.mit.csail.uid import Region as JRegion
import inspect
import __main__

class Region(JRegion):

   def __init__(self, x, y, w, h):
      JRegion.__init__(self, x, y, w, h)
      self.lastMatch = None
      self.lastMatches = None

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

   #######################################################################
   #---- SIKULI  PUBLIC  API
   #######################################################################

   ##
   # Looks for the best match of a particular GUI element to interact with. 
   # It takes the file name of
   # an image that specifies the element's appearance, 
   # searches the whole screen 
   # and returns the best region matching this pattern or 
   # None if no such region can be found. <br/>
   # In addition to the return value, find() also stores the returning matched 
   # region in find.region. <br/>
   # If the auto waiting timeout ({@link #setAutoWaitTimeout}) is set to 
   # a non-zero
   # value, all find() just act as the {@link #wait} method.
   # @param img The file name of an image, which can be an absolute path or a relative path to file in the source bundle (.sikuli). It also can be a <a href="edu/mit/csail/uid/Pattern.html">Pattern</a> object.
   # @return a <a href="edu/mit/csail/uid/Match.html">Match</a> object that contains the best matching region, or None if nothing is found.
   #
   def find(self, target):
      ret = JRegion.find(self, target)
      if ret != None:
         self.lastMatch = ret
      return ret

   ##
   # Looks for all instance of a particular GUI element to interact with. 
   # It takes the file name of an image that specifies the element's 
   # appearance, searches the whole screen 
   # and returns the regions matching this pattern 
   # or None if no such region can be found. <br/>
   # In addition to the return value, findAll() also stores the returning matched 
   # regions in find.regions and the best matched region in find.region. <br/>
   # If the auto waiting timeout ({@link #setAutoWaitTimeout}) is set to a non-zero
   # value, all findAll() just act as the {@link #wait} method.
   # @param img The file name of an image, which can be an absolute path or a relative path to file in the source bundle (.sikuli). It also can be a <a href="edu/mit/csail/uid/Pattern.html">Pattern</a> object.
   # @return a <a href="edu/mit/csail/uid/Matches.html">Matches</a> object that contains a list of <a href="edu/mit/csail/uid/Match.html">Match</a> objects, or None if nothing is found.
   #
   def findAll(self, target):
      ret = JRegion.findAll(self, target)
      if ret != None:
         self.lastMatch = ret[0]
         self.lastMatches = ret
      return ret

   ##
   # Keeps searching the given image on the screen until the image appears or 
   # the specified amount of time has elapsed.
   # @param img The file name of an image, which can be an absolute path or a relative path to the file in the source bundle (.sikuli).
   # @param timeout The amount of waiting time, in milliseconds. This value orverrides the auto waiting timeout set by {@link #setAutoWaitTimeout}.
   # @return a <a href="edu/mit/csail/uid/Matches.html">Matches</a> object that contains a list of <a href="edu/mit/csail/uid/Match.html">Match</a> objects, or None if timeout occurs.
   def wait(self, target, timeout=3):
      ret = JRegion.wait(self, target, timeout)
      if ret != None:
         self.lastMatch = ret
      return ret
   
   ##
   # Performs a mouse clicking on the best matched position of the 
   # given image pattern. It calls
   # find() to locate the pattern if a file name or a <a href="edu/mit/csail/uid/Pattern.html">Pattern</a> object is given.
   # @param img The file name of an image; a <a href="edu/mit/csail/uid/Pattern.html">Pattern</a> object; a <a href="edu/mit/csail/uid/Match.html">Match</a> object; or a <a href="edu/mit/csail/uid/Matches.html">Matches</a> object.
   # @param modifiers The key modifiers. This can be one modifier or union of multiple modifiers combined by the OR(|) operator.
   # @return The number of performed clicking. <br/> Returns -1 if find() fails.
   def click(self, target, modifiers=0):
      return JRegion.click(self, target, modifiers)

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

