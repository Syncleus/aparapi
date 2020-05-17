LIBPREFIX=../../com.amd.aparapi.jni/dist/libaparapi_$(uname -m)
case $(uname -s) in 
  Darwin) LIBNAME=${LIBPREFIX}.dyLib;;
  Linux)  LIBNAME=${LIBPREFIX}.so;;
esac
java\
 -agentpath:${LIBNAME}\
 -classpath ../../com.amd.aparapi/dist/aparapi.jar:info.jar \
 com.amd.aparapi.sample.info.Main
