#!/bin/bash

OPTS="--add-modules java.xml --enable-preview --enable-native-access=ALL-UNNAMED -jar client/target/jminesweep-client.jar"
if [ "$#" == "0" ] ; then
  java ${OPTS} -l 1 -c 20 -r 10 127.0.0.1
else
  java ${OPTS} "$@"
fi
