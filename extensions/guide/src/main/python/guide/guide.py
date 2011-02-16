from org.sikuli.guide import SikuliGuide
from org.sikuli.script import UnionScreen
from org.sikuli.guide import Flag
from org.sikuli.guide import Bracket
from org.sikuli.guide import NavigationDialog
from org.sikuli.script import Location


s = UnionScreen()
_g = SikuliGuide(s);


def spotlight(target):
	r = s.getRegionFromPSRM(target)
	_g.addSpotlight(r)

def dialog(text, title = None, location = None):	
	d = NavigationDialog(_g, text, SikuliGuide.SIMPLE)
	if title:
		d.setTitle(title)
	if location:		
		d.setLocation(location)
	else:
		d.pack()
		d.setLocationRelativeTo(_g)
	_g.addDialog(d)

def bracket(target, side='left'):
	r = s.getRegionFromPSRM(target)
	b = Bracket(r)
	if (side == 'top'):
		b.setSide(Bracket.SIDE_TOP)
	elif (side == 'right'):
		b.setSide(Bracket.SIDE_RIGHT)
	elif (side == 'bottom'):
		b.setSide(Bracket.SIDE_BOTTOM)
	else: # left
		b.setSide(Bracket.SIDE_LEFT)
	_g.addComponent(b)	

def flag(target, text, side='left'):
	r = s.getRegionFromPSRM(target)
	if (side == 'right'):	
		b = Flag(Location(r.x+r.w,r.y+r.h/2), text)	
		b.setDirection(Flag.DIRECTION_WEST)
	elif (side == 'top'):
		b = Flag(Location(r.x+r.w/2,r.y), text)	
		b.setDirection(Flag.DIRECTION_SOUTH)		
	else: # left
		b = Flag(Location(r.x,r.y+r.h/2), text)	
		b.setDirection(Flag.DIRECTION_EAST)
	_g.addComponent(b)

def text(target, msg, side='bottom'):
	r = s.getRegionFromPSRM(target)
	if (side == 'top'):
		_g.addText(r, msg, SikuliGuide.Side.TOP)
	elif (side == 'left'):
		_g.addText(r, msg, SikuliGuide.Side.LEFT)
	elif (side == 'right'):
		_g.addText(r, msg, SikuliGuide.Side.RIGHT)
	else:
		_g.addText(r, msg, SikuliGuide.Side.BOTTOM)

	
def rectangle(target):
	r = s.getRegionFromPSRM(target)
	_g.addRectangle(r)

def clickable(target, name = ""):
	r = s.getRegionFromPSRM(target)
	_g.addClickTarget(r, name)

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
