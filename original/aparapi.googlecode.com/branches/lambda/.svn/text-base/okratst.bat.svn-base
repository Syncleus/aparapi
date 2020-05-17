setlocal 
set PATH=
set HSA_RUNTIME=1
set GPU_BLIT_ENGINE_TYPE=2
set ENABLE64=1
set APARAPI_HOME=C:\Users\user1\aparapi\branches\lambda
set OKRA_HOME=C:\Users\user1\okra
set PATH=c:\Users\user1\okra\hsa\bin\x86_64;c:\Program Files\Java\jdk1.8.0\bin
java -Ddispatch=true -XX:-UseCompressedOops -agentpath:%APARAPI_HOME%\com.amd.aparapi.jni\dist\aparapi_x86_64.dll -Djava.library.path=%OKRA_HOME%\dist\bin;%OKRA_HOME%\hsa\bin\x86_64 -Dbodies=8192 -classpath %APARAPI_HOME%\com.amd.aparapi\dist\aparapi.jar;%OKRA_HOME%\dist\okra.jar %1 %2 %3 %4 %5 %6 %7 %8 %9
endlocal

