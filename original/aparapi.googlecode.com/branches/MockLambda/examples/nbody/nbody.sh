
java \
  -Djava.library.path=../../com.amd.aparapi.jni/dist:jogamp \
  -Dcom.amd.aparapi.executionMode=$1 \
  -Dcom.amd.aparapi.logLevel=INFO \
  -Dcom.amd.aparapi.enableShowGeneratedOpenCL=INFO \
  -Dbodies=$1 \
  -Dheight=800 \
  -Dwidth=1200 \
  -classpath jogamp/jogl.all.jar:jogamp/gluegen-rt.jar:../../com.amd.aparapi/dist/aparapi.jar:nbody.jar \
  com.amd.aparapi.examples.nbody.Main 

