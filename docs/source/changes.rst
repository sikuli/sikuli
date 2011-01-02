Changes in Sikuli
=================

X 1.0 rc1 (2010-12-23)
----------------------

* New computer vision engine - faster and more reliable.
* Text recognition and matching (EXPERIMENTAL)
   * find("OK") returns all regions with a "OK" label.
   * region.text() returns the text in the region.
* Screenshot naming - screenshots can be named automatically with timestamps or the text in them as well as manually with prompted filenames.
* Supports remote images e.g. click("http://sikuli.org/docx/_static/ide-icon-64.png")
* Image search path (SIKULI_IMAGE_PATH) - images can be stored anywhere you like.
* .sikuli source can be imported as a module - contained images are found automatically
* New App class replaces the old openApp, switchApp, closeApp functions
   * App.open(), App.close(), App.focus()
   * App.window() returns the bound of the app window as a Region, so you can restrict following actions within that region. (EXPERIMENTAL: Windows and Mac only)
* Beautified "Run in slow motion" mode.
* Smooth mouse movement.
* Better capture mode on Mac (supports multi-screens, no flicker anymore)
* More special keys are supported (PrintScreen, Num Pad, CapsLock...)
* New region highlighting API: region.highlight(). (EXPERIMENTAL: Windows and Mac only)
* New Mouse API: wheel(target, WHEEL_UP/WHEEL_DOWN, steps) for scrolling the mouse wheel.
* New multi-lingual interface translation: Bulgarian, French, Dutch, Polish, Japanese, Simplified Chinese.

Special Notes
^^^^^^^^^^^^^

* Text recognition and matching is implemented with the Tesseract OCR engine, which was
  originally designed for recognizing scanned documents. Please note the OCR technology
  is not perfect, especially for screen text. We believe there is still much space for tuning its
  performance and we are working hard to improve it. Please look forward to it.

* The observer is unstable in this version. We will fix it in the next release.

* If you use Sikuli in your Java programs, please note the java package name of Sikuli has been changed from "edu.mit.csail.uid" to "org.sikuli.script". You need to replace the package name in all import statements.

Bug Fixes
^^^^^^^^^

* #594529 Screenshot hotkey uses first char of key description instead of key
* #599955 openApp on Linux foregrounds apps by default
* #606405 windows multi monitor: screen(0) needs (0,0) as upper left
* #591759 mouse wheel support
* #644982 [request] implement an Application class
* #583085 findAll on a region returns duplicate matches         
* #584471 parser error: 'with' will become a reserved keyword in Python 2.6               
* #595283 Sikuli IDE "Save" Operation Overwrites Existing Files Without Warning           
* #597525 exported executable scripts not exiting after running           
* #604514 Observe() not working on Windows                
* #608736 Num keys not accesible          
* #612434 find doesn't return the best match              
* #620598 from __future__ fails in unit test script               
* #625068 Temp files not cleared up               
* #627986 [request] support Python import for reusable scripts            
* #630412 Check Update misleadingly suggest I update to an earlier release                
* #667561 ShowStopper : "PrintScreen" hotkey unable to press using Key.PrintScreen function               
* #676051 [request IDE] Enhanced Support: rename an image or load an external image               
* #681065 [feature request] control over how rapidly / when the screen is sampled         
* #684815 Delay must be 0 to 60,000ms             
* #687559 --args not working with Sikuli JUnit tests?             
* #693570 Save As: Overwrite existing .sikuli corrupts existing Image             

0.10.2 (2010-08-07)
-------------------

