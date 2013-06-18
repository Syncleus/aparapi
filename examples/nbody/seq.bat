@echo off

java ^
  -Djava.library.path=..\third-party\jogamp ^
  -Dbodies=%1 ^
  -Dheight=600 ^
  -Dwidth=600 ^
  -classpath ..\third-party\jogamp\gluegen-rt.jar;..\third-party\jogamp\jogl-all.jar;nbody.jar ^
  com.amd.aparapi.examples.nbody.Seq


