#!/bin/bash
java --enable-native-access=ALL-UNNAMED -jar server/target/jminesweep-server.jar "$@"
