def setUp(self):
	App.open("Google Chrome")
	type("t", KEY_CMD)
	type("www.youtube.com/watch?v=FxDOlhysFcM" + Key.ENTER)
	wait("Yuu.png", 20)
	
def testPlayButton(self):
	click("1298505377359.png")
	assert exists("1298505385280.png")
	click("1298505385280.png")
	assert exists("1298505427959.png")
	assert not exists("1298505385280.png")

def testMuteButton(self):
	click("ia.png")
	assert exists("1298505629152.png")
	click("1298505629152.png")
