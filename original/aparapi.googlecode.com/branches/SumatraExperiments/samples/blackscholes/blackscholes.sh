#!/bin/sh

set -x

java \
	  -Dsize=$1  \
	  -Diterations=$2 \
	  -Dcom.amd.aparapi.logLevel=INFO \
	  -classpath blackscholes.jar \
	  com.amd.aparapi.samples.blackscholes.Main 
