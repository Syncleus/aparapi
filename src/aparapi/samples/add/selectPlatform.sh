
#
# select platform and flow test script. 
# runs add kernel on diffrent platforms/flows.
#
# $1 = platformHint [AMD,Altera,Intel,NVidia]
# $2 = deviceType  [CPU,CPU,ACC]
# $3 = deviceId [0=first available device..n=available device]
# $4 = flowTypeStr [source,binary,default]
#      can use -Dcom.amd.aparapi.flowType instead
# $5 = dist.[std,fpga] to load -> !!! note -> remember to build and copy libaparapi_x86_64.so to the correct place
#      example: cp ../../com.amd.aparapi.jni/dist/libaparapi_x86_64.so ../../com.amd.aparapi.jni/dist.std/
#      Alternatively use a script verion below without $5 ->
#      -Djava.library.path=../../com.amd.aparapi.jni/dist \

# usage examples:
# sh selectPlatform.sh AMD CPU 0 source std
# sh selectPlatform.sh Altera ACC 0 binary fpga

#java \
# -Djava.library.path=../../com.amd.aparapi.jni/dist.$5 \
# -classpath ../../com.amd.aparapi/dist/aparapi.jar:add.jar \
# com.amd.aparapi.sample.add.MainSelectPlatform $1 $2 $3 $4

#
# other script variants uncomment to use ->
#

# script version with libaparapi_x86_64.so in dist
#java \
# -Djava.library.path=../../com.amd.aparapi.jni/dist \
# -classpath ../../com.amd.aparapi/dist/aparapi.jar:add.jar \
# com.amd.aparapi.sample.add.MainSelectPlatform $1 $2 $3

# script version with flow type set through config 
#java \
# -Djava.library.path=../../com.amd.aparapi.jni/dist.$5 \
# -Dcom.amd.aparapi.flowType=$4 \
# -classpath ../../com.amd.aparapi/dist/aparapi.jar:add.jar \
# com.amd.aparapi.sample.add.MainSelectPlatform $1 $2 $3

# to inc logging level
#-Dcom.amd.aparapi.logLevel=FINE \
#-Dcom.amd.aparapi.MemOverideLib=[abs path to MemOverideLib.so] \

java \
 -Djava.library.path=../../com.amd.aparapi.jni/dist.$5 \
 -classpath ../../com.amd.aparapi/dist/aparapi.jar:add.jar \
 com.amd.aparapi.sample.add.MainSelectPlatform $1 $2 $3 $4


