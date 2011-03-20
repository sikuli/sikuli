from org.sikuli.script import UnionScreen
from org.sikuli.script import Pattern


from org.sikuli.guide import SikuliGuide
from org.sikuli.guide import Flag
from org.sikuli.guide import Bracket
from org.sikuli.guide import NavigationDialog

from org.sikuli.script import Location
from org.sikuli.script import Region

from org.sikuli.guide import Portal

from org.sikuli.guide import SikuliGuideComponent
from org.sikuli.guide import SikuliGuideFlag
from org.sikuli.guide import SikuliGuideBracket
from org.sikuli.guide import SikuliGuideText
from org.sikuli.guide import SikuliGuideSpotlight
from org.sikuli.guide import SikuliGuideCircle
from org.sikuli.guide import SikuliGuideRectangle

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
    
def beam(target):
    r = s.getRegionFromPSRM(target)
    _g.addBeam(r)

def dialog(text, title = None, location = None):    
    d = _g.getDialog()
    d.setTitle(title)
    if location:        
        d.setLocation(location)
    d.setMessage(text)
    _g.setTransition(d)

def _setLocationRelativeToRegion(comp, r_, side='left', offset=(0,0),
                                 horizontalalignment = 'center',
                                 verticalalignment = 'center'):    
    r = Region(r_)
    
    # Offset
    (dx,dy) = offset
    r.x += dx
    r.y += dy
    
    # Side
    if (side == 'right'):    
        comp.setLocationRelativeToRegion(r, SikuliGuideComponent.RIGHT);
    elif (side == 'top'):
        comp.setLocationRelativeToRegion(r, SikuliGuideComponent.TOP);
    elif (side == 'bottom'):
        comp.setLocationRelativeToRegion(r, SikuliGuideComponent.BOTTOM);
    else: # left
        comp.setLocationRelativeToRegion(r, SikuliGuideComponent.LEFT);
        
    # Alignment
    if (horizontalalignment == 'left'):
        comp.setHorizontalAlignmentWithRegion(r,0.0)
    elif (horizontalalignment == 'right'):
        comp.setHorizontalAlignmentWithRegion(r,1.0)

    if (verticalalignment == 'top'):
        comp.setVerticalAlignmentWithRegion(r,0.0)
    elif (verticalalignment == 'bottom'):
        comp.setVerticalAlignmentWithRegion(r,1.0)

def _adjustRegion(r_, offset = (0,0), expand=(0,0,0,0)):
    
    r = Region(r_)
    
    # Offset
    (dx,dy) = offset
    r.x += dx
    r.y += dy
    
    # Expansion
    (dt,dl,db,dr) = expand
    r.x -= dl
    r.y -= dt
    r.w = r.w + dl + dr
    r.h = r.h + dt + db
    
    return r


def circle(target, **kwargs):
    r = s.getRegionFromPSRM(target)
    r1 = _adjustRegion(r, **kwargs)
    comp = SikuliGuideCircle(_g,r1)
    if isinstance(target, str):
        _g.addTracker(target,r,comp)
    _g.addComponent(comp)

def rectangle(target,**kwargs):
    r = s.getRegionFromPSRM(target)
    r1 = _adjustRegion(r, **kwargs)
    comp = SikuliGuideRectangle(_g,r1)
    if isinstance(target, str):
        _g.addTracker(target,r,comp)
    _g.addComponent(comp)

def spotlight(target, shape = 'rectangle', **kwargs):
    r = s.getRegionFromPSRM(target)
    r1 = _adjustRegion(r, **kwargs)
    comp = SikuliGuideSpotlight(_g,r1)
    if isinstance(target, str):
        _g.addTracker(target,r,comp)    
    if shape == 'rectangle':
        comp.setShape(SikuliGuideSpotlight.RECTANGLE)
    elif shape == 'circle':
        comp.setShape(SikuliGuideSpotlight.CIRCLE)
    _g.addComponent(comp)    

def bracket(target, side='left', **kwargs):
    r = s.getRegionFromPSRM(target)
    comp = SikuliGuideBracket(_g)
    _setLocationRelativeToRegion(comp,r,side,**kwargs)
    if isinstance(target, str):
        _g.addTracker(target,r,comp)
    _g.addComponent(comp)    

def flag(target, text='    ', **kwargs):
    r = s.getRegionFromPSRM(target)
    comp = SikuliGuideFlag(_g,text)
    _setLocationRelativeToRegion(comp,r,**kwargs)
    if isinstance(target, str):
        _g.addTracker(target,r,comp)
    _g.addComponent(comp)    
    
def text(target, txt, fontsize = 16,side='bottom',**kwargs):
    r = s.getRegionFromPSRM(target)
    comp = SikuliGuideText(_g, txt)
    comp.setFontSize(fontsize)
    _setLocationRelativeToRegion(comp,r,side,**kwargs)
    if isinstance(target, str):
        _g.addTracker(target,r,comp)
    _g.addComponent(comp)        

    
def clickable(target, name = ""):
    r = s.getRegionFromPSRM(target)
    _g.addClickTarget(r, name)


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
    
    
h = dict()
def addEntry(target, keys):
    r = s.getRegionFromPSRM(target)
    for k in keys:
        if isinstance(k, tuple):
            h[k[0]] = k[1]
            _g.addSearchEntry(k[0], r)
        else:
            _g.addSearchEntry(k, r)    

def search(model = None):
    if model:
        do_search(model, _g)
    else:
        ret = _g.showNow()
        if ret in h:
            h[ret]()
        
        
    