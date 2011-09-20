#!/bin/sh
SIKULI_SCRIPT_JAR=sikuli-script.jar
ANDROID_ROBOT_JAR=android-robot-0.2.jar
ANDROID_SDK=/opt/android-sdk-mac_x86

ANDROID_SDK_LIB=$ANDROID_SDK/tools/lib
CLASSPATH=$SIKULI_SCRIPT_JAR:$ANDROID_ROBOT_JAR:$ANDROID_SDK_LIB/monkeyrunner.jar:$ANDROID_SDK_LIB/guavalib.jar:$ANDROID_SDK_LIB/sdklib.jar::$ANDROID_SDK_LIB/ddmlib.jar 

java -cp $CLASSPATH \
     -Dpython.path=$SIKULI_SCRIPT_JAR/Lib \
     -Djava.library.path=$ANDROID_SDK/tools/lib \
     -Dcom.android.monkeyrunner.bindir=$ANDROID_SDK/tools \
     org.sikuli.script.SikuliScript $*
