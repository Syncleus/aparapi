---------
README
---------
some scripts use the following notation to find libaparapi_x86_64.so => -Djava.library.path=../../com.amd.aparapi.jni/dist.[X]
Where X stands for platform type name: std, fpga etc.
build and copy the X version of libaparapi_x86_64.so to this folder so scripts can pick it up:
goto: com.amd.aparapi.jni
run: ant -f build_X_.xml
copy: dist/libaparapi_x86_64.so dist.X

