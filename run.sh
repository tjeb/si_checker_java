#!/bin/bash
MY_PATH="`dirname \"$0\"`"
cd $MY_PATH
export CLASSPATH=lib/saxon9he.jar:lib/argparse4j.jar:lib/javax.mail.jar:lib/ibex-crane-ss-4.7.2.7.jar:lib/fop.jar:deps/fop-2.0/lib/*:build/
java SiChecker "$@"
