@echo off

java ^
  -Djava.library.path=..\..\com.amd.aparapi.jni\dist;..\third-party\jogamp ^
  -Dcom.amd.aparapi.executionMode=%1 ^
  -Dcom.amd.aparapi.enableShowGeneratedOpenCL=true ^
  -Dcom.amd.aparapi.enableVerboseJNI=false ^
  -Dcom.amd.aparapi.enableProfiling=true ^
  -Dbodies=%2 ^
  -Dheight=600 ^
  -Dwidth=600 ^
  -classpath ..\third-party\jogamp\gluegen-rt.jar;..\third-party\jogamp\jogl.all.jar;..\..\com.amd.aparapi\dist\aparapi.jar;nbody.jar ^
  com.amd.aparapi.examples.nbody.Local


