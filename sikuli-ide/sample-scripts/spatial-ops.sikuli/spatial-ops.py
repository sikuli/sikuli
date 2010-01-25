switchApp("System Preferences.app")
click("1257868243950.png")
m = find("1257866890251.png")
thumb = m.right().find("1257867315619.png")
dragDrop(thumb, m)

#click("1257868272526.png")
#click(find("1257867639181.png").left().find("1257867652701.png"))


