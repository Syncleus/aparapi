@echo off

java ^
  -Djava.library.path=..\..\com.amd.aparapi.jni\dist ^
  -Dcom.amd.aparapi.executionMode=%1 ^
  -Dcom.amd.aparapi.enableProfiling=false ^
  -classpath ..\..\com.amd.aparapi\dist\aparapi.jar;mdarray.jar ^
  gov.pnnl.aparapi.sample.mdarray.MDArray 