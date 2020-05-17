LIBPREFIX=../../com.amd.aparapi.jni/dist/libaparapi_$(uname -m)
case $(uname -s) in 
  Darwin) LIBNAME=${LIBPREFIX}.dyLib;;
  Linux)  LIBNAME=${LIBPREFIX}.so;;
esac
java\
 -agentpath:${LIBNAME}\
 -Dcom.amd.aparapi.executionMode=$1 \
 -Dcom.amd.aparapi.enableShowGeneratedOpenCL=true \
 -classpath ../../com.amd.aparapi/dist/aparapi.jar:convolution.jar \
 com.amd.aparapi.sample.convolution.Convolution $2

