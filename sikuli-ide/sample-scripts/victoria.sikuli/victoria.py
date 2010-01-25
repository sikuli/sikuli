def dragRight(t):
  dragDrop(t,  [ t.x + 200, t.y ] )

# http://maps.google.com/maps?f=q&source=s_q&hl=en&geocode=&q=victoria,+bc&sll=10.305461,-61.280365&sspn=0.524239,0.826721&g=victoria&ie=UTF8&ll=48.458807,-123.380413&spn=0.044169,0.168743&z=13

while True:
  t = find(Pattern("1254430393129.png").similar(0.70).firstN(1))[0]
  if t:
    dragRight(t)
  else:
    break