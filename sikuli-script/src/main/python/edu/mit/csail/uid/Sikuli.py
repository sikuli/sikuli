##
# This module provides a Jython interface of Sikuli Script to automate GUI
# interactions.
##
import time
from edu.mit.csail.uid import SikuliScript
from edu.mit.csail.uid import Match
from edu.mit.csail.uid import Pattern
from edu.mit.csail.uid import Similar
from edu.mit.csail.uid import SubregionJ
from edu.mit.csail.uid import VDictProxy
from java.awt.event import InputEvent


_si = SikuliScript()

##
# The Shift key modifier constant. 
# This equals to java.awt.event.InputEvent.SHIFT_MASK.
KEY_SHIFT = InputEvent.SHIFT_MASK
##
# The Ctrl key modifier constant. 
# This equals to java.awt.event.InputEvent.CTRL_MASK.
KEY_CTRL = InputEvent.CTRL_MASK
##
# The Meta/Windows/Apple key modifier constant. 
# This equals to java.awt.event.InputEvent.META_MASK.
KEY_META = InputEvent.META_MASK
##
# The Apple(Command) key modifier constant. 
# This equals to java.awt.event.InputEvent.META_MASK.
KEY_CMD  = InputEvent.META_MASK
##
# The Windows key modifier constant. 
# This equals to java.awt.event.InputEvent.META_MASK.
KEY_WIN  = InputEvent.META_MASK
##
# The Alt key modifier constant. 
# This equals to java.awt.event.InputEvent.ALT_MASK.
KEY_ALT = InputEvent.ALT_MASK

## 
# VDict implements a visual dictionary that has Python's conventional dict
# interfaces.
#
# A visual dictionary is a data type for storing key-value pairs using 
# images as keys. Using a visual dictionary, a user can easily automate 
# the tasks of saving and retrieving arbitrary data objects by images. 
# The syntax of the visual dictionary data type is modeled after that of 
# the built-in Python dictionary data type.

class VDict(VDictProxy):

   ##
   # the default similarity for fuzzy matching. The range of this is from
   # 0 to 1.0, where 0 matches everything and 1.0 does exactly matching.
   # <br/>
   # The default similarity is 0.7.
   _DEFAULT_SIMILARITY = 0.7

   _DEFAULT_GET_ITEM_N = 0
   _bundlePath = "."
   _keys = {}

   ##
   # Constructs a new visual dictionary with the same mapping as the given dict.
   #
   def __init__(self, dict=None):
      if dict:
         for k in dict.keys():
            self[k] = dict[k]

   def _setBundlePath(cls, path):
      VDict._bundlePath = path

   _setBundlePath = classmethod(_setBundlePath)

   def _getInBundle(self, f):
      if f[0] == '/':   return f
      return self._bundlePath + "/" + f

   ##
   # Returns the number of keys in this visual dictionary.
   #
   def __len__(self):
      return self.size()

   ##
   # Maps the specified key to the specified item in this visual dictionary.
   #
   def __setitem__(self, key, item):
      self._keys[key] = item
      key = self._getInBundle(key)
      self.insert(key, item)

   ##
   # Tests if the specified object looks like a key in this visual dictionary
   # with the default similarity.
   #
   def __contains__(self, key):
      return len(self.get(key)) > 0

   ##
   # Returns all values to which the specified key is fuzzily matched in 
   # this visual dictionary with the default similarity.
   # <br/>
   # This is a wrapper for the {@link #VDict.get get} method.
   def __getitem__(self, key):
      return self.get(key)

   ##
   # Deletes the key and its corresponding value from this visual dictionary.
   #
   def __delitem__(self, key):
      del self._keys[key]
      key = self._getInBundle(key)
      self.erase(key)

   ##
   # Returns a list of the keys in this visual dictionary.
   #
   def keys(self):
      return self._keys.keys()

   ##
   # Returns the value to which the specified key is exactly matched in 
   # this visual dictionary.
   #
   def get_exact(self, key):
      if key == None: return None
      key = self._getInBundle(key)
      return self.lookup(key)

   ##
   # Returns the values to which the specified key is fuzzily matched in 
   # this visual dictionary with the given similarity and the given maximum 
   # number of return items.
   # @param similarity the similarity for matching.
   # @param n maximum number of return items.
   #
   def get(self, key, similarity=_DEFAULT_SIMILARITY, n=_DEFAULT_GET_ITEM_N):
      if key == None: return None
      key = self._getInBundle(key)
      return self.lookup_similar_n(key, similarity, n)

   ##
   # Returns the value to which the specified key is best matched in 
   # this visual dictionary with the given similarity.
   # @param similarity the similarity for matching.
   #
   def get1(self, key, similarity=_DEFAULT_SIMILARITY):
      if key == None: return None
      key = self._getInBundle(key)
      return self.lookup_similar(key, similarity)
   

