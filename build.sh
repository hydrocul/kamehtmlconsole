#!/bin/bash

if [ ! -d ./build ] ; then
mkdir ./build
fi

pushd class >/dev/null
jar -cvf ../build/hydrocul-kamehtmlconsole.jar *
popd >/dev/null
