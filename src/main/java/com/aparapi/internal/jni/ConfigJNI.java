/**
 * Copyright (c) 2016 - 2018 Syncleus, Inc.
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
    * Value defaults to com.codegen.config if not overridden by extending classes
    */
   protected static final String propPkgName = Config.class.getPackage().getName();

   /**
    * Allows the user to turn on OpenCL profiling for the JNI/OpenCL layer.
    * 
    * Usage -Dcom.codegen.enableProfiling={true|false}
    * 
    */
   @UsedByJNICode public static final boolean enableProfiling = Boolean.getBoolean(propPkgName + ".enableProfiling");

   /**
    * Allows the user to turn on OpenCL profiling for the JNI/OpenCL layer, this information will be written to CSV file
    * 
    * Usage -Dcom.codegen.enableProfiling={true|false}
    * 
    */
   @UsedByJNICode public static final boolean enableProfilingCSV = Boolean.getBoolean(propPkgName + ".enableProfilingCSV");

   /**
    * Allows the user to request that verbose JNI messages be dumped to stderr.
    * 
    * Usage -Dcom.codegen.enableVerboseJNI={true|false}
    * 
    */
   @UsedByJNICode public static final boolean enableVerboseJNI = Boolean.getBoolean(propPkgName + ".enableVerboseJNI");

   /**
    * Allows the user to request tracking of opencl resources.
    * 
    * This is really a debugging option to help locate leaking OpenCL resources, this will be dumped to stderr.
    * 
    * Usage -Dcom.codegen.enableOpenCLResourceTracking={true|false}
    * 
    */
   @UsedByJNICode public static final boolean enableVerboseJNIOpenCLResourceTracking = Boolean.getBoolean(propPkgName
         + ".enableVerboseJNIOpenCLResourceTracking");

}
