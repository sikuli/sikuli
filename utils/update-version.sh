#!/bin/sh
VER=$2
FILES=$1
PATTERN="(SikuliVersion = )\".*\"(;)"
perl -pi -e "s/$PATTERN/\1\"$2\"\2/" $1
