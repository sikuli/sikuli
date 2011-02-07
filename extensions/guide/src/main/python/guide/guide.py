from org.sikuli.guide import ScreenAnnotator
from org.sikuli.script import UnionScreen

s = UnionScreen()
_sa = ScreenAnnotator(s);

def rectangle(target):
	r = s.getRegionFromPSRM(target)
	_sa.addHighlight(r)

def text(target, text):
	r = s.getRegionFromPSRM(target)
	_sa.addHighlight(r)
	_sa.addText(r.getBottomLeft(), text)

def clickable(target, name = ""):
	r = s.getRegionFromPSRM(target)
	_sa.addClickTarget(r, name)

def dialog(text):
	_sa.addDialog("Next", text)

def circle(target):
	r = s.getRegionFromPSRM(target)
	_sa.addCircle(r)

def tooltip(target, text):
	r = s.getRegionFromPSRM(target)
	_sa.addHighlight(r)
	_sa.addToolTip(r.getBottomLeft().below(5), text)

def show(secs):
	_sa.showNow(secs)

def show():
	_sa.showNow()
