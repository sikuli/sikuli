How to run Sikuli from Command Line
===================================

Sikuli IDE can be used on command line to run a Sikuli script or a Sikuli test case. 

The usage on each platform:

.. windows::

   :command:`PATH-TO-SIKULI/sikuli-ide.exe [options]` or 

   :command:`PATH-TO-SIKULI/sikuli-ide.bat [options]`

.. mac::

   :command:`/Applications/Sikuli-IDE.app/sikuli-ide.sh [options]`

.. linux::

   :command:`PATH-TO-SIKULI/sikuli-ide.sh [options]`

Command Line Options
--------------------

:program:`Sikuli-IDE`

.. option:: --args <arguments>          

   specify the arguments passed to Jython's sys.argv

.. option::  -h,--help                      

   print this help message

   ::

      usage: 
      Sikuli-IDE [--args <arguments>] [-h] [-r <sikuli-file>] [-s] [-t <sikuli-test-case>]
       --args <arguments>             specify the arguments passed to Jython's sys.argv
       -h,--help                      print this help message
       -r,--run <sikuli-file>         run .sikuli or .skl file
       -s,--stderr                    print runtime errors to stderr instead of popping up a message box
       -t,--test <sikuli-test-case>   run .sikuli as a unit test case with junit's text UI runner

.. option::  -r,--run <sikuli-file>         

   run .sikuli or .skl file
   
.. option::  -s,--stderr                    

   print runtime errors to stderr instead of popping up a message box

.. option::  -t,--test <sikuli-test-case>   

   run .sikuli as a unit test case with junit's text UI runner


               
Example - Run xxxx.sikuli with three arguments: 
------------------------------------------------

.. windows::

   :command:`PATH-TO-SIKULI/sikuli-ide.exe -r xxxx.sikuli ---args a1 a2 a3`

.. linux::

   :command:`PATH-TO-SIKULI/sikuli-ide.sh -r xxxx.sikuli ---args a1 a2 a3`

.. mac::

   :command:`/Applications/Sikuli-IDE.app/sikuli-ide.sh -r xxxx.sikuli ---args a1 a2 a3`
   
Mac: using open to run a script
-------------------------------

On Mac there is an alternative way to run a .skl file using open. 

.. mac::

   :command:`open /Applications/Sikuli-IDE.app ---args ABSOLUTE-PATH-TO-A-SKL`

With "open -g", you even can run a sikuli script without bringing Sikuli-IDE to the foreground. So the focus remains on the current window when executing a sikuli script.

.. mac::

   :command:`open -g /Applications/Sikuli-IDE.app ---args ABSOLUTE-PATH-TO-A-SKL`
   
.. _runsikuliscript:

Running sikuli-script.jar from Command line
---------------------------------------------

.. versionadded:: X1.0-rc2

Interested in the :ref:`Option -i (interactive Sikuli) <sikuliscriptinteractive>`?

If you follow these standards, you can do the following out of the box:

* **Windows:** install Sikuli X using the installer (the system %PATH% will be set as needed)
	There is a new environment variable **%SIKULI_HOME%** that is expected to contain the directory, where Sikuli X is installed. You have to set it, if you have Sikuli X in a different place.
		
	**Be aware:** using the zipped version, you have to take care for %PATH% and %SIKULI_HOME% yourself.

* **Mac:** have Sikuli-IDE.app in its standard place /Applications. There is generally no need to put it elsewhere.

* **Linux:** you generally have to take care for the environment yourself.

You might want to add more java options. They are left out here to concentrate on the point.

**NOTE:** In the commands below, some environment variables are only defined, to keep the lines short. The term ``path-to-your-script`` has to be specified according to your situation.

On **Linux** it should be principally like on Mac, with your specific adjustments.

**Run a script without IDE**

.. windows::

	:command:`java -jar %SIKULI_HOME%\\sikuli-script.jar path-to-your-script\\yourScript.sikuli`
	
.. mac::
	
	:command:`SIKULI_HOME=/Applications/Sikuli-IDE.app/Contents/Resources/Java`
	
	:command:`java -jar $SIKULI_HOME/sikuli-script.jar path-to-your-script/yourScript.sikuli`
	   
**Run a script from Command line using the Sikuli contained Jython**

This option might be helpful in some cases, where you want to have access to the Jython layer, before any Sikuli feature is touched. Be aware, that a ``from sikuli.Sikuli import *`` is needed to have access to Sikuli. You might have to take care somehow, that images are found, since bundle path is not set.

.. windows::

	:command:`set SCRIPT=path-to-your-script\\yourScript.sikuli\\yourScript.py`

	:command:`java -cp %SIKULI_HOME%\\sikuli-script.jar org.python.util.jython %SCRIPT%`
	
.. mac::
	
	:command:`SIKULI_HOME=/Applications/Sikuli-IDE.app/Contents/Resources/Java`
	
	:command:`java -cp $SIKULI_HOME/sikuli-script.jar org.python.util.jython path-to-your-script/yourScript.sikuli/yourScript.py`

.. _sikuliscriptinteractive:
	
Interactive Sikuli Jython Session from Command Line (Option -i)
---------------------------------------------------------------
	   
:program:`sikuli-script.jar`

.. option:: -i

	Start an interactive Jython session with the Sikuli environment already in place.
	
This might be helpful to do some testing on commandline, without having to start the Sikuli IDE. A specified script and other options will be ignored.

.. windows::

	:command:`java -jar %SIKULI_HOME%\\sikuli-script.jar -i`
	
.. mac::
	
	:command:`SIKULI_HOME=/Applications/Sikuli-IDE.app/Contents/Resources/Java`
	
	:command:`java -jar $SIKULI_HOME/sikuli-script.jar -i`
         
This is a Mac sample session::

	....$ java -jar /Applications/Sikuli-IDE.app/Contents/Resources/Java/sikuli-script.jar -i
	[info] Sikuli vision engine loaded.
	[info] Mac OS X utilities loaded.
	[info] VDictProxy loaded.
	>>> Settings.ActionLogs=False
	>>> Settings.InfoLogs=False  
	>>> Settings.DebugLogs=False
	>>> img = capture()
	>>> click(img)     
	1
	>>> ret = click(img)
	>>> Settings.ActionLogs=True 
	>>> click(img)              
	[log] CLICK on (1834,432)
	1
	>>> exit()



