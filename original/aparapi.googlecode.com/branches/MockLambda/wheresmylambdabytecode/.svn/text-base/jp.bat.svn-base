setlocal 
call ..\setvars.bat
%LAMBDA_JAVA_HOME%\bin\javap > jphelp

%LAMBDA_JAVA_HOME%\bin\javap ^
   -classpath classes ^
   -c -v -p  ^
   Main > Main.bytecode

%LAMBDA_JAVA_HOME%\bin\javap ^
   -classpath classes ^
   -c  -v -p ^
   Aparapi > Aparapi.bytecode

%LAMBDA_JAVA_HOME%\bin\javap ^
   -classpath classes ^
   -c  -v -p ^
   Aparapi$SAM > Aparapi$SAM.bytecode
   
endlocal 

