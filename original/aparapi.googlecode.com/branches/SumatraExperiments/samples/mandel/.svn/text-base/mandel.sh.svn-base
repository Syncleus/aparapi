#!/bin/sh
set -x

# Note this sample requires JDK 8
# JAVA_HOME=/home/ecaspole/views/lambda/lambda/build/linux-x86_64-normal-server-release/images/j2sdk-image/
JAVA=$JAVA_HOME/bin/java


# -Dcom.amd.aparapi.enableVerboseJNI=true \
# -Dcom.amd.aparapi.enableInstructionDecodeViewer=true \

$JAVA -ea   \
 -Dcom.amd.aparapi.logLevel=INFO \
 -Dcom.amd.aparapi.enableShowGeneratedOpenCL=true \
 -Djava.library.path=../../com.amd.aparapi.jni/dist/ \
 -javaagent:../../com.amd.aparapi/dist/aparapi-agent.jar \
 -classpath mandel.jar:../../com.amd.aparapi/dist/aparapi.jar \
 com.amd.aparapi.sample.mandel.Main
