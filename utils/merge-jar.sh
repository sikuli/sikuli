#!/bin/sh
TMP=jar-tmp
OUT=out.jar
mkdir -p $TMP
cd $TMP
for jar in $@
do
   jar xf ../$jar
done
jar cMf ../out.jar *
cd ..
rm -rf $TMP
