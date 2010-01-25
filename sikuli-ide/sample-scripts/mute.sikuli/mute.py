def dragLeft(t):
  dragDrop(t,  [t.x - 200,   t.y])

def dragRight(t):
  dragDrop(t,  [t.x + 200,   t.y])


#click(Pattern("1254427505267.png").similar(0.82).firstN(1))
switchApp("System Preferences.app")
click(Pattern("1254427520117.png").similar(0.70).firstN(1))
for t in find(Pattern("1254425961872.png").similar(0.48).firstN(2)):
  dragLeft(t) # off
  #dragRight(t)  # on
