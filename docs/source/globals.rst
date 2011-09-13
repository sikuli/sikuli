Global Functions and Features
=============================

.. index:: import .sikuli

.. _ImportingSikuliScripts:

Importing other Sikuli Scripts (reuse code and images)
------------------------------------------------------

When getting more experienced with scripting or when you are used to structure
your solutions into a modular system, you might want to have access to the
related features of the programming environment - in this case the
Python/Jython features of module support - for your scripts too.

This is possible with Sikuli X:

* import other .sikuli in a way that is fully compatible with Python import
* import a python module structure including underlying Java classes from a
  jar-file, that is dynamically loaded using the function :py:func:`load(jar-file) <load>`
* automatically access images contained in the imported .sikuli (no need to use
  :py:func:`setBundlePath`) 

**Note**: Currently a .skl cannot be imported. As a circumvention it is up to you 
to unzip the .skl on the fly (e.g. with gzip on the command line) to a place of 
your choice as .sikuli (e.g. temp directory) and import it from there.

**The prerequisites**:

* the directories/folders containing your .sikuli's you want to import have to
  be in ``sys.path`` (see below: Usage)

  .. versionadded:: X1.0-rc3
  
* Sikuli automatically finds other Sikuli scripts in the same directory, when they are imported

* your imported script must contain (recommendation: as first line) the
  following statement: ``from sikuli.Sikuli import *`` (this is necessary for the
  Python environment to know the Sikuli classes, methods, functions and global
  names) 

**Usage**:

* Prepare sys.path (since X-1.0rc3: except for scripts in same directory)

* Import your .sikuli using just it's name 

* the example contains a recommendation to avoid double entries::

	# an example - choose your own naming
	# on Windows
	myScriptPath = "c:\\someDirectory\\myLibrary"
	# on Mac/Linux
	myScriptPath = "/someDirectory/myLibrary"

	# all systems
	if not myScriptPath in sys.path: sys.path.append(myScriptPath)

	# supposing there is a myLib.sikuli
	import myLib

	# supposing myLib.sikuli contains a function "def myFunction():"
	myLib.myFunction() # makes the call


**Note on contained images:** Together with the import, Sikuli internally uses
the new :ref:`SIKULI_IMAGE_PATH <ImageSearchPath>` to make sure that images contained in imported
.sikuli's are found automatically.

**Some comments for readers not familiar with Python import**

*	An import is only processed once (the first time it is found in the program
	flow). So be aware: 
	
	*	If your imported script contains code outside of any function definitions ( ``def()`` ),
		this code is only processed once at the first time, when the import is evaluated

	*	Since the IDE is not reset at rerun of scripts: when changing
	 	imported scripts while they are in use, you have to restart the IDE. 
	 	
	* Alternative, to restarting the IDE, while editing imported scripts:
	
		use the following combinations with Jythons reload() function:: 

			# instead of: import module
			import module
			reload(module) 

			# instead of: from module import *
			import module
			reload(module)
			from module import *  	
 	
*	Python has a so called namespace concept: names (variables, functions,
	classes) are only known in it's namespace your main script has it's own namespace

	*	Each imported script has its own namespace. So names contained in an 
		imported script have to be qualified with the	module name (e.g. ``myLib.myFunction()`` )

	*	You may use ``from myLib import *``, which integrates all names from myLib
		into your current namespace. So you can use ``myFunction()`` directly. When you
		decide to use this version, be sure you have a naming convention that
		prevents naming conflicts.


**Another example: Importing from the same directory**

This approach allows to develop a modularized script app that is contained in
one directory. This directory can be moved around with no changes and even
distributed as a zipped file::

	# works on all platforms
	import os
	# get the directory containing your running .sikuli
	myPath = os.path.dirname(getBundlePath()) 
	if not myPath in sys.path: sys.path.append(myPath)

	# now you can import every .sikuli in the same directory
	import myLib
	
.. versionadded:: X1.0-rc3

Since scripts in same directory are found automatically::

    # nothing else needed
	# now you can import every .sikuli in the same directory
	import myLib


**Loading a jar-file containing Java/Python modules**
	
