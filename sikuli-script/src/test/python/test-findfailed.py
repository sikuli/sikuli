from sikuli import *

popup("Please press skip")
SCREEN.setFindFailedResponse(FindFailedResponse.PROMPT)
SCREEN.setAutoWaitTimeout(0)
assert find("test-res/google.png") == None


#should skip
SCREEN.setFindFailedResponse(FindFailedResponse.SKIP)
assert wait("test-res/google.png", 2) == None

#should skip
SCREEN.setFindFailedResponse(FindFailedResponse.SKIP)
assert find("test-res/apple.png") != None
assert waitVanish("test-res/apple.png", 2) == False

#should throw exception
SCREEN.setFindFailedResponse(FindFailedResponse.ABORT)
try:
   findAll("test-res/google.png")
   assert False, "find didn't throw an exception"
except FindFailed,e:
   pass

