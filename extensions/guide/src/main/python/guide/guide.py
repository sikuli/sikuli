from org.sikuli.script import UnionScreen
from org.sikuli.script import Pattern
from org.sikuli.script import Location
from org.sikuli.script import Region
from org.sikuli.script import Screen

from org.sikuli.guide import SikuliGuide
from org.sikuli.guide import Portal

from org.sikuli.guide import SikuliGuideComponent
from org.sikuli.guide import SikuliGuideFlag
from org.sikuli.guide import SikuliGuideBracket
from org.sikuli.guide import SikuliGuideText
from org.sikuli.guide import SikuliGuideSpotlight
from org.sikuli.guide import SikuliGuideCircle
from org.sikuli.guide import SikuliGuideRectangle
from org.sikuli.guide import SikuliGuideArrow
from org.sikuli.guide import SikuliGuideCallout
from org.sikuli.guide import SikuliGuideButton
from org.sikuli.guide import Clickable

from org.sikuli.guide import TransitionDialog

from org.sikuli.guide import TreeSearchDialog
from org.sikuli.guide.model import GUIModel
from org.sikuli.guide.model import GUINode

s = UnionScreen()
_g = SikuliGuide(s)

#######################     
#      Core API       #
#######################

#================
# Area Graphics
#================

def circle(target, **kwargs):
    r = s.getRegionFromPSRM(target)
    r1 = _adjustRegion(r, **kwargs)
    comp = SikuliGuideCircle(r1)
    if isinstance(target, str):
        _g.addTracker(target,r,comp)
    _g.addComponent(comp)

def rectangle(target,**kwargs):
    r = s.getRegionFromPSRM(target)
    r1 = _adjustRegion(r, **kwargs)
    comp = SikuliGuideRectangle(r1)
    if isinstance(target, str):
        _g.addTracker(target,r,comp)
    _g.addComponent(comp)
    

def spotlight(target, shape = 'circle', **kwargs):
    r = s.getRegionFromPSRM(target)
    r1 = _adjustRegion(r, **kwargs)
    comp = SikuliGuideSpotlight(r1)
    if isinstance(target, str):
        _g.addTracker(target,r,comp)    
    if shape == 'rectangle':
        comp.setShape(SikuliGuideSpotlight.RECTANGLE)
    elif shape == 'circle':
        comp.setShape(SikuliGuideSpotlight.CIRCLE)
    _g.addComponent(comp)    

def bracket(target, side='left', **kwargs):
    r = s.getRegionFromPSRM(target)
    comp = SikuliGuideBracket()
    _setLocationRelativeToRegion(comp,r,side,**kwargs)
    if isinstance(target, str):
        _g.addTracker(target,r,comp)
    _g.addComponent(comp)    
    
def clickable(target, name = ""):
    r = s.getRegionFromPSRM(target)
    comp = Clickable(r)
    comp.setName(name)
    _g.addClickable(comp)
    
def button(name, location = None):
    comp = SikuliGuideButton(name)
    if not location:
        location = Screen().getCenter()
        comp.setLocationRelativeToPoint(location, SikuliGuideComponent.CENTER)
    elif isinstance(location, tuple):
        (x,y) = location
        comp.setLocation(x,y)
    _g.addClickable(comp)
    
#================
# Text Elements
#================

def flag(target, text='    ', **kwargs):
    r = s.getRegionFromPSRM(target)
    comp = SikuliGuideFlag(text)
    _setLocationRelativeToRegion(comp,r,**kwargs)
    if isinstance(target, str):
        _g.addTracker(target,r,comp)
    _g.addComponent(comp)   
    
def text(target, txt, fontsize = 16,side='bottom',**kwargs):
    r = s.getRegionFromPSRM(target)
    comp = SikuliGuideText( txt)
    comp.setFontSize(fontsize)
    _setLocationRelativeToRegion(comp,r,side,**kwargs)
    if isinstance(target, str):
        _g.addTracker(target,r,comp)
    _g.addComponent(comp)        

