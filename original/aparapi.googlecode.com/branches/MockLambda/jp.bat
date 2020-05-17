setlocal 
call setvars.bat
%LAMBDA_JAVA_HOME%\bin\javap ^
   -classpath aparapi.jar;samples.jar ^
   -c -p -v ^
   com.amd.aparapi.samples.%1 %2 %3 %4 %6 %7 %8 %9 
endlocal 

