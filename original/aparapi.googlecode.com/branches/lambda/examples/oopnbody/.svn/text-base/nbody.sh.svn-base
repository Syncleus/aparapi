LIBPREFIX=../../com.amd.aparapi.jni/dist/libaparapi_$(uname -m)
case $(uname -s) in 
  Darwin) LIBNAME=${LIBPREFIX}.dyLib;;
  Linux)  LIBNAME=${LIBPREFIX}.so;;
esac
java\
  -agentpath:${LIBNAME}\
  -Dcom.amd.aparapi.executionMode=$1 \
  -Dcom.amd.aparapi.logLevel=INFO \
  -Dcom.amd.aparapi.enableShowGeneratedOpenCL=true \
  -Dbodies=$2 \
  -Dheight=800 \
  -Dwidth=1200 \
  -classpath ../third-party/jogamp/jogl-all.jar:../third-party/jogamp/gluegen-rt.jar:../../com.amd.aparapi/dist/aparapi.jar:oopnbody.jar \
  com.amd.aparapi.examples.oopnbody.Main 

