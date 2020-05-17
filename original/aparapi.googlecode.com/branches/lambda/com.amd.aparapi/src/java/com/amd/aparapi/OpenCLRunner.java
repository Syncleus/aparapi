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

import com.amd.aparapi.Annotations.UsedByJNICode;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The class is responsible for executing <code>Kernel</code> implementations. <br/>
 * <p/>
 * The <code>KernelRunner</code> is the real workhorse for Aparapi.  Each <code>Kernel</code> instance creates a single
 * <code>KernelRunner</code> to encapsulate state and to help coordinate interactions between the <code>Kernel</code>
 * and it's execution logic.<br/>
 * <p/>
 * The <code>KernelRunner</code> is created <i>lazily</i> as a result of calling <code>Kernel.execute()</code>. OREF this
 * time the <code>ExecutionMode</code> is consulted to determine the default requested mode.  This will dictate how
 * the <code>KernelRunner</code> will attempt to execute the <code>Kernel</code>
 *
 * @author gfrost
 * @see Kernel#execute(int _globalSize)
 */
class OpenCLRunner{


   protected static Logger logger = Logger.getLogger(Config.getLoggerName());

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> represents a <code>boolean</code> prefix (array or primitive).
    *
    * @author gfrost
    * @see com.amd.aparapi.Annotations.UsedByJNICode
    * @see com.amd.aparapi.OpenCLRunner.KernelArg
    */
   @UsedByJNICode
   public static final int ARG_BOOLEAN = 1 << 0;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> represents a <code>byte</code> prefix (array or primitive).
    *
    * @author gfrost
    * @see com.amd.aparapi.Annotations.UsedByJNICode
    * @see com.amd.aparapi.OpenCLRunner.KernelArg
    */
   @UsedByJNICode
   public static final int ARG_BYTE = 1 << 1;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> represents a <code>float</code> prefix (array or primitive).
    *
    * @author gfrost
    * @see com.amd.aparapi.Annotations.UsedByJNICode
    * @see com.amd.aparapi.OpenCLRunner.KernelArg
    */
   @UsedByJNICode
   public static final int ARG_FLOAT = 1 << 2;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> represents a <code>int</code> prefix (array or primitive).
    *
    * @author gfrost
    * @see com.amd.aparapi.Annotations.UsedByJNICode
    * @see com.amd.aparapi.OpenCLRunner.KernelArg
    */
   @UsedByJNICode
   public static final int ARG_INT = 1 << 3;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> represents a <code>double</code> prefix (array or primitive).
    *
    * @author gfrost
    * @see com.amd.aparapi.Annotations.UsedByJNICode
    * @see com.amd.aparapi.OpenCLRunner.KernelArg
    */
   @UsedByJNICode
   public static final int ARG_DOUBLE = 1 << 4;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> represents a <code>long</code> prefix (array or primitive).
    *
    * @author gfrost
    * @see com.amd.aparapi.Annotations.UsedByJNICode
    * @see com.amd.aparapi.OpenCLRunner.KernelArg
    */
   @UsedByJNICode
   public static final int ARG_LONG = 1 << 5;

