#ifndef CONFIG_SETTINGS_H
#define CONFIG_SETTINGS_H

// !!! oren changes ->
// configuration settings for building platform specific code
// TODO: consider moving parts of this to a configuration file later on and load settings dynamically

#include <string>
#include <map>
#include <tuple>
#include <memory>

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

#define FILE_EXT_LENGTH 8

class PlatformConfig
{
public:
	PlatformConfig(int flowSupport, const char *fileExt, char fileSep)
    {
		setFlowSupport(flowSupport);
        setFileExtension(fileExt);
		setFileSeperator(fileSep);
    }

	void setFlowSupport(int flowSupport)
	{
		m_flowSupport = flowSupport;
	}

	int getFlowSupport()
	{
		return m_flowSupport;
	}

	void setFileExtension(const char *fileExt)
	{
		m_fileExt = fileExt;
	}

	const char *getFileExtension()
	{
		return m_fileExt.c_str();
	}

	void setFileSeperator(char fileSep)
	{
		m_fileSep = fileSep;
	}

	char getFileSeperator()
	{
		return m_fileSep;
	}

protected:
	// data
	int m_flowSupport;
	//char m_fileExt[FILE_EXT_LENGTH];
	std::string m_fileExt;
	char m_fileSep;
};

class PlatformConfigFactory
{
public:
	  typedef std::shared_ptr<PlatformConfig> PlatformConfigPtr;
	  typedef std::pair<std::string,PlatformConfigPtr> PlatformConfigTuple;
	  typedef std::map<std::string,PlatformConfigTuple> PlatformConfigMap;

	  static PlatformConfigFactory &getPlatformConfigFactory()//openclManager *oclMgr)
	  {
		  static PlatformConfigFactory *pcf;
		  if(pcf==NULL)
			  pcf = new PlatformConfigFactory();
		  return *pcf;
	  }

	  bool registerPlatformConfig(const char *name, PlatformConfigPtr platformConfigPtr)
	  {
		  m_platformConfigMap[name]=PlatformConfigTuple(name,platformConfigPtr);
		  return true;
	  }

#define REGISTER_PLLATFORM_CONFIG(name,platformConfigPtr) bool name##PlatformConfig=PlatformConfigFactory::getPlatformConfigFactory().register(#name,platformConfigPtr);


	  PlatformConfigPtr findPlatformConfigByName(const char *name)
	  {
		  PlatformConfigMap::iterator itr = m_platformConfigMap.find(name);
		          if (itr != m_platformConfigMap.end())
		          {
		              return itr->second.second;
		          }
		          else
		        	  return PlatformConfigPtr();
	  }

	  // data
	  PlatformConfigMap m_platformConfigMap;



};

///////////////////////////
// define platform settings
//////////////////////////
// BINARY_FILE_EXT => define binary file extension
// BINARY_FILE_SEP => define binary file separator, replaces java's $ signs in file names -> examples: .,_ etc.
///////////////////////////
// Altera platform specific
///////////////////////////
#ifdef ALTERA_OPENCL
  #define PLATFORM_FLOW_SUPPORT (SOURCE_FLOW | BINARY_FLOW | DEFAULT_FLOW)
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
  #define PLATFORM_FLOW_SUPPORT (SOURCE_FLOW | BINARY_FLOW | DEFAULT_FLOW)
  #define PLATFORM_DEFAULT_FLOW SOURCE_FLOW
  #define BINARY_FILE_EXT ".bcl"
  #define BINARY_FILE_SEP '.'
#endif // ALTERA_OPENCL

#endif // CONFIG_SETTINGS_H


