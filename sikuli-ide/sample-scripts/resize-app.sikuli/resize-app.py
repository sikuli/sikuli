def resizeApp(app, dx, dy):
	switchApp(app)
	corner = find(Pattern("1273159241516.png").targetOffset(3,14))
	dragDrop(corner, corner.getCenter().offset(dx, dy))

resizeApp("Safari", 50, 50)
# exists("1273159241516.png")
# click(Pattern("1273159241516.png").targetOffset(3,14).similar(0.7).firstN(2))
# with Region(10,100,300,300):
#    pass
# click("__SIKULI-CAPTURE-BUTTON__")
	
