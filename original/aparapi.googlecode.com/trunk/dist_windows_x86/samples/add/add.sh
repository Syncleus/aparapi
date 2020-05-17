java \
 -Djava.library.path=../.. \
 -Dcom.amd.aparapi.executionMode=%1 \
 -classpath ../../aparapi.jar:add.jar \
 com.amd.aparapi.sample.add.Main
