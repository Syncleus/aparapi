
case $(uname -i) in 
    x86_64 )
        export ARCH=linux-amd64 ;;
    *)
        export ARCH=linux-i586 ;;
esac

java \
  -Djava.library.path=../../com.amd.aparapi.jni/dist:jogamp/${ARCH}  \
  -classpath jogamp/gluegen-rt.jar:jogamp/jogl-all.jar:../../com.amd.aparapi/dist/aparapi.jar:javaonedemo.jar \
  com.amd.aparapi.examples.javaonedemo.NBody 
