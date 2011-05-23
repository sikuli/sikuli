import unittest
from sikuli import *

class TestBasic(unittest.TestCase):
   def testImport(self):
      from sikuli import Region

   def testGlobalFunction(self):
      assert SCREEN != None
      assert getBounds() != None

   def testBasicClass(self):
      assert Region(0,0,100,100) != None
      assert Screen() != None
      assert Key.ENTER != None
      assert FOREVER != None

   def testImagePath(self):
      env_path = java.lang.System.getenv("SIKULI_IMAGE_PATH")
      sys_path = java.lang.System.getProperty("SIKULI_IMAGE_PATH")
      img_path = getImagePath()
      if env_path == None and sys_path == None:
         assert(len(img_path) == 0)
      else:
         assert(len(img_path) > 0)

      prev_len = len(getImagePath())
      if Env.getOS() == OS.WINDOWS:
         addImagePath("c:\\temp")
      else:
         addImagePath("/tmp")
      assert( len(getImagePath()) == prev_len + 1)

      if Env.getOS() == OS.WINDOWS:
         assert( "c:\\temp\\" in set(getImagePath()))
      else:
         assert('/tmp/' in set(getImagePath()))

      addImagePath("/path/to/image")
      addImagePath("http://sikuli.org/path-image/")
      assert( len(getImagePath()) == prev_len + 3)

      if Env.getOS() == OS.WINDOWS:
         removeImagePath("c:\\temp")
      else:
         removeImagePath("/tmp")
      assert( len(getImagePath()) == prev_len + 2)
      if Env.getOS() == OS.WINDOWS:
         removeImagePath("c:\\temp")
      else:
         removeImagePath("/tmp")
      assert( len(getImagePath()) == prev_len + 2)

   def testPattern(self):
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
