def changed(event):
    print "something changed in ", event.region
    for ch in event.changes:
        ch.highlight() # highlight all changes
    sleep(1)
    for ch in event.changes:
        ch.highlight() # turn off the highlights

with selectRegion("select a region to observe") as r:
   # any change larger than 50 pixels would trigger the changed function
    onChange(50, changed)
    observe(background=True)

wait(30) # another way to observe for 30 seconds
r.stopObserver()