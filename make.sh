#!/bin/bash

if [ ! -f buildlib.tmp ] ; then
wget --no-check-certificate https://github.com/hydrocul/kamebuild/raw/master/buildlib.sh
mv buildlib.sh buildlib.tmp
fi

. buildlib.tmp



case $1 in
################################
compile)
################################
buildlib_mkdir_lib
if [ $? -ne 0 ] ; then
#buildlib_dl_kameutil
buildlib_dl_kameq
fi

buildlib_compile

if [ $? -ne 0 ] ; then
exit 1
fi

. make.sh test

;;
################################
test)
################################
buildlib_test

;;
################################
build)
################################
buildlib_build_jar hydrocul-kamehtmlconsole.jar

;;
################################
scaladoc)
################################
if [ ! -d ./scaladoc ] ; then
mkdir ./scaladoc
fi
scaladoc -d scaladoc -classpath "lib/*:class/" -encoding UTF-8 src/hydrocul/kamehtmlconsole/*.scala src/hydrocul/kamehtmlconsole/html/*.scala

;;
################################
*)
################################
echo "Usage: ./make.sh {compile|test|build|scaladoc}"

;;
################################
esac



