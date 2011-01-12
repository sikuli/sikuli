from sikuli import *

popup("Please press skip")
SCREEN.setDefaultFindFailedResponse(FindFailedResponse.PROMPT)
SCREEN.setAutoWaitTimeout(0)
find("test-res/google.png")

assert find("test-res/apple.png") != None

#should skip
SCREEN.setDefaultFindFailedResponse(FindFailedResponse.SKIP)
wait("test-res/google.png", 2)

#should skip
SCREEN.setDefaultFindFailedResponse(FindFailedResponse.SKIP)
waitVanish("test-res/apple.png")

#should throw exception
SCREEN.setDefaultFindFailedResponse(FindFailedResponse.ABORT)
try:
   findAll("test-res/google.png")
   assert False, "find didn't throw an exception"
except FindFailed,e:
   pass

