#  -Dcom.amd.aparapi.executionMode=$1 \

${JAVA_HOME}/bin/java -ea \
  -Djava.library.path=../../com.amd.aparapi.jni/dist:jogamp \
  -Dcom.amd.aparapi.logLevel=INFO \
  -Dcom.amd.aparapi.enableShowGeneratedOpenCL=true \
  -Dbodies=$1 \
  -Dheight=800 \
  -Dwidth=1200 \
  -javaagent:../../com.amd.aparapi/dist/aparapi-agent.jar \
  -classpath jogamp/jogl-all.jar:jogamp/gluegen-rt.jar:../../com.amd.aparapi/dist/aparapi.jar:oopnbody.jar \
  com.amd.aparapi.examples.oopnbody.Main 

