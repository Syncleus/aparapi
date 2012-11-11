
java \
  -Djava.library.path=../../com.amd.aparapi.jni/dist:jogamp \
  -Dcom.amd.aparapi.executionMode=$1 \
  -Dbodies=$1 \
  -Dheight=600 \
  -Dwidth=600 \
  -classpath jogamp/jogl-all.jar:jogamp/gluegen-rt.jar:../../com.amd.aparapi/dist/aparapi.jar:nbody.jar \
  com.amd.aparapi.examples.nbody.Main 

