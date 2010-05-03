#!/bin/sh
DIR=`dirname $0`
java -Xms256M -Xmx1024M -Dfile.encoding=UTF-8 -jar $DIR/sikuli-ide.jar $*
