#!/bin/bash

SIKULI_DIR=$HOME/sikuli
LIB_DIR=${SIKULI_DIR}/lib

JARS="
${LIB_DIR}/commons-cli-1.2.jar
${LIB_DIR}/junit-3.8.1.jar
${LIB_DIR}/jxgrabkey/lib/JXGrabKey.jar
${LIB_DIR}/swing-layout-1.0.1.jar
"
#${LIB_DIR}/mx-native-loader-1.2/target/mx-native-loader-1.2.jar
#${LIB_DIR}/lib-sikuli.jar
#${LIB_DIR}/jython-lib-2.5.1.jar

JARS=`echo $JARS`
CLASSPATH="${JARS// /:}"
CLASSPATH="${CLASSPATH}:${SIKULI_DIR}/sikuli-ide/target/classes"
CLASSPATH="${CLASSPATH}:${SIKULI_DIR}/sikuli-script/target/classes"
CLASSPATH="${CLASSPATH}:${SIKULI_DIR}/sikuli-ide/resources"
CLASSPATH="${CLASSPATH}:${LIB_DIR}/sikuli-script.jar"
#CLASSPATH="${CLASSPATH}:${LIB_DIR}/jython-2.5.1.jar"

JAVA_OPT="-Dsikuli.console=true -Xms64M -Xmx512M -Dfile.encoding=UTF-8"

java $JAVA_OPT -cp "$CLASSPATH" org.sikuli.ide.SikuliIDE
