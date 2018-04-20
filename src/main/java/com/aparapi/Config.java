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
/*
Copyright (c) 2010-2011, Advanced Micro Devices, Inc.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following
disclaimer. 

Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
disclaimer in the documentation and/or other materials provided with the distribution. 

Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products
derived from this software without specific prior written permission. 

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

If you use the software (in whole or in part), you shall adhere to all applicable U.S., European, and other export
laws, including but not limited to the U.S. Export Administration Regulations ("EAR"), (15 C.F.R. Sections 730 through
774), and E.U. Council Regulation (EC) No 1334/2000 of 22 June 2000.  Further, pursuant to Section 740.6 of the EAR,
you hereby certify that, except pursuant to a license granted by the United States Department of Commerce Bureau of 
Industry and Security or as otherwise permitted pursuant to a License Exception under the U.S. Export Administration 
Regulations ("EAR"), you will not (1) export, re-export or release to a national of a country in Country Groups D:1,
E:1 or E:2 any restricted technology, software, or source code you receive hereunder, or (2) export to Country Groups
D:1, E:1 or E:2 the direct product of such technology or software, if such foreign produced direct product is subject
to national security controls as identified on the Commerce Control List (currently found in Supplement 1 to Part 774
of EAR).  For the most current Country Group listings, or for additional information about the EAR or your obligations
under those regulations, please refer to the U.S. Bureau of Industry and Security's website at http://www.bis.doc.gov/. 

 */
package com.aparapi;

import com.aparapi.internal.instruction.*;
import com.aparapi.internal.jni.*;
import com.aparapi.internal.tool.*;

import java.util.logging.*;

/**
 * A central location for holding all runtime configurable properties as well as logging configuration.
 * 
 * Ideally we will find all properties used by <code>Aparapi</code> here. Please consider updating this class if you wish
 * to add new properties which control <code>Aparapi</code>s behavior.
 * 
 * @author gfrost
 * 
 */
public class Config extends ConfigJNI{

   // Logging setup
   private static final String logPropName = propPkgName + ".logLevel";

   private static final Logger logger = Logger.getLogger(Config.getLoggerName());

   /**
    * Allows the user to request to use a jvmti agent to 
    * access JNI code rather than loading explicitly.
    * 
    * Usage -agentpath=/full/path/to/agent.dll -Dcom.codegen.useAgent=true
    */

   public static final boolean useAgent = Boolean.getBoolean(propPkgName + ".useAgent");

   /**
    * Disable Unsafe
    */
   public static final boolean disableUnsafe = Boolean.getBoolean(propPkgName + ".disableUnsafe");

   /**
    * Allows the user to request a specific Kernel.EXECUTION_MODE enum value for all Kernels.
    *
    *  Usage -Dcom.codegen.executionMode={SEQ|JTP|CPU|GPU|ACC}
    *  
    *  @see com.aparapi.Kernel.EXECUTION_MODE
    */
   public static final String executionMode = System.getProperty(propPkgName + ".executionMode");

   /**
    * Allows the user to request that the execution mode of each kernel invocation be reported to stdout.
    *
    *  Usage -Dcom.codegen.enableExecutionModeReporting={true|false}
    *  
    */
   public static final boolean enableExecutionModeReporting = Boolean.getBoolean(propPkgName + ".enableExecutionModeReporting");

   /**
    * Allows the user to request that generated OpenCL code is dumped to standard out.
    *
    *  Usage -Dcom.codegen.enableShowGeneratedOpenCL={true|false}
    *  
    */
   public static final boolean enableShowGeneratedOpenCL = Boolean.getBoolean(propPkgName + ".enableShowGeneratedOpenCL");
   
   /**
    * Upon exiting the JVM, dumps kernel profiling info to standard out.
    *
    *  Usage -Dcom.codegen.dumpProfilesOnExit={true|false}
    *  
    */
   public static final boolean dumpProfilesOnExit = Boolean.getBoolean(propPkgName + ".dumpProfilesOnExit");

   /**
    * Dumps profiling info (for a single execution) after every Kernel execution.
    *
    *  Usage -Dcom.codegen.dumpProfileOnExecution={true|false}
    *
    */
   public static final boolean dumpProfileOnExecution = Boolean.getBoolean(propPkgName + ".dumpProfileOnExecution");

   // Pragma/OpenCL codegen related flags
   public static final boolean enableAtomic32 = Boolean.getBoolean(propPkgName + ".enableAtomic32");

   public static final boolean enableAtomic64 = Boolean.getBoolean(propPkgName + ".enableAtomic64");

   public static final boolean enableByteWrites = Boolean.getBoolean(propPkgName + ".enableByteWrites");

   public static final boolean enableDoubles = Boolean.getBoolean(propPkgName + ".enableDoubles");