#print dir(SikuliScript)

##
# Sets the path for searching images in all Sikuli Script methods. <br/>
# Sikuli IDE sets this to the path of the bundle of source code (.sikuli) 
# automatically. If you write Sikuli scripts by the Sikuli IDE, you should
# not call this method.
#
def setBundlePath(path):
   VDict._setBundlePath(path)
   _si.setBundlePath(path)

##
# Sets the flag of throwing exceptions if {@link #find find()} fails. <br/>
# Setting this flag to <i>True</i> enables all methods that use 
# find() throws an exception if the find()
# can not find anything similar on the screen.
# Once the flag is set to <i>False</i>, all methods that use find()
# just return <i>None</i> if nothing is found. <br/>
# The default value of thie flag is <i>True</i>.
#
def setThrowException(flag):
   _si.setThrowException(flag)

##
# Sets the maximum waiting time in milliseconds for {@link #find find()}. <br/>
# Setting this time to a non-zero value enables all methods that use find()
# wait the appearing of the given image pattern until the specified amount of
# time has elapsed. <br/>
# The default timeout is <i>3000ms</i>.
#
def setAutoWaitTimeout(ms):
   _si.setAutoWaitTimeout(ms)


##
# Sikuli shows actions (click, dragDrop, ... etc.) if this flag is set to <i>True</i>.
# The default setting is <i>False</i>.
#
def setShowActions(flag):
   _si.setShowActions(flag)


##
# Shows a question-message dialog requesting input from the user.
# @param msg The message to display.
# @return The user's input string.
#
def input(msg=""):
   return _si.input(msg)

##
# Enters the screen-capture mode asking the user to capture a region of 
# the screen if no arguments are given.
# If any arguments are specified, capture() automatically captures the given
# region of the screen.
# @param *args The args can be 4 integers: x, y, w, and h, a {@link #Match} object or a {@link #Subregion} object.
# @return The path to the captured image.
#
def capture(*args):
   if len(args) == 0:
      return _si.capture()
   else:
      if isinstance(args[0],Subregion):
         r = args[0]
         return _si.captureScreen(r.x, r.y, r.w, r.h)
      elif isinstance(args[0],Match):
         r = args[0]
         return _si.captureScreen(r.getX(), r.getY(), r.getW(), r.getH())
      else:
         return _si.captureScreen(args[0], args[1], args[2], args[3])


##
# Switches the frontmost application to the given application. 
# If the given application is not running, it will be launched by openApp()
# automatically. <br/>
# Note: On Windows, Sikule searches in the text on the title bar 
# instead of the application name.
# @param app The name of the application. (case-insensitive)
#
def switchApp(app):
   return _si.switchApp(app)

##
# Opens the given application. <br/>
# @param app The name of an application if it is in the environment variable PATH, or the full path to an application.
#
def openApp(app):
   return _si.openApp(app)

##
# Closes the given application. <br/>
# @param app The name of the application. (case-insensitive)
#
def closeApp(app):
   return _si.closeApp(app)

##
# Looks for the best match of a particular GUI element to interact with. It takes the file name of
# an image that specifies the element's appearance, searches the whole screen 
# and returns the best region matching this pattern or None if no such region can 
# be found. <br/>
# In addition to the return value, find() also stores the returning matched 
# region in find.region. <br/>
# If the auto waiting timeout ({@link #setAutoWaitTimeout}) is set to a non-zero
# value, all find() just act as the {@link #wait} method.
# @param img The file name of an image, which can be an absolute path or a relative path to file in the source bundle (.sikuli). It also can be a {@link #Pattern} object.
# @return a {@link #Match} object that contains the best matching region, or None if nothing is found.
#
def find(img):
   find.regions = _si.find(img)
   if len(find.regions) > 0:
      find.region = find.regions[0]
   else:
      find.region = None
   return find.region

