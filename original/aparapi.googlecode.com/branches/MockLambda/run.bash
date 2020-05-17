/Users/garyfrost/lambda/build/macosx-x86_64-fastdebug/bin/java \
    -XX:+UnlockDiagnosticVMOptions \
    -XX:+LogCompilation  \
    -XX:+PrintOptoAssembly  \
    -XX:+PrintLIR \
    -classpath aparapi.jar:samples.jar \
    com.amd.aparapi.samples.$1 $2 $3 $4 $6 $7 $8 $9 

#    -XX:+PrintLIR 
#    -XX:+LogCompilation 
#    -XX:+PrintOptoAssembly 
