@echo off

java ^
  -Djava.library.path=..\..\..\..\trunk\com.amd.aparapi.jni\dist;jogamp ^
  -DUseAparapi=true^
  -Dcom.amd.aparapi.executionMode=%1 ^
  -Dcom.amd.aparapi.enableProfiling=false ^
  -Dbodies=%2 ^
  -Dheight=600 ^
  -Dwidth=600 ^
  -classpath jogamp\gluegen-rt.jar;jogamp\jogl.all.jar;..\..\..\..\trunk\com.amd.aparapi\dist\aparapi.jar;nbody.jar ^
  com.amd.aparapi.examples.nbody.Main 