.. versionadded:: X1.0-rc2
.. py:function:: load(jar-file)

	Loads a jar-file and puts the absolute path to it into sys.path, so 
        the Java or Python code in that jar-file can be imported afterwards.
	
	:param jar-file: either a ``filename.jar`` without any path or the absolute 
		path to ``filename.jar``
	:return: ``True`` if the file was found, otherwise ``False``
	
	**Note:** if no path is specified, Sikuli first looks into the bundle (the
	Sikuli folder of the running script) and then into the extensions folder.
	(more information: :ref:`Sikuli Extensions <sikuliextensions>`)

.. _ControllingSikuliScriptsandtheirBehavior:

Controlling Sikuli Scripts and their Behavior
---------------------------------------------

.. py:function:: setShowActions(False | True)

	If set to *True*, when a script is run, Sikuli shows a visual effect (a blinking
	double lined red circle) on the spot where the action will take place before
	executing actions (e.g. ``click()``, ``dragDrop()``, ``type()``, etc) for about 2 seconds in the
	standard (see :py:attr:`Settings.SlowMotionDelay` ). The default setting is False.

.. py:function:: exit([value])

	Stops the script gracefully at this point. The value is returned to the calling
	environment. 

.. py:class:: Settings

.. versionadded:: X1.0-rc2
.. py:attribute:: Settings.ActionLogs
	Settings.InfoLogs
	Settings.DebugLogs
	
	Either option might be switched on (True) or off (False), to show or hide the respective message type in the IDE console or on command line ([log], [info], [debug]).
	
.. py:attribute:: Settings.MinSimilarity

	The default minimum similiarty of find operations.
        While using a :py:meth:`Region.find` operation, 
        if only an image file is provided, Sikuli searches
        the region using a default minimum similarity of 0.7.

.. py:attribute:: Settings.MoveMouseDelay

	Control the time taken for mouse movement to a target location by setting this
	value to a decimal value (default 0.5). The unit is seconds.  Setting it to
	0 will switch off any animation (the mouse will "jump" to the target location). 

	As a standard behavior the time to move the mouse pointer from the current
	location to the target location given by mouse actions is 0.5 seconds. During
	this time, the mouse pointer is moved continuosly with decreasing speed to the
	target point. An additional benefit of this behavior is, that it gives the
	active application some time to react on the previous mouse action, since the
	e.g. click is simulated at the end of the mouse movement::

		mmd = Settings.MoveMouseDelay # save default/actual value
		click(image1) # implicitly wait 0.5 seconds before click
		Settings.MoveMouseDelay = 3
		click(image2) # give app 3 seconds time before clicking again
		Settings.MoveMouseDelay = mmd # reset to original value

.. py:attribute:: Settings.DelayAfterDrag
			Settings.DelayBeforeDrop

	*DelayAfterDrag* specifies the waiting time after mouse down at the source
	location as a decimal value (seconds). 
	
	*DelayBeforeDrop* specifies the
	waiting time before mouse up at the target location as a decimal value
	(seconds).

	**Usage**: When using :py:meth:`Region.dragDrop` you may have situations, where the
	operation is not processed as expected. This may be due to the fact, that the
	Sikuli actions are too fast for the target application to react properly. With
	these settings the waiting time after the mouse down at the source location and
	before the mouse up at the target location of a dragDrop operation  are
	controlled. The standard settings are 0.3 seconds for each value. The time that
	is taken, to move the mouse from source to target is controlled by
	:py:attr:`Settings.MoveMouseDelay`::


		# you may wish to save the actual settings before
		Settings.DelayAfterDrag = 1
		Settings.DelayBeforeDrop = 1
		Settings.MoveMouseDelay = 3
		dragDrop(source_image, target_image)
		# time for complete dragDrop: about 5 seconds + search times


.. py:attribute:: Settings.SlowMotionDelay

	Control the duration of the visual effect (seconds).


.. py:attribute:: Settings.WaitScanRate
			Settings.ObserveScanRate

	Specify the number of times actual search operations are performed per second
	while waiting for a pattern to appear or vanish.
	
	As a standard behavior Sikuli internally processes about 3 search operations per
	second, when processing a :py:meth:`Region.wait`, :py:meth:`Region.exists`,
	:py:meth:`Region.waitVanish`, :py:meth:`Region.observe`).  In cases where this
	leads to an excessive usage of system ressources or if you intentionally want to
	look for the visual object not so often, you may set the respective values to
	what you need. Since the value is used as a rate per second, specifying values
	between 1 and near zero, leads to scans every x seconds (e.g. specifying 0.5
	will lead to scans every 2 seconds)::

		def myHandler(e):
			print "it happened"
			
		# you may wish to save the actual settings before
		Settings.ObserveScanRate = 0.2
		onAppear(some_image, myHandler)
		observe(FOREVER, background = True)
		# the observer will look every 5 seconds
		# since your script does not wait here, you 
		# might want to stop the observing later on ;-)

