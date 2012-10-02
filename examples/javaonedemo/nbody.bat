@echo off

java ^
  -Djava.library.path=..\..\com.amd.aparapi.jni\dist;jogamp ^
  -Dcom.amd.aparapi.executionMode=%1 ^
  -Dcom.amd.aparapi.enableProfiling=false ^
  -Dbodies=%2 ^
  -Dheight=600 ^
  -Dwidth=600 ^
  -classpath jogamp\gluegen-rt.jar;jogamp\jogl.all.jar;..\..\com.amd.aparapi\dist\aparapi.jar;javaonedemo.jar ^
  com.amd.aparapi.examples.javaonedemo.NBody 


