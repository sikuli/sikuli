def OCR_demo():
	popup(selectRegion().text())

def OCR_demo2():
	skype = Region(App.open("Skype").window())
	skype.click("Add Contact")


def Screenshot_naming():
	#screenshot naming
	#"SikuliIDE.png"

	pass

def AppClass():
	app = App.open("Skype")
	app.window().highlight()
	popup("dismiss highlight")
	app.window().highlight()
	
#OCR_demo()
#OCR_demo2()
AppClass()
