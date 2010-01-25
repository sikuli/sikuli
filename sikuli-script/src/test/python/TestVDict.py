from python.edu.mit.csail.uid.Sikuli import *

dict = VDict()
assert len(dict) == 0
dict["test-res/1.png"] = 1
dict["test-res/2a.png"] = 2
dict["test-res/2b.png"] = 3
assert "test-res/1.png" in dict
assert "test-res/2.png" in dict
assert not ("test-res/big.png" in dict)
assert len(dict.keys()) == 3
assert len(dict) == 3
vals = dict["test-res/2.png"] 
assert vals[0] == 2
assert vals[1] == 3
#print dict.keys()
vals = dict["test-res/big.png"] 
assert len(vals) == 0
assert len(dict.keys()) == 3
del dict["test-res/1.png"]
assert len(dict.keys()) == 2


print "all test cases passed."
