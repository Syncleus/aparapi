setlocal 
call ..\setvars.bat

rm -r -f classes 

mkdir classes

%LAMBDA_JAVA_HOME%\bin\javac ^
 -XDlambdaToMethod ^
 -g ^
 -d classes ^
 Main.java ^
 Aparapi.java 

endlocal 
