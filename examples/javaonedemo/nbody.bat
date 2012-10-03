@echo off
java ^
  -Djava.library.path=..\..\com.amd.aparapi.jni\dist;jogamp ^
  -classpath jogamp\gluegen-rt.jar;jogamp\jogl.all.jar;..\..\com.amd.aparapi\dist\aparapi.jar;javaonedemo.jar ^
  com.amd.aparapi.examples.javaonedemo.NBody 


