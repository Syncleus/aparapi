LIBPREFIX=../../com.amd.aparapi.jni/dist/libaparapi_$(uname -m)
case $(uname -s) in 
  Darwin) LIBNAME=${LIBPREFIX}.dyLib;;
  Linux)  LIBNAME=${LIBPREFIX}.so;;
esac
java\
   -agentpath:${LIBNAME} \
   -Dcom.amd.aparapi.executionMode=$1 \
   -Dsize=$2  \
   -Diterations=$3 \
   -classpath blackscholes.jar:../../com.amd.aparapi/dist/aparapi.jar \
   com.amd.aparapi.samples.blackscholes.Main 
