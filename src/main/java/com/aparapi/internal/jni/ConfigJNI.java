/**
 * Copyright (c) 2016 - 2017 Syncleus, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aparapi.internal.jni;

import com.aparapi.Config;
import com.aparapi.internal.annotation.UsedByJNICode;

/**
 * This class is intended to be used as a 'proxy' or 'facade' object for Java code to interact with JNI
 */
public abstract class ConfigJNI{

   /**
    * Value defaults to com.aparapi.config if not overridden by extending classes
    */
   protected static final String propPkgName = Config.class.getPackage().getName();

   /**
    * Allows the user to turn on OpenCL profiling for the JNI/OpenCL layer.
    * 
    * Usage -Dcom.aparapi.enableProfiling={true|false}
    * 
    */
   @UsedByJNICode public static final boolean enableProfiling = Boolean.getBoolean(propPkgName + ".enableProfiling");

   /**
    * Allows the user to turn on OpenCL profiling for the JNI/OpenCL layer, this information will be written to CSV file
    * 
    * Usage -Dcom.aparapi.enableProfiling={true|false}
    * 
    */
   @UsedByJNICode public static final boolean enableProfilingCSV = Boolean.getBoolean(propPkgName + ".enableProfilingCSV");

   //!!! oren change 2.15.19 -> Allows the user to set profile file name format 
   /**
    * Allows the user to set profile file name format 
    * 
    * Usage -Dcom.amd.aparapi.profilingFileNameFormatStr={format string}
    * 
    */
   @UsedByJNICode public static final String profilingFileNameFormatStr =  System.getProperty(propPkgName + ".profilingFileNameFormatStr");

   /**
    * Allows the user to request that verbose JNI messages be dumped to stderr.
    * 
    * Usage -Dcom.aparapi.enableVerboseJNI={true|false}
    * 
    */
   @UsedByJNICode public static final boolean enableVerboseJNI = Boolean.getBoolean(propPkgName + ".enableVerboseJNI");

   /**
    * Allows the user to request tracking of opencl resources.
    * 
    * This is really a debugging option to help locate leaking OpenCL resources, this will be dumped to stderr.
    * 
    * Usage -Dcom.aparapi.enableOpenCLResourceTracking={true|false}
    * 
    */
   @UsedByJNICode public static final boolean enableVerboseJNIOpenCLResourceTracking = Boolean.getBoolean(propPkgName
         + ".enableVerboseJNIOpenCLResourceTracking");

}