##
# Looks for all instance of a particular GUI element to interact with. It takes the file name of
# an image that specifies the element's appearance, searches the whole screen 
# and returns the regions matching this pattern or None if no such region can 
# be found. <br/>
# In addition to the return value, findAll() also stores the returning matched 
# regions in find.regions and the best matched region in find.region. <br/>
# If the auto waiting timeout ({@link #setAutoWaitTimeout}) is set to a non-zero
# value, all findAll() just act as the {@link #wait} method.
# @param img The file name of an image, which can be an absolute path or a relative path to file in the source bundle (.sikuli). It also can be a {@link #Pattern} object.
# @return a {@link #Matches} object that contains a list of {@link #Match} objects, or None if nothing is found.
#
def findAll(img):
   find.regions = _si.findAll(img)
   if len(find.regions) > 0:
      find.region = find.regions[0]
   return find.regions

##
# Keeps searching the given image on the screen until the image appears or 
# the specified amount of time has elapsed.
# @param img The file name of an image, which can be an absolute path or a relative path to the file in the source bundle (.sikuli).
# @param timeout The amount of waiting time, in milliseconds. This value orverrides the auto waiting timeout set by {@link #setAutoWaitTimeout}.
# @return a {@link #Matches} object that contains a list of {@link #Match} objects, or None if timeout occurs.
#
def wait(img, timeout=3000):
   wait.regions = _si.wait(img, timeout)
   if len(wait.regions) > 0:
      wait.region = wait.regions[0]
   return wait.regions

##
# Keeps waiting the disapearance of the given image on the screen until 
# the specified amount of time has elapsed.
# @param img The file name of an image, which can be an absolute path or a relative path to the file in the source bundle (.sikuli).
# @param timeout The amount of waiting time, in milliseconds. 
#
def untilNotExist(img, timeout=3000):
   _si.waitNotExist(img, timeout)

############### SUBREGION ###############

class Subregion(SubregionJ):
   def __init__(self, x, y, w, h):
      SubregionJ.__init__(self, x, y, w, h, _si)


########### SPATIAL OPERATORS ###########
#def anyscale(): FIXME


############ ACTIONS ############ 

##
# Performs a mouse clicking on the best matched position of the 
# given image pattern. It calls
# find() to locate the pattern if a file name or a Pattern object is given.
# @param img The file name of an image; a Pattern object; a Match object; or a Matches object.
# @param modifiers The key modifiers. This can be one modifier or union of multiple modifiers combined by the OR(|) operator.
# @return The number of performed clicking. <br/> Returns -1 if find() fails.
def click(img, modifiers=0):
   return _si.click(img, modifiers)

##
# Performs a mouse clicking for each matched position of the given image pattern. It calls
# find() to locate the pattern if a file name or a Pattern object is given.
# @param img The file name of an image; a Pattern object; or a Matches object.
# @param modifiers The key modifiers. This can be one modifier or union of multiple modifiers combined by the OR(|) operator.
# @return The number of performed clicking. <br/> Returns -1 if find() fails.
def clickAll(img, modifiers=0):
   return _si.clickAll(img, modifiers)

##
# Repeatedly performs a mouse clicking for each matched position of the 
# given image pattern until no more matching can be found. It keeps calling
# find() to locate the pattern on the screen for clicking, and stops if
# nothing is found.
# @param img The file name of an image, or a Pattern object.
# @param modifiers The key modifiers. This can be one modifier or union of multiple modifiers combined by the OR(|) operator.
# @return The number of performed clicking. 
def repeatClickAll(img, modifiers=0):
   return _si.repeatClickAll(img, modifiers)

