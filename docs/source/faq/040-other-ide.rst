How to use Sikuli together with other IDE's
===========================================

.. versionadded:: X1.0-rc2

**Draft - to be completed**

In this section we want to talk about requirements and todos, when you want to develop Sikuli scripts in other IDE's than the one that comes with the distribution. 

These and more might be the reasons:

* you want to stick with your favorite IDE

* you want more features, than Sikuli-IDE currently has

* you want to develop packages/applications combining Sikuli script and Java or even other languages like Ruby

* you want to develop a :ref:`Sikuli Extension <sikuliextensions>`

* you need a debugger for your script/package/application

* you want to integrate with a versioning system 

* you might have more reasons ;-)

General Requirements
--------------------

This is the easiest part: If you are able to run scripts from commandline using sikuli-script.jar (:ref:`as documented here <runsikuliscript>`), then nothing more is needed to start to setup your IDE to support the developement of Sikuli scripts.

These are the key points (Sikuli X-1.0rc2 or later):

* **Windows:** install Sikuli X using the installer (the system %PATH% will be set as needed)
	There is a new environment variable **%SIK_HOME%** that is expected to contain the directory, where Sikuli X is installed. You have to set it, if you have Sikuli X in a different place.
		
	**Be aware:** using the zipped version, you have to take care for %PATH% and %SIK_HOME% yourself.

* **Mac:** have Sikuli-IDE.app in its standard place /Applications. There is generally no need to put it elsewhere.

* **Linux:** you generally have to take care for the environment yourself. 
               
Netbeans  
--------

   
Eclipse
-------


Developement parallel with Sikuli-IDE
-------------------------------------