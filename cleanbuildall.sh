#!/bin/bash

sikulidir=`dirname $0`
cd $sikulidir
rm -rf sikuli-script/build sikuli-ide/build sikuli-script/target/jar/tessdata 2> /dev/null
mkdir -p sikuli-script/build
pushd sikuli-script/build
cmake .. || exit 1
make || exit 1
popd
mkdir -p sikuli-ide/build
pushd sikuli-ide/build
cmake .. || exit 1
make package || exit 1
popd
