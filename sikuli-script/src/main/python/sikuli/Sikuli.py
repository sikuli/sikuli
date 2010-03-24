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
from edu.mit.csail.uid import FindFailed
import __builtin__
import __main__
import types

from edu.mit.csail.uid import Region as JRegion
from Key import *
from Region import *
from Screen import *
from VDict import *



_si = SikuliScript()





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
# @param *args The args can be 4 integers: x, y, w, and h, a <a href="edu/mit/csail/uid/Match.html">Match</a> object or a {@link #Region} object.
# @return The path to the captured image.
#
def capture(*args):
   if len(args) == 0:
      return _si.capture()
   else:
      if isinstance(args[0],JRegion):
         r = args[0]
         return _si.captureScreen(r.x, r.y, r.w, r.h).getFilename()
      elif len(args) == 4:
         return _si.captureScreen(args[0], args[1], args[2], args[3]).getFilename()
      else:
         return None


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
# Move the mouse cursor to the best matched position of the 
# given image pattern. It calls
# find() to locate the pattern if a file name or a <a href="edu/mit/csail/uid/Pattern.html">Pattern</a> object is given.
# @param img The file name of an image; a <a href="edu/mit/csail/uid/Pattern.html">Pattern</a>  object; a <a href="edu/mit/csail/uid/Match.html">Match</a> object; or a <a href="edu/mit/csail/uid/Matches.html">Matches</a> object.
# @return 0 <br/> Returns -1 if find() fails.
def hover(img):
   return _si.hover(img)


##
# Performs a mouse clicking for each matched position of the given image pattern. It calls
# find() to locate the pattern if a file name or a <a href="edu/mit/csail/uid/Pattern.html">Pattern</a> object is given.
# @param img The file name of an image; a <a href="edu/mit/csail/uid/Pattern.html">Pattern</a> object; or a <a href="edu/mit/csail/uid/Matches.html">Matches</a> object.
# @param modifiers The key modifiers. This can be one modifier or union of multiple modifiers combined by the OR(|) operator.
# @return The number of performed clicking. <br/> Returns -1 if find() fails.
def clickAll(img, modifiers=0):
   return _si.clickAll(img, modifiers)

##
# Repeatedly performs a mouse clicking for each matched position of the 
# given image pattern until no more matching can be found. It keeps calling
# find() to locate the pattern on the screen for clicking, and stops if
# nothing is found.
# @param img The file name of an image, or a <a href="edu/mit/csail/uid/Pattern.html">Pattern</a> object.
# @param modifiers The key modifiers. This can be one modifier or union of multiple modifiers combined by the OR(|) operator.
# @return The number of performed clicking. 
def repeatClickAll(img, modifiers=0):
   return _si.repeatClickAll(img, modifiers)

##
# Performs a double clicking on the best matched position of the given 
# image pattern. It calls
# find() to locate the pattern if a file name or a <a href="edu/mit/csail/uid/Pattern.html">Pattern</a> object is given.
# @param img The file name of an image; a <a href="edu/mit/csail/uid/Pattern.html">Pattern</a> object; a <a href="edu/mit/csail/uid/Match.html">Match</a> object; or a <a href="edu/mit/csail/uid/Matches.html">Matches</a> object.
# @param modifiers The key modifiers. This can be one modifier or union of multiple modifiers combined by the OR(|) operator.
# @return The number of performed clicking. <br/> Returns -1 if find() fails.
def doubleClick(img, modifiers=0):
   return _si.doubleClick(img, modifiers)

##
# Performs a double clicking for each matched position of the given image pattern. It calls
# find() to locate the pattern if a file name or a <a href="edu/mit/csail/uid/Pattern.html">Pattern</a> object is given.
# @param img The file name of an image; a <a href="edu/mit/csail/uid/Pattern.html">Pattern</a> object; or a <a href="edu/mit/csail/uid/Matches.html">Matches</a> object.
# @param modifiers The key modifiers. This can be one modifier or union of multiple modifiers combined by the OR(|) operator.
# @return The number of performed clicking. <br/> Returns -1 if find() fails.
def doubleClickAll(img, modifiers=0):
   return _si.doubleClickAll(img, modifiers)


