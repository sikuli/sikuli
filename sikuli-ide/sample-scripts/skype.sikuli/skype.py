v = VDict({
  "1254515416894.png" : "1254512196727.png",
  "1254512579147.png" : "1254512182329.png"})

while True:
  call = find(Pattern("1254810120075.png").similar(0.27).firstN(1))
  if call:

    face = capture(call[0].x, call[0].y, 65, 65)

    doubleClick(v[face][0])

