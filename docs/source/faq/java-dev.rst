
How to use Sikuli Script in your JAVA programs
==============================================

The core of Sikuli Script is written in Java, which means you can use Sikuli Script as a standard JAVA library in your program. This document lets you know how to do that.

1. Get sikuli-script.jar from your Sikuli IDE installation path.
----------------------------------------------------------------
Sikuli Script is packed in a JAR file - sikuli-script.jar. Depending on the operating system you use, you can find the sikuli-script.jar in according places.

 * Windows, Linux: Sikuli-IDE/sikuli-script.jar
 * Mac OS X: Sikuli-IDE.app/Contents/Resources/Java/sikuli-script.jar

2. Install OpenCV.
------------------
Sikuli Script uses OpenCV as its computer vision engine. On Windows, you can simply add the path Sikuli-IDE/tmplib to the environment variable PATH, and then Windows can automatically find OpenCV's DLLs in there. However, it's more complicated on Mac and Linux. Therefore, our suggestion is to install OpenCV by yourself.

 * Mac OS X: you can install OpenCV using `Darwin Ports <http://darwinports.com/>`_. Type "port install opencv" in Terminal.app once you have installed Darwin Ports.
 * Linux: OpenCV is often packed as a series of packages. For example, libcv4, libcvaux4, libhighgui4 on Ubuntu Linux.

3. Include sikuli-script.jar in the CLASSPATH of your Java project.
------------------------------------------------------------------- 

We use Eclipse as an example. After adding sikuli-script.jar as a library into your project, the project hierarchy should look like this.

.. image:: test-sikuli-project.png

4. Import the Sikuli classes you need
-------------------------------------

You can simply "import org.sikuli.script.*" or import the classes you need. In most cases, you would need at least :py:class:`Region` or :py:class:`Screen`.

.. versionchanged:: X-1.0
   In the version 0.9.x and 0.10.x, the package name was edu.mit.csail.uid.


5. Write code!
--------------

Here is a hello world example on Mac. 
The program clicks on the spotlight icon on the screen, waits spotlight's input window appears, and then types "hello world" and hits ENTER.

.. code-block:: java

	import org.sikuli.script.*;
	
	public class TestSikuli {
	
	        public static void main(String[] args) {
	                Screen s = new Screen();
	                try{
	                        s.click("imgs/spotlight.png", 0);
	                        s.wait("imgs/spotlight-input.png");
	                        s.type(null, "hello world\n", 0);
	                }
	                catch(FindFailed e){
	                        e.printStackTrace();                    
	                }
	
	        }
	
	}


See also
--------
 * `Javadoc of Sikuli Script <http://sikuli.org/doc/java-x/>`_.
 * :doc:`/sikuli-script-index`.

