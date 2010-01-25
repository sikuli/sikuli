# create some notes
v = VDict()
v["1254684338981.png"] = 'use 42.242.123.43'
v["1254685582365.png"] = 'use my default login'

# add a new note
popup('Take a screenshot to add a note')
new_image = capture()
new_note = input()
v[new_image] = new_note

# lookup some notes
for i in (1,2):
  popup('Take a screenshot to retrieve a note')
  query_image = capture()
  retrieved_note = v[query_image][0]
  popup(retrieved_note)
