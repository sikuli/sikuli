Sikuli Guide
============

(Coming Soon in Sikuli X 1.0-rc2)

Sikuli Guide is an extension to Sikuli that provides a revolutionary way to
create guided tours or tutorials for GUI applications. The revolutionary aspect
is that the content of the tours or tutorials can be displayed right on the
**actual interface**, rather than in a video or a series of screenshots on a
web page.


Quick Start
^^^^^^^^^^^

First Example
-------------

In our first example, suppose we want to create a guided tour of this very documentation
page you are currently reading. We want to bring your attention to the logo
picture to the right. Using the functions provided by Sikuli Guide, we can
write the following script to accomplish this:

.. sikulicode::

	from guide import *
	addText("sikuli-logo.png", "This is Sikuli's logo")
	show(5)

When you run this script, Sikuli Guide will search for the logo's image on the
screen, highlight it, and display the text ''This is Sikuli's logo'' below the
image, like the figure below: 

.. image:: sikuli-logo-highlight.png

Again, this happens in the **actual interface**, rather than in a video or a
screenshot. The logo image that is highlighted is the actual interface element
users can click on.

Let's explain the script line by line. The first line is an ``import``
statement that tells Sikuli to load the Sikuli Guide extension. The secod line
uses the :py:func:`addText(pattern, text) <addText>` function to add ``text``
next to a given ``pattern``, in this case, the logo image. Note that by default
the text added is not displayed immediately. In the third line, we call
:py:func:`show(secs) <show>` to explicitly tell Sikuli Guide to display the
text for the duration specified by ``secs``. 

Adding Multiple Annotations
---------------------------

It is possible to add text or other annotations to multiple annotation elements before
calling ``show()`` in order to show them on the screen at the same time.

.. sikulicode::

	from guide import *
	addText("sikuli-logo.png", "This is Sikuli's logo")
	addTooltip("previous.png","Previous")
	addTooltip("next.png","Next")
	addTooltip("index.png","Index")
	show(5)

The script above uses the function :py:func:`addTooltip` to add tooltips to
three links in addition to the text annotation. The result of running this
script is shown below: 

.. image:: multiple-annotations.png

Rather than showing the annotations all at once, we can also show them one by
one using separate ``show()`` statements. Below is an example where we cycle
through the three links and show the tooltip of each link one at the time.

.. sikulicode::

	from guide import *
	while True():
		addTooltip("previous.png","Previous") 
		show(3) 
		addTooltip("next.png","Next")
		show(3)
		addTooltip("index.png","Index")
		show(3)

The result of running this script is shown below (3x speed-up):

.. image:: animated-tooltips.gif

Adding Interaction
------------------

Another way to control the flow of a guided tour is to display a message box
and let users click on the button to continue to the next part of the tour.
Sikuli Guide provides a function :py:func:`nextStep(message) <guide.nextStep>`
to accomplish this easily. Below is an example using this function to create a
two-part guided tour.

.. sikulicode::

	from guide import *
	addText("links.png","Use these to jump to other parts")
	nextStep("Part 1: Navigation Links")
	addText("sikuli-logo.png","Use this to go back to Home")
	nextStep("Part 2: Logo")

The tour presented by the script above introduces the navigation links above
and the Sikuli's logo as a shortcut to go back to the documentation's HOME
page. The function call ``nextStep("Part 1")`` indicates the tour is about to
move to the next part. At this point, Sikuli Guide shows all pending
annotations and displays a message box. The caption of this message box is
the string (i.e., Part 1) passed to the function. Users can spend as much
time as they want in the current part. When they are ready to move on, they
can click on the *Next* button.

The figure below shows what happens after Line 3:

.. image:: step1.png

After users click on the *Next* button, the tour moves to the next part. The
screen will look like below:

.. image:: step2.png

Function References
^^^^^^^^^^^^^^^^^^^


Annotations
-----------

.. py:function:: guide.addText

	Add some text.

.. py:function:: guide.addTooltip

	Add a tooltip.

.. py:function:: guide.addArrow

	Add an arrow from one element to another element

Control
-------

.. py:function:: guide.show

	Show annotations added so far for a specified amount of time. 


.. py:function:: guide.nextStep

	Show annotations added so far and display a confirmation message box
	for users to interactively move to the next step.



