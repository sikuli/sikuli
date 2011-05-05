# Copyright 2010-2011, Sikuli.org
# Released under the MIT License.
from sikuli import *

p = Pattern("image.png")
p1 = p.similar(0.99)
p2 = p.targetOffset(10,0)
p3 = p.similar(0.99).targetOffset(10,0)
p4 = p.targetOffset(10,0).similar(0.99)

assert p.toString() == 'Pattern("image.png").similar(0.7)'
assert p1.toString() == 'Pattern("image.png").similar(0.99)'
assert p2.toString() == 'Pattern("image.png").similar(0.7).targetOffset(10,0)'
assert p3.toString() == 'Pattern("image.png").similar(0.99).targetOffset(10,0)'
assert p4.toString() == 'Pattern("image.png").similar(0.99).targetOffset(10,0)'
