LIBPREFIX=../../com.amd.aparapi.jni/dist/libaparapi_$(uname -m)
case $(uname -s) in 
  Darwin) LIBNAME=${LIBPREFIX}.dyLib;;
  Linux)  LIBNAME=${LIBPREFIX}.so;;
esac
java\
  -agentpath:${LIBNAME}\
  -Dheight=1000 \
  -Dwidth=1000 \
  -classpath ../third-party/jogamp/jogl-all.jar:../third-party/jogamp/gluegen-rt.jar:../../com.amd.aparapi/dist/aparapi.jar:nbody.jar \
  com.amd.aparapi.examples.nbody.Test

