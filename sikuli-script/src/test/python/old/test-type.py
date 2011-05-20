# Copyright 2010-2011, Sikuli.org
# Released under the MIT License.
from __future__ import with_statement
from sikuli.Sikuli import *

switchApp("TextEdit.app")
type("lowercase 1234\n")
type("uppercase 1234\n", KEY_SHIFT)


