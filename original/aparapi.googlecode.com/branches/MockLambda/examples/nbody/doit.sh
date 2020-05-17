#!/bin/sh

# Build the nbody example in trunk, then make a link to the jogamp dir
# in that directory like 
# jogamp -> ../../../trunk/examples/nbody/jogamp/
# so the correct jogamp will be available

set -x

rm -rf classes
mkdir classes

cd src
javac -d ../classes -classpath ../jogamp/jogl-all.jar:../jogamp/gluegen-rt.jar  com/amd/aparapi/examples/nbody/*.java

cd ..

cp src/com/amd/aparapi/examples/nbody/*jpg classes/com/amd/aparapi/examples/nbody/

java -Dheight=800 -Dwidth=1000  -Dbodies=$1 -Djava.library.path=jogamp -classpath jogamp/jogl-all.jar:jogamp/gluegen-rt.jar:classes/:.  com.amd.aparapi.examples.nbody.Main
