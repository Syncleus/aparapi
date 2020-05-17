LIBPREFIX=../../com.amd.aparapi.jni/dist/libaparapi_$(uname -m)
case $(uname -s) in 
  Darwin) LIBNAME=${LIBPREFIX}.dyLib;;
  Linux)  LIBNAME=${LIBPREFIX}.so;;
esac
java\
 -agentpath:${LIBNAME}\
 -Dcom.amd.aparapi.useAgent=true\
 -Dcom.amd.aparapi.executionMode=$1\
 -Dcom.amd.aparapi.enableVerboseJNI=false\
 -classpath ../../com.amd.aparapi/dist/aparapi.jar:mandel.jar\
 com.amd.aparapi.sample.mandel.Main
