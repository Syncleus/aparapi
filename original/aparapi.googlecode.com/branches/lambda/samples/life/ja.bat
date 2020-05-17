rm -rf classes life.jar
mkdir classes
%JAVA_HOME%/bin/javac -sourcepath src -d classes -g -cp ..\..\com.amd.aparapi\dist\aparapi.jar src\com\amd\aparapi\sample\life\Main.java
%JAVA_HOME%/bin/jar cvf life.jar -C classes com
%JAVA_HOME%/bin/java ^
 -verbose ^
 -agentpath:..\..\com.amd.aparapi.jni\dist\aparapi_x86_64.dll^
 -Dcom.amd.aparapi.useAgent=true ^
 -Djava.library.path=..\..\com.amd.aparapi.jni\dist ^
 -Dsequential=false^
 -Dcom.amd.aparapi.executionMode=%1 ^
 -Dcom.amd.aparapi.enableProfiling=false ^
 -Dcom.amd.aparapi.enableVerboseJNI=false ^
 -Dcom.amd.aparapi.enableShowGeneratedOpenCL=true ^
 -classpath ..\..\com.amd.aparapi\dist\aparapi.jar;life.jar ^
 com.amd.aparapi.sample.life.Main






