SETLOCAL 

if /I %PROCESSOR_ARCHITECTURE%==x86 goto win32
echo "win64!"
set PATH=%PATH%;ffmpeg\ffmpeg-git-9c2651a-win64-shared\bin
goto win64
:win32
echo "win32!"
set PATH=%PATH%;ffmpeg\ffmpeg-git-9c2651a-win32-shared\bin
:win64
ffmpeg %1 %2 %3 %4 %5 %6 %7 %8 %9
ENDLOCAL
