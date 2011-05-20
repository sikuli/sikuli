# Copyright 2010-2011, Sikuli.org
# Released under the MIT License.
from sikuli.Sikuli import *
from java.lang import System

m = find("http://groups.csail.mit.edu/uid/sikuli/examples/clickapple.sikuli/apple.png")
assert(m != None)

System.setProperty("SIKULI_IMAGE_PATH","http://groups.csail.mit.edu/uid/sikuli/examples/clickapple.sikuli:test-res/")
click("apple.png")
