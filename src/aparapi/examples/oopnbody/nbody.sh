
java \
  -Djava.library.path=../../com.amd.aparapi.jni/dist:../third-party/jogamp \
  -Dcom.amd.aparapi.executionMode=$1 \
  -Dcom.amd.aparapi.logLevel=INFO \
  -Dcom.amd.aparapi.enableShowGeneratedOpenCL=true \
  -Dbodies=$2 \
  -Dheight=800 \
  -Dwidth=1200 \
  -classpath ../third-party/jogamp/jogl-all.jar:../third-party/jogamp/gluegen-rt.jar:../../com.amd.aparapi/dist/aparapi.jar:oopnbody.jar \
  com.amd.aparapi.examples.oopnbody.Main 

