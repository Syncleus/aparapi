@echo off

java ^
  -Djava.library.path=..\..\com.amd.aparapi.jni;jogamp ^
  -Dcom.amd.aparapi.executionMode=%1 ^
  -Dcom.amd.aparapi.enableShowGeneratedOpenCL=true ^
  -Dcom.amd.aparapi.enableVerboseJNI=false ^
  -Dbodies=%2 ^
  -Dheight=600 ^
  -Dwidth=600 ^
  -classpath jogamp\gluegen-rt.jar;jogamp\jogl.all.jar;..\..\com.amd.aparapi\aparapi.jar;nbody.jar ^
  com.amd.aparapi.examples.nbody.Local


