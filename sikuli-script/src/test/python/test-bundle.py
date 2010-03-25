from __future__ import with_statement
from sikuli.Sikuli import *

setThrowException(False)
setAutoWaitTimeout(0.3)
assert( find("apple.png") == None )
setThrowException(True)
setBundlePath("test-res/")
assert( find("apple.png") != None )
