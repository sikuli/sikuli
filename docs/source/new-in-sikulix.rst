What is new in Sikuli X
=======================

Sikuli X is a new experimental branch of Sikuli. (X stands for eXperimental.)
For all current users of Sikuli 0.9 or 0.10 we recommend to upgrade to X.

However, please keep in mind some new features are still experimental, e.g. text
recognition and the new API to get the bound of any windows, which means they may
not work well or not support all platforms yet.

*	New computer vision engine - faster and more reliable

*	Better capture mode on Mac (supports multi-screens, no flicker anymore)

*	Text recognition and matching 
	
	*	``find("OK")`` returns all regions with a "OK" label 
		(see :ref:`Finding a Pattern (Image or Text) <FindinginsideaRegionandWaitingforaVisualEvent>`)

	* new :py:meth:`Region.text` returns the text in the region 

*	Screenshot Naming in the IDE:
	
	*	screenshots can be automatically named
		
		*	with timestamps
		
		*	with part of the text found in them

		*	manually at time of capture

	* 	and renamed every time using the preview pane

*	Remote Images are supported

	*	e.g. ``click("http://sikuli.org/example/ok_button.png")``

*	There is an Image Search Path - images can be stored wherever you like 
	(see :ref:`Image Search Path <ImageSearchPath>`)

*	Scripts can be imported from .sikuli sources as a module (Python style) 
	(see :ref:`Importing Sikuli Scripts <ImportingSikuliScripts>`)

*	New App Class replaces the old openApp, switchApp, closeApp functions 
	(see Class :py:class:`App`)
	
	*	:py:meth:`App.open`, :py:meth:`App.close`, :py:meth:`App.focus`
	
	*	:py:meth:`App.window` returns the bound of the app window as a Region, so
		you can restrict following actions within that region. (Windows and Mac
		only)

*	Beautified Run in Slow Motion mode 
	(see :ref:`Controlling Sikuli Scripts <ControllingSikuliScriptsandtheirBehavior>`)

*	Smooth mouse movement 
	(see :ref:`Controlling Sikuli Scripts <ControllingSikuliScriptsandtheirBehavior>`)

*	More Special Keys are supported (PrintScreen, NumPad, CapsLock...) (see Class :py:class:`Key`)

*	New Region Highlighting: :py:meth:`Region.highlight` (Windows	and Mac only)

*	Mouse Wheel supported: :py:meth:`wheel(target, WHEEL_UP | WHEEL_DOWN, steps)
	<Region.wheel>` for scrolling the mouse wheel

