@echo off

java ^
  -Djava.library.path=..\..\com.amd.aparapi.jni\dist;jogamp ^
  -Dcom.amd.aparapi.executionMode=%1 ^
  -Dbodies=%2 ^
  -Dheight=600 ^
  -Dwidth=600 ^
  -classpath jogamp\gluegen-rt.jar;jogamp\jogl.all.jar;..\..\com.amd.aparapi\dist\aparapi.jar;nbody.jar ^
  com.amd.aparapi.examples.nbody.Main 