   /**
    * TODO:
    *
    * @author gfrost
    * @see com.amd.aparapi.Annotations.UsedByJNICode
    * @see com.amd.aparapi.OpenCLRunner.KernelArg
    */
   @UsedByJNICode
   public static final int ARG_SHORT = 1 << 6;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> represents an array.<br/>
    * So <code>ARG_ARRAY|ARG_INT</code> tells us this arg is an array of <code>int</code>.
    *
    * @author gfrost
    * @see com.amd.aparapi.Annotations.UsedByJNICode
    * @see com.amd.aparapi.OpenCLRunner.KernelArg
    */
   @UsedByJNICode
   public static final int ARG_ARRAY = 1 << 7;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> represents a primitive (non array).<br/>
    * So <code>ARG_PRIMITIVE|ARG_INT</code> tells us this arg is a primitive <code>int</code>.
    *
    * @author gfrost
    * @see com.amd.aparapi.Annotations.UsedByJNICode
    * @see com.amd.aparapi.OpenCLRunner.KernelArg
    */
   @UsedByJNICode
   public static final int ARG_PRIMITIVE = 1 << 8;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> is read by the Kernel (note from the Kernel's point of view).<br/>
    * So <code>ARG_ARRAY|ARG_INT|ARG_READ</code> tells us this arg is an array of int's that are read by the kernel.
    *
    * @author gfrost
    * @see com.amd.aparapi.Annotations.UsedByJNICode
    * @see com.amd.aparapi.OpenCLRunner.KernelArg
    */
   @UsedByJNICode
   public static final int ARG_READ = 1 << 9;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> is mutated by the Kernel (note from the Kernel's point of view).<br/>
    * So <code>ARG_ARRAY|ARG_INT|ARG_WRITE</code> tells us this arg is an array of int's that we expect the kernel to mutate.
    *
    * @author gfrost
    * @see com.amd.aparapi.Annotations.UsedByJNICode
    * @see com.amd.aparapi.OpenCLRunner.KernelArg
    */
   @UsedByJNICode
   public static final int ARG_WRITE = 1 << 10;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> resides in local memory in the generated OpenCL code.<br/>
    *
    * @author gfrost
    * @see com.amd.aparapi.Annotations.UsedByJNICode
    * @see com.amd.aparapi.Annotations.Experimental
    * @see com.amd.aparapi.OpenCLRunner.KernelArg
    */
   @Annotations.Experimental
   @UsedByJNICode
   public static final int ARG_LOCAL = 1 << 11;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> resides in global memory in the generated OpenCL code.<br/>
    *
    * @author gfrost
    * @see com.amd.aparapi.Annotations.UsedByJNICode
    * @see com.amd.aparapi.Annotations.Experimental
    * @see com.amd.aparapi.OpenCLRunner.KernelArg
    */
   @Annotations.Experimental
   @UsedByJNICode
   public static final int ARG_GLOBAL = 1 << 12;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> resides in constant memory in the generated OpenCL code.<br/>
    *
    * @author gfrost
    * @see com.amd.aparapi.Annotations.UsedByJNICode
    * @see com.amd.aparapi.Annotations.Experimental
    * @see com.amd.aparapi.OpenCLRunner.KernelArg
    */
   @Annotations.Experimental
   @UsedByJNICode
   public static final int ARG_CONSTANT = 1 << 13;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> has it's length reference, in which case a synthetic arg is passed (name mangled) to the OpenCL kernel.<br/>
    *
    * @author gfrost
    * @see com.amd.aparapi.Annotations.UsedByJNICode
    * @see com.amd.aparapi.OpenCLRunner.KernelArg
    */
   @UsedByJNICode
   public static final int ARG_ARRAYLENGTH = 1 << 14;

   /**
    * TODO:
    *
    * @author gfrost
    * @see com.amd.aparapi.Annotations.UsedByJNICode
    * @see com.amd.aparapi.OpenCLRunner.KernelArg
    */
   @UsedByJNICode
   public static final int ARG_APARAPI_BUF = 1 << 15;

   /**
    * This 'bit' indicates that the arg has been explicitly marked for reading
    *
    * @author gfrost
    * @see com.amd.aparapi.Annotations.UsedByJNICode
    * @see com.amd.aparapi.OpenCLRunner.KernelArg
    */
   @UsedByJNICode
   public static final int ARG_EXPLICIT = 1 << 16;

   /**
    * This 'bit' indicates that the arg has been explicitly marked for writing
    *
    * @author gfrost
    * @see com.amd.aparapi.Annotations.UsedByJNICode
    * @see com.amd.aparapi.OpenCLRunner.KernelArg
    */
   @UsedByJNICode
   public static final int ARG_EXPLICIT_WRITE = 1 << 17;

   /**
    * TODO:
    *
    * @author gfrost
    * @see com.amd.aparapi.Annotations.UsedByJNICode
    * @see com.amd.aparapi.OpenCLRunner.KernelArg
    */
   @UsedByJNICode
   public static final int ARG_OBJ_ARRAY_STRUCT = 1 << 18;

   /**
    * TODO:
    *
    * @see com.amd.aparapi.Annotations.UsedByJNICode
    * @see com.amd.aparapi.OpenCLRunner.KernelArg
    *
    * @author gfrost
    */
   // @UsedByJNICode public static final int ARG_APARAPI_BUF_HAS_ARRAY = 1 << 19;

   /**
    * TODO:
    *
    * @see com.amd.aparapi.Annotations.UsedByJNICode
    * @see com.amd.aparapi.OpenCLRunner.KernelArg
    *
    * @author gfrost
    */
   // @UsedByJNICode public static final int ARG_APARAPI_BUF_IS_DIRECT = 1 << 20;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> represents a <code>char</code> prefix (array or primitive).
    *
    * @author rlamothe
    * @see com.amd.aparapi.Annotations.UsedByJNICode
    * @see com.amd.aparapi.OpenCLRunner.KernelArg
    */
   @UsedByJNICode
   public static final int ARG_CHAR = 1 << 21;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> represents a <code>static</code> field (array or primitive).
    *
    * @author gfrost
    * @see com.amd.aparapi.Annotations.UsedByJNICode
    * @see com.amd.aparapi.OpenCLRunner.KernelArg
    */
   @UsedByJNICode
   public static final int ARG_STATIC = 1 << 22;

   static final String CL_KHR_FP64 = "cl_khr_fp64";

   static final String CL_KHR_SELECT_FPROUNDING_MODE = "cl_khr_select_fprounding_mode";

   static final String CL_KHR_GLOBAL_INT32_BASE_ATOMICS = "cl_khr_global_int32_base_atomics";

   static final String CL_KHR_GLOBAL_INT32_EXTENDED_ATOMICS = "cl_khr_global_int32_extended_atomics";

   static final String CL_KHR_LOCAL_INT32_BASE_ATOMICS = "cl_khr_local_int32_base_atomics";

   static final String CL_KHR_LOCAL_INT32_EXTENDED_ATOMICS = "cl_khr_local_int32_extended_atomics";

   static final String CL_KHR_INT64_BASE_ATOMICS = "cl_khr_int64_base_atomics";

   static final String CL_KHR_INT64_EXTENDED_ATOMICS = "cl_khr_int64_extended_atomics";

   static final String CL_KHR_3D_IMAGE_WRITES = "cl_khr_3d_image_writes";

   static final String CL_KHR_BYTE_ADDRESSABLE_SUPPORT = "cl_khr_byte_addressable_store";

   static final String CL_KHR_FP16 = "cl_khr_fp16";

   static final String CL_KHR_GL_SHARING = "cl_khr_gl_sharing";

   /**
    * This 'bit' indicates that we wish to enable profiling from the JNI code.
    *
    *
    * @see com.amd.aparapi.Annotations.UsedByJNICode
    *
    * @author gfrost
    */
   //@UsedByJNICode public static final int JNI_FLAG_ENABLE_PROFILING = 1 << 0;

   /**
    * This 'bit' indicates that we wish to array_store profiling information in a CSV file from JNI code.
    *
    *
    * @see com.amd.aparapi.Annotations.UsedByJNICode
    *
    * @author gfrost
    */
   // @UsedByJNICode public static final int JNI_FLAG_ENABLE_PROFILING_CSV = 1 << 1;

   /**
    * This 'bit' indicates that we want to execute on the GPU.
    * <p/>
    * <p/>
    * Be careful changing final constants starting with JNI.<br/>
    *
    * @author gfrost
    * @see com.amd.aparapi.Annotations.UsedByJNICode
    */
   @UsedByJNICode
   public static final int JNI_FLAG_USE_GPU = 1 << 2;


   /**
    * This 'bit' indicates that this kernel represents a Java 8 lambda.
    * <p/>
    * <p/>
    * Be careful changing final constants starting with JNI.<br/>
    *
    * @author gfrost
    * @see com.amd.aparapi.Annotations.UsedByJNICode
    */
   @UsedByJNICode
   public static final int JNI_FLAG_LAMBDA_KERNEL = 1 << 3;

   /**
    * This 'bit' indicates that this kernel represents a 'classic' kernel rather than a java 8 lambda.
    * <p/>
    * <p/>
    * Be careful changing final constants starting with JNI.<br/>
    *
    * @author gfrost
    * @see com.amd.aparapi.Annotations.UsedByJNICode
    */
   @UsedByJNICode
   public static final int JNI_FLAG_CLASSIC_KERNEL = 1 << 4;

   /**
    * This 'bit' indicates that we wish to enable verbose JNI layer messages to stderr.<br/>
    *
    * @see com.amd.aparapi.Annotations.UsedByJNICode
    * @see com.amd.aparapi.Annotations.Experimental
    *
    * @author gfrost
    */

   // @UsedByJNICode public static final int JNI_FLAG_ENABLE_VERBOSE_JNI = 1 << 4;

   /**
    * This 'bit' indicates that we wish to enable OpenCL resource tracking by JNI layer to be written to stderr.<br/>
    *
    * @see com.amd.aparapi.Annotations.UsedByJNICode
    * @see com.amd.aparapi.Annotations.Experimental
    *
    * @author gfrost
    */

   //  @UsedByJNICode @Annotations.Experimental public static final int JNI_FLAG_ENABLE_VERBOSE_JNI_OPENCL_RESOURCE_TRACKING = 1 << 4;

   /**
    * Each field (or captured field in the case of an anonymous inner class) referenced by any bytecode reachable from the users Kernel.run(), will
    * need to be represented as a <code>KernelArg</code>.
    *
    * @author gfrost
    * @see Kernel#execute(int _globalSize)
    */
   static protected class KernelArg{

      /**
       * The prefix of this KernelArg. Created by oring appropriate flags
       */
      @UsedByJNICode
      public int type;

      /**
       * Name of the field
       */
      @UsedByJNICode
      public String name;

      /**
       * If this field represents a Java array then the instance will be captured here
       */
      @UsedByJNICode
      public Object javaArray;

      /**
       * If this is an array or a buffer then the size (in bytes) is held here
       */
      @UsedByJNICode
      public int sizeInBytes;

      /**
       * If this is an array buffer then the number of elements is stored here
       */
      @UsedByJNICode
      public int numElements;

      /**
       * If this is an array buffer then the number of elements is stored here.
       *
       * At present only set for AparapiLocalBuffer objs, JNI multiplies this by localSize
       */
      //  @Annotations.Unused @UsedByJNICode public int bytesPerLocalWidth;

      /**
       * Only set for array objs, not used on JNI
       */
      @UsedByJNICode
      public Object array;

      /**
       * Field in Kernel class corresponding to this arg
       */
      @UsedByJNICode
      public Object fieldHolder;

      /**
       * Field in Kernel class corresponding to this arg
       */
      @UsedByJNICode
      public Field field;

      /**
       * The byte array for obj conversion passed to opencl
       */
      byte[] objArrayBuffer;

      /**
       * The ByteBuffer fronting the byte array
       */

      ByteBuffer objArrayByteBuffer;

      /**
       * ClassModel of the array elements (not used on JNI side)
       */
      ClassModel objArrayElementModel;

      /**
       * Only set for AparapiBuffer objs,
       */
      Object primitiveBuf;

      /**
       * Size of this primitive
       */
      int primitiveSize;
   }

   protected long jniContextHandle = 0;


   /**
    * Create a OpenCLRunner
    */
   OpenCLRunner(){
   }

   /**
    * <code>Kernel.dispose()</code> delegates to <code>KernelRunner.dispose()</code> which delegates to <code>disposeJNI()</code> to actually close JNI data structures.<br/>
    *
    * @see com.amd.aparapi.OpenCLRunner#disposeJNI(long)
    */
   void dispose(){
      disposeJNI(jniContextHandle);
   }

   /**
    * TODO:
    * <p/>
    * synchronized to avoid race in clGetPlatformIDs() in OpenCL lib problem should fixed in some future OpenCL version
    *
    * @param _kernel
    * @param _flags
    * @param _device
    * @return
    */
   protected native static synchronized long initJNI(Object _kernel, OpenCLDevice _device, int _flags);

   protected native int setArgsJNI(long _jniContextHandle, KernelArg[] _args, int argc);

   protected native int runJNI(long _jniContextHandle, Range _range, boolean _needSync, int _passes);

   protected native long buildProgramJNI(long _jniContextHandle, String _source);

   protected native int disposeJNI(long _jniContextHandle);

   protected native String getExtensionsJNI(long _jniContextHandle);

   protected synchronized native List<ProfileInfo> getProfileInfoJNI(long _jniContextHandle);

   protected Set<String> capabilitiesSet;

   protected long accumulatedExecutionTime = 0;

   protected long conversionTime = 0;

   protected long executionTime = 0;

   final boolean hasFP64Support(){
      if(capabilitiesSet == null){
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return (capabilitiesSet.contains(CL_KHR_FP64));
   }

   final boolean hasSelectFPRoundingModeSupport(){
      if(capabilitiesSet == null){
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(CL_KHR_SELECT_FPROUNDING_MODE);
   }

   final boolean hasGlobalInt32BaseAtomicsSupport(){
      if(capabilitiesSet == null){
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(CL_KHR_GLOBAL_INT32_BASE_ATOMICS);
   }

   final boolean hasGlobalInt32ExtendedAtomicsSupport(){
      if(capabilitiesSet == null){
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(CL_KHR_GLOBAL_INT32_EXTENDED_ATOMICS);
   }

   final boolean hasLocalInt32BaseAtomicsSupport(){
      if(capabilitiesSet == null){
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(CL_KHR_LOCAL_INT32_BASE_ATOMICS);
   }

   final boolean hasLocalInt32ExtendedAtomicsSupport(){
      if(capabilitiesSet == null){
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(CL_KHR_LOCAL_INT32_EXTENDED_ATOMICS);
   }

   final boolean hasInt64BaseAtomicsSupport(){
      if(capabilitiesSet == null){
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(CL_KHR_INT64_BASE_ATOMICS);
   }

   final boolean hasInt64ExtendedAtomicsSupport(){
      if(capabilitiesSet == null){
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(CL_KHR_INT64_EXTENDED_ATOMICS);
   }

   final boolean has3DImageWritesSupport(){
      if(capabilitiesSet == null){
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(CL_KHR_3D_IMAGE_WRITES);
   }

   final boolean hasByteAddressableStoreSupport(){
      if(capabilitiesSet == null){
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(CL_KHR_BYTE_ADDRESSABLE_SUPPORT);
   }

   final boolean hasFP16Support(){
      if(capabilitiesSet == null){
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(CL_KHR_FP16);
   }

   final boolean hasGLSharingSupport(){
      if(capabilitiesSet == null){
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(CL_KHR_GL_SHARING);
   }

   enum MappedMethod{
      ABS_D("abs", "fabs", "D"),
      ABS_F("abs", "fabs", "F"),
      ABS_I("abs", "abs", "I"),
      ABS_J("abs", "abs", "J"),
      ACOS_D("acos", "acos", "D"),
      ASIN_D("asin", "asin", "D"),
      ATAN_D("atan", "atan", "D"),
      ATAN2_D("atan2", "atan2", "D"),
      CEIL_D("ceil", "ceil", "D"),
      COS_D("cos", "cos", "D"),
      EXP_D("exp", "exp", "D"),
      EXP_F("exp", "exp", "F"),
      FLOOR_D("floor", "floor", "D"),
      FLOOR_F("floor", "floor", "F"),
      LOG_D("log", "log", "D"),
      LOG_F("log", "log", "D"),
      MAX_D("max", "max", "D"),
      MAX_F("max", "max", "F"),
      MAX_I("max", "max", "I"),
      MAX_J("max", "max", "J"),
      MIN_D("max", "max", "D"),
      MIN_F("max", "max", "F"),
      MIN_I("max", "max", "I"),
      MIN_J("max", "max", "J"),
      POW_D("pow", "pow", "D"),
      RINT_D("rint", "rint", "D"),
      ROUND_D("round", "round", "D"),
      ROUND_F("round", "round", "F"),
      SIN_D("sin", "sin", "D"),
      SQRT_D("sqrt", "sqrt", "D"),
      TAN_D("tan", "tan", "D");
      private String targetMethodName;
      private String mappedMethodName;
      private String returnType;

      private MappedMethod(String name, String mapTo, String ret){
         targetMethodName = name;
         mappedMethodName = mapTo;
         returnType = ret;
      }

      public String getTargetName(){
         return targetMethodName;
      }

      public String getMappedName(){
         return mappedMethodName;
      }

      public String getReturnType(){
         return returnType;
      }
   }

   ;

   static boolean isMappedMethod(ClassModel.ConstantPool.MethodReferenceEntry methodReferenceEntry){
      boolean isMapped = false;
      for(MappedMethod mappedMethod : MappedMethod.values()){
         if(methodReferenceEntry.getNameAndTypeEntry().getNameUTF8Entry().getUTF8().equals(mappedMethod.getTargetName())){
            // well they have the same name ;)
            isMapped = true;
         }
      }
      return (isMapped);
   }

   private static String descriptorToReturnTypeLetter(String desc){
      // find the letter after the closed parenthesis
      return desc.substring(desc.lastIndexOf(')') + 1);
   }

   static String getMappedMethodName(ClassModel.ConstantPool.MethodReferenceEntry _methodReferenceEntry){
      String mappedName = null;
      String name = _methodReferenceEntry.getNameAndTypeEntry().getNameUTF8Entry().getUTF8();
      for(MappedMethod mappedMethod : MappedMethod.values()){
         if(_methodReferenceEntry.getNameAndTypeEntry().getNameUTF8Entry().getUTF8().equals(mappedMethod.getTargetName())
               && descriptorToReturnTypeLetter(_methodReferenceEntry.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8())
               .equals(mappedMethod.getReturnType())){
            String mapTo = mappedMethod.getMappedName();
            if(!mapTo.equals("")){
               mappedName = mapTo;
            }
         }
      }
      if((mappedName != null) && (logger.isLoggable(Level.FINE))){
         logger.fine("Selected mapped method " + mappedName);
      }
      return (mappedName);
   }


   /**
    * Determine the time taken to convert bytecode to OpenCL for first Kernel.execute(range) call.
    *
    * @return The time spent preparing the kernel for execution using GPU
    */
   final public long getConversionTime(){
      return conversionTime;
   }

   /**
    * Determine the execution time of the previous Kernel.execute(range) call.
    *
    * @return The time spent executing the kernel (ms)
    */
   final public long getExecutionTime(){
      return executionTime;
   }

   /**
    * Determine the accumulated execution time of all previous Kernel.execute(range) calls.
    *
    * @return The accumulated time spent executing this kernel (ms)
    */
   final public long getAccumulatedExecutionTime(){
      return accumulatedExecutionTime;
   }
}
