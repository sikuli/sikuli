from python.edu.mit.csail.uid.Sikuli import *
import time


def test_key_modifiers():
   switchApp("/Applications/DrJava.app")
   type(",", KEY_META)

test_key_modifiers()

def test_open_close_app():
   openApp("/Applications/DrJava.app")
   switchApp("/Applications/DrJava.app")
   closeApp("DrJava.app")


#while True:
#   matches =  Subregion(174,298,780,454).inside().find("/afs/csail.mit.edu/u/v/vgod/projects/sikuli/automation/IDE/src/captureImages/1238208687662.png")
#   if matches:
#      popup("found!")
#      break


def test_Click_File_NoMod():
   rightClick(Pattern("testimages/disk.png").similar(0.69))

#test_Click_File_NoMod()

def test_DragDrop():
   dragDrop(Pattern("/afs/csail.mit.edu/u/v/vgod/projects/sikuli/automation/IDE/src/captureImages/1237906539790.png").similar(0.87), Pattern("/afs/csail.mit.edu/u/v/vgod/projects/sikuli/automation/IDE/src/captureImages/1237906549039.png").similar(0.90))

#test_DragDrop()


def test_SelectAllPDF():
   matches = find(Pattern("/afs/csail.mit.edu/u/v/vgod/projects/sikuli/automation/python/captureImages/1237351941137.png").similar(0.91).firstN(24))
   for m in matches:
      click(m, KEY_SHIFT)


#test_SelectAllPDF()

def test_RenameFile():
   iconName = find(Pattern("/afs/csail.mit.edu/u/v/vgod/projects/sikuli/automation/python/captureImages/1237321564202.png").similar(0.93)).nearby().find("/afs/csail.mit.edu/u/v/vgod/projects/sikuli/automation/python/captureImages/1237321578842.png")
   click(iconName)
   sleep(1)
   click(iconName)
   sleep(1)
   type("new-name\n")

#test_RenameFile()


def test_Click_Matches_Mod():
   match = find(Pattern("testimages/disk.png").similar(0.49))
   click(match, KEY_CTRL)

#test_Click_Matches_Mod()


def test_Union():
   match = find("testimages/disk.png").addAll(find("testimages/icon1.png"))
   print match



def test_DragMap():
   while find(Pattern("/afs/csail.mit.edu/u/v/vgod/projects/sikuli/automation/python/captureImages/1237323032478.png").similar(0.40)):
      m = find.matches[0]
      dragDrop(m, [m.x + 100, m.y])
      sleep(1)

#test_DragMap()
