java ^
 -agentpath:../../aparapi_x86.dll ^
 -Djava.library.path=../.. ^
 -Dcom.amd.aparapi.useAgent=true ^
 -Dcom.amd.aparapi.executionMode=%1 ^
 -Dcom.amd.aparapi.logLevel=OFF^
 -Dcom.amd.aparapi.enableVerboseJNI=false ^
 -Dcom.amd.aparapi.enableProfiling=false ^
 -Dcom.amd.aparapi.enableShowGeneratedOpenCL=true ^
 -Dcom.amd.aparapi.enableVerboseJNIOpenCLResourceTracking=false ^
 -Dcom.amd.aparapi.dumpFlags=true ^
 -Dcom.amd.aparapi.enableInstructionDecodeViewer=false ^
 -classpath ../../aparapi.jar;mandel.jar ^
 com.amd.aparapi.sample.mandel.Main


