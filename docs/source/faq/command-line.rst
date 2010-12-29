How to run Sikuli from Command Line
===================================

Sikuli IDE can be used under command line to run a Sikuli script or a Sikuli test case. It is a little bit different to invoke the Sikuli IDE under command line on each platform.


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

      usage: Sikuli-IDE [--args <arguments>] [-h] [-r <sikuli-file>] [-s] [-t <sikuli-test-case>]
          --args <arguments>          specify the arguments passed to Jython's sys.argv
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

   :command:`PATH-TO-SIKULI/sikuli-ide.exe -r xxxx.sikuli --args a1 a2 a3`

.. linux::

   :command:`PATH-TO-SIKULI/sikuli-ide.sh -r xxxx.sikuli --args a1 a2 a3`

.. mac::

   :command:`/Applications/Sikuli-IDE.app/sikuli-ide.sh -r xxxx.sikuli --args a1 a2 a3`

   On a Mac, there is an alternative way to run a .skl file using open. 

   :command:`open /Applications/Sikuli-IDE.app --args ABSOLUTE-PATH-TO-A-SKL`

   With "open -g", you even can run a sikuli script without bringing Sikuli-IDE to the foreground. That is, the focus can remain on the current window while executing a sikuli script.

   :command:`open -g /Applications/Sikuli-IDE.app --args ABSOLUTE-PATH-TO-A-SKL`



