from sikuli.Sikuli import *

env_path = java.lang.System.getenv("SIKULI_IMAGE_PATH")
sys_path = java.lang.System.getProperty("SIKULI_IMAGE_PATH")
img_path = getImagePath()
if env_path == None and sys_path == None:
   assert(len(img_path) == 0)
else:
   assert(len(img_path) > 0)

prev_len = len(getImagePath())
addImagePath("/tmp")
assert( len(getImagePath()) == prev_len + 1)
assert('/tmp/' in set(getImagePath()))
addImagePath("/path/to/image")
addImagePath("http://sikuli.org/path-image/")
assert( len(getImagePath()) == prev_len + 3)

removeImagePath("/tmp")
assert( len(getImagePath()) == prev_len + 2)
removeImagePath("/tmp")
assert( len(getImagePath()) == prev_len + 2)
