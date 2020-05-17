java ^
 -Djava.library.path=../.. ^
 -Dcom.amd.aparapi.executionMode=%1 ^
 -classpath ../../aparapi.jar;convolution.jar ^
 com.amd.aparapi.sample.convolution.ConvolutionOpenCL %2

