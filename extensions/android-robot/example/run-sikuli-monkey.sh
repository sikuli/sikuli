#!/bin/sh
SIKULI_SCRIPT_JAR=sikuli-script.jar
ANDROID_ROBOT_JAR=android-robot-0.3.jar
ANDROID_SDK=/opt/android-sdk-mac_x86

CLASSPATH=$SIKULI_SCRIPT_JAR:$ANDROID_ROBOT_JAR

java -cp $CLASSPATH \
     -Dandroid.path=$ANDROID_SDK \
     org.sikuli.script.SikuliScript $*
