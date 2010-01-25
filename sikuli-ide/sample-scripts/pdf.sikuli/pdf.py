setThrowException(False)
x = capture()
popup("switch to pdf")
while not find(x):
     click(Pattern("1254500709806.png").similar(0.72).firstN(1))
popup("found")














