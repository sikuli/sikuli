How to create Unit Testing Scripts for GUI
==========================================

Sikuli integrates with jUnit and supports unit testing for Graphical User Interfaces (GUI).
The unit testing panel can be opened by clicking the menu
:menuselection:`&View --> Unit Test` or by 
the hot key :kbd:`⌘-U` on Mac or :kbd:`Ctrl-U` on Windows/Linux.

Sikuli IDE aims to minimize the effort of writing code. 
With Sikuli IDE, a Python class inherited from 
`junit.framework.TestCase <http://junit.sourceforge.net/junit3.8.1/javadoc/junit/framework/TestCase.html>`_
is automatically generated to wrap your unit testing script.

A typical unit testing script consists of two constructing and 
destructing methods, 
`setUp() <http://junit.sourceforge.net/junit3.8.1/javadoc/junit/framework/TestCase.html#setUp()>`_ and `tearDown() <http://junit.sourceforge.net/junit3.8.1/javadoc/junit/framework/TestCase.html#tearDown()>`_, 
and a bunch of methods named with a prefix :token:`test`. 

..
   Two specific Sikuli functions for testing are available: 
   assertExist() and assertNotExist(), that raise an !AssertionError if pattern or image is not found or found respectively. (Details: [http://sikuli.org/documentation.shtml#doc/pythondoc-python.edu.mit.csail.uid.SikuliTest.html Command Reference] )

The basic structure of a script is given as following:

.. sikulicode::

   def setUp(self):
     openApp("AnyRandom.app")
     wait(SCREENSHOT_OF_THE_APP) # wait until the app appears

   def tearDown(self):
     closeApp("AnyRandom.app")
     untilNotExist(SCREENSHOT_OF_THE_APP) # wait until the app disappears

   def testA(self):
     ....
     assert exists(PICTURE_THAT_SHOULD_BE_THERE)

   def testB(self):
     ....
     assert not exists(PICTURE_THAT_SHOULD_NOT_BE_THERE)


Here is `a complete example <http://sikuli.org/examples/TestJEdit.sikuli/TestJEdit.html>`_.

To run a unit testing script, you need to click on the :guilabel:`Run`
button in the unit testing panel instead of the ordinary button. 

**IMPORTANT:** Before you try to run your script in this test mode the first time, it has to be saved. Everytime you change something, you have to save it again, before the next test run.

Alternatively, you also can run unit testing scripts from command line
using the option :option:`-t test-script <-t>`.

