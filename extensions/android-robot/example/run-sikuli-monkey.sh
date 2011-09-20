#!/bin/sh
SIKULI_SCRIPT_JAR=sikuli-script.jar
ANDROID_ROBOT_JAR=android-robot-0.2.jar
ANDROID_SDK=/opt/android-sdk-mac_x86

ANDROID_SDK_LIB=$ANDROID_SDK/tools/lib
CLASSPATH=$SIKULI_SCRIPT_JAR:$ANDROID_ROBOT_JAR:$ANDROID_SDK_LIB/monkeyrunner.jar:$ANDROID_SDK_LIB/guavalib.jar:$ANDROID_SDK_LIB/sdklib.jar::$ANDROID_SDK_LIB/ddmlib.jar 

java -cp $CLASSPATH \
     -Dandroid.path=$ANDROID_SDK \
     org.sikuli.script.SikuliScript $*
