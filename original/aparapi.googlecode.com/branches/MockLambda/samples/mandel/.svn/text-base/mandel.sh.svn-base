#!/bin/sh
set -x

#java -ea -Xrunhprof:cpu=samples,thread=y,depth=16,file=mandel.$$.hprof.txt  -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining -XX:+PrintCompilation -XX:CICompilerCount=1 \
java -ea  \
 -classpath mandel.jar \
 com.amd.aparapi.sample.mandel.Main
