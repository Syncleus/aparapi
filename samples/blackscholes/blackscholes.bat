java ^
   -Djava.library.path=..\..\com.amd.aparapi.jni ^
   -Dcom.amd.aparapi.executionMode=%1 ^
   -Dsize=%2 ^
   -Diterations=%3 ^
   -classpath blackscholes.jar;..\..\com.amd.aparapi\aparapi.jar ^
   com.amd.aparapi.samples.blackscholes.Main 
