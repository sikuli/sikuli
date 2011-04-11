How to use Sikuli together with other IDE's
===========================================

.. versionadded:: X1.0-rc2

**Draft - to be completed   ---   NetBeans ready**

In this section we want to talk about requirements and todos, when you want to develop Sikuli scripts in other IDE's than the one that comes with the distribution. You should read further, if you want to develop at least a part of your package using **Python language**. If you "only" want to develop in Java using the Java level API of Sikuli, then you should look here: :ref:`How to use Sikuli Script in your JAVA programs <howtojava>`.

Very detailed background information and helpful examples can be found in the **Jython e-book** `Chapter 11: Using Jython in a IDE <http://jythonpodcast.hostjava.net/jythonbook/en/1.0/JythonIDE.html#chapter-11-using-jython-in-an-ide>`_. So we will concentrate on the key points here.

The following approaches are evaluated and tested on Mac OS X 10.6 and Windows 7 32Bit. Linux users have to find the appropriate setups themselves - but they should be similar or even identical.

These and more might be your reasons:

* you want to stick with your favorite IDE

* you want more features, than Sikuli-IDE currently has

* you want to develop packages/applications combining Sikuli script and Java or even other languages

* you want to develop a :ref:`Sikuli Extension <sikuliextensions>`

* you need a debugger for your script/package/application

* you want to integrate with a versioning system 

* you might have more reasons ;-)

General Requirements and Comments
---------------------------------

**Install Sikuli**

* **Windows:** install Sikuli X using the installer (the system %PATH% will be set as needed)
	There is a new environment variable **%SIKULI_HOME%** that is expected to contain the directory, where Sikuli X is installed. You have to set it, if you have Sikuli X in a different place.
		
	**Be aware:** using the zipped version, you have to take care for %PATH% and %SIKULI_HOME% yourself.

* **Mac:** have Sikuli-IDE.app in its standard place /Applications. There is generally no need to put it elsewhere.

* **Linux:** you generally have to take care for the environment yourself. 
               
**Meanings of shortcuts used**:

* **path-to/sikuli-script.jar** if we use this, it should be replaced by the absolute path to the file sikuli-script.jar, where you installed Sikuli.

**Images and importing other Sikuli scripts**

It is recommended to carefully read through :ref:`Importing other Sikuli Scripts (reuse code and images) <importingsikuliscripts>`, since you need to have a concept how to capture, how to name and where to store your images. As a first start, just decide to use Sikuli IDE in parallel to capture and store your images and use the import feature or the image path to make them available in your script.

Netbeans  
--------

This is based on **Netbeans 6.9**. We suppose you have it running in a setup that allows to at least develop Java applications. 

Since this is not a tutorial how to use NetBeans in general, you should be familiar with the basic concepts (projects, preferences, editor, build and run, ...).

**Install Python plugin**

Since the top level language used by Sikuli script is Python, you need the Python plugin that comes bundled with Jython 2.5.1.

In the Python plugin NetBeans is not strict in naming: though they mainly talk about Python, Jython is meant as well. The Python interpreter to use is selected on the project level.

Go to menu **Tools** -> **Plugins** -> **Available Plugins**

In the list select everything that is in the category Python and install. If suggested by the install process always restart Netbeans.

**Configure for using Sikuli script features at runtime**

The plugin itself has nothing to configure, that is of value for the Sikuli usage. Everything is done on the project level, though some basic preferences act like plugin globals - we just have to know and accept this ;-)

These are the setup steps:

* Start a new project: **File** -> **New Project** -> **Categories: Python** -> **Python Project** -> click **Next**

* In the second step **Name and Location** (last line), select as Python Platform: **Jython 2.5.1**.

* you might want to click **Make Default**, if you have real Python also available (But remember: Sikuli scripts cannot be run with real Python!)

* To integrate Sikuli: click the button **Manage** (only needed with the first project - it is remembered)

* on the tab **Python Path** we need an additional entry, that points to **path-to/sikuli-script.jar/Lib**.

  * Windows: click the button **Add** and click through to *path-to/sikuli-script.jar*. Before clicking **Open**, edit the filename so that it shows sikuli-script.jar\\Lib.

  * Mac: since the Netbeans file dialog does not allow to dive into a bundle like Sikuli-IDE.app, we have to edit the preference file directly. Since it is the same with the Java path, we have documented it seperatly below :ref:`Mac/Netbeans: select library path <nbmacselectlibpath>`.

* on the tab **Java Path** we need an additional entry, that points to **path-to/sikuli-script.jar**. If you have other Java libraries, that you need in your project, add them herer too.

  * Windows: click the button **Add** and click through to *path-to/sikuli-script.jar*. Clicking **Open**.

  * Mac: same again, see below :ref:`Mac/Netbeans: Select library path <nbmacselectlibpath>`.
  
Now you are able to run your first script. Remember, that in every script including the main script, that you are editing now, as the first line you need *from sikuli.Sikuli import **, to have access to the Sikuli features at runtime.

Everytime later on you might come back to the project's preferences with **File** -> **Project Properties (your-project's-name)** . You will find the above information in the categorie Python.

**Prepare to use AutoComplete for the Sikuli methods**

The NetBeans editor in the Python plugin is not able to recognize Python classes and methods, as long as the respective source code is embedded inside a jar-file as it is the fact with Sikuli. If you want AutoComplete to work, you have to extract the folder **Lib/sikuli** from *sikuli-script.jar*, place it  somewhere and add the reference to this folder to the Python Path in the project's preferences (see above: Configure Python Path).

To extract the folder you might use the jar utility, that comes with the Java JDK or any other utility, that allows to unjar a jar-file.

This is a Mac example how to use the jar uility, supposing it can be found on the system path:

* in a Terminal window go to an appropriate folder, that should contain Lib/sikuli afterwards

* run: jar -xf /Applications/Sikuli-IDE.app/Contents/Resources/Java/sikuli-script.jar Lib/sikuli
   
.. _nbmacselectlibpath:

**Mac/NetBeans: Select library path**

To perpare the preference file modification we first add the path to *Sikuli-IDE.app* on both tabs: click the button **Add** and click through to */Applications/Sikuli-IDE.app*. Depending on your selected language, the folder Applications is named like you see it in the Finder. Save the preferences by clicking close and/or ok.

The preference file is located at /Users/your-name/.netbeans/6.9/build.properties. Since it is hidden, you might have to use the Finder option "Go to Folder" (Shift-Command-G), to open the folder /Users/your-name/.netbeans/. 

Open the file build.properties in your favorite editor and search for the text */Applications/Sikuli-IDE.app*. It should be at the end of 2 lines beginning with *pythonplatform.*.

On the line beginning *pythonplatform. ... .javalib=* extend */Applications/Sikuli-IDE.app* so that it reads: /Applications/Sikuli-IDE.app/Contents/Resources/Java/sikuli-script.jar.

On the line beginning *pythonplatform. ... .pythonlib=* extend */Applications/Sikuli-IDE.app* so that it reads: /Applications/Sikuli-IDE.app/Contents/Resources/Java/sikuli-script.jar/Lib.

Save the file and restart Netbeans. It is a good idea to check, that the correct entries are found on the 2 tabs now. 

Eclipse
-------

