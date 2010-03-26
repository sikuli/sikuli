from sikuli.Sikuli import *

popup("select the region consisting of the apple")
r = selectRegion()
assert( r.find("test-res/apple.png") != None )
popup("select the region not consisting of the apple")
r = selectRegion()
r.setThrowException(False)
r.setAutoWaitTimeout(0)
assert( r.find("test-res/apple.png") == None )
