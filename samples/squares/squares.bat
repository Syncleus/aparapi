%JAVA_HOME%/bin/java ^
 -Djava.library.path=../../com.amd.aparapi.jni ^
 -Dcom.amd.aparapi.executionMode=%1 ^
 -classpath ../../com.amd.aparapi/aparapi.jar;squares.jar ^
 com.amd.aparapi.sample.squares.Main