.. versionadded:: X1.0-rc2		
.. py:attribute:: Settings.ObserveMinChangedPixels

	 The minimum size in pixels of a change to trigger a change event when using :py:meth:`Region.onChange` without specifying this value.
	 The default value is 50.


Controlling Applications and their Windows
------------------------------------------

Here we talk about opening or closing other applications, switching to them (bring
their windows to front) or accessing an application's windows.

The three global functions :py:func:`openApp`, :py:func:`switchApp` and
:py:func:`closeApp` introduced in Sikuli 0.9 and 0.10 are still valid in the moment,
but they should be considered as deprecated.  They are being replaced by a new
:py:class:`App` class introduced in Sikuli X. This class makes it possible to treat
a specific application as an object with attributes and methods.  We recommend to
switch to the class App and its features, the next time you work with one of your
existing scripts and in all cases, when developing new scripts.

**General hint for Windows users** on backslashes \\ and double apostrophes "

In a Sikuli script in normal strings enclosed in " (double apostrophes), 
these special characters \\ and " have to be escaped using a backslash, 
when you have them inside the string. So for one backslash you need \\\\ 
and for one " you need \\". In a string enclosed in ' (single apostrophes), a ' 
has to be \\' and a " is taken as such.

To avoid any problems, it is recommended to use the raw string ``r'some text with \\ and " ...'``,
since there is no need for escaping.
This is especially useful, when you have to specify Windows path's or want to 
setup command lines for use with App.open(), openApp(), os.popen or Jythons Subprocess module.

a fictive command line example::
	
	cmd = r'c:\Program Files\myapp.exe -x "c:\Some Place\some.txt" >..\log.txt'
	openApp(cmd)

**This is a comparism of old (xxxApp) and new (App.xxx) functions:** 

*	Open an application: :py:func:`openApp` --> :py:meth:`App.open`
*	Switch to an application or application window: :py:func:`switchApp` -->
	:py:meth:`App.focus`
*	Close an application: :py:func:`closeApp` --> :py:meth:`App.close`

.. py:function:: openApp(application)

	Open the specified application.

	:param application: a string containing the name of an application (case-insensitive), that can be
		found in the path used by the system to locate applications. Or it can be the
		full path to an application.
		
		**Note for Windows:**  (since X-1.0rc3) The string may contain commandline parameters 
		for the specified program or batch file after the name or full path.

	This function opens the specified application and brings its windows to the
	front. This is equivalent to :py:meth:`App.open`. Depending on the system and/or
	the application, this function may switch to an already opened application or
	may open a new instance of the application.

	Examples::

		# Windows: opens command prompt (found through PATH)
		openApp("cmd.exe")
		
		#Windows (since X-1.0rc3): with parameters (no sense, only to show ;-)
		openApp(r'cmd.exe /c start c:\Program Files\myapp.bat')

		# Windows: opens Firefox (full path specified)
		openApp("c:\\Program Files\\Mozilla Firefox\\firefox.exe") 
		
		# Mac: opens Safari
		openApp("Safari")

.. py:function:: switchApp(application)

	Switch to the specified application.

	:param application: the name of an application (case-insensitive) or (part of) a
		window title (Windows/Linux).

	This function switches the focus to the specified application and brings its
	windows to the front. This function is equivalent to :py:meth:`App.focus`. 
	
	On Windows/Linux, the window is the one identified by the *application* string.
	This string is used to search the title text of all the opened windows for any
	part of the title matching the string. Thus, this string needs not be an
	application's name. For example, it can be a filename of an opened document that
	is displayed in the title bar. It is useful for choosing a particular window out
	of the many windows with different titles.

	On Mac, the *application* string is used to identify the application. If the
	application has multiple windows opened, all these windows will be brought to
	the front. The relatively ordering among these windows remain the same.

	Example::

		# Windows: switches to an existing command prompt or starts a new one
		switchApp("cmd.exe")

		# Windows: opens a new browser window
		switchApp("c:\\Program Files\\Mozilla Firefox\\firefox.exe")

		# Windows: switches to the frontmost opened browser window (or does nothing
		# if no browser window is currently opened)
		switchApp("mozilla firefox")

		# Mac: switches to Safari or starts it
		switchApp("Safari")

