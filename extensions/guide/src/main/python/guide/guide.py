from org.sikuli.guide import ScreenAnnotator
from org.sikuli.script import UnionScreen

s = UnionScreen()
_sa = ScreenAnnotator(s);

def addHighlight(target):
	r = s.getRegionFromPSRM(target)
	_sa.addHighlight(r)

def addText(target, text):
	r = s.getRegionFromPSRM(target)
	_sa.addHighlight(r)
	_sa.addText(r.getBottomLeft(), text)

def addClickTarget(target, name = ""):
	r = s.getRegionFromPSRM(target)
	_sa.addClickTarget(r, name)

def addDialog(text):
	_sa.addDialog("Next", text)

def addCircle(target):
	r = s.getRegionFromPSRM(target)
	_sa.addCircle(r)

def addTooltip(target, text):
	r = s.getRegionFromPSRM(target)
	_sa.addHighlight(r)
	_sa.addToolTip(r.getBottomLeft().below(5), text)



def showNow():
	_sa.showNow()

def show(secs):
	_sa.show(secs)

def nextStep(text):
	_sa.showWaitForButtonClick("Next", text)


