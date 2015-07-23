#include "ConfigSettings.h"

///
// Specify the different platform configuration in this file
//
// Platform parameters can be set in the following format:
// A. REGISTER_PLATFORM_CONFIG -> calls PlatformConfig(const char *name, int flowSupport, int defaultFlowSupport, const char *binFileExt, char fileSep)
// B. REGISTER_PLATFORM_CONFIG_WITH_SEARCH_STR -> calls PlatformConfig(const char *name, const char *searchStr, int flowSupport, int defaultFlowSupport, const char *binFileExt, char fileSep)

// ****************************************************************
// Below is the default settings for any platform (unless specified otherwise in another specific configuration line)
// Note that by default we have source and binary flows, source is the default flow, '.bcl' is the binary extension and '_' is the file separator
// ****************************************************************
REGISTER_PLATFORM_CONFIG(DEFAULT_PLATFORM_CONFIG_NAME, SOURCE_FLOW | BINARY_FLOW | DEFAULT_FLOW, SOURCE_FLOW, ".bcl", '_');
// ****************************************************************
// Altera OpenCL specific configuration
// ****************************************************************
REGISTER_PLATFORM_CONFIG(Altera, BINARY_FLOW | DEFAULT_FLOW, BINARY_FLOW, ".aocx",'.');
// if you need a search string different then name use this form instead, it includes a search string
//REGISTER_PLATFORM_CONFIG_WITH_SEARCH_STR(Altera, "Altera SDK for OpenCL", BINARY_FLOW | DEFAULT_FLOW, BINARY_FLOW, ".aocx",'.');
// ****************************************************************
// example for AMD ...
// ****************************************************************
REGISTER_PLATFORM_CONFIG(AMD,SOURCE_FLOW | BINARY_FLOW | DEFAULT_FLOW, SOURCE_FLOW, ".bcl", '.');
// ****************************************************************
// example for Intel ...
// ****************************************************************
REGISTER_PLATFORM_CONFIG(Intel,SOURCE_FLOW | BINARY_FLOW | DEFAULT_FLOW, SOURCE_FLOW, ".bcl", '.');

