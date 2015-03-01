# -Dcom.amd.aparapi.logLevel=FINE\
# -Dcom.amd.aparapi.executionMode=$1\

java\
 -Djava.library.path=../../com.amd.aparapi.jni/dist\
 -Dcom.amd.aparapi.executionMode=$1\
 -Dsize=$2  \
 -Dclusters=$3 \
 -Ddim=$4 \
 -classpath ../../com.amd.aparapi/dist/aparapi.jar:kmeans.jar\
 com.hp.aparapi.sample.kmeans.Main
