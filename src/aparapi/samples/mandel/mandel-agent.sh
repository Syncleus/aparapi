java\
 -agentpath:../../com.amd.aparapi.jni/dist/libaparapi_x86_64.so\
 -Djava.library.path=../../com.amd.aparapi.jni/dist\
 -Dcom.amd.aparapi.useAgent=true\
 -Dcom.amd.aparapi.executionMode=$1\
 -classpath ../../com.amd.aparapi/dist/aparapi.jar:mandel.jar\
 com.amd.aparapi.sample.mandel.Main