.. py:function:: closeApp(application)

	Close the specified application.

	:param application: the name of an application (case-insensitive) or (part of) a
		window title (Windows/Linux)

	This function closes the application indicated by the string *application* (Mac) or
	the windows whose titles contain the string *application* (Windows/Linux). this
	function is equivalent to :py:meth:`App.close`. On Windows/Linux, the
	application itself may be closed if the main window is closed or if all the
	windows of the application are closed.

	Example::

		# Windows: closes an existing command prompt
		closeApp("cmd.exe")

		# Windows: does nothing, since text can not be found in the window title
		closeApp("c:\\Program Files\\Mozilla Firefox\\firefox.exe")

		# Windows: stops firefox including all its windows
		closeApp("mozilla firefox")

		# Mac: closes Safari including all its windows
		closeApp("Safari")

.. py:function:: run(command)

	Run *command* in the command line

	:param command: a command that can be run from the command line.

	This function executes the command and the script waits for its completion.

	
The Application Class
^^^^^^^^^^^^^^^^^^^^^^^^^

.. py:class:: App

Sikuli-X introduces the new class called :py:class:`App` to provide a more
convenient and flexible way to control the application and its windows.

:ref:`go directly to the methods <ClassAppMethods>`

**Using class methods or instance methods**

Generally you have the choice between using the class methods (e.g.
``App.open("application-identifier")``) or first create an App instance and use
the instance methods afterwards (e.g. ``myApp = App("application-identifier")``
and then later on ``myApp.open()``). In the current state of the feature
developement of the class App, there is no recomendation for a preferred usage.
The only real difference is, that you might save some ressources, when using the
instance approach, since using the class methods produces more intermediate
objects. 

.. _CreateAppInstance:

**How to create an App instance**

The basic choice is to just say ``someApp = App("some-app-identifier")`` and you
have your app instance, that you can later on use together with its methods,
without having to specify the string again. 

Additionally ``App.open("some-app-identifier")`` and ``App.focus("some-app-identifier")``
return an app instance, that you might save in a variable to use it later on in
your script. 

**Differences between Windows/Linux and Mac**

Windows/Linux: Sikuli's strategy on these systems in the moment is to rely on
implicit or explicit path specifications to find an application, that has to be
started. Running "applications" can either be identified using their PID
(process ID) or by using the window titles. So using a path specification will
only switch to an open application, if the application internally handles the
"more than one instance" situation".

You usually will use ``App.open("c:\\Program Files\\Mozilla Firefox\\Firefox.exe")``
to start Firefox. This might open an additional window. And you can use
``App.focus("Firefox")`` to switch to the frontmost Firefox window (which has no
effect if no window is found). To clarify your situation you may use the new
window() method, which allows to look for existing windows. The second possible
approach is to store the App instance, that is returned by ``App.open()``, in a
variable and use it later on with the instance methods (see examples below).

If you specify the exact window title of an open window, you will get exactly
this one. But if you specify some text, that is found in more than one open
window title, you will get all these windows in return. So this is good e.g.
with Firefox, where every window title contains "Mozilla Firefox", but it might
be inconvenient when looking for "Untitled" which may be in use by different
apps for new documents. So if you want exactly one specific window, you either
need to know the exact window title or at least some part of the title text,
that makes this window unique in the current context (e.g. save a document with
a specific name, before accessing it's window).

On Mac OS X, on the system level the information is available, which windows
belong to which applications. Sikuli uses this information. So by default using
e.g. ``App.focus("Safari")`` starts Safari if not open already and switches to the
application Safari if it is open, without doing anything with it's windows (the
z-order is not touched). Additionally, you can get all windows of an
application, without knowing it's titles.

Note on Windows: when specifying a path in a string, you have to use \\ (double
backslash) for each \ (backslash)
e.g. ``myPath = "c:\\Program Files\\Sikuli-IDE\\Lib\\"`` )

.. _ClassAppMethods:

