d = VDict({
  "1254350186871.png": "Rob", 
  "1254350214900.png": "Michael",
  "1254350243471.png": "Greg"
})

d["1254350295318.png"] = "Jones"

running = True
while running:
  region = Subregion(265,295,558,179)
  for person in d.keys():
    if region.find(person):
      m = region.matches[0]
      photo = capture(m)
      if photo in d:
        popup(d[photo][0] + " posts a new message")
        running = False
        break
      else:
        sleep(1)

