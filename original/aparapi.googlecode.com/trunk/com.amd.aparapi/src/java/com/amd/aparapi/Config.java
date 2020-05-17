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
package com.amd.aparapi;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amd.aparapi.KernelRunner.UsedByJNICode;

/**
 * A central location for holding all runtime configurable properties as well as logging configuration.
 * 
 * Ideally we will find all properties used by <code>Aparapi</code> here.  Please consider updating this class if you wish
 * to add new properties which control <code>Aparapi</code>s behavior. 
 *
 * @author gfrost
 *
 */
class Config{

   private static final String propPkgName = Config.class.getPackage().getName();

   /**
    * Allows the user to request to use a jvmti agent to access JNI code rather than loading explicitly.
    * 
    * Usage -agentpath=/full/path/to/agent.dll -Dcom.amd.aparapi.useAgent=true
    */

   static final boolean useAgent = Boolean.getBoolean(propPkgName + ".useAgent");

   static final boolean disableUnsafe = Boolean.getBoolean(propPkgName + ".disableUnsafe");

   /**
    * Allows the user to request a specific Kernel.EXECUTION_MODE enum value for all Kernels.
    * 
    *  Usage -Dcom.amd.aparapi.executionMode={SEQ|JTP|CPU|GPU}
    *  
    *  @see com.amd.aparapi.Kernel.EXECUTION_MODE
    */
   static final String executionMode = System.getProperty(propPkgName + ".executionMode");

   /**
    * Allows the user to turn on OpenCL profiling for the JNI/OpenCL layer.
    * 
    *  Usage -Dcom.amd.aparapi.enableProfiling={true|false}
    *  
    */
   @UsedByJNICode static final boolean enableProfiling = Boolean.getBoolean(propPkgName + ".enableProfiling");

   /**
    * Allows the user to turn on OpenCL profiling for the JNI/OpenCL layer, this information will be written to CSV file
    * 
    *  Usage -Dcom.amd.aparapi.enableProfiling={true|false}
    *  
    */
   @UsedByJNICode static final boolean enableProfilingCSV = Boolean.getBoolean(propPkgName + ".enableProfilingCSV");

   /**
    * Allows the user to request that verbose JNI messages be dumped to stderr.
    * 
    *  Usage -Dcom.amd.aparapi.enableVerboseJNI={true|false}
    *  
    */
   @UsedByJNICode static final boolean enableVerboseJNI = Boolean.getBoolean(propPkgName + ".enableVerboseJNI");

   /**
    * Allows the user to request tracking of opencl resources.  
    * 
    *  This is really a debugging option to help locate leaking OpenCL resources, this will be dumped to stderr.
    * 
    *  Usage -Dcom.amd.aparapi.enableOpenCLResourceTracking={true|false}
    *  
    */
   @UsedByJNICode static final boolean enableVerboseJNIOpenCLResourceTracking = Boolean.getBoolean(propPkgName
         + ".enableVerboseJNIOpenCLResourceTracking");

   /**
    * Allows the user to request that the execution mode of each kernel invocation be reported to stdout.
    * 
    *  Usage -Dcom.amd.aparapi.enableExecutionModeReporting={true|false}
    *  
    */
   static final boolean enableExecutionModeReporting = Boolean.getBoolean(propPkgName + ".enableExecutionModeReporting");

   /**
    * Allows the user to request that generated OpenCL code is dumped to standard out.
    * 
    *  Usage -Dcom.amd.aparapi.enableShowGeneratedOpenCL={true|false}
    *  
    */
   static final boolean enableShowGeneratedOpenCL = Boolean.getBoolean(propPkgName + ".enableShowGeneratedOpenCL");

   // Pragma/OpenCL codegen related flags
   static final boolean enableAtomic32 = Boolean.getBoolean(propPkgName + ".enableAtomic32");

   static final boolean enableAtomic64 = Boolean.getBoolean(propPkgName + ".enableAtomic64");

   static final boolean enableByteWrites = Boolean.getBoolean(propPkgName + ".enableByteWrites");

   public static final boolean enableDoubles = Boolean.getBoolean(propPkgName + ".enableDoubles");

   // Debugging related flags
   static final boolean verboseComparitor = Boolean.getBoolean(propPkgName + ".verboseComparitor");

   static final boolean dumpFlags = Boolean.getBoolean(propPkgName + ".dumpFlags");

   // Individual bytecode support related flags
   static final boolean enablePUTFIELD = Boolean.getBoolean(propPkgName + ".enable.PUTFIELD");

   static final boolean enableARETURN = !Boolean.getBoolean(propPkgName + ".disable.ARETURN");

   static final boolean enablePUTSTATIC = Boolean.getBoolean(propPkgName + ".enable.PUTSTATIC");

   // Allow static array accesses 
   static final boolean enableGETSTATIC = true; //Boolean.getBoolean(propPkgName + ".enable.GETSTATIC");

   static final boolean enableINVOKEINTERFACE = Boolean.getBoolean(propPkgName + ".enable.INVOKEINTERFACE");

   static final boolean enableMONITOR = Boolean.getBoolean(propPkgName + ".enable.MONITOR");

   static final boolean enableNEW = Boolean.getBoolean(propPkgName + ".enable.NEW");

   static final boolean enableATHROW = Boolean.getBoolean(propPkgName + ".enable.ATHROW");

   static final boolean enableMETHODARRAYPASSING = !Boolean.getBoolean(propPkgName + ".disable.METHODARRAYPASSING");

   static final boolean enableARRAYLENGTH = Boolean.getBoolean(propPkgName + ".enable.ARRAYLENGTH");

   static final boolean enableSWITCH = Boolean.getBoolean(propPkgName + ".enable.SWITCH");

   public static boolean enableShowFakeLocalVariableTable = Boolean.getBoolean(propPkgName + ".enableShowFakeLocalVariableTable");

   // Logging setup
   private static final String logPropName = propPkgName + ".logLevel";

   static String getLoggerName() {
      return logPropName;
   }

   static Logger logger = Logger.getLogger(Config.getLoggerName());
   static {

      try {

         Level level = Level.parse(System.getProperty(getLoggerName(), "WARNING"));

         Handler[] handlers = Logger.getLogger("").getHandlers();
         for (int index = 0; index < handlers.length; index++) {
            handlers[index].setLevel(level);
         }

         logger.setLevel(level);

      } catch (Exception e) {
         System.out.println("Exception " + e + " in Aparapi logging setup.");
         e.printStackTrace();
      }

   };

   public interface InstructionListener{
      void showAndTell(String message, Instruction _start, Instruction _instruction);
   }

   static final boolean enableInstructionDecodeViewer = Boolean.getBoolean(propPkgName + ".enableInstructionDecodeViewer");

   static String instructionListenerClassName = System.getProperty(propPkgName + ".instructionListenerClass");

   static public InstructionListener instructionListener = null;

   static {
      if (enableInstructionDecodeViewer && (instructionListenerClassName == null || instructionListenerClassName.equals(""))) {
         instructionListenerClassName = InstructionViewer.class.getName();
      }
      if (instructionListenerClassName != null && !instructionListenerClassName.equals("")) {
         try {
            Class<?> instructionListenerClass = Class.forName(instructionListenerClassName);
            instructionListener = (InstructionListener) instructionListenerClass.newInstance();
         } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
      if (dumpFlags) {

         System.out.println(propPkgName + ".executionMode{GPU|CPU|JTP|SEQ}=" + executionMode);
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
               + ".instructionListenerClassName{<class name which extends com.amd.aparapi.Config.InstructionListener>}="
               + instructionListenerClassName);

      }
   }

}
