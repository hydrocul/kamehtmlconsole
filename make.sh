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
#buildlib_dl_kameq
buildlib_dl_scalastm
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
buildlib_scaladoc

;;
################################
*)
################################
echo "Usage: ./make.sh {compile|test|build|scaladoc}"

;;
################################
esac



