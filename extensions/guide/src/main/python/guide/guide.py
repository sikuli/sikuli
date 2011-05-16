from sikuli.Sikuli import *

from org.sikuli.script import UnionScreen
from org.sikuli.script import Pattern
from org.sikuli.script import Location
from org.sikuli.script import Region
from org.sikuli.script import Screen

from org.sikuli.guide import SikuliGuide
from org.sikuli.guide import Portal

from org.sikuli.guide import SikuliGuideComponent
from org.sikuli.guide.SikuliGuideComponent import Layout

from org.sikuli.guide import SikuliGuideFlag
from org.sikuli.guide import SikuliGuideBracket
from org.sikuli.guide import SikuliGuideText
from org.sikuli.guide import SikuliGuideSpotlight
from org.sikuli.guide import SikuliGuideCircle
from org.sikuli.guide import SikuliGuideRectangle
from org.sikuli.guide import SikuliGuideArrow
from org.sikuli.guide import SikuliGuideCallout
from org.sikuli.guide import SikuliGuideButton
from org.sikuli.guide import SikuliGuideImage
from org.sikuli.guide import Clickable
from org.sikuli.guide import Hotspot
from org.sikuli.guide import SikuliGuideArea
from org.sikuli.guide import SikuliGuideAnchor
from org.sikuli.guide import Magnet


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
# Area Components
#================

def circle(target, **kwargs):
    comp = SikuliGuideCircle()
    return _addComponentHelper(comp, target, side = 'over', **kwargs)

def rectangle(target,**kwargs):
    comp = SikuliGuideRectangle(None)
    return _addComponentHelper(comp, target, side = 'over',  **kwargs)

def spotlight(target, shape = 'circle', **kwargs):
    
    comp = SikuliGuideSpotlight(None)
    if shape == 'rectangle':
        comp.setShape(SikuliGuideSpotlight.RECTANGLE)
    elif shape == 'circle':
        comp.setShape(SikuliGuideSpotlight.CIRCLE)
        
    return _addComponentHelper(comp, target, side = 'over', **kwargs)
    
    
def clickable(target, name = "", **kwargs):
    comp = Clickable(None)
    comp.setName(name)
    return _addComponentHelper(comp, target, side = 'over', **kwargs)   

def button(target, name, side = 'bottom', **kwargs):
    comp = SikuliGuideButton(name)
    return _addComponentHelper(comp, target, side = side, **kwargs)   
  

def arrow(srcTarget, destTarget):
    def getComponentFromTarget(target):        
        if isinstance(target, str) or isinstance(target, Pattern):
            return anchor(target)
        elif isinstance(target, SikuliGuideComponent):
            return target
                    
    comp1 = getComponentFromTarget(srcTarget)
    comp2 = getComponentFromTarget(destTarget)
         
    comp = SikuliGuideArrow(comp1, comp2)
    _g.addToFront(comp)
    return comp


#=====================
# Positioning Elements
#======================

def anchor(target):    
    if isinstance(target, Pattern):
        pattern = target
    elif isinstance(target, str):
        pattern = Pattern(target)
    comp = SikuliGuideAnchor(pattern)
    _g.addToFront(comp)    
    return comp

def area(targets):
    patterns = [Pattern(target) for target in targets] 
    comp = SikuliGuideArea()    
    for pattern in patterns:
        anchor = SikuliGuideAnchor(pattern)
        _g.addToFront(anchor)
        comp.addLandmark(anchor)   
    _g.addToFront(comp)
    return comp

#================
# Text Elements
#================

def bracket(target, side='left', **kwargs):
    comp = SikuliGuideBracket()
    return _addComponentHelper(comp, target, side = side, **kwargs)

def flag(target, text='    ', side = 'left', **kwargs):
    comp = SikuliGuideFlag(text)    
    return _addComponentHelper(comp, target, side = side, **kwargs)

    
