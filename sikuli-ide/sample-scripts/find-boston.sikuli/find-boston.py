setAutoWaitTimeout(5000)

switchApp("Firefox.app")
type("t", KEY_META)
type("maps.google.com\n")

thumb = find("1254359884578.png")[0]
dragDrop(thumb, [thumb.x, thumb.y+1000])

maps = ["1254359121572.png", "1254359136164.png", "1254359152708.png", "1254359165924.png", "1254359183116.png", "1254359486921.png", "1254359330134.png"]

for m in maps:
  doubleClick(m)

popup("Here we are!")
