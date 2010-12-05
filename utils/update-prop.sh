#!/bin/sh
RESOURCE_DIR=$1
for f in $RESOURCE_DIR/i18n/IDE/*.po; do
  echo convert $f
  po2prop -t $RESOURCE_DIR/i18n/IDE_en_US.properties $f `echo $f | sed -e 's/IDE-\(.*\).po/..\/IDE_\1.properties/'`
done