.. py:class:: App
  
	.. py:classmethod:: open(application)
	
		*Usage:* ``App.open(application)``

		Open the specified application.

		:param application: The name of an application (case-insensitive), that can
			be found in the path used by the system to locate applications, or the
			full path to an application (Windows: use double backslash \\ in the
			path string to represent a backslash)
			
			**Note for Windows:** (since X-1.0rc3) The string may contain commandline parameters 
			for the specified program or batch file after the name or full path (see: :py:func:`openApp`)

			
		:return: an App object, that can be used with the instance methods.
		
		This method is functionally equivalent to :py:func:`openApp`. It opens the
		specified application and brings its window the front. Whether this
		operation switches to an already opened application or opens a new instance
		of the application depends on the system and application.

	.. py:method:: open()
	
		*Usage:* ``someApp.open()`` where App instance ``someApp`` was :ref:`created before <CreateAppInstance>`.
	
		Open this application.


	.. py:classmethod:: focus(application)

		*Usage:* ``App.focus(application)``

		Switch the focus to an application.

		:param application: The name of an application (case-insensitive) or (part
			of) a window title (Windows/Linux).

		:return: an App object, that can be used with the instance methods.
		
	.. py:method:: focus()
	
		*Usage:* ``someApp.focus()`` where App instance ``someApp`` was :ref:`created before <CreateAppInstance>`.

		Switch the focus to this application.


	.. py:classmethod:: close(application)
	
		*Usage:* ``App.close(application)``

		Close the specified application.

		:param application: The name of an application (case-insensitive) or (part
			of) a window title (Windows/Linux).

		This method is functionally equivalent to :py:func:`closeApp`. It closes the
		given application or the matching windows (Windows/Linux). It does nothing
		if no opened window (Windows/Linux) or running application (Mac) can be
		found. On Windows/Linux, whether the application itself is closed depends on
		weather all open windows are closed or a main window of the application is
		closed, that in turn closes all other opened windows. 

	.. py:method:: close()

		*Usage:* ``someApp.close()`` where App instance ``someApp`` was :ref:`created before <CreateAppInstance>`.

		Close this application.

	.. py:classmethod:: focusedWindow()

		*Usage:* ``App.focusedWindow()``

		Identify the currently focused or the frontmost window and switch to it.
		Sikuli does not tell you, to which application this window belongs.

		:return: a :py:class:`Region` object representing the window or *None* if
			there is no such window.

		On Mac, when starting a script, Sikuli hides its window and starts
		processing the script. In this moment, no window has focus. Thus, it is
		necessary to first click somewhere or use ``App.focus()`` to focus on a
		window. In this case, this method may return *None*.

		On Windows, this method always returns a region. When there is no window
		opened on the desktop, the region may refer to a special window such as the
		task bar or an icon in the system tray.
	
		Example::

			# highlight the currently fontmost window for 2 seconds
			App.focusedWindow().highlight(2)

			# save the windows region before
			firstWindow = App.focusedWindow()
			firstWindow.highlight(2)

	.. py:method:: window([n])

		*Usage 1:* ``App(application).window([n])`` an App instance is created on the fly.
		
		*Usage 2:* ``someApp.window([n])`` where App instance ``someApp`` was :ref:`created before <CreateAppInstance>`.

		Get the region corresponding to the n-th window of this application (Mac) or
		a series of windows with the matching title (Windows/Linux). 

		:param n: 0 or a positive integer number. If ommitted, 0 is taken as
			default.

		:return: the region on the screen occupied by the window, if such window
			exists and *None* if otherwise.
	
		Below is an example that tries to open a Firefox browser window and switches
		to the address field (Windows)::	

			# using an existing window if possible
			myApp = App("Firefox")
			if not myApp.window(): # no window(0) - Firefox not open
				App.open("c:\\Program Files\\Mozilla Firefox\\Firefox.exe")
				wait(2)
			myApp.focus()
			wait(1)
			type("l", KEY_CTRL) # switch to address field

		Afterwards, it focuses on the Firefox application, uses the ``window()`` method to
		obtain the region of the frontmost window, applies some operations
		within the region, and finally closes the window::

			# using a new window
			firefox = App.open("c:\\Program Files\\Mozilla Firefox\\Firefox.exe");
			wait(2)
			firefox.focus()
			wait(1)
			# now your just opened new window should be the frontmost 
			with firefox.window(): # see the general notes below
				# some actions inside the window(0)'s region
				click("somebutton.png")
			firefox.close() # close the window - stop the process


		Below is another example that highlights all the windows of an
		application by looping through them (Mac)::

			# not more than 100 windows should be open ;-)
			myApp = App("Safari")
			for n in range(100):
				w = myApp.window(n)
				if not w: break # no more windows
				w.highlight(2) # window highlighted for 2 second


		General notes:

		*	Be aware, that especially the window handling feature is experimental
			and under further development.

		*	Especially on Windows be aware, that there might be many matching
			windows and windows, that might not be visible at all. Currently the
			``window()`` function has no feature to identify a special window besides
			returning the region. So you might need some additional checks to be
			sure you are acting on the right window. 

		*	Windows/Linux: The ``close()`` function currently kills the application,
			without closing it's windows before. This is an abnormal termination and
			might be recognized by your application at the next start (e.g. Firefox
			usually tries to reload the pages).

		*	Even if the windows are hidden/minimized, their region that they have in
			the visible state is returned. Currently there is no Sikuli feature, to
			decide wether the given window(n) is visible or not or if it is
			currently the frontmost window. The only guarentee: ``window()``/``window(0)``
			is the topmost window of an application (Mac) or a series of matching
			windows (Windows/Linux). 

		*	Currently there are no methods available to act on such a window
			(resize, bring to front, get the window title, ...).

		Some tips:

		*	Check the position of a window's returned region: some apps hide there
			windows by giving them "outside" coordinates (e.g. negative) 

		*	Check the size of a window's returned region: normally your app windows
			will occupy major parts of the screen, so a window's returned region of
			e.g. 150x30 might be some invisible stuff or an overlay on the real app
			window (e.g. the "search in history" input field on the Safari Top-Sites
			page, which is reported as ``windows(0)``)

		*	If you have more than one application window, try to position them at
			different coordinates, so you can decide which one you act on in the
			moment.

		*	It is sometimes possible to use the OCR text extraction feature 
			:py:meth:`Region.text` to obtain the window title.



