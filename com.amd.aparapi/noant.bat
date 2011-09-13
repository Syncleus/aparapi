echo off
setlocal 
echo removing classes and include directories
rm -rf classes
rm -rf include
echo removing aparapi.jar from previous builds
rm -rf aparapi.jar

set ARCH=x86
set ARCH=x86_64
set SUBDIR=amd64/
set SUBDIR2=x64/
set HERE=%~dp0
set CC_HOME=C:\Program Files\Microsoft Visual Studio 10.0\vc
set CC_SDK_HOME=C:\Program Files\Microsoft SDKs\Windows\v7.0A
set CC_IDE_HOME=C:\Program Files\Microsoft Visual Studio 10.0\Common7\IDE

set CC_HOME=c:\progra~2\micros~1.0\VC
set CC_IDE_HOME=c:\progra~2\micros~1.0\Common7\IDE
set CC_SDK_HOME=c:\progra~1\mia713~1\windows\v6.0a


for %%X in ("%HERE%") do set HERE=%%~sX
for %%X in ("%JAVA_HOME%") do set JAVA_HOME=%%~sX
for %%X in ("%ATISTREAMSDKROOT%") do set ATISTREAMSDKROOT=%%~sX
for %%X in ("%CC_HOME%") do set CC_HOME=%%~sX
for %%X in ("%CC_SDK_HOME%") do set CC_SDK_HOME=%%~sX
for %%X in ("%CC_IDE_HOME%") do set CC_IDE_HOME=%%~sX


set PATH=%PATH%;%CC_IDE_HOME%

set SRC=

set SRC=%SRC% src\java\com\amd\aparapi\classtools\BranchSet.java
set SRC=%SRC% src\java\com\amd\aparapi\classtools\ByteBuffer.java
set SRC=%SRC% src\java\com\amd\aparapi\classtools\ByteReader.java
set SRC=%SRC% src\java\com\amd\aparapi\classtools\ClassModel.java
set SRC=%SRC% src\java\com\amd\aparapi\classtools\ClassParseException.java
set SRC=%SRC% src\java\com\amd\aparapi\classtools\ExpressionList.java
set SRC=%SRC% src\java\com\amd\aparapi\classtools\Instruction.java
set SRC=%SRC% src\java\com\amd\aparapi\classtools\InstructionPattern.java
set SRC=%SRC% src\java\com\amd\aparapi\classtools\InstructionSet.java
set SRC=%SRC% src\java\com\amd\aparapi\classtools\MethodModel.java
set SRC=%SRC% src\java\com\amd\aparapi\classtools\writer\BlockWriter.java
set SRC=%SRC% src\java\com\amd\aparapi\classtools\writer\CodeGenException.java
set SRC=%SRC% src\java\com\amd\aparapi\classtools\writer\InstructionHelper.java
set SRC=%SRC% src\java\com\amd\aparapi\classtools\writer\JavaWriter.java
set SRC=%SRC% src\java\com\amd\aparapi\classtools\writer\KernelWriter.java
set SRC=%SRC% src\java\com\amd\aparapi\classtools\writer\StringWriter.java
set SRC=%SRC% src\java\com\amd\aparapi\classtools\writer\Table.java
set SRC=%SRC% src\java\com\amd\aparapi\Config.java
set SRC=%SRC% src\java\com\amd\aparapi\Kernel.java
set SRC=%SRC% src\java\com\amd\aparapi\KernelRunner.java
set SRC=%SRC% src\java\com\amd\aparapi\KernelUtil.java
set SRC=%SRC% src\java\com\amd\aparapi\tools\CodeGen.java
set SRC=%SRC% src\java\com\amd\aparapi\tools\Info.java
 
echo Compiling java to classes
mkdir classes
%JAVA_HOME%\bin\javac -g -d classes -sourcepath src\java %SRC%
echo Jaring classes to aparapi.jar
%JAVA_HOME%\bin\jar -cf aparapi.jar -C classes com
echo Creating JNI headers for com.amd.aparapi.KernelRunner in include directory
mkdir include
%JAVA_HOME%\bin\javah -classpath aparapi.jar -d include -force com.amd.aparapi.KernelRunner

set INCS=
set INCS=%INCS% /I%CC_HOME%\include
set INCS=%INCS% /I%CC_SDK_HOME%\include
set INCS=%INCS% /I%JAVA_HOME%\include
set INCS=%INCS% /I%JAVA_HOME%\include\win32
set INCS=%INCS% /I%HERE%\include
set INCS=%INCS% /I%ATISTREAMSDKROOT%\include

echo Compiling aparapi.cpp to aparapi.obj
%CC_HOME%\bin\%SUBDIR%cl /nologo /TP /Ox %INCS% /c src\cpp\aparapi.cpp

set LIBS=
set LIBS=%LIBS%  /LIBPATH:%CC_HOME%\lib\%SUBDIR%
set LIBS=%LIBS%  /LIBPATH:%CC_SDK_HOME%\lib\%SUBDIR2%
set LIBS=%LIBS%  /LIBPATH:%ATISTREAMSDKROOT%\lib\%ARCH%
echo Creating aparapi.dll from aparapi.obj
%CC_HOME%\bin\cl /nologo /LD aparapi.obj /link %LIBS% OpenCL.lib /OUT:aparapi.dll

echo Done
endlocal