   // Debugging related flags
   public static final boolean verboseComparitor = Boolean.getBoolean(propPkgName + ".verboseComparitor");

   public static final boolean dumpFlags = Boolean.getBoolean(propPkgName + ".dumpFlags");

   // Individual bytecode support related flags
   public static final boolean enablePUTFIELD = Boolean.getBoolean(propPkgName + ".enable.PUTFIELD");

   public static final boolean enableARETURN = !Boolean.getBoolean(propPkgName + ".disable.ARETURN");

   public static final boolean enablePUTSTATIC = Boolean.getBoolean(propPkgName + ".enable.PUTSTATIC");

   // Allow static array accesses 
   public static final boolean enableGETSTATIC = true; //Boolean.getBoolean(propPkgName + ".enable.GETSTATIC");

   public static final boolean enableINVOKEINTERFACE = Boolean.getBoolean(propPkgName + ".enable.INVOKEINTERFACE");

   public static final boolean enableMONITOR = Boolean.getBoolean(propPkgName + ".enable.MONITOR");

   public static final boolean enableARRAY = !Boolean.getBoolean(propPkgName + ".disable.ARRAY");

   public static final boolean enableNEW = Boolean.getBoolean(propPkgName + ".enable.NEW");

   public static final boolean enableATHROW = Boolean.getBoolean(propPkgName + ".enable.ATHROW");

   public static final boolean enableMETHODARRAYPASSING = !Boolean.getBoolean(propPkgName + ".disable.METHODARRAYPASSING");

   public static final boolean enableARRAYLENGTH = Boolean.getBoolean(propPkgName + ".enable.ARRAYLENGTH");

   public static final boolean enableSWITCH = Boolean.getBoolean(propPkgName + ".enable.SWITCH");

   public static boolean enableShowFakeLocalVariableTable = Boolean.getBoolean(propPkgName + ".enableShowFakeLocalVariableTable");

   public static final boolean enableInstructionDecodeViewer = Boolean.getBoolean(propPkgName + ".enableInstructionDecodeViewer");

   public static String instructionListenerClassName = System.getProperty(propPkgName + ".instructionListenerClass");

   public static InstructionListener instructionListener = null;

   public interface InstructionListener{
      void showAndTell(String message, Instruction _start, Instruction _instruction);
   }

   static {
      try {
         final Level level = Level.parse(System.getProperty(getLoggerName(), "WARNING"));

         final Handler[] handlers = Logger.getLogger("").getHandlers();
         for (final Handler handler : handlers) {
            handler.setLevel(level);
         }

         logger.setLevel(level);
      } catch (final Exception e) {
         System.out.println("Exception " + e + " in Aparapi logging setup");
         e.printStackTrace();
      }
   };

   static {
      if (enableInstructionDecodeViewer && ((instructionListenerClassName == null) || instructionListenerClassName.equals(""))) {
         instructionListenerClassName = InstructionViewer.class.getName();
      }

      if ((instructionListenerClassName != null) && !instructionListenerClassName.equals("")) {
         try {
            final Class<?> instructionListenerClass = Class.forName(instructionListenerClassName);
            instructionListener = (InstructionListener) instructionListenerClass.newInstance();
         } catch (final ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         } catch (final InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         } catch (final IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }

      if (dumpFlags) {
         System.out.println(propPkgName + ".executionMode{GPU|ACC|CPU|JTP|SEQ}=" + executionMode);
         System.out.println(propPkgName + ".logLevel{OFF|FINEST|FINER|FINE|WARNING|SEVERE|ALL}=" + logger.getLevel());
         System.out.println(propPkgName + ".enableProfiling{true|false}=" + enableProfiling);
         System.out.println(propPkgName + ".enableProfilingCSV{true|false}=" + enableProfilingCSV);
         System.out.println(propPkgName + ".enableVerboseJNI{true|false}=" + enableVerboseJNI);
         System.out.println(propPkgName + ".enableVerboseJNIOpenCLResourceTracking{true|false}="
               + enableVerboseJNIOpenCLResourceTracking);
         System.out.println(propPkgName + ".enableShowGeneratedOpenCL{true|false}=" + enableShowGeneratedOpenCL);
         System.out.println(propPkgName + ".enableExecutionModeReporting{true|false}=" + enableExecutionModeReporting);
         System.out.println(propPkgName + ".enableInstructionDecodeViewer{true|false}=" + enableInstructionDecodeViewer);
         System.out.println(propPkgName
               + ".instructionListenerClassName{<class name which extends com.codegen.Config.InstructionListener>}="
               + instructionListenerClassName);
      }
   }

   public static String getLoggerName() {
      return logPropName;
   }
}
