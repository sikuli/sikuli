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
	_g.addDialog(text)

def circle(target):
	r = s.getRegionFromPSRM(target)
	_g.addCircle(r)

def tooltip(target, text):
	r = s.getRegionFromPSRM(target)
	_g.addToolTip(r.getBottomLeft().below(5), text)
	

def _show_steps(steps):
	i = 0
	n = len(steps)
	while True:
		step = steps[i]
		step()
		if i == 0:
			ret = _g.showNowWithDialog(SikuliGuide.FIRST)
		elif i < n - 1:
			ret = _g.showNowWithDialog(SikuliGuide.MIDDLE)
		elif i == n - 1:
			ret = _g.showNowWithDialog(SikuliGuide.LAST)
		
		if (ret == "Previous"):
			i = i - 1
		elif (ret == "Next"):
			i = i + 1
		else:
			return 


def show(arg = None):
	if isinstance(arg, list):
		_show_steps(arg)
	elif isinstance(arg, float) or isinstance(arg, int):
		_g.showNow(arg)
	else:
		_g.showNow()
