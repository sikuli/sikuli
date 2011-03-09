from guide import *

skype = App.focus("Skype")

dialog("This tutorial teaches you how to use Skype.")
bracket(skype.window(), side="left")
show()

text("1297814068061.png", "click me to open the dialpad", side="top")
spotlight("1297814068061.png")
show()


dialog("You can call any phone number with the dialpad")
show()


clickable("1297814068061.png")
rectangle("1297814068061.png")
text("1297814068061.png", "click me again to close the dialpad")
#dialog("Let's close the dialpad")
show()

click(Pattern("1297814235567.png").targetOffset(80,2))

flag("1297814547591.png", "Click me to call", side="left")
circle("1297814547591.png")
dialog("you can call a certain person in the address book")
show()


flag("1297814553097.png", "Click me to enable video chat", side="left")
dialog("You can also call with video chat...")
show()