Interacting with the User
-------------------------

.. versionadded:: X1.0-rc3
.. py:function:: popup(text, [title])

	Display a dialog box with an *OK* button and *text* as the message. The script
	then waits for the user to click the *OK* button.
	
	:param text: text to be displayed as message
	
	:param title: optional title for the messagebox

	Example::

		popup("Hello World!\nHave fun with Sikuli!")
	
	A dialog box that looks like below will popup (Note: `\n` can break a line).

	.. image:: popup.png

.. py:function:: input([text])

	Display a dialog box with an input field, a Cancel button, and an OK button. The
	optional *text* can be displayed as a caption. The script then waits for the
	user to click either the Cancel or the OK button.
	
	:param text: optional text to be displayed as message
	
	:return: the text, the user has entered, when clicked **OK**

		**None**, if the user pressed the **Cancel** button

	Example::

		name = input("Please enter your name to log in:")

	.. image:: input.png
	
	A dialog box that looks like above will appear to allow the user to
	interactively enter some text. This text is then assigned to the variable
	*name*, which can be used in other parts of the script, such as ``paste(name)``
	to paste the text to a login box.


Listening to Global Hotkeys
---------------------------

Sikuli can listen to global hotkeys that you register with ``Env.addHotkey`` 
and call the corresponding handler (sikuli functions) when the user presses
the hotkeys.

.. versionadded:: X1.0-rc3
.. py:method:: Env.addHotkey(key, modifiers, handler)

 	Register the specified *key* + *modifiers* as a global hotkey. 
 	When the hotkey is pressed, the specified function *handler* will be called.

	:param key: a character or a constant value defined in :py:class:`Key`.

	:param modifiers: Key modifiers, which can be one or multiple constants defined in :py:class:`KeyModifier`.

	:return: True if success.

        .. sikulicode::

           def openAppleMenu(event):
              click("apple.png")

           # When the user pressed Ctrl+Alt+F1, click the top-left apple icon.
           Env.addHotkey(Key.F1, KeyModifier.ALT+KeyModifier.CTRL, openAppleMenu)


.. versionadded:: X1.0-rc3
.. py:method:: Env.removeHotkey(key, modifiers)

 	Unregister the registered global hotkey *key* + *modifiers*. 

	:param key: a character or a constant value defined in :py:class:`Key`.

	:param modifiers: Key modifiers, which can be one or multiple constants defined in :py:class:`KeyModifier`.

	:return: True if success.



General Settings and Access to Environment Information
------------------------------------------------------


**Sikuli Level**

