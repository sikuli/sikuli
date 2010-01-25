#!/bin/sh
for dylib in *.dylib *.jnilib
do
   for ref in `otool -L $dylib | grep @executable | awk '{print $1'}`
   do
      install_name_tool -change $ref @loader_path/`basename $ref` $dylib
   done
done
