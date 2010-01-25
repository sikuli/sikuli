# store some notes
d = VDict()
d["1254600920086.png"] = 'monitor'
d["1254601094354.png"] = 'light'

# add a new note
img = capture()
d[img] = input()

# lookup notes
for i in (1,2):
  query = capture()
  note = d[query][0]
  popup(note)