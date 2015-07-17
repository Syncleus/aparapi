#ifndef CONFIG_SETTINGS_H
#define CONFIG_SETTINGS_H

// !!! oren changes ->
// configuration settings for building platform specific code
// TODO: consider moving parts of this to a configuration file later on and load settings dynamically

// use values from JNI config
#include "com_amd_aparapi_internal_jni_KernelRunnerJNI.h"


// auto output kernel.cl file
#define OUTPUT_OCL_FILE
// allows defining an alternative folder where bin files should be loaded from
// Useful when running in Aparapi embedded mode
#define BINARY_FOLDER_ENV_VAR "APARAPI_CL_BIN_FOLDER"

///////////////////////////
// help determine if platform supports source/binary flows
///////////////////////////
#define SOURCE_FLOW   com_amd_aparapi_internal_jni_KernelRunnerJNI_JNI_FLAG_SOURCE_FLOW
#define BINARY_FLOW   com_amd_aparapi_internal_jni_KernelRunnerJNI_JNI_FLAG_BINARY_FLOW
#define DEFAULT_FLOW  com_amd_aparapi_internal_jni_KernelRunnerJNI_JNI_FLAG_DEFAULT_FLOW
///////////////////////////


///////////////////////////
// define platform settings
//////////////////////////
// BINARY_FILE_EXT => define binary file extension
// BINARY_FILE_SEP => define binary file separator, replaces java's $ signs in file names -> examples: .,_ etc.
///////////////////////////
// Altera platform specific
///////////////////////////
#ifdef ALTERA_OPENCL
  #define PLATFORM_FLOW_SUPPORT BINARY_FLOW
  #define PLATFORM_DEFAULT_FLOW BINARY_FLOW
  #define BINARY_FILE_EXT ".aocx"
  #define BINARY_FILE_SEP '.'
#elif AMD_OPENCL
// AMD specific
#elif INTEL_OPENCL
// Intel specific
#elif NVIDIA_OPENCL
// NVidia specific
#else // default settings
///////////////////////////
// All other platforms - set the default for other platforms
///////////////////////////
  #define PLATFORM_FLOW_SUPPORT (BINARY_FLOW | SOURCE_FLOW)
  #define PLATFORM_DEFAULT_FLOW SOURCE_FLOW
  #define BINARY_FILE_EXT ".bcl"
  #define BINARY_FILE_SEP '.'
#endif // ALTERA_OPENCL

#endif // CONFIG_SETTINGS_H


