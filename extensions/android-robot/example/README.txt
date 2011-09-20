WARNING
=======

This Android extension for Sikuli is still under development.  
Use it at your own risk.


How to run the Android examples?
================================


1. copy/link sikuli-script.jar to this directory.

2. build android-robot, and copy/link android-robot-0.3.jar to this directory.


Run the Java example
--------------------

1. cd java-example 

2. Edit build.xml and change the property ANDROID_SDK to the path to your Android SDK. You may also need to edit "JUnit 4.libraryclasspath".

3. ant build SikuliAndroidTest


Run the Sikuli Script example(Jython)
-------------------------------------

1. Edit run-sikuli-monkey.sh and change ANDROID_SDK to the path to your Android SDK.

2. ./run-sikuli-monkey.sh android-example.sikuli

