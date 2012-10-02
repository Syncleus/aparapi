java ^
 -Djava.library.path=../../com.amd.aparapi.jni/dist ^
 -Dcom.amd.aparapi.executionMode=%1 ^
 -Dcom.amd.aparapi.logLevel=SEVERE^
 -Dcom.amd.aparapi.enableVerboseJNI=false ^
 -Dcom.amd.aparapi.enableProfiling=false ^
 -Dcom.amd.aparapi.enableShowGeneratedOpenCL=false ^
 -Dcom.amd.aparapi.enableVerboseJNIOpenCLResourceTracking=false ^
 -Dcom.amd.aparapi.dumpFlags=true ^
 -Dcom.amd.aparapi.enableInstructionDecodeViewer=false ^
 -classpath ../../com.amd.aparapi/dist/aparapi.jar;javaonedemo.jar ^
 com.amd.aparapi.examples.javaonedemo.Mandel


