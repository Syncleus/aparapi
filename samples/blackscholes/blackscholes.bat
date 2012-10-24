java ^
   -Djava.library.path=..\..\com.amd.aparapi.jni\dist ^
   -Dcom.amd.aparapi.executionMode=%1 ^
   -Dcom.amd.aparapi.enableShowGeneratedOpenCL=true ^
   -Dcom.amd.aparapi.enableShowFakeLocalVariableTable=true ^
   -Dsize=%2 ^
   -Diterations=%3 ^
   -classpath blackscholes.jar;..\..\com.amd.aparapi\dist\aparapi.jar ^
   com.amd.aparapi.samples.blackscholes.Main 
