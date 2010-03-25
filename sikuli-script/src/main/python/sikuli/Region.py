from edu.mit.csail.uid import Region as JRegion
from edu.mit.csail.uid import Location
from edu.mit.csail.uid import Settings
import inspect
import types
import time
import java.lang.String
import __main__
import __builtin__

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
      return self

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
         self.lastMatches = ret
      return ret

   ##
   # Keeps searching the given image on the screen until the image appears or 
   # the specified amount of time has elapsed.
   # @param img The file name of an image, which can be an absolute path or a relative path to the file in the source bundle (.sikuli).
   # @param timeout The amount of waiting time, in milliseconds. This value orverrides the auto waiting timeout set by {@link #setAutoWaitTimeout}.
   # @return a <a href="edu/mit/csail/uid/Matches.html">Matches</a> object that contains a list of <a href="edu/mit/csail/uid/Match.html">Match</a> objects, or None if timeout occurs.
   def wait(self, target, timeout=3):
      ttype = __builtin__.type(target)
      if ttype is types.IntType or ttype is types.FloatType:
         time.sleep(target)
         return
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
   # Performs a double clicking on the best matched position of the given 
   # image pattern. It calls
   # find() to locate the pattern if a file name or a <a href="edu/mit/csail/uid/Pattern.html">Pattern</a> object is given.
   # @param img The file name of an image; a <a href="edu/mit/csail/uid/Pattern.html">Pattern</a> object; a <a href="edu/mit/csail/uid/Match.html">Match</a> object; or a <a href="edu/mit/csail/uid/Matches.html">Matches</a> object.
   # @param modifiers The key modifiers. This can be one modifier or union of multiple modifiers combined by the OR(|) operator.
   # @return The number of performed clicking. <br/> Returns -1 if find() fails.
   def doubleClick(self, target, modifiers=0):
      return JRegion.doubleClick(self, target, modifiers)

   ##
   # Performs a right clicking on the best matched position of the given 
   # image pattern. It calls
   # find() to locate the pattern if a file name or a <a href="edu/mit/csail/uid/Pattern.html">Pattern</a> object is given.
   # @param img The file name of an image; a <a href="edu/mit/csail/uid/Pattern.html">Pattern</a> object; a <a href="edu/mit/csail/uid/Match.html">Match</a> object; or a <a href="edu/mit/csail/uid/Matches.html">Matches</a> object.
   # @param modifiers The key modifiers. This can be one modifier or union of multiple modifiers combined by the OR(|) operator.
   # @return The number of performed clicking. <br/> Returns -1 if find() fails.
   def rightClick(self, target, modifiers=0):
      return JRegion.rightClick(self, target, modifiers)

   ##
   # Move the mouse cursor to the best matched position of the 
   # given image pattern. It calls
   # find() to locate the pattern if a file name or a <a href="edu/mit/csail/uid/Pattern.html">Pattern</a> object is given.
   # @param img The file name of an image; a <a href="edu/mit/csail/uid/Pattern.html">Pattern</a>  object; a <a href="edu/mit/csail/uid/Match.html">Match</a> object; or a <a href="edu/mit/csail/uid/Matches.html">Matches</a> object.
   # @return 0 <br/> Returns -1 if find() fails.
   def hover(self, target):
      return JRegion.hover(self, target)

   ##
   # Simulate keyboard typing on the best matched position of the given 
   # image pattern. It performs a mouse clicking on the matched position to gain 
   # the focus automatically before typing. If args contains only a string, it
   # performs the typing on the current focused component.
   # See {@link #Key the Key class} for typing special keys, and {@link #paste paste()} if you need to "type" international characters or you are using diffrent keymaps other than QWERTY.
   # @param *args The parameters can be (string), (string, modifiers), (image pattern, string), or (image pattern, string, modifiers). The string specifies the string to be typed in, which can be concatenated with the special keys defined in {@link #Key the Key class}.  The image pattern specifies the object that needs the focus before typing. The modifiers specifies the key modifiers to be pressed while typing.
   # @return Returns 0 if nothing is typed, otherwise returns 1.
   def type(self, *args):
      if len(args) == 1:
         return JRegion.type(self, None, args[0], 0)
      if len(args) == 2:
         if __builtin__.type(args[1]) is types.IntType:
            return JRegion.type(self, None, args[0], args[1])
         else:
            return JRegion.type(self, args[0], args[1], 0)
      return JRegion.type(self, args[0], args[1], args[2])

   ##
   # Paste the given string to the best matched position of the given 
   # image pattern. It performs a mouse clicking on the matched position to gain 
   # the focus automatically before pasting. If args contains only a string, it
   # performs the pasting on the current focused component. Pasting is performed 
   # using OS-level shortcut (Ctrl-V or Cmd-V), so it would mess up the clipboard.
   # paste() is a temporary solution for typing international characters or 
   # typing on different keyboard layouts.
   # @param *args The parameters can be (string) or (image pattern, string). The string specifies the string to be typed in. The image pattern specifies the object that needs the focus before pasting. 
   # @return Returns 0 if nothing is pasted, otherwise returns 1.
   def paste(self, *args):
      if len(args) == 1:
         return JRegion.paste(self, None, java.lang.String(args[0], "utf-8"))
      elif len(args) == 2:
         return JRegion.paste(self, args[0], java.lang.String(args[1], "utf-8"))
      return 0

   ##
   # Drags from the position of <i>src</i>, 
   # and drops on the position of <i>dest</i>.
   # @param src This can be a file name of an image; a <a href="edu/mit/csail/uid/Pattern.html">Pattern</a> object; or a <a href="edu/mit/csail/uid/Match.html">Match</a> object.
   # @param dest This can be a file name of an image; a <a href="edu/mit/csail/uid/Pattern.html">Pattern</a> object; or a <a href="edu/mit/csail/uid/Match.html">Match</a> object. It also can be a tuple or a list of 2 integers <i>x</i> and <i>y</i> that indicates the absolute location of the destination on the screen.
   # @return Returns 1 if both src and dest can be found, otherwise returns 0.
   def dragDrop(self, src, dest):
      if isinstance(dest, list) or isinstance(dest, tuple):
         return JRegion.dragDrop(self, src, Location(dest[0], dest[1]), 0)
      else:
         return JRegion.dragDrop(self, src, dest, 0)

   def drag(self, target):
      return JRegion.drag(self, target)

   def dropAt(self, target, delay=None):
      if delay == None:
         delay = Settings.DelayBeforeDrop
      return JRegion.dropAt(self, target, delay)

   def mouseMove(self, target):
      return JRegion.hover(self, target)

   def mouseDown(self, buttons):
      return JRegion.mouseDown(self, buttons)

   def mouseUp(self, buttons=0):
      return JRegion.mouseDown(self, buttons)


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

