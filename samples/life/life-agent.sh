java\
 -agentpath:../../com.amd.aparapi.jni/dist/libaparapi_x86_64.so\
 -Dcom.amd.aparapi.executionMode=$1\
 -Dcom.amd.aparapi.useAgent=true\
 -classpath ../../com.amd.aparapi/dist/aparapi.jar:life.jar\
 com.amd.aparapi.sample.life.Main
