LIBPREFIX=../../com.amd.aparapi.jni/dist/libaparapi_$(uname -m)
case $(uname -s) in 
  Darwin) LIBNAME=${LIBPREFIX}.dyLib;;
  Linux)  LIBNAME=${LIBPREFIX}.so;;
esac
java\
  -agentpath:${LIBNAME}\
  -Dcom.amd.aparapi.executionMode=$1 \
  -Dbodies=$2 \
  -Dheight=600 \
  -Dwidth=600 \
  -classpath ../third-party/jogamp/jogl-all.jar:../third-party/jogamp/gluegen-rt.jar:../../com.amd.aparapi/dist/aparapi.jar:nbody.jar \
  com.amd.aparapi.examples.nbody.Main

