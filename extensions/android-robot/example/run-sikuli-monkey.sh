#!/bin/sh
SIKULI_SCRIPT_JAR=sikuli-script.jar
ANDROID_ROBOT_JAR=android-robot-0.1.jar
ANDROID_SDK=/opt/android-sdk-mac_x86

java -cp $SIKULI_SCRIPT_JAR:$ANDROID_ROBOT_JAR:$ANDROID_SDK/tools/lib/monkeyrunner.jar -Djava.ext.dirs=$ANDROID_SDK/tools/lib -Djava.library.path=$ANDROID_SDK/tools/lib -Dcom.android.monkeyrunner.bindir=$ANDROID_SDK/tools -Dpython.path=$SIKULI_SCRIPT_JAR com.android.monkeyrunner.MonkeyRunnerStarter $*
