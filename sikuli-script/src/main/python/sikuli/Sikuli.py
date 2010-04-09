##
# This module provides a Jython interface of Sikuli Script to automate GUI
# interactions.
##
import java.io.File
import time
from edu.mit.csail.uid import SikuliScript
from edu.mit.csail.uid import Match
from edu.mit.csail.uid import Pattern
from edu.mit.csail.uid import FindFailed
import __builtin__
import __main__
import types
import sys

from edu.mit.csail.uid import Region as JRegion
from edu.mit.csail.uid import Finder
from edu.mit.csail.uid import Location
from edu.mit.csail.uid import Settings
from edu.mit.csail.uid import Env
from edu.mit.csail.uid import OS
from Key import *
from Button import *
from Region import *
from Screen import *
from VDict import *

_si = SikuliScript()

##
# Sets the path for searching images in all Sikuli Script methods. <br/>
# Sikuli IDE sets this to the path of the bundle of source code (.sikuli) 
# automatically. If you write Sikuli scripts by the Sikuli IDE, you should
# not call this method.
#
def setBundlePath(path):
   if path[-1:] == java.io.File.separator:
      path = path[:-1]
   Settings.BundlePath = path
   VDict._setBundlePath(path)


##
# Sikuli shows actions (click, dragDrop, ... etc.) if this flag is set to <i>True</i>.
# The default setting is <i>False</i>.
#
def setShowActions(flag):
   Settings.ShowActions = flag


##
# Shows a question-message dialog requesting input from the user.
# @param msg The message to display.
# @return The user's input string.
#
def input(msg=""):
   return _si.input(msg)



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
# Sleeps until the given amount of time in seconds has elapsed.
# @param sec The amount of sleeping time in seconds.
def sleep(sec):
   time.sleep(sec)

##
# Shows a message dialog containing the given message.
# @param msg The given message string.
def popup(msg):
   _si.popup(msg)

def exit():
   sys.exit()

##
# Runs the given string command.
# @param msg The given string command.
# @return Returns the output from the executed command.
def run(cmd):
    return _si.run(cmd)

############### HELPER FUNCTIONS ################

def search(img, host):
  id = run('curl -F query[photo_file]=@' + img + ';type=image/png ' + host + ':3000/screenshot/remote_query')
  id = id.strip()
  url = host + ':3000/pdf_book/query_result?query_id=' + id
  print url
  run('open ' + url)

############### SECRET FUNCTIONS ################

def getSikuliScript():
   return _si

def initSikuli():
   if not 'screen' in dir(__main__):
      __main__.screen = Screen()
      __main__.screen._exposeAllMethods()
      print "Sikuli is initialized." 


initSikuli()
