from org.sikuli.script import UnionScreen
from org.sikuli.script import Pattern


from org.sikuli.guide import SikuliGuide
from org.sikuli.guide import Flag
from org.sikuli.guide import Bracket
from org.sikuli.guide import NavigationDialog
from org.sikuli.script import Location
from org.sikuli.guide import Portal

from org.sikuli.guide import TreeSearchDialog
from org.sikuli.guide.model import GUIModel
from org.sikuli.guide.model import GUINode


s = UnionScreen()
_g = SikuliGuide(s);


def portal(targets):
    p = Portal(_g)
    for target in targets:
        r = s.getRegionFromPSRM(target)
        p.addEntry("",r)
    _g.addSingleton(p)

def magnifier(target):
    r = s.getRegionFromPSRM(target)
    _g.addMagnifier(r)    

def spotlight(target):
    r = s.getRegionFromPSRM(target)
    _g.addSpotlight(r)

def circle(target):
    r = s.getRegionFromPSRM(target)
    _g.addCircle(r)

h = dict()
def addEntry(target, keys):
    r = s.getRegionFromPSRM(target)
    for k in keys:
        if isinstance(k, tuple):
            h[k[0]] = k[1]
            _g.addSearchEntry(k[0], r)
        else:
            _g.addSearchEntry(k, r)
            
def beam(target):
    r = s.getRegionFromPSRM(target)
    _g.addBeam(r)

def dialog(text, title = None, location = None):    
    d = _g.getDialog()
    if title:
        d.setTitle(title)
    if location:        
        d.setLocation(location)
    d.setMessage(text)
    _g.setTransition(d)

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

def flag(target, text='    ', side='left'):
    r = s.getRegionFromPSRM(target)
    if (side == 'right'):    
        b = Flag(Location(r.x+r.w,r.y+r.h/2), text)    
        b.setDirection(Flag.DIRECTION_WEST)
    elif (side == 'top'):
        b = Flag(Location(r.x+r.w/2,r.y), text)    
        b.setDirection(Flag.DIRECTION_SOUTH)     
    elif (side == 'bottom'):
        b = Flag(Location(r.x+r.w/2,r.y+r.h), text)    
        b.setDirection(Flag.DIRECTION_NORTH)         	   
    else: # left
        b = Flag(Location(r.x,r.y+r.h/2), text)    
        b.setDirection(Flag.DIRECTION_EAST)
    b.guide = _g
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
    n = len(steps)
    i = 0
    while True:
        step = steps[i]
        step()
        
        if n == 1: # only one step
            ret = _g.showNowWithDialog(SikuliGuide.SIMPLE)
            return
                    
        elif i == 0:
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
    elif callable(arg):
        arg()
        _g.showNowWithDialog(SikuliGuide.SIMPLE)
    elif isinstance(arg, float) or isinstance(arg, int):
        _g.showNow(arg)
    else:
        _g.showNow()    
        
        
def parse_model(gui, level=0):
    for i in range(0,level):
        print "----",
    n = gui[0]
    ps,name = n
    node_n = GUINode(Pattern(ps).similar(0.75))
    node_n.setName(name)
    print node_n
    children = gui[1:]
    for c in children:
        node_c = parse_model(c, level+1)
        node_n.add(node_c)        
    return node_n
        

def do_search(guidefs, guide):
    root = GUINode(None)
    model = GUIModel(root)
    for guidef in guidefs:
        root.add(parse_model(guidef))
        
    search = TreeSearchDialog(guide, model)
    search.setLocationRelativeTo(None)
    search.setAlwaysOnTop(True)
    guide.setSearchDialog(search)
    guide.showNow()
    

def search(model = None):
    if model:
        do_search(model, _g)
    else:
        ret = _g.showNow()
        if ret in h:
            h[ret]()
        
        
    