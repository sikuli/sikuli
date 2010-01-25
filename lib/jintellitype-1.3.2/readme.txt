JIntellitype Read Me
http://www.melloware.com/

Copyright 1999-2008 Emil A. Lefkof III

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contents
--------
1. Overview
2. Features
3. Installation
4. Quick Usage
5. Acknowledgements
6. Feedback

1. Overview
   --------
   JIntellitype is an API for interacting with Microsoft Intellitype keyboard
   commands as well as registering for Global Hotkeys in your application.  
   The API is a Java JNI library that uses a DLL to do all the communication
   with Windows.   This library ONLY works on Windows.
   
   Have you ever wanted to have CTRL+SHIFT+G maximize your Swing application 
   on the desktop even if that application did not have focus?  Now you can by
   registering a Windows Hotkey combination your application will be alerted 
   when the combination you select is pressed anywhere in Windows.
   
   Have you ever wanted to react to those special Play, Pause, Stop keys on some
   Microsoft and Logitech keyboards?  Even some laptops now have those special
   keys built in and if you want your application to "listen" for them, now you
   can! 
   
	
2. Features
   --------
   -> Can register global hotkey combinations in Windows 
   -> Application is notified even if it does not have focus.
   -> Can react to those Play, Pause, Stop, Next, Forward Media keys like Winamp
   -> Very little code, easy to use API
   -> Examples included in JIntellitypeTester.java

3. Installation
   ------------
   
   FOR USERS:
   -> Copy the following files into your classpath 
        -> jintellitype.jar
        -> JIntellitype.dll (or put in Windows/System32)
        
   FOR DEVELOPERS:
   -> To build you need Maven 2.0.7 or higher installed from Apache.  Just run "mvn package" from the
      directory where the pom.xml is located to build the project.

   -> To build the C++ code you need Bloodshed C++ IDE.  When you load the .dev project included do not
      forget to edit Project->Options and under Directories Tab change the Includes directory to contain 
	  both:
	      /java5/include
		  /java5/include/win32
		  
      Where "java5" is the location of your Java JDK.
	  

4. Quick Usage
   ------------

// Create JIntellitype
	...
        JIntellitype.getInstance().addHotKeyListener(new HotKeyListener() {...);
        JIntellitype.getInstance().addIntellitypeListener(new IntellitypeListener() {...);
	...

// Assign global hotkeys to Windows+A and ALT+SHIFT+B
        JIntellitype.getInstance().registerHotKey(1, JIntellitype.MOD_WIN, (int)'A');
        JIntellitype.getInstance().registerHotKey(2, JIntellitype.MOD_ALT + JIntellitype.MOD_SHIFT, (int)'B');
        
// listen for hotkey
        public void onHotKey(int aIdentifier) {
           if (aIdentifier == 1)
             System.out.println("WINDOWS+A hotkey pressed");
           }
        }
        
// listen for intellitype play/pause command
        public void onIntellitype(int aCommand) {
           switch (aCommand) {
		case JIntellitype.APPCOMMAND_MEDIA_PLAY_PAUSE:
			System.out.println("Play/Pause message received " + Integer.toString(aCommand));
			break;
		default:
			System.out.println("Undefined INTELLITYPE message caught " + Integer.toString(aCommand));
			break;
		}
        }

// Termination
	...
	JIntellitype.getInstance().cleanUp();
	System.exit(0);



See demo at test/com/melloware/jintellitype/JIntellitypeTester.java for more info..

5. Acknowledgements
   ----------------
   JIntellitype is distributed with a small number of libraries on which it depends.
   Those libraries are:
   
   -> Jakarta Commons Logging (http://jakarta.apache.org/commons/logging/)
      Commons logging is used to provide Stripes with a logging mechanism that
      does not tie it to a specific logging implementation. In reality, most
      users will probably be using Log4J, and so will need to configure commons
      logging to point at Log4J.  A sample configuration file is included in
      the example application at: examples/src/commons-logging.properties
      Commons Logging is licensed under the Apache License Version 2.0, a copy
      of which is included in lib/commons-logging.license
      
      
6. Feedback
   --------
   Your feedback on JIntellitype (hopefully constructive) is always welcome.  Please
   visit http://www.melloware.com/ for links to browse and join mailing
   lists, file bugs and submit feature requests.  
   
   Also a forum is set up at http://forum.melloware.com/index.php for discussion.
