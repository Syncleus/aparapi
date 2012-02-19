java \
   -Djava.library.path=..\..\com.amd.aparapi.jni\dist \
   -Dcom.amd.aparapi.executionMode=$1 \
   -Dsize=$2  \
   -Diterations=$3 \
   -classpath blackscholes.jar:..\..\com.amd.aparapi\dist\aparapi.jar \
   com.amd.aparapi.samples.blackscholes.Main 
