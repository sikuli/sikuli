Sikuli Guide
============

(Coming Soon in Sikuli X 1.0-rc2)

Sikuli Guide is an extension to Sikuli that provides a revolutionary way to
create guided tours or tutorials for GUI application. The revolutionary aspect
is that the content of the tours or tutorials can be displayed right at the
**actual interface**, rather than in a video or a series of screenshots on a
web page.

For example, suppose we want to create a guided tour of this very documentation
page you are currently reading. Frist we want to bring your attention to the logo
picture to the right. Using the functions provided by Sikuli Guide, we can
write the following script to accomplish this:

.. sikulicode::

	from sikuliext.guide import *
	addText("sikuli-logo.png", "This is Sikuli's logo")
	show(5)

When you run this script, Sikuli Guide will search for the logo's image on the
screen, highlight it, and display the text ''This is Sikuli's logo'' below the
image, like the figure below. 

.. image:: sikuli-logo-highlight.png

Again, this happens in the **actual interface** rather than in a video or a
screenshot.

Let's explain the script line by line. The first line is an import statement
that tells Sikuli to load the Sikuli Guide extension. The secod line uses the
:py:func:`addText(pattern, text) <addText>` function to add ``text`` next to a
given ``pattern``, in this case, the logo image. Note that by default the text
added is not drawn immediately. In the third line, we call :py:func:`show(secs)
<show>` to explicitly tell Sikuli Guide to display the text for the duration
specified by ``secs``. 

It is possible to add text or other annotations to multiple elements before
calling ``show()`` to show them on the screen at the same time.

.. sikulicode::

	from sikuliext.guide import *
	addText("sikuli-logo.png", "This is Sikuli's logo")
	addTooltip("previous.png","Previous")
	addTooltip("next.png","Next")
	addTooltip("index.png","Index")
	show(5)

The script below uses the function :py:func:`addTooltip` to add tooltips to three links in addition to the text annotation. The result of running this script is shown below: 

.. image:: multiple-annotations.png

Rather than showing the annotations all at once, we can also show them one by
one using separate ``show()`` statements. Below is an example where we show
the tooltip of each element one after another in an infinite cycle. 

.. sikulicode::

	from sikuliext.guide import *
	while True():
		addTooltip("previous.png","Previous") 
		show(3) 
		addTooltip("next.png","Next")
		show(3)
		addTooltip("index.png","Index")
		show(3)

The result of running this script is shown below (3x speed-up):

.. image:: animated-tooltips.gif


