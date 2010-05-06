def resizeApp(app, dx, dy):
	switchApp(app)
	corner = find(Pattern("1273159241516.png").targetOffset(3,14))
	dragDrop(corner, corner.getCenter().offset(dx, dy))

resize("Safari", 50, 50)

