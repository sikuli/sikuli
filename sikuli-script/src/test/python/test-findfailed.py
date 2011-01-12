from sikuli import *

popup("Please press skip")
SCREEN.setDefaultFindFailedResponse(FindFailedResponse.PROMPT)
SCREEN.setAutoWaitTimeout(0)
assert find("test-res/google.png") == None


#should skip
SCREEN.setDefaultFindFailedResponse(FindFailedResponse.SKIP)
assert wait("test-res/google.png", 2) == None

#should skip
SCREEN.setDefaultFindFailedResponse(FindFailedResponse.SKIP)
assert find("test-res/apple.png") != None
assert waitVanish("test-res/apple.png", 2) == False

#should throw exception
SCREEN.setDefaultFindFailedResponse(FindFailedResponse.ABORT)
try:
   findAll("test-res/google.png")
   assert False, "find didn't throw an exception"
except FindFailed,e:
   pass