def callout(target, txt, fontsize = 16, side='right',**kwargs):
    r = s.getRegionFromPSRM(target)
    comp = SikuliGuideCallout( txt)
    #comp.setFontSize(fontsize)
    _setLocationRelativeToRegion(comp,r,side,**kwargs)
    if isinstance(target, str):
        _g.addTracker(target,r,comp)
    _g.addComponent(comp)
    
def arrow(srcTarget, destTarget):
    r1 = s.getRegionFromPSRM(srcTarget)
    r2 = s.getRegionFromPSRM(destTarget)
    comp = SikuliGuideArrow(r1.getCenter(),r2.getCenter())
    _g.addComponent(comp)

def tooltip(target, txt,**kwargs ):
    text(target, txt, fontsize = 8,**kwargs)
    
#=====================
# Transition Elements
#=====================
    
# timeout should be expressed in seconds
def dialog(text, title = None, location = None, timeout = None, buttons = None):    
    d = TransitionDialog()
    d.setText(text)
    if title:
        d.setTitle(title)
    if buttons:
        for b in buttons:
            d.addButton(b)        
    d.pack() 
    if location:        
        d.setLocation(location)
    else:
        d.setLocationToUserPreferredLocation()    
    if timeout:    
        d.setTimeout(timeout*1000)
    _g.setTransition(d)    
    
def show(arg = None, timeout = 5):
    # show a list of steps
    if isinstance(arg, list) or isinstance(arg, tuple):
        _show_steps(arg, timeout)
    # show a single step
    elif callable(arg):
        arg()
        return _g.showNow(timeout)
    # show for some period of time
    elif isinstance(arg, float) or isinstance(arg, int):
        return _g.showNow(arg)
    # show for the default period of time
    else:
        return _g.showNow(timeout)
        
def setDefaultTimeout(timeout):
    _g.setDefaultTimeout(timeout)                
    
####################
# Helper functions #
####################
def _setLocationRelativeToRegion(comp, r_, side='left', offset=(0,0), expand=(0,0,0,0),
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
    elif (side == 'left'):
        comp.setLocationRelativeToRegion(r, SikuliGuideComponent.LEFT);
    elif (side == 'inside'):
        comp.setLocationRelativeToRegion(r, SikuliGuideComponent.INSIDE);
        
        
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
    if isinstance(expand, tuple):
        (dt,dl,db,dr) = expand
    else:
        (dt,dl,db,dr) = (expand,expand,expand,expand)

    r.x -= dl
    r.y -= dt
    r.w = r.w + dl + dr
    r.h = r.h + dt + db
    
    return r

# functions for showing
def _show_steps(steps, timeout = None):

    # only keep callables
    steps = filter(lambda x: callable(x), steps)
    print steps
    n = len(steps)
    i = 0
        
    while True:
        step = steps[i]
        step()
        
        d = TransitionDialog()
        
        text = "Step %d of %d" % (i+1, n)
        d.setText(text)
                
        if n == 1: # only one step            
            d.addButton("Close")
        elif i == 0: # first step            
            d.addButton("Next")
            d.addButton("Close")
        elif i < n - 1: # between
            d.addButton("Previous")            
            d.addButton("Next")
            d.addButton("Close")           
        elif i == n - 1: # final step
            d.addButton("Previous")            
            d.addButton("Close")           
        
        d.setLocationToUserPreferredLocation()
        if timeout:
            d.setTimeout(timeout*1000)        
        
        _g.setTransition(d)        
        ret = _g.showNow()
        
        if (ret == "Previous" and i > 0):
            i = i - 1
        elif (ret == "Next" and i < n - 1):
            i = i + 1
        elif (ret == None and i < n - 1): # timeout
            i = i + 1
        elif (ret == "Close"):
            return 
        else:
            return


        

#########################        
# Experimental Features #
#########################

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
            
            
def gui_search(guidefs, keyword):    
    root = GUINode(None)
    model = GUIModel(root)
    for guidef in guidefs:
        root.add(parse_model(guidef))

    model.drawPathTo(_g, keyword);      
    _g.showNow(3);


def search(model = None):
    if model:
        do_search(model, _g)
    else:
        ret = _g.showNow()
        if ret in h:
            h[ret]()
    