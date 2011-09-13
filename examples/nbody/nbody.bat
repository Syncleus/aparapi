@echo off

set JOGLDIR=..\..\..\..\jogl\jogl-2.0-b23-20110303-windows-i586

set JOGLJARS=
set JOGLJARS=%JOGLJARS%;%JOGLDIR%\jar\jogl.all.jar
set JOGLJARS=%JOGLJARS%;%JOGLDIR%\jar\nativewindow.all.jar
set JOGLJARS=%JOGLJARS%;%JOGLDIR%\jar\gluegen-rt.jar

java ^
  -Djava.library.path=..\..\com.amd.aparapi.jni;%JOGLDIR%\lib ^
  -Dcom.amd.aparapi.executionMode=%1 ^
  -Dbodies=%2 ^
  -Dheight=600 ^
  -Dwidth=600 ^
  -classpath %JOGLJARS%;..\..\com.amd.aparapi\aparapi.jar;nbody.jar ^
  com.amd.aparapi.examples.nbody.Main 


