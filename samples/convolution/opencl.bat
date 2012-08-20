java ^
 -Djava.library.path=../../com.amd.aparapi.jni/dist ^
 -Dcom.amd.aparapi.executionMode=%1 ^
 -classpath ../../com.amd.aparapi/dist/aparapi.jar;convolution.jar ^
 com.amd.aparapi.sample.convolution.ConvolutionOpenCL