def text(target, txt, fontsize = 16, side = 'bottom', **kwargs):
    comp = SikuliGuideText(txt)
    comp.setFontSize(fontsize)
    return _addComponentHelper(comp, target, side = side , **kwargs)

def callout(target, txt, fontsize = 16, side='right', **kwargs):
    comp = SikuliGuideCallout( txt)
    #comp.setFontSize(fontsize)
    return _addComponentHelper(comp, target, side = side , **kwargs)

def tooltip(target, txt,**kwargs ):
    return text(target, txt, fontsize = 8,**kwargs)
    
def image(target, imgurl, **kwargs):    
    comp = SikuliGuideImage(imgurl)
    return _addComponentHelper(comp, target, **kwargs)
    
#=====================
# Interactive Elements
#=====================    
    
def hotspot(target, message, side = 'right'):
    # TODO allow hotspot's positions to be automatically updated
    r = _getRegionFromTarget(target)  
    txtcomp = SikuliGuideCallout(message)
    r1 = Region(r)
    r1.x -= 10
    r1.w += 20
    _setLocationRelativeToRegion(txtcomp,r1,side)
    txtcomp.setShadow(10,2)

    comp = Hotspot(r, txtcomp, _g)
    _g.addToFront(comp)
    return comp    
    
#=====================
# Transition Elements
#=====================
    
# (deprecated)
#def dialog(text, title = None, location = None, timeout = None, buttons = None):    
#    d = TransitionDialog()
#    d.setText(text)
#    if title:
#        d.setTitle(title)
#    if buttons:
#        for b in buttons:
#            d.addButton(b)        
#    d.pack() 
#    if location:        
#        d.setLocation(location)
#    else:
#        d.setLocationToUserPreferredLocation()    
#    if timeout:    
#        d.setTimeout(timeout*1000)
#    _g.setTransition(d)    
    
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
    # show 
    else:
        return _g.showNow()
        
def setDefaultTimeout(timeout):
    _g.setDefaultTimeout(timeout)                
    
####################
# Helper functions #
####################

def _addComponentHelper(comp, target, side = 'best', margin = 0, offset = (0,0),
                         horizontalalignment = 'center', verticalalignment = 'center',
                         shadow = 'default'):
            
    # Margin
    if margin:        
        if isinstance(margin, tuple):        
            (dt,dl,db,dr) = margin
        else:
            (dt,dl,db,dr) = (margin,margin,margin,margin)    
        comp.setMargin(dt,dl,db,dr)
        
    # Offset
    if offset:
        (x,y) = offset
        comp.setOffset(x,y)
        
        
    # Side
    if (side == 'right'):    
        sideConstant = Layout.RIGHT
    elif (side == 'top'):
        sideConstant = Layout.TOP
    elif (side == 'bottom'):
        sideConstant = Layout.BOTTOM
    elif (side == 'left'):
        sideConstant = Layout.LEFT
    elif (side == 'inside'):
        sideConstant = Layout.INSIDE
    elif (side == 'over'):
        sideConstant = Layout.OVER
        

    # Alignment
#    if (horizontalalignment == 'left'):
#        comp.setHorizontalAlignmentWithRegion(r,0.0)
#    elif (horizontalalignment == 'right'):
#
#   if (verticalalignment == 'top'):
#       comp.setVerticalAlignmentWithRegion(r,0.0)
#   elif (verticalalignment == 'bottom'):
#        comp.setVerticalAlignmentWithRegion(r,1.0)        


    if isinstance(target, Region):
        # absolute location wrt a Region 
        comp.setLocationRelativeToRegion(target, sideConstant)
    elif isinstance(target, tuple):
        # absolute location wrt a point (specified as (x,y)) 
        comp.setLocationRelativeToRegion(Region(target[0], target[1],1,1), Layout.RIGHT)
    else: 
        
        if isinstance(target, str):
            # relative location to a string (image filename)        
            targetComponent = anchor(Pattern(target))
            targetComponent.setOpacity(0)            
        elif isinstance(target, Pattern):
            # relative location to a pattern
            targetComponent = anchor(target)
            targetComponent.setOpacity(0)
        elif isinstance(target, SikuliGuideComponent):
            targetComponent = target
        
        comp.setLocationRelativeToComponent(targetComponent, sideConstant)
            
    # set shadow, different sizes for different types of components
    if shadow == 'default':
            
        if (isinstance(comp, SikuliGuideCircle) or 
                isinstance(comp, SikuliGuideRectangle) or
                isinstance(comp, SikuliGuideBracket)):
            comp.setShadow(5,2)
        elif not (isinstance(comp, SikuliGuideSpotlight)):
            comp.setShadow(10,2)
        
        
    # add the component to guide    
    _g.addToFront(comp)
    
    return comp
    

