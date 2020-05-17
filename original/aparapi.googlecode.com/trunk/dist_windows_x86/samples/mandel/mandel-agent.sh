java\
 -agentpath:../../libaparapi_x86_64.so\
 -Djava.library.path=../..\
 -Dcom.amd.aparapi.useAgent=true\
 -Dcom.amd.aparapi.executionMode=$1\
 -classpath ../../aparapi.jar:mandel.jar\
 com.amd.aparapi.sample.mandel.Main
