
How to use Sikuli Script in your JAVA programs
==============================================

The core of Sikuli Script is written in Java, which means you can use Sikuli Script as a standard JAVA library in your program. This document lets you know how to do that. 

After having installed Sikuli on your system, as recommended on the `download page <http://sikuli.org/download.shtml>`_, you have to do the following:

1. Get sikuli-script.jar from your Sikuli IDE installation path.
----------------------------------------------------------------
Sikuli Script is packed in a JAR file - sikuli-script.jar. Depending on the operating system you use, you can find the sikuli-script.jar in according places.

 * Windows, Linux: Sikuli-IDE/sikuli-script.jar
 * Mac OS X: Sikuli-IDE.app/Contents/Resources/Java/sikuli-script.jar

2. Make the native libraries available
--------------------------------------
Sikuli Script uses OpenCV as its computer vision engine and, depending on the system environment, needs some other native libraries. These libraries are packaged with the distribution (except Linux). 
 * **Windows:** follow the instructions at `FAQ Using sikuli-script.jar standalone ... <https://answers.launchpad.net/sikuli/+faq/1436>`_.
 * **Mac OS X:** follow the instructions at `FAQ Using sikuli-script.jar standalone ... <https://answers.launchpad.net/sikuli/+faq/1436>`_.
 * **Linux:** no additional requirements, when Sikuli-IDE works as expected.

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
Be aware, that some method signatures differ from the Sikuli Script level.
 * `Javadoc of Sikuli Script <http://sikuli.org/doc/java-x/>`_.
 * :doc:`/sikuli-script-index`.

