setThrowException(False)
setAutoWaitTimeout(100)

x = Pattern("1254490074804.png").similar(0.70).firstN(1)
while True:
  if find(x):
    doubleClick(x)
    break
  else:
    click(Pattern("1254423632665.png").similar(0.76).firstN(1))







