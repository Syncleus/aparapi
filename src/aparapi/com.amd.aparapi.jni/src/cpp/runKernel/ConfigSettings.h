#ifndef CONFIG_SETTINGS_H
#define CONFIG_SETTINGS_H

// !!! oren changes ->
// configuration settings for building platform specific code
// TODO: consider moving parts of this to a configuration file lateron and load settings dynamically

#include <string>
#include <cstring>
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

//#define FILE_EXT_LENGTH 8

class PlatformConfig
{
public:

    typedef std::shared_ptr<PlatformConfig> Ptr;

	PlatformConfig(const char *name, int flowSupport, int defaultFlowSupport, const char *binFileExt, char fileSep)
    {
		setName(name);
		setSearchStr(name); // default search string is name!
		setFlowSupport(flowSupport);
		setDefaultFlowSupport(defaultFlowSupport);
        setBinFileExtension(binFileExt);
		setFileSeperator(fileSep);
    }

	PlatformConfig(const char *name, const char *searchStr, int flowSupport, int defaultFlowSupport, const char *binFileExt, char fileSep)
    {
		setName(name);
		setSearchStr(searchStr);
		setFlowSupport(flowSupport);
		setDefaultFlowSupport(defaultFlowSupport);
        setBinFileExtension(binFileExt);
		setFileSeperator(fileSep);
    }

	void setName(const char *name)
	{
		m_name = name;
	}
	const char *getName()
	{
		return m_name.c_str();
	}

	void setSearchStr(const char *searchStr)
	{
		m_searchStr = searchStr;
	}

	const char *getsearchStr()
	{
		return m_searchStr.c_str();
	}

	void setFlowSupport(int flowSupport)
	{
		m_flowSupport = flowSupport;
	}

	int getFlowSupport()
	{
		return m_flowSupport;
	}

	void setDefaultFlowSupport(int defaultFlowSupport)
	{
		m_defaultFlowSupport = defaultFlowSupport;
	}

	int getDefaultFlowSupport()
	{
		return m_defaultFlowSupport;
	}

	void setBinFileExtension(const char *binFileExt)
	{
		m_binFileExt = binFileExt;
	}

	const char *getBinFileExtension()
	{
		return m_binFileExt.c_str();
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
	int m_defaultFlowSupport;
	//char m_fileExt[FILE_EXT_LENGTH];
	std::string m_binFileExt;
	char m_fileSep;
	std::string m_name;
	std::string m_searchStr; // platform name search string
};

class PlatformConfigFactory
{
public:
	  //typedef std::shared_ptr<PlatformConfig> PlatformConfigPtr;
	  //typedef std::pair<std::string,PlatformConfigPtr> PlatformConfigTuple;
	  //typedef std::map<std::string,PlatformConfigTuple> PlatformConfigMap;
	  typedef std::map<std::string,PlatformConfig::Ptr> PlatformConfigMap;

	  static PlatformConfigFactory &getPlatformConfigFactory()//openclManager *oclMgr)
	  {
		  static PlatformConfigFactory *pcf;
		  if(pcf==NULL)
			  pcf = new PlatformConfigFactory();
		  return *pcf;
	  }

	  bool registerPlatformConfig(const char *name, PlatformConfig::Ptr platformConfigPtr)
	  {
		  //m_platformConfigMap[name]=PlatformConfigTuple(name,platformConfigPtr);
		  m_platformConfigMap[name]=platformConfigPtr;
		  return true;
	  }

#define REGISTER_PLATFORM_CONFIG_BASE(name,platformConfigPtr) bool name##PlatformConfig=PlatformConfigFactory::getPlatformConfigFactory().registerPlatformConfig(#name,PlatformConfig::Ptr(platformConfigPtr));
#define REGISTER_PLATFORM_CONFIG(name,flowSupport,defaultFlowSupport,binFileExt,fileSep) REGISTER_PLATFORM_CONFIG_BASE(name, new PlatformConfig(#name,flowSupport,defaultFlowSupport,binFileExt,fileSep))
#define REGISTER_PLATFORM_CONFIG_WITH_SEARCH_STR(name,searchStr,flowSupport,defaultFlowSupport,binFileExt,fileSep) REGISTER_PLATFORM_CONFIG_BASE(name, new PlatformConfig(#name,searchStr,flowSupport,defaultFlowSupport,binFileExt,fileSep))

#define DEFAULT_PLATFORM_CONFIG_NAME DEFAULT_PCN
#define NAME_TO_STR(s) #s
#define DEFINE_NAME_TO_STR(s) NAME_TO_STR(s)

	  PlatformConfig::Ptr findPlatformConfigByName(const char *name)
	  {
		  printf("findPlatformConfigByName: %s\n",name);
		  PlatformConfigMap::iterator itr = m_platformConfigMap.find(name);
		          if (itr != m_platformConfigMap.end())
		          {
		              //return itr->second.second;
		              return itr->second;
		          }
		          else
		        	  return PlatformConfig::Ptr();
	  }

	  PlatformConfig::Ptr findPlatformConfigFromFullName(const char *fullPlatformName)
	  {
		  printf("findPlatformConfigFromFullName: %s\n",fullPlatformName);
		  // Requires C++11 -> leave minimum compiler support at C++0x for now ...
		  //for ( const auto &itr : m_platformConfigMap )
		  for (PlatformConfigMap::iterator itr = m_platformConfigMap.begin(); itr != m_platformConfigMap.end(); itr++ )
		  {
		     if(std::strstr(fullPlatformName,itr->second->getsearchStr()))
		    	 return itr->second;
		  }

		  // if not found search for default cplatform config
    	  return  findPlatformConfigByName(DEFINE_NAME_TO_STR(DEFAULT_PLATFORM_CONFIG_NAME));
	  }

	  PlatformConfigMap &getConfigMap()
	  {
         return m_platformConfigMap;
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
/*
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
*/
#endif // CONFIG_SETTINGS_H


