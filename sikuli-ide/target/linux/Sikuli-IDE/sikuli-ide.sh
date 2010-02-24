#!/bin/sh
DIR=`dirname $0`
java -Dfile.encoding=UTF-8 -jar $DIR/sikuli-ide.jar $*
