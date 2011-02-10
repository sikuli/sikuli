How to use Sikuli together with other IDE's
===========================================

.. versionadded:: X1.0-rc2

**Draft - to be completed**

In this section we want to talk about requirements and todos, when you want to develop Sikuli scripts in other IDE's than the one that comes with the distribution. You should red further, if you want to develop at least a part of your package using Python language. If you "only" want to develop in Java using the Java level API of Sikuli, the you should look here: .

Very detailed backgrund information and helpful examples can be found in the **Jython e-book** `Chapter 11: Using Jython in a IDE <http://jythonpodcast.hostjava.net/jythonbook/en/1.0/JythonIDE.html#chapter-11-using-jython-in-an-ide>`_. So we will concentrate on the key points here. 

These and more might be your reasons:

* you want to stick with your favorite IDE

* you want more features, than Sikuli-IDE currently has

* you want to develop packages/applications combining Sikuli script and Java or even other languages

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

Based on Netbeans 6.9 and supposing you have it installed and running in the base version that allows to at least develop Java applications. If not stated explicitely, it is valid for Windows, Mac and Linux.

Since this is not a tutorial how to use NetBeans in general, you should be familiar with the basic concepts (projects, preferences, editor, build and run, ...).

Since the top level language used by Sikuli script is Python, you need the Python plugin that comes bundled with Jython 2.5.1.

NetBeans is not strict in naming the Plugin: though they mainly talk about Python, Jython is meant as well.

Go to menu **Tools** -> **Plugins** -> **Available Plugins**

In the list select everything that is related to Python and install. After finishing, it is a good idea to restart NetBeans and check again.
   

Eclipse
-------


Developement parallel with Sikuli-IDE
-------------------------------------