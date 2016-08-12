SETLOCAL 
if /I %PROCESSOR_ARCHITECTURE%==x86 goto win32
echo "win64!"
set PATH=%PATH%;ffmpeg\ffmpeg-git-9c2651a-win64-shared\bin
set PATH=%PATH%;jjmpeg\jjmpeg-0.0\native\mswin-amd64
goto win64
:win32
echo "win32!"
set PATH=%PATH%;ffmpeg\ffmpeg-git-9c2651a-win32-shared\bin
set PATH=%PATH%;jjmpeg\jjmpeg-0.0\native\mswin-i386
:win64
set PATH=%PATH%;..\..\com.amd.aparapi.jni\dist
java -classpath jjmpeg\jjmpeg-0.0\dist\jjmpeg.jar;..\..\com.amd.aparapi\dist\aparapi.jar;movie.jar; com.amd.aparapi.examples.movie.%1 %2

ENDLOCAL
