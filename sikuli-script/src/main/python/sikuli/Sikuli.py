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
from edu.mit.csail.uid import Location
from edu.mit.csail.uid import Settings
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


############ ACTIONS ############ 








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
