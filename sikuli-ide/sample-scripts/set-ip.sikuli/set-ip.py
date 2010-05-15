ip = input("please enter the new IP address:")
switchApp("System Preferences.app")
title = "1273537071282.png"
with Region(find(title).below()):
	click("1273537235732.png")
	click("1254367484704.png")
	click("1256519960853.png")
	click("1254367285543.png" )
	wait("1256520016190.png")
	type(ip + "\t")
	type("255.255.255.0\t")
	type("192.168.0.254\t")
	click("1254367352295.png")
	