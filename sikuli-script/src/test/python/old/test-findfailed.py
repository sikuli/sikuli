# Copyright 2010-2011, Sikuli.org
# Released under the MIT License.
from sikuli import *

addImagePath("test-res")
popup("Please press skip")
SCREEN.setFindFailedResponse(PROMPT)
SCREEN.setAutoWaitTimeout(0)
assert find("recycle_bin.jpg") == None


#should skip
SCREEN.setFindFailedResponse(SKIP)
assert wait("test-res/google.png", 2) == None

#should skip
SCREEN.setFindFailedResponse(SKIP)
assert find("test-res/apple.png") != None
assert waitVanish("test-res/apple.png", 2) == False

#should throw exception
SCREEN.setFindFailedResponse(ABORT)
try:
   findAll("test-res/google.png")
   assert False, "find didn't throw an exception"
except FindFailed,e:
   pass

