java ^
 -agentpath:../../com.amd.aparapi.jni/dist/aparapi_x86_64.dll^
 -Dcom.amd.aparapi.useAgent=true ^
 -Djava.library.path=../../com.amd.aparapi.jni/dist ^
 -Dsequential=false^
 -Dcom.amd.aparapi.executionMode=%1 ^
 -Dcom.amd.aparapi.enableProfiling=false ^
 -Dcom.amd.aparapi.enableVerboseJNI=false ^
 -Dcom.amd.aparapi.enableShowGeneratedOpenCL=true ^
 -classpath ../../com.amd.aparapi/dist/aparapi.jar;life.jar ^
 com.amd.aparapi.sample.life.Main


