#!/bin/sh
DIR=`dirname $0`
java -Dsikuli.console=false -Xms64M -Xmx512M -Dfile.encoding=UTF-8 -jar $DIR/sikuli-ide.jar $*
