export JOGLDIR=../../../../jogl/jogl-2.0-b23-20110303-linux-amd64

export JOGLJARS=
export JOGLJARS=${JOGLJARS}:${JOGLDIR}/jar/jogl.all.jar
export JOGLJARS=${JOGLJARS}:${JOGLDIR}/jar/nativewindow.all.jar
export JOGLJARS=${JOGLJARS}:${JOGLDIR}/jar/gluegen-rt.jar

${JAVA_HOME}/bin/java \
  -Djava.library.path=../../com.amd.aparapi.jni:${JOGLDIR}/lib \
  -Dcom.amd.aparapi.executionMode=$1 \
  -Dbodies=$1 \
  -Dheight=600 \
  -Dwidth=600 \
  -classpath ${JOGLJARS}:../../com.amd.aparapi/aparapi.jar:nbody.jar \
  com.amd.aparapi.examples.nbody.Main 

