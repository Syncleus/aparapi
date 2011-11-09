
java \
  -Djava.library.path=../../com.amd.aparapi.jni:lib \
  -Dcom.amd.aparapi.executionMode=$1 \
  -Dbodies=$1 \
  -Dheight=600 \
  -Dwidth=600 \
  -classpath lib/jogl.all.jar:lib/gluegen-rt.jar:../../com.amd.aparapi/aparapi.jar:nbody.jar \
  com.amd.aparapi.examples.nbody.Main 

