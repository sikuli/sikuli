##
# This module provides a Jython interface of Sikuli Script to automate GUI
# interactions.
##
import java.io.File
import time
from org.sikuli.script import SikuliScript
from org.sikuli.script import Match
from org.sikuli.script import Pattern
from org.sikuli.script import FindFailed
import __builtin__
import __main__
import types
import sys

from org.sikuli.script import Region as JRegion
from org.sikuli.script import UnionScreen
from org.sikuli.script import Finder
from org.sikuli.script import Location
from org.sikuli.script import Settings
from org.sikuli.script import Env
from org.sikuli.script import OS
from org.sikuli.script import App
from org.sikuli.script import ScreenHighlighter
from org.sikuli.script import ImageLocator
from java.awt import Rectangle
from Key import *
from Button import *
from Region import *
from Screen import *
from VDict import *
from Helper import *
import SikuliImporter

_si = SikuliScript()

def addImagePath(path):
   ImageLocator.addImagePath(path)

def getImagePath():
   return ImageLocator.getImagePath()

def removeImagePath(path):
   ImageLocator.removeImagePath(path)

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

def getBundlePath():
   return Settings.BundlePath

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

def capture(*args):
   scr = UnionScreen()
   if len(args) == 0:
      simg = scr.userCapture()
      if simg: 
         return simg.getFilename()
      else:
         return None
   elif len(args) == 1:
      if __builtin__.type(args[0]) is types.StringType or __builtin__.type(args[0]) is types.UnicodeType:
         simg = scr.userCapture(args[0])
         if simg:
            return simg.getFilename()
         else:
            return None
      else:
         return scr.capture(args[0]).getFilename()
   elif len(args) == 4:
      return scr.capture(args[0], args[1], args[2], args[3]).getFilename()
   else:
      return None


def selectRegion(msg=None):
   if msg:
      r = UnionScreen().selectRegion(msg)
   else:
      r = UnionScreen().selectRegion()
   if r:
      return Region(r)
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
# Sleeps until the given amount of time in seconds has elapsed.
# @param sec The amount of sleeping time in seconds.
def sleep(sec):
   time.sleep(sec)

##
# Shows a message dialog containing the given message.
# @param msg The given message string.
def popup(msg):
   _si.popup(msg)

def exit(code=0):
   ScreenHighlighter.closeAll()
   sys.exit(code)

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
   dict = globals()
   dict['SCREEN'] = Screen()
   dict['SCREEN']._exposeAllMethods(__name__)
   #print "Sikuli is initialized. ", id(dict['SCREEN'])


initSikuli()
