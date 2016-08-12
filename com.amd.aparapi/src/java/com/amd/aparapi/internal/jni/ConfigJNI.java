package com.amd.aparapi.internal.jni;

import com.amd.aparapi.Config;
import com.amd.aparapi.internal.annotation.UsedByJNICode;

/**
 * This class is intended to be used as a 'proxy' or 'facade' object for Java code to interact with JNI
 */
public abstract class ConfigJNI{

   /**
    * Value defaults to com.amd.aparapi.config if not overridden by extending classes
    */
   protected static final String propPkgName = Config.class.getPackage().getName();

   /**
    * Allows the user to turn on OpenCL profiling for the JNI/OpenCL layer.
    * 
    * Usage -Dcom.amd.aparapi.enableProfiling={true|false}
    * 
    */
   @UsedByJNICode public static final boolean enableProfiling = Boolean.getBoolean(propPkgName + ".enableProfiling");

   /**
    * Allows the user to turn on OpenCL profiling for the JNI/OpenCL layer, this information will be written to CSV file
    * 
    * Usage -Dcom.amd.aparapi.enableProfiling={true|false}
    * 
    */
   @UsedByJNICode public static final boolean enableProfilingCSV = Boolean.getBoolean(propPkgName + ".enableProfilingCSV");

   /**
    * Allows the user to request that verbose JNI messages be dumped to stderr.
    * 
    * Usage -Dcom.amd.aparapi.enableVerboseJNI={true|false}
    * 
    */
   @UsedByJNICode public static final boolean enableVerboseJNI = Boolean.getBoolean(propPkgName + ".enableVerboseJNI");

   /**
    * Allows the user to request tracking of opencl resources.
    * 
    * This is really a debugging option to help locate leaking OpenCL resources, this will be dumped to stderr.
    * 
    * Usage -Dcom.amd.aparapi.enableOpenCLResourceTracking={true|false}
    * 
    */
   @UsedByJNICode public static final boolean enableVerboseJNIOpenCLResourceTracking = Boolean.getBoolean(propPkgName
         + ".enableVerboseJNIOpenCLResourceTracking");

}
