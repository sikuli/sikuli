d = VDict()
d["1254333137772.png"] = "folder"
d["1254333158003.png"] = "tar"
d["1254333179627.png"] = "disk"



popup("select a icon")
img = capture()
result = d[img]
if result:
  popup(result[0])
else:
  popup("unknown")