def _addSideComponentToTarget(comp, target, **kwargs):
    r = _getRegionFromTarget(target)
    _setLocationRelativeToRegion(comp,r,**kwargs)
    if isinstance(target, str):
        _g.addTracker(Pattern(target),r,comp)
    elif isinstance(target, Pattern):
        _g.addTracker(target,r,comp)        
    elif isinstance(target, SikuliGuideComponent):
        target.addFollower(comp)
    _g.addComponent(comp)        
    return comp    

def _addAraeComponentToTarget(comp_func, target, **kwargs):
    r = _getRegionFromTarget(target)
    r1 = _adjustRegion(r, **kwargs)
    comp = comp_func(r1)
    if isinstance(target, str):
        _g.addTracker(Pattern(target),r1,comp)
    elif isinstance(target, Pattern):
        _g.addTracker(target,r1,comp)        
    elif isinstance(target, SikuliGuideComponent):
        target.addFollower(comp)
    _g.addComponent(comp)        
    return comp

def _getRegionFromTarget(target):
    if isinstance(target, SikuliGuideComponent):
        return Region(target.getBounds())        
    else:
        return s.getRegionFromPSRM(target)    
    
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
        comp.setLocationRelativeToRegion(r, Layout.RIGHT);
    elif (side == 'top'):
        comp.setLocationRelativeToRegion(r, Layout.TOP);
    elif (side == 'bottom'):
        comp.setLocationRelativeToRegion(r, Layout.BOTTOM);
    elif (side == 'left'):
        comp.setLocationRelativeToRegion(r, Layout.LEFT);
    elif (side == 'inside'):
        comp.setLocationRelativeToRegion(r, Layout.INSIDE);
        
        
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



def addCloseButton(a):
    button(a,"Close", side="left", offset = (200,0))
def addNextButton(a):            
    button(a,"Next",side="left", offset = (60,0))
def addPreviousButton(a):
    button(a,"Previous", side ="left", offset = (0,0))            

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
        
        msg = "Step %d of %d" % (i+1, n)
        a = rectangle(Region(100,100,0,0))                
        text((10,50), msg, fontsize = 10)
            
        if n == 1: # only one step            
            addCloseButton(a)
        elif i == 0: # first step              
            addNextButton(a)
            addCloseButton(a)
        elif i < n - 1: # between
            addPreviousButton(a)
            addNextButton(a)
            addCloseButton(a)
        elif i == n - 1: # final step
            addPreviousButton(a)
            addCloseButton(a)
                
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
            # some other transitions happened
            if (i < n - 1):
                i = i + 1
            else:
                return

# functions for showing
def _show_steps_old(steps, timeout = None):

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
# Cursor Enhancement    #
#########################

def beam(target):
    r = s.getRegionFromPSRM(target)
    c = _g.addBeam(r)
    return c    

def magnet(arg):
    m = Magnet(_g)
    
    def addTarget(x):
        if (isinstance(x, Pattern)):
            pattern = x
        elif (isinstance(x, str)):
            pattern = Pattern(x)
        m.addTarget(pattern)
    
    if isinstance(arg, list) or isinstance(arg, tuple):
        for x in arg:
            addTarget(x)
    else:
        addTarget(x)
        
    _g.addTransition(m)

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
    