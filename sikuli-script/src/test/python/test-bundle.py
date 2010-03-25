from __future__ import with_statement
from sikuli.Sikuli import *

setThrowException(False)
setAutoWaitTimeout(0.3)
print "(before setting bundle path) can't find the image: "
find("apple.png")
setThrowException(True)
setBundlePath("test-res/")
m = find("apple.png")
print "(fter setting bundle path) found: " + str(m) 
assert( m != None )
