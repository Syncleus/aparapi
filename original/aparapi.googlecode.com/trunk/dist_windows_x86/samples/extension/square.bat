java ^
 -Djava.library.path=../.. ^
 -Dcom.amd.aparapi.executionMode=%1 ^
 -Dcom.amd.aparapi.enableProfiling=false ^
 -Dcom.amd.aparapi.enableShowGeneratedOpenCL=true ^
 -classpath ../../aparapi.jar;extension.jar ^
 com.amd.aparapi.sample.extension.SquareExample


