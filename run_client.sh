#!/bin/bash

# OPTS="--add-opens java.base/java.xml=ALL-UNNAMED --add-modules java.xml"
OPTS="--add-modules java.xml"
java ${OPTS} --enable-preview --enable-native-access=ALL-UNNAMED -jar client/target/jminesweep-client.jar -l 1 -c 20 -r 10 127.0.0.1
