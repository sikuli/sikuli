#!/bin/sh
DIR=`dirname $0`
java -Xms64M -Xmx512M -Dfile.encoding=UTF-8 -jar $DIR/sikuli-ide.jar $*
