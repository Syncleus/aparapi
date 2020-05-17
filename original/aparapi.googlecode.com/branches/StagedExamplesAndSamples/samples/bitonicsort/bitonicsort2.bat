java ^
   -ea^
   -Djava.library.path=..\..\com.amd.aparapi.jni ^
   -Dcom.amd.aparapi.executionMode=%1 ^
   -Dcom.amd.aparapi.enableShowGeneratedOpenCL=true ^
   -classpath bitonicsort.jar;..\..\com.amd.aparapi\aparapi.jar ^
   com.amd.aparapi.samples.bitonicsort.Main2
