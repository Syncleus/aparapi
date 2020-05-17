SETLOCAL 
java -Djava.library.path=..\..\..\..\trunk\com.amd.aparapi.jni\dist -Dcom.amd.aparapi.enableProfiling=true  -classpath ..\..\..\..\trunk\com.amd.aparapi\dist\aparapi.jar;jviolajones.jar;jdom.jar com.amd.aparapi.examples.jviolajones.Test %1 haarcascade_frontalface_alt2.xml

