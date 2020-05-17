@echo off

java ^
  -agentpath:..\..\com.amd.aparapi.jni\dist\aparapi_x86_64.dll ^
  -Djava.library.path=..\third-party\jogamp ^
  -Dcom.amd.aparapi.executionMode=%1 ^
  -Dcom.amd.aparapi.enableProfiling=false ^
  -Dcom.amd.aparapi.enableShowGeneratedOpenCL=true ^
  -Dbodies=%2 ^
  -Dheight=600 ^
  -Dwidth=600 ^
  -classpath ..\third-party\jogamp\jogl-all.jar;..\third-party\jogamp\gluegen-rt.jar;..\..\com.amd.aparapi\dist\aparapi.jar;oopnbody.jar ^
  com.amd.aparapi.examples.oopnbody.Main 


