java\
 -Djava.library.path=../../com.amd.aparapi.jni/dist.fpga\
 -Dcom.amd.aparapi.executionMode=$1\
 -classpath ../../com.amd.aparapi/dist/aparapi.jar:mandel.jar\
 com.amd.aparapi.sample.mandel.Main
