java -XX:-UseCompressedOops -agentpath:com.amd.aparapi.jni/dist/libaparapi_x86_64.dylib -cp mockra/mockra.jar:com.amd.aparapi/dist/aparapi.jar $1 $2 $3 $4 $5 $6
#java -XX:-UseCompressedOops -agentpath:com.amd.aparapi.jni/dist/libaparapi_x86_64.so -cp com.amd.aparapi/dist/aparapi.jar:${OKRA}/dist/okra.jar  $1 $2 $3 $4 $5 $6