##
# Repeatedly performs a double clicking for each matched position of the 
# given image pattern until no more matching can be found. It keeps calling
# find() to locate the pattern on the screen for clicking, and stops if
# nothing is found.
# @param img The file name of an image, or a <a href="edu/mit/csail/uid/Pattern.html">Pattern</a> object.
# @param modifiers The key modifiers. This can be one modifier or union of multiple modifiers combined by the OR(|) operator.
# @return The number of performed clicking. 
def repeatDoubleClickAll(img, modifiers=0):
   return _si.repeatDoubleClickAll(img, modifiers)

##
# Performs a right clicking on the best matched position of the given 
# image pattern. It calls
# find() to locate the pattern if a file name or a <a href="edu/mit/csail/uid/Pattern.html">Pattern</a> object is given.
# @param img The file name of an image; a <a href="edu/mit/csail/uid/Pattern.html">Pattern</a> object; a <a href="edu/mit/csail/uid/Match.html">Match</a> object; or a <a href="edu/mit/csail/uid/Matches.html">Matches</a> object.
# @param modifiers The key modifiers. This can be one modifier or union of multiple modifiers combined by the OR(|) operator.
# @return The number of performed clicking. <br/> Returns -1 if find() fails.
def rightClick(img, modifiers=0):
   return _si.rightClick(img, modifiers)


##
# Simulate keyboard typing on the best matched position of the given 
# image pattern. It performs a mouse clicking on the matched position to gain 
# the focus automatically before typing. If args contains only a string, it
# performs the typing on the current focused component.
# See {@link #Key the Key class} for typing special keys, and {@link #paste paste()} if you need to "type" international characters or you are using diffrent keymaps other than QWERTY.
# @param *args The parameters can be (string), (string, modifiers), (image pattern, string), or (image pattern, string, modifiers). The string specifies the string to be typed in, which can be concatenated with the special keys defined in {@link #Key the Key class}.  The image pattern specifies the object that needs the focus before typing. The modifiers specifies the key modifiers to be pressed while typing.
# @return Returns 0 if nothing is typed, otherwise returns 1.
def type(*args):
   if len(args) == 1:
      return _si.type(None, args[0])
   if len(args) == 2:
      if __builtin__.type(args[1]) is types.IntType:
         return _si.type(None, args[0], args[1])
      else:
         return _si.type(args[0], args[1])
   return _si.type(args[0], args[1], args[2])

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
def paste(*args):
   import java.lang.String
   if len(args) == 1:
      return _si.paste(None, java.lang.String(args[0], "utf-8"))
   if len(args) == 2:
      return _si.paste(args[0], java.lang.String(args[1], "utf-8"))
   return 0

##
# Drags from the position of <i>src</i>, 
# and drops on the position of <i>dest</i>.
# @param src This can be a file name of an image; a <a href="edu/mit/csail/uid/Pattern.html">Pattern</a> object; or a <a href="edu/mit/csail/uid/Match.html">Match</a> object.
# @param dest This can be a file name of an image; a <a href="edu/mit/csail/uid/Pattern.html">Pattern</a> object; or a <a href="edu/mit/csail/uid/Match.html">Match</a> object. It also can be a tuple or a list of 2 integers <i>x</i> and <i>y</i> that indicates the absolute location of the destination on the screen.
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

##
# Runs the given string command.
# @param msg The given string command.
# @return Returns the output from the executed command.
def run(cmd):
    return _si.run(cmd)

############### HELPER FUNCTIONS ################

#def toList(jarray):
#   return map(lambda x:x, jarray)

def search(img, host):
  id = run('curl -F query[photo_file]=@' + img + ';type=image/png ' + host + ':3000/screenshot/remote_query')
  id = id.strip()
  url = host + ':3000/pdf_book/query_result?query_id=' + id
  print url
  run('open ' + url)

############### SECRET FUNCTIONS ################

def getSikuliScript():
   return _si


__main__.screen = Screen()
__main__.screen._exposeAllMethods()
