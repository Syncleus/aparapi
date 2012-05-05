java ^
 -Djava.library.path=../../com.amd.aparapi.jni/dist ^
 -Dcom.amd.aparapi.executionMode=%1 ^
 -Dcom.amd.aparapi.logLevel=ERROR ^
 -Dcom.amd.aparapi.enableVerboseJNI=true ^
 -Dcom.amd.aparapi.enableProfiling=false ^
 -Dcom.amd.aparapi.enableShowGeneratedOpenCL=true ^
 -classpath ../../com.amd.aparapi/dist/aparapi.jar;mandel.jar ^
 com.amd.aparapi.sample.mandel.Main


