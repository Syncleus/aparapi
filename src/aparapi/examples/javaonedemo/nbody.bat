@echo off
java ^
  -Djava.library.path=..\..\com.amd.aparapi.jni\dist;..\third-party\jogamp\windows-%PROCESSOR_ARCHITECTURE% ^
  -classpath ..\third-party\jogamp\gluegen-rt.jar;..\third-party\jogamp\jogl-all.jar;..\..\com.amd.aparapi\dist\aparapi.jar;javaonedemo.jar ^
  com.amd.aparapi.examples.javaonedemo.NBody 


