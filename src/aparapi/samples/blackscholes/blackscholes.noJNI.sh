java \
 -Dcom.amd.aparapi.executionMode=%1 \
 -Dsize=$2  \
 -Diterations=$3 \
 -classpath ../../com.amd.aparapi/dist/aparapi.jar:blackscholes.jar \
 com.amd.aparapi.samples.blackscholes.Main 
