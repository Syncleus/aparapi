@echo off
java ^
  -agentpath:..\..\com.amd.aparapi.jni\dist\aparapi_x86_64.dll ^
  -Djava.library.path=..\third-party\jogamp\windows-%PROCESSOR_ARCHITECTURE% ^
  -classpath ..\third-party\jogamp\gluegen-rt.jar;..\third-party\jogamp\jogl-all.jar;..\..\com.amd.aparapi\dist\aparapi.jar;javaonedemo.jar ^
  com.amd.aparapi.examples.javaonedemo.NBody 


