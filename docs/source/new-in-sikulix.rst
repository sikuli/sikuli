What is new in Sikuli X
=======================

Sikuli X is a new experimental branch of Sikuli. (X stands for eXperimental.)
For all current users of Sikuli 0.9 or 0.10 we recommend to upgrade to X.

However, please keep in mind some new features are still experimental, e.g. text
recognition and the new API to get the bound of any windows, which means they may
not work well or not support all platforms yet.

.. versionadded:: X1.0-rc2

Features marked with RC2 in the following list are only available beginning with Sikuli X-1.0rc2. 
In the documentation sections you might look for the above marker to identify new features.

* RC2: the IDE outfit is revised. Find and Undo/Redo is added. Console messages are colored now. The font is now monospaced and smaller.

* RC2: The message concept is completely revised. Verbosity can be adjusted now
	(see :py:attr:`Sikuli Messages <Settings.ActionLogs>`).

* RC2: Extensions (jar-files containing complementary Sikuli features) can be downloaded from a repository managed by the Sikuli developers. As the first extension **Sikuli Guide** (an annotation tool - Windows and Mac only) is available. You may contribute your own extensions. 
	(see: :ref:`Extensions <sikuliextensions>`).

* RC2: sikuli-script.jar together with the native libraries can now be used really standalone (only a few standards have to be followed), so it is easier now to integrate Sikuli with other IDE's, frameworks or applications and :ref:`run scripts from commandline <runsikuliscript>`. 

* RC2: sikuli-script.jar on commandline now accepts a :ref:`new option -i <sikuliscriptinteractive>`, that starts up the interactive Jython with the Sikuli environment already initialized (for tests on the fly from commandline without the IDE). 

* RC2: :py:meth:`Env.getSikuliVersion` returns the Sikuli version string.

*	New computer vision engine - faster and more reliable

*	Better capture mode on Mac (supports multi-screens, no flicker anymore)

*	Text recognition and matching 
	
	*	``find("OK")`` returns all regions with a "OK" label 
		(see :ref:`Finding a Pattern (Image or Text) <FindinginsideaRegionandWaitingforaVisualEvent>`)

	* new :py:meth:`Region.text` returns the text in the region 
	
	*

*	Screenshot Naming in the IDE:
	
	*	screenshots can be automatically named
		
		*	with timestamps
		
		*	with part of the text found in them

		*	manually at time of capture

	* and renamed every time using the preview pane
	
	*

*	Remote Images are supported
		e.g. ``click("http://sikuli.org/example/ok_button.png")``

*	There is an Image Search Path - images can be stored wherever you like, even in the web 
		(see :ref:`Image Search Path <ImageSearchPath>`)

*	Scripts can be imported from .sikuli sources as a module (Python style). 
		RC2: jar-files containing java classes and/or Python modules can be loaded at runtime.
			(see :ref:`Importing Sikuli Scripts <ImportingSikuliScripts>`)

*	New App Class replaces the old openApp, switchApp, closeApp functions 
	(see Class :py:class:`App`)	

	*	:py:meth:`App.open`, :py:meth:`App.close`, :py:meth:`App.focus`

	*	:py:meth:`App.window` returns the bound of the app window as a Region, so
		you can restrict following actions within that region. (Windows and Mac only)
		
	*

*	Beautified Run in Slow Motion mode together with some additional options available now through class :py:class:`Settings`
			(see :ref:`Controlling Sikuli Scripts <ControllingSikuliScriptsandtheirBehavior>`).

*	Smooth mouse movement 
	(see :ref:`Controlling Sikuli Scripts <ControllingSikuliScriptsandtheirBehavior>`)

*	More Special Keys are supported (PrintScreen, NumPad, CapsLock...) (see Class :py:class:`Key`)
		RC2: The status of NumLock, CapsLock and ScrollLock can be requested 
			(see :py:meth:`Env.isLockOn`).

*	New Region Highlighting: :py:meth:`Region.highlight` (Windows	and Mac only)

* RC2: Additional helper methods to relocate or change region objects and to get their corners (e.g. :py:meth:`reg.moveTo() <Region.moveTo>` or :py:meth:`reg.getTopLeft() <Region.getTopLeft>`). Other new API's like :py:meth:`reg.getRegionFromPSRM() <Region.getRegionPSRM>` might be helpful when developing complex scripts and packages like :ref:`Extensions <sikuliextensions>`. 
	
* RC2: The :ref:`observer feature <ObservingVisualEventsinaRegion>` is completely revised. A class :py:class:`SikuliEvent` is added, containing environmental information about the observed event.
	
* RC2: A new FindFailed handling option is added, that might come up with a prompt at runtime
	(see :ref:`ExceptionFindFailed`)

*	Mouse Wheel supported: :py:meth:`wheel(target, WHEEL_UP | WHEEL_DOWN, steps)
	<Region.wheel>` for scrolling the mouse wheel
