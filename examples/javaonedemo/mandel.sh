#!/bin/bash 
java \
   -Djava.library.path=../../com.amd.aparapi.jni/dist \
   -classpath ../../com.amd.aparapi/dist/aparapi.jar:javaonedemo.jar \
   com.amd.aparapi.examples.javaonedemo.Mandel