##
# Performs a double clicking on the best matched position of the given 
# image pattern. It calls
# find() to locate the pattern if a file name or a Pattern object is given.
# @param img The file name of an image; a Pattern object; a Match object; or a Matches object.
# @param modifiers The key modifiers. This can be one modifier or union of multiple modifiers combined by the OR(|) operator.
# @return The number of performed clicking. <br/> Returns -1 if find() fails.
def doubleClick(img, modifiers=0):
   return _si.doubleClick(img, modifiers)

##
# Performs a double clicking for each matched position of the given image pattern. It calls
# find() to locate the pattern if a file name or a Pattern object is given.
# @param img The file name of an image; a Pattern object; or a Matches object.
# @param modifiers The key modifiers. This can be one modifier or union of multiple modifiers combined by the OR(|) operator.
# @return The number of performed clicking. <br/> Returns -1 if find() fails.
def doubleClickAll(img, modifiers=0):
   return _si.doubleClickAll(img, modifiers)


##
# Repeatedly performs a double clicking for each matched position of the 
# given image pattern until no more matching can be found. It keeps calling
# find() to locate the pattern on the screen for clicking, and stops if
# nothing is found.
# @param img The file name of an image, or a Pattern object.
# @param modifiers The key modifiers. This can be one modifier or union of multiple modifiers combined by the OR(|) operator.
# @return The number of performed clicking. 
def repeatDoubleClickAll(img, modifiers=0):
   return _si.repeatDoubleClickAll(img, modifiers)

##
# Performs a right clicking on the best matched position of the given 
# image pattern. It calls
# find() to locate the pattern if a file name or a Pattern object is given.
# @param img The file name of an image; a Pattern object; a Match object; or a Matches object.
# @param modifiers The key modifiers. This can be one modifier or union of multiple modifiers combined by the OR(|) operator.
# @return The number of performed clicking. <br/> Returns -1 if find() fails.
def rightClick(img, modifiers=0):
   return _si.rightClick(img, modifiers)


##
# Simulate keyboard typing on the best matched position of the given 
# image pattern. It performs a mouse clicking on the matched position to gain 
# the focus automatically before typing. If args contains only a string, it
# performs the typing on the current focused component.
# @param *args The parameters can be (string), (string, modifiers), (image pattern, string), or (image pattern, string, modifiers). The string specifies the string to be typed in. The image pattern specifies the object that needs the focus before typing. The modifiers specifies the key modifiers to be pressed while typing.
# @return Returns 0 if nothing is typed, otherwise returns 1.
def type(*args):
   import __builtin__
   import types
   if len(args) == 1:
      return _si.type(None, args[0])
   if len(args) == 2:
      if __builtin__.type(args[1]) is types.IntType:
         return _si.type(None, args[0], args[1])
      else:
         return _si.type(args[0], args[1])
   return _si.type(args[0], args[1], args[2])

##
# Drags from the position of <i>src</i>, 
# and drops on the position of <i>dest</i>.
# @param src This can be a file name of an image; a Pattern object; or a Match object.
# @param dest This can be a file name of an image; a Pattern object; or a Match object. It also can be a tuple or a list of 2 integers <i>x</i> and <i>y</i> that indicates the absolute location of the destination on the screen.
# @return Returns 1 if both src and dest can be found, otherwise returns 0.
def dragDrop(src, dest):
   if isinstance(dest, list) or isinstance(dest, tuple):
      return _si.dragDrop(src, dest[0], dest[1])
   else:
      return _si.dragDrop(src, dest)

##
# Sleeps until the given amount of time in seconds has elapsed.
# @param sec The amount of sleeping time in seconds.
def sleep(sec):
   time.sleep(sec)

##
# Shows a message dialog containing the given message.
# @param msg The given message string.
def popup(msg):
   _si.popup(msg)


############### HELPER FUNCTIONS ################

#def toList(jarray):
#   return map(lambda x:x, jarray)

def cmdexec(cmd):
    return _si.cmdexec(cmd)
	
def search(img, host):
  id = cmdexec('curl -F query[photo_file]=@' + img + ';type=image/png ' + host + ':3000/screenshot/remote_query')
  id = id.strip()
  url = host + ':3000/pdf_book/query_result?query_id=' + id
  print url
  cmdexec('open ' + url)

############### SECRET FUNCTIONS ################

def getSikuliScript():
   return _si
