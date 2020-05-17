setlocal 
call setvars.bat 

rm -r -f ^
  aparapi.exp ^
  aparapi.lib ^
  aparapi.obj ^
  aparapi_x86_64.dll ^
  aparapi_x86.dll ^
  arrayBuffer.obj ^
  clHelper.obj ^
  config.obj ^
  jniHelper.obj ^
  opencljni.obj ^
  profileInfo.obj ^
  include 

mkdir include

%LAMBDA_JAVA_HOME%\bin\javah ^
 -classpath aparapi.jar ^
 -d include ^
 -force  ^
 com.amd.aparapi.KernelRunner ^
 com.amd.aparapi.OpenCLJNI ^
 com.amd.aparapi.OpenCLArgDescriptor ^
 com.amd.aparapi.OpenCLMem 

if "%PROCESSOR_ARCHITECTURE%" == "x86" (
  "%MSVC_DIR%\vc\bin\cl.exe" ^
   "/nologo" ^
   "/TP" ^
   "/Ox" ^
   "/I%MSVC_DIR%\vc\include" ^
   "/I%MSVC_SDK_DIR%\include" ^
   "/I%LAMBDA_JAVA_HOME%\include" ^
   "/I%LAMBDA_JAVA_HOME%\include\win32" ^
   "/Iinclude" ^
   "/I%AMD_APP_SDK_DIR%\include" ^
   "src\cpp\aparapi.cpp" ^
   "src\cpp\config.cpp" ^
   "src\cpp\profileInfo.cpp" ^
   "src\cpp\arrayBuffer.cpp" ^
   "src\cpp\opencljni.cpp" ^
   "src\cpp\jniHelper.cpp" ^
   "src\cpp\clHelper.cpp" ^
   "/LD" ^
   "/link" ^
   "/libpath:%MSVC_DIR%\vc\lib" ^
   "/libpath:%MSVC_SDK_DIR%\lib" ^
   "/libpath:%AMD_APP_SDK_DIR%\lib\x86" ^
   "OpenCL.lib" ^
   "/out:aparapi_x86.dll" 
)
if not "%PROCESSOR_ARCHITECTURE%" == "x86" (
  "%MSVC_DIR%\vc\bin\amd64\cl.exe" ^
   "/nologo" ^
   "/TP" ^
   "/Ox" ^
   "/I%MSVC_DIR%\vc\include" ^
   "/I%MSVC_SDK_DIR%\include" ^
   "/I%LAMBDA_JAVA_HOME%\include" ^
   "/I%LAMBDA_JAVA_HOME%\include\win32" ^
   "/Iinclude" ^
   "/I%AMD_APP_SDK_DIR%\include" ^
   "src\cpp\aparapi.cpp" ^
   "src\cpp\config.cpp" ^
   "src\cpp\profileInfo.cpp" ^
   "src\cpp\arrayBuffer.cpp" ^
   "src\cpp\opencljni.cpp" ^
   "src\cpp\jniHelper.cpp" ^
   "src\cpp\clHelper.cpp" ^
   "/LD" ^
   "/link" ^
   "/libpath:%MSVC_DIR%\vc\lib\amd64" ^
   "/libpath:%MSVC_SDK_DIR%\lib\x64" ^
   "/libpath:%AMD_APP_SDK_DIR%\lib\x86_64" ^
   "OpenCL.lib" ^
   "/out:aparapi_x86_64.dll" 
)

endlocal 
