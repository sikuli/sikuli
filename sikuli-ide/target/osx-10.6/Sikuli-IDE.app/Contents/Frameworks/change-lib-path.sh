#!/bin/sh
for dylib in *.dylib *.jnilib
do
   for ref in `otool -L $dylib | grep opt | awk '{print $1'}`
   do
      install_name_tool -change $ref @executable_path/../Frameworks/`basename $ref` $dylib
   done
done
