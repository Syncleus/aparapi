java\
 -agentpath:../../libaparapi_x86_64.so\
 -Dcom.amd.aparapi.executionMode=$1\
 -Dcom.amd.aparapi.useAgent=true\
 -classpath ../../aparapi.jar:life.jar\
 com.amd.aparapi.sample.life.Main
