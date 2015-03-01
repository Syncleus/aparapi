java\
 -Djava.library.path=../../com.amd.aparapi.jni/dist.std\
 -Dcom.amd.aparapi.executionMode=$1\
 -Dcom.amd.aparapi.platformHint=$2\
 -classpath ../../com.amd.aparapi/dist/aparapi.jar:mandel.jar\
 com.amd.aparapi.sample.mandel.Main
