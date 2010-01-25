
def setUp(self):
#  self.startDrJava()
  setAutoWaitTimeout(5000)

def tearDown(self):
  pass
#  self.closeDrJava()
 # wait(1000)

def closeDrJava(self):
  closeApp("DrJava.app")

def startDrJava(self):
  switchApp("/Applications/DrJava.app")
  wait("/afs/csail.mit.edu/u/v/vgod/projects/sikuli/automation/IDE/src/captureImages/1251388220219.png", 10000)

def goPreferences(self):
  type(",", KEY_META)

def testCloseDialog(self):
  self.startDrJava()
  click("/afs/csail.mit.edu/u/v/vgod/projects/sikuli/automation/IDE/src/captureImages/1251060112345.png")
  click("/afs/csail.mit.edu/u/v/vgod/projects/sikuli/automation/IDE/src/captureImages/1251060091042.png")
  assertExist("/afs/csail.mit.edu/u/v/vgod/projects/sikuli/automation/IDE/src/captureImages/1251060146737.png")
  click("/afs/csail.mit.edu/u/v/vgod/projects/sikuli/automation/IDE/src/captureImages/1251060166473.png")
  self.closeDrJava()

def testRadioBoxes(self):
  self.startDrJava()
  self.goPreferences()
  click(Pattern("/afs/csail.mit.edu/u/v/vgod/projects/sikuli/automation/IDE/src/captureImages/1251138742839.png").similar(0.88))
  assertExist("/afs/csail.mit.edu/u/v/vgod/projects/sikuli/automation/IDE/src/captureImages/1251138336975.png")
  click("/afs/csail.mit.edu/u/v/vgod/projects/sikuli/automation/IDE/src/captureImages/1251138351599.png")
  apply = "/afs/csail.mit.edu/u/v/vgod/projects/sikuli/automation/IDE/src/captureImages/1251138365863.png"
  click(apply)
  assertExist("/afs/csail.mit.edu/u/v/vgod/projects/sikuli/automation/IDE/src/captureImages/1251388408305.png")
  click("/afs/csail.mit.edu/u/v/vgod/projects/sikuli/automation/IDE/src/captureImages/1251138411744.png")
  click(apply)
  assertExist("/afs/csail.mit.edu/u/v/vgod/projects/sikuli/automation/IDE/src/captureImages/1251388438385.png")
  self.closeDrJava()
