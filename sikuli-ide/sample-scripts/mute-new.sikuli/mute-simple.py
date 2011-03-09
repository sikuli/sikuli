setAutoWaitTimeout(5)

def dragLeft(t):
  dragDrop(t,  t.getCenter().left(200))

sys = App.open("System Preferences.app")
with Region(sys.window()):
	click("1273526123226.png")
	click("1273526171905.png")
	thumbs = findAll("1273527194228.png")
	for t in list(thumbs)[:2]: # only take the first two 
	  dragLeft(t) # off
