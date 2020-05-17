. ./setvars.bash

rm -r -f \
  samples.jar \
  classes 

mkdir classes

${JAVA_HOME}/bin/javac \
 -classpath aparapi.jar \
 -XDlambdaToMethod \
 -g \
 -d classes \
 -sourcepath src/java \
 src/java/com/amd/aparapi/samples/WheresMyLambdaBytecode.java \
 src/java/com/amd/aparapi/samples/Mandel.java \
 src/java/com/amd/aparapi/ParseJava8.java \
 src/java/com/amd/aparapi/samples/Squares.java 

${JAVA_HOME}/bin/jar \
  cf samples.jar \
  -C classes \
  com
