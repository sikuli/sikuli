from org.sikuli.guide import SikuliGuide
from org.sikuli.script import UnionScreen

s = UnionScreen()
_g = SikuliGuide(s);

def rectangle(target):
	r = s.getRegionFromPSRM(target)
	_g.addRectangle(r)

def text(target, text):
	r = s.getRegionFromPSRM(target)
	#_sa.addRectangle(r)
	_g.addText(r.getBottomLeft().below(5), text)

def clickable(target, name = ""):
	r = s.getRegionFromPSRM(target)
	_g.addClickTarget(r, name)

def dialog(text):
	_g.addDialog("Next", text)

def circle(target):
	r = s.getRegionFromPSRM(target)
	_g.addCircle(r)

def tooltip(target, text):
	r = s.getRegionFromPSRM(target)
	_g.addToolTip(r.getBottomLeft().below(5), text)

def show(secs = None):
	if secs:
		_g.showNow(secs)
	else:
		_g.showNow()