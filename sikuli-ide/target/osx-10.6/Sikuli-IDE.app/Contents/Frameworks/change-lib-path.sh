#!/bin/sh
for dylib in *.dylib *.jnilib
do
   for ref in `otool -L $dylib | grep "lib.*.dylib[^:]" | awk '{print $1'} | grep -v '^/usr/lib' | grep -v '^/System/'`
   do
      #echo $ref
      install_name_tool -change $ref @loader_path/`basename $ref` $dylib
   done
done
