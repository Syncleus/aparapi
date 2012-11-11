java \
  -Djava.library.path=../../com.amd.aparapi.jni/dist \
  -Dcom.amd.aparapi.executionMode=$1 \
  -Dheight=600 \
  -Dwidth=600 \
  -classpath ../../com.amd.aparapi/dist/aparapi.jar:javaonedemo.jar \
  com.amd.aparapi.examples.javaonedemo.Life
