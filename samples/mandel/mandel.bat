
set PATH=%PATH%;C:\Program Files\AMD APP\bin\x86
%JAVA_HOME%/bin/java ^
 -Djava.library.path=../../com.amd.aparapi.jni ^
 -Dcom.amd.aparapi.executionMode=%1 ^
 -classpath ../../com.amd.aparapi/aparapi.jar;mandel.jar ^
 com.amd.aparapi.sample.mandel.Main


