### VNCRobot version

Connects up to a VNC server instead of tying up the mouse/keyboard.

* Example... (replace 192.168.1.111 with the ip address of the vncserver)

```python
vncscreenid=JScreen.connectVNC(["vncviewer","192.168.1.111"],"password")
vncscreen=Screen(vncscreenid)

# Then everything will have to be accessed via the new "vncscreen" object.

vncscreen.click(Location(1,1))
vncscreen.type("abc")
vncscreen.find("xxx.png")
```

* You can add vncviewer options to the connection....

```python
vncscreenid=JScreen.connectVNC(["vncviewer","-encodings","tight","-quality","5","192.168.1.111"],"password")
```


A new version is available per May 22nd 2013 
=====


named **SikuliX-1.0** consisting of 2 major parts:
 - **SikuliX-IDE** (made for scripting in Python language) [more ...](https://github.com/RaiMan/SikuliX-IDE)
 - **SikuliX-API** (for Java programming and usage with other IDE's) [more ...](https://github.com/RaiMan/SikuliX-API)

More information about **downloadable packages**: [click here ...](http://www.sikuli.org/download.html)

or go to [**Sikuli's Launchpad page**](https://launchpad.net/sikuli)

**a word on the future ...**<br />
Most probably this repo will never come back to life again. So if you are interested in the further developement of Sikuli either as a user or as a possible contributor, you should decide to switch to the above mentioned reepos. 

What is Sikuli?
===============

Sikuli is a visual technology to automate graphical user interfaces (GUI)
using images (screenshots). The current release of Sikuli includes Sikuli
Script, a visual scripting API for Jython, and Sikuli IDE, an integrated
development environment for writing visual scripts with screenshots easily.
Sikuli Script automates anything you see on the screen without
internal API's support. You can programmatically control a web page, a
desktop application running on Windows/Linux/Mac OS X, or even an
iphone application running in an emulator.

More detail information can be found on http://sikuli.org

How To Build Sikuli From Source
===============================

Sikuli is hosted on https://github.com/sikuli/sikuli using the git version
control system (http://git-scm.com/). If you don't have git
installed on you computer, please visit its web site and download its
latest version for your system.

Sikuli's source can be checked out with the following command:

      git clone git://github.com/sikuli/sikuli.git

The instructions to build Sikuli can be found in the build-win32.txt,
build-linux.txt, or build-mac.txt, according to your platform in
the top directory of the source tree.

Found bugs? Have questions?
===========================

If you've seen something wrong, please report bugs at https://bugs.launchpad.net/sikuli.

Have questions? Feel free to ask at https://answers.launchpad.net/sikuli.



Copyright
=========

Sikuli is a research project being developed by Tsung-Hsiang Chang and Tom Yeh
under the supervision of Professor Rob Miller in MIT Computer Science and
Artificial Intelligence Laboratory (CSAIL).

Sikuli is released under the MIT License. The details of the license
is in the copyright.txt.
