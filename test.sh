#!/bin/bash

java -classpath ./test/class:./class:./lib/*:$SCALA_HOME/lib/* Test
if [ $? -ne 0 ] ; then
exit 1
fi

echo "Success!"
