java \
 -Djava.library.path=../.. \
 -Dcom.amd.aparapi.executionMode=%1 \
 -classpath ../../aparapi.jar:squares.jar \
 com.amd.aparapi.sample.squares.Main