* add an option "-test xxx.sikuli" to run a .sikuli as a test case.
* add an option "-s" to output runtime errors to stderr instead of popping up a message box.
* Sikuli-IDE.bat uses 32bit JRE automatically on Windows x64 (Thanks Brian Creation's patch)
* fixed inline images overlapping on code
* fixed unsatisfied linking error (Sikuli-IDE.exe) on Windows x64
* the path to Sikuli scripts can have non-roman characters on Mac and Linux now (Thanks for niknah's idea)
* fixed a critical memory leak issue (Thanks for niknah's patch)
* fixed png pattern parsing failure (Thanks Timothy Fridey's patch)
* fixed Korean IDE i18n resources
* fixed the default width of the unit test panel, the default height of the console panel

New Command Line Usage
^^^^^^^^^^^^^^^^^^^^^^

 Sikuli-IDE [--args <arguments>] [-h] [-r <sikuli-file>] [-s] [-t <sikuli-test-case>]
  | --args <arguments> specify the arguments passed to Jython's sys.argv
  | -h,--help print this help message
  | -r, --run <sikuli-file> run .sikuli or .skl file
  | -s,--stderr print runtime errors to stderr instead of popping up a message box
  | -t,--test <sikuli-test-case> run .sikuli as a unit test case with junit's text UI runner

Bug Fixes
^^^^^^^^^

*  #586699 Solution: 32bit and 64bit issues + java console output window
*  #595741 image button displayed on top of code WinXP
*  #514616 run unit testing scripts from command line
*  #512476 "the path to sikuli scripts can't contain non-roman characters" on Mac OS X.
*  #610122 Failing silently when sikuli-ide.sh cannot find script, or if script ends with "/"
*  #597853 A sikuli script's exit code doesn't correspond with script success
*  #599528 delay before capture setting not honored by auto capture
*  #575585 Memory leak crashes scripts running or waiting long
*  #526818 IDE does not display images with capitalized file extensions -- ".PNG"
*  #594959 Images dissapear in IDE when adding to dictonary
*  #599484 dragDrop steals left mouse button permanently

0.10.1 (2010-05-23)
-------------------

* Support more languages: Brazilian Portuguese, Danish, Korean, Russian, Spanish, Turkish, and Ukrainian.
* Auto-update checking can be disabled in the Preferences window.
* Add an Edit menu, including Cut, Copy, Paste, Select All, Indent, Un-Indent.
* Fixed several critical memory leaks that cause crashes on Windows.
* Reduce default memory consumption.
* Default hot-keys are changed on Windows.
  * Switching tabs: CTRL-TAB, CTRL-SHIFT-TAB
  * Screen capturing: CTRL-SHIFT-2
* Add command line options to Sikuli IDE.

  Usage: Sikuli-IDE [--args <arguments>] [-h] [-r <sikuli-file>]
   | --args <arguments> specify the arguments passed to Jython's sys.argv
   | -h,--help print this help message
   | -r,--run <sikuli-file> run .sikuli or .skl file

Bug Fixes
^^^^^^^^^

* #581712 Unit testing in Sikuli 0.10 doesn't work. (Unit testing panel is now working.)
* #577610 capture(region) gives (partly) black images
* #577220 exit() Function gives Runtime Error from Command Line
* #575585 Memory leak in Sikuli 0.10 crashes long scripts
* #562393 Win XP: CTRL+ARROW shortcut is double mapped
* #574951 I got JNI Exception: failed to create the Java VM
* #583096 Data loss when running Sikuli in Windows
* #570248 Screenshots in the script are not longer found after saving with "save as..."
* #580000 Sikuli-IDE crashing when clicking on PS which is no loger visible for find()
* fixed parsing error if using non-digits in Pattern.similar or Pattern.targetOffset.
* Sikuli 0.10 functions and classes can be imported and used in Jython modules other than only in main scripts. See libo's post https://answers.launchpad.net/sikuli/+question/111193 for the details.



0.10 (2010-05-03)
-----------------

Sikuli 0.10 is a big milestone. The core API of Sikuli Script has been completely redesigned and rewritten to support more flexible uses. Sikuli 0.10 is also faster, more robust, and more universal - internationalized interfaces and 64bit platforms are supported. Low-level keyboard and mouse actions, and multi-screen environments are supported for advanced users. We even provide a new programming model - visual event driven programming in this version. Last, of course many bugs in 0.9 are also fixed in this version.

We have heard lots of suggestions and feature requests from you guys. Sikuli 0.10 have many new features and improvements (and also bug fixes). Here are some items you may be interested to know.

* MUCH FASTER - The matching algorithm is improved. Screen shots are directly processed in memory, so no more temporary files.
* The whole architecture of Sikuli Script has been redesigned and rewritten. New APIs are clearer, more consistent, and more flexible.
* Visual event driven programming - actions can be executed when something appears, disappears, or changes.
* Search and actions can be easily restricted within a region or a screen.
* Internationalized user interfaces of Sikuli IDE: Traditional Chinese and German interfaces are shipped with 0.10. More languages are being translated.
* Sikuli IDE lists all common commands and their usage aside - No need to look up command manuals anymore.
* A Finder class is opened for you to search visual patterns in any images.
* Low level keyboard and mouse actions are supported.
* Multi-screen environments are supported.
* Linux 64bit is supported - real 64bit binary.
* Windows 64bit runs Sikuli 0.10 with a 32bit Java RE.
* Better Windows support: .SKL can be run directly by a double-click.
* and lots of bugs are fixed!

A great reference document The Complete Guide to Sikuli Script (http://sikuli.org/guide) is released along with Sikuli 0.10. This document is mainly written by an expert Sikuli user, Raimund Hocke. Thanks for his great contribution so that we can have a comprehensive manual for Sikuli 0.10. At last, we thank all contributors who reported bugs, suggested new features or new design, provided ideas, shared your cool Sikuli scripts to us, or supported us in any ways. Sikuli becomes better and better because of your support and contribution.


0.9.9 (2010-02-23)
------------------

General Notes
^^^^^^^^^^^^^

* Sikuli IDE saves all scripts in UTF-8 since this version. In addtion, paste() also supports unicode strings now, so international characters should be able to "paste" into any applications.
* If a image pattern can not be found, find() and all actions that implicitly use find() throw a FindFailed exception by default. This exception can be handled by the try-except statement of Python if needed.
* New API: run(command) - runs a string command and returns its output. This would be useful for running command line programs.
* The vision engine is recompiled with a optimization flag (O2), so it will be faster a little bit.
* The key modifier for click is fixed. Now you can do Ctrl+Alt+Shift+click.

Platform Specific Fixes
^^^^^^^^^^^^^^^^^^^^^^^

Mac OS X

* Uses Mac native file dialogs for open/save/export.

Windows

* Fixed Bug #515914 (IDE crashes if VDict takes capture() as a key)
* Last location of opening files is remembered.

Linux

* Last location of opening files is remembered.

Bug Fixes
^^^^^^^^^

* #515914 IDE crashed when running the note.sikuli sample script
* #518491 type command turns characters in message string
* #516795 UnknownFormatConversionException: Conversion = 'p'
* #523718 setAutoWaitTimeout(0) stops exception handling
* #516375 WinXP: "Save as..." doesn't remember last saving location
* #517113 MacOS - Save dialog is non-standard and defaults to /
* #516233 openApp appears to do nothing in OS X 10.5
* #523660 find() if not found throws exception - has problems
* #519321 class Key on Windows 7
* #525267 type("%") gives UnknownFormatConversionException in IDE
* #519916 click modifiers don't work (OS X 10.6)
* #517243 Syntax error in MacUtil.java

0.9.8 (2010-02-01)
------------------

Sikuli 0.9.8 release added the missing feature, global shortcuts, to the Linux version and also fixed a serious bug that causes memory leak on all platforms. Anti-aliasing for text is enabled for Windows and Linux.

Platform Specific Fixes
^^^^^^^^^^^^^^^^^^^^^^^

Linux

* [NEW] Global shortcuts for taking screenshots and breaking scripts worked!!
* Enabled anti-alias for text rendering

Windows

* Enabled anti-alias for text rendering

Bug Fixes
^^^^^^^^^

* #515592 Feature Request: Turn on font aliasing in Windows version
* #515406 double "\" generated into Sikuli-IDE.bat on Windows 2000
* #511770 running out of memory soon
* Fixed "Unsatisfied link error" on Linux


0.9.7 (2010-01-27)
------------------

* [NEW] Scripts can be exported as executable files (.skl) - double-click on .skl runs it (Mac only now)!
* [NEW] Special keys are supported (see class Key in the Jython API reference.)
    TAB, ESC, F1~F15, UP, DOWN, RIGHT, LEFT, ENTER, BACKSPACE,
    INSERT, DELETE, HOME, END, PAGE_UP, PAGE_DOWN
* [NEW] New API: hover(img) - move mouse cursor to the best matched position of the given image.
* [NEW] New API: paste([img], text) - Paste the given string to the best matched position of the given image. (This's a temporary solution for different keymaps and international characters.)
* Image matching is SPEEDED UP !!
* JVM requirement goes down to Java 5
* A sikuli executable script (.skl) can be run from command line using Sikuli IDE
   * Mac: open /Applications/Sikuli-IDE.app xxx.skl
   * Windows: PATH-TO-SIKULI/sikuli-ide.bat xxx.skl
   * Linux: PATH-TO-SIKULI/sikuli-ide.sh xxx.skl
* Jython API references are updated
* Added HowTo's on the web site

Platform Specific Fixes
^^^^^^^^^^^^^^^^^^^^^^^

Mac OS X Leopard(10.5)

* Updated to the latest version - LOTS of bugs are gone.

Linux

* Supported openApp, switchApp(wmctrl needed), closeApp

Bug Fixes
^^^^^^^^^

* #511748  screenshot shortcut in the capture mode should be disabled
* #511749  the number in Pattern.similar() has wrong decimal separator on French Windows
* #512429  the Basic functions like openApp and switchApp of the api is not functional in linux
* #511771  file extension is needed while saving a file
* #512480  a script can't have '.' in its filename

