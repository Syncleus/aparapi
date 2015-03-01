
java ^
 -Djava.library.path=../../com.amd.aparapi.jni/dist ^
 -Dsequential=false^
 -Dcom.amd.aparapi.executionMode=GPU ^
 -Dcom.amd.aparapi.enableProfiling=false ^
 -Dcom.amd.aparapi.enableVerboseJNI=false ^
 -Dcom.amd.aparapi.enableShowGeneratedOpenCL=true ^
 -classpath ../../com.amd.aparapi/dist/aparapi.jar;life.jar ^
 com.amd.aparapi.sample.life.Main


