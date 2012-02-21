java ^
 -Djava.library.path=../../com.amd.aparapi.jni/dist ^
 -Dcom.amd.aparapi.executionMode=%1 ^
 -Dcom.amd.aparapi.enableProfiling=true ^
 -classpath ../../com.amd.aparapi/dist/aparapi.jar;mandel.jar ^
 com.amd.aparapi.sample.mandel.Main


