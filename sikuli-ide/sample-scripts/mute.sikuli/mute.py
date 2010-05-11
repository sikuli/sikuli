def dragLeft(t):
  dragDrop(t,  t.getCenter().left(200))

def dragRight(t):
  dragDrop(t, t.getCenter().right(200))

def dragToMute(t):
  dragDrop(t, t.nearby().left().find("1273527108356.png"))

switchApp("System Preferences.app")
click("1273526123226.png")
click("1273526171905.png")
thumbs = findAll("1273527194228.png")
for t in list(thumbs)[:2]: # only take the first two 
  dragLeft(t) # off
  #dragRight(t)  # on
  #dragToMute(t)

