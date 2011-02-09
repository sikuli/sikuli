General Information About Sikuli Extensions
===========================================

.. _sikuliextensions:

.. versionadded:: X1.0-rc2

Extensions allow to implement new Sikuli features by adding packages to your current Sikuli installation. They are maintained at http://depot.sikuli.org/ by the developers of Sikuli (see **Technical Details** below). If you want to contribute a new extension or a modified exisiting one, please look at **How to contribute an extension** below.

How to Download and use an Extension
------------------------------------

The download of an extension is supported by the IDE through the menu 
:menuselection:`Tools -> Extensions`.
You get a popup, that lists the available and already installed extensions and allows to download new packages or updates for installed ones.

This popup shows a new **package not yet installed**:

.. image:: extension-new.png

If you need more information about the features of the extension, just click :guilabel:`More Info` - this will open the related documentation from the web in a browser window.

If you want to install the extension, just click the :guilabel:`Install...` button. The package will be downloaded and added to your extensions repository. 

This popup shows an **installed package**:

.. image:: extension-installed.png

If a new version would be available at that time, the :guilabel:`Install...` button would be active again, showing the new version number. Now you could click and download the new version.

**How to Use an Extension**

To use the features of an installed extension in one of your scripts, just say ``from extension-name import *``. For an usage example read :ref:`Sikuli Guide <sikuliguide>`.

For information about features, usage and API use menu :menuselection:`Tools -> Extensions -> More Info` in the IDE.


Technical Details
-----------------

Extensions are Java JAR files containing some Java classes (usually the core functions) and/or Python modules, which define the API to be used in a script.

Sikuli maintains a local extensions directory, where downloaded extensions are stored together with a hidden list of the installed extensions (Windows: ``%APPDATA%\Sikuli\extensions``, Mac: ``~/Library/Application Support/Sikuli/extensions``, Linux: ``~/.sikuli/extensions``).

Once an extension is imported using ``import extension-name``,
Sikuli automatically searches and loads the JAR file of that extension
into the current context with :py:func:`load(path-to-jar-file) <load>`.


How to develop an extension
---------------------------

The **source structure** of an extension named ``extension-name`` looks like this: ::

	Java
	- org/com
	-- your-organization-or-company
	--- extension-name
	---- yourClass1.java
	---- yourClass2.java
	---- .... more classes
	python
	- extension-name
	-- __init__.py
	-- extension-name.py
	
The **final structure of a JAR** (filename ``extension-name-X.Y`` where X.Y is the version string) looks like this: ::
	
	org/com
	- your-organization-or-company
	-- extension-name
	--- yourClass1.class
	--- yourClass2.class
	--- .... more classes
	extension-name
	- __init__.py
	- extension-name.py
	META-INF
	- MANIFEST.MF

The file ``__init__.py`` contains at least ``from extension-name import *`` to avoid one qualification level. So in a script you might either use::

	import extension-name
	extension-name.functionXYZ()
	
or::

	from extension-name import *
	functionXYZ()
	
The second case requires more investement in a naming convention, that avoids naming conflicts.

The file ``extension-name.py`` contains the classes and methods, that represent the API, that one might use in a Sikuli script. 

As an example you may take the source of the extension Sikuli Guide.

Name your extensions properly
-----------------------------

Sikuli extensions can be Python/Jython modules or Java classes.

For Java classes, following the reverse URL convention of Java is a good idea (for example, org.foo.your-extension). However, **DO NOT use Java's convention for Python/Jython modules**. You need to come up with a unique extension name that does not conflict with existing Python modules and other Sikuli extensions.

Please read `Naming Python Modules and Packages <http://jythonpodcast.hostjava.net/jythonbook/en/1.0/ModulesPackages.html#naming-python-modules-and-packages>`_ to learn the details for naming a Python module.


How to test your extension
--------------------------

While developing your extensions, you can put the JAR file in Sikuli's 
extension directory or in the same .sikuli folder as your test script.
The JAR file should not have a version number in its file name, 
e.g. ``extension-name.jar``.
Because Sikuli searches extensions the .sikuli folder first, and then
the Sikuli extension folder, it is usually a good idea to put your
developing extensions in the .sikuli folder.

The option that always works is to use the :py:func:`<load>` function with an absolute path to your ``extension-name.jar``, which is the first in the row. 
If load() succeed, it returns ``True`` and puts 
``absolute-path-to-your-extension-name.jar`` into ``sys.path``, 
so you can use ``import extension-name`` afterwards.

How to contribute your extension
--------------------------------

Currently you have to contact the developers of Sikuli 
and agree on how to proceed.
