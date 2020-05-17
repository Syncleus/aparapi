java \
 -Djava.library.path=../.. \
 -Dcom.amd.aparapi.executionMode=$1 \
 -Dcom.amd.aparapi.enableShowGeneratedOpenCL=true \
 -classpath ../../aparapi.jar:convolution.jar \
 com.amd.aparapi.sample.convolution.Convolution $2