Sikuli internally uses the class :py:class:`Settings` to store globally used
settings. Publicly available attributes may be accessed by using
``Settings.[name-of-an-attribute]`` to get it's value and ``Settings.attribute = value``
to set it. It is highly recommended to only modify attributes, that are described in
this document or when you really know, what you are doing.

Actually all attributes of some value for scripting are described in the 
topic :ref:`Controlling Sikuli Scripts and their Behavior <ControllingSikuliScriptsandtheirBehavior>`.

**Jython/Python Level**

You may use all settings, that are defined in standard Python/Jython and that are
available in your system environment. The modules sys and time are already imported,
so you can use their methods without the need for an import statement.

``sys.path`` may be one of the most valuable settings, since it is used by
Python/Jython to locate modules, that are referenced using ``import module``. It is
a list of path's, that is e.g. maintained by Sikuli to implement :ref:`Importing
other Sikuli Scripts <ImportingSikuliScripts>` as a standard
compliant feature.

If you want to use ``sys.path``, it is recommended to do it as shown in the following
example, to avoid appending the same entry again::

	myPath = "some-absolute-path"
	if not myPath in sys.path:
		sys.path.append(myPath)

**Java Level**

Java maintains a global storage for settings (key/value pairs), that can be accessed
by the program/script. Sikuli uses it too for some of it's settings. Normally it is
not necessary to access these settings at the Java level from a Sikuli script, since
Sikuli provides getter and setter methods for accessing values, that make sense for
scripting. One example is the list of paths, that Sikuli maintains to specify
additional places to search for images (please refer to :ref:`Importing
other Sikuli Scripts <ImportingSikuliScripts>` for more information).

If needed, you may access the java settings storage as shown in the following
example::

	import java
	
	# get a value
	val = java.lang.System.getProperty("key-of-property")
	
	# set a property's value
	java.lang.System.getProperty("key-of-property", value)

.. index:: 
	pair: Image Search Path; SIKULI_IMAGE_PATH

.. _ImageSearchPath:

**Image Search Path**

Sikuli maintains a list of locations to search for images when they are not found in
the current .sikuli folder (a.k.a. bundle path). This list is maintained internally
but can be inspected and/or modified using the following functions:

.. py:function:: getImagePath()

	Get a list of paths where Sikuli will search for images. ::
	
		# getImagePath() returns a Java array of unicode strings
		imgPath = list(getImagePath()) # makes it a Python list
		# to loop through
		for p in imgPath:
			print p

.. py:function:: addImagePath(a-new-path)

	Add a new path to the list of image search paths

.. py:function:: removeImagePath(a-path-already-in-the-list)

	Remove a path from the list of image search paths

*Note*: paths must be specified using the correct path separators (slash on Mac
and Unix and double blackslashes on Windows).

This list is automatically extended by Sikuli with script folders, that are imported 
(see: :ref:`Importing other Sikuli Scripts <ImportingSikuliScripts>`), 
so their contained images can be accessed. If you want to
be sure of the results of your manipulations, you can use ``getImagePath`` and check
the content of the returned list.  When searching images, the path's are scanned in
the order of the list. The first image file with a matching image name is used.

*Note*: Behind the scenes this list is maintained in the java property store with the
key SIKULI_IMAGE_PATH. This can be preset when starting the JVM using the
environment variable SIKULI_IMAGE_PATH and can be accessed at runtime using the
approach as mentioned under Accessing Settings - Java level. Be aware, that this is
one string, where the different entries are separated with a colon ( : ).

.. index:: Bundle Path

**The default bundle path** can also be accessed and modified by the two functions
below:

.. py:function:: setBundlePath(path-to-a-folder)

	Set the path for searching images in all Sikuli Script methods. Sikuli IDE sets
	this automatically to the path of the folder where it saves the script
	(.sikuli). Therefore, you should use this function only if you really know what
	you are doing. Using it generally means that you would like to take care of your
	captured images by yourself.

	Additionally images are searched for in the :ref:`SIKULI_IMAGE_PATH <ImageSearchPath>`, that is a global
	list of other places to look for images. It is implicitly extended by script
	folders, that are imported (see: :ref:`Reuse of Code and Images <ImportingSikuliScripts>`).

.. py:function:: getBundlePath()

	Get a string containing a fully qualified path to a folder containing your images
	used for finding patterns. Note: Sikuli IDE sets this automatically to the path
	of the folder where it saves the script (.sikuli). You may use this function if,
	for example, to package your private files together with the script or to access
	the picture files in the .sikuli bundles for other purposes. Sikuli only gives
	you to access to the path name, so you may need other python modules for I/O or
	other purposes.

	Other places, where Sikuli looks for images, might be in the :ref:`SIKULI_IMAGE_PATH <ImageSearchPath>`.

**Other Environment Information**

.. py:method:: Env.getOS()
		Env.getOSVersion()
		
	Get the type ( ``getOS()`` ) and version ( ``getOSVersion()`` ) of the operating system your
	script is running on.
	
	An example using these methods on a Mac is shown below::

		# on a Mac
		myOS = Env.getOS()
		myVer = Env.getOSVersion()

		if myOS == OS.MAC:
			print "Mac " + myVer # e.g., Mac 10.6.3
		else:
			print "Sorry, not a Mac"

		myOS = str(Env.getOS()) 
		if myOS == "MAC" or myOS.startswith("M"):
			print "Mac " + myVer # e.g., Mac 10.6.3
		else:
			print "Sorry, not a Mac"

.. versionadded:: X1.0-rc2
.. py:method:: Env.getSikuliVersion()
		
	Get the version of Sikuli.
	
	:return: a string containing the version text of the IDE window title without "Sikuli "
	
	An example for Sikuli X-1.0rc2::

		if not Env.getSikuliVersion() == "X-1.0rc2":
			print "This script needs Sikuli X-1.0rc2"
			exit(1)
	
.. py:method:: Env.getClipboard()

	Get the content of the clipboard if it is text, otherwise an empty string.

	*Note*: Be careful, when using ``Env.getClipboard()`` together with ``paste()``,
	since paste internally uses the clipboard to transfer text to other
	applications, the clipboard will contain what you just pasted. Therefore, if you
	need the content of the clipboard, you should call ``Env.getClipboard()`` before
	using ``paste()``.

	*Tip*: When the clipboard content was copied from a web page that mixes images and
	text, you should be aware, that there may be whitespace characters around and
	inside your text, that you did not expect. In this case, you can use
	``Env.getClipboard().strip()`` to get rid of surrounding white spaces.

.. versionadded:: X1.0-rc2
.. py:method:: Env.isLockOn(key-constant)

	Get the current status ( on / off ) off the respective key. Only one key can be specified.
	
	:parameter key-constant: one of the key constants ``Key.CAPS_LOCK``, ``Key.NUM_LOCK``, ``Key.SCROLL_LOCK``
	:return: True if the specified key is on, False otherwise
	
	Further information about key constants can be found in Class :py:class:`Key`.


.. versionadded:: X1.0-rc2
.. py:method:: Env.getMouseLocation()

	Get the current location of the mouse cursor.
	
	:return: a :py:class:`Location` object of the position of the mouse cursor on the screen.
	


Advanced Settings for Tuning Vision Algorithm
---------------------------------------------


.. versionadded:: X1.0-rc3
.. py:method:: Vision.setParameter(param, value)

	Set the parameter *param* of the vision algorithm to *value*.
	
	:parameter param: a string that indicates the parameter to set.
	:parameter value: a float value.


.. py:method:: Vision.getParameter(param)

	Get the parameter *param* of the vision algorithm.
	
	:parameter param: a string that indicates the parameter to get.
	:return: the float value of the specified parameter.



The available parameters for tuning the vision algorithm of Sikuli is listed as
follows.

.. _min-target-size:

.. versionadded:: X1.0-rc3

MinTargetSize
^^^^^^^^^^^^^

``MinTargetSize`` is the minimum image size to which Sikuli can resize. 

Sikuli resizes the screen images to a smaller scale for faster matching. This scaling process speeds up the matching process, but also increases the possibility 
of false matching.
The default value of ``MinTargetSize`` in X-1.0rc3 is 12, which makes the matching algorithm be balanced between speed and robustness. 
If you feel that Sikuli is running too slow, 
try a smaller value than 12. On the other hand, if you see Sikuli returns a match that is not what you expect, i.e. a false match, 
try to increase ``MinTargetSize`` to make Sikuli be more robust to small details.

You can tune this parameter using the following Jython code. 

.. sikulicode::

   from org.sikuli.script.natives import Vision

   Vision.setParameter("MinTargetSize", 6) # the default is 12. Setting the size to a smaller value would make the matching algorithm be faster.


