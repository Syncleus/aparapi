
java \
  -Djava.library.path=../../com.amd.aparapi.jni:jogamp \
  -Dcom.amd.aparapi.executionMode=$1 \
  -Dbodies=$1 \
  -Dheight=600 \
  -Dwidth=600 \
  -classpath jogamp/jogl.all.jar:jogamp/gluegen-rt.jar:../../com.amd.aparapi/aparapi.jar:nbody.jar \
  com.amd.aparapi.examples.nbody.Main 

