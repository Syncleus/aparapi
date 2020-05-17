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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
//import java.lang.invoke.InnerClassLambdaMetafactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.function.IntBlock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

import com.amd.aparapi.ClassModel.ConstantPool.MethodEntry;
import com.amd.aparapi.ClassModel.ConstantPool.MethodReferenceEntry;
import com.amd.aparapi.InstructionSet.AccessField;
import com.amd.aparapi.InstructionSet.MethodCall;
import com.amd.aparapi.InstructionSet.VirtualMethodCall;

import com.amd.aparapi.InstructionSet.TypeSpec;
//import com.amd.aparapi.Kernel.EXECUTION_MODE;

/**
 * The class is responsible for executing <code>Kernel</code> implementations. <br/>
 * 
 * The <code>KernelRunner</code> is the real workhorse for Aparapi.  Each <code>Kernel</code> instance creates a single
 * <code>KernelRunner</code> to encapsulate state and to help coordinate interactions between the <code>Kernel</code> 
 * and it's execution logic.<br/>
 * 
 * The <code>KernelRunner</code> is created <i>lazily</i> as a result of calling <code>Kernel.execute()</code>. A this 
 * time the <code>ExecutionMode</code> is consulted to determine the default requested mode.  This will dictate how 
 * the <code>KernelRunner</code> will attempt to execute the <code>Kernel</code>
 *   
 * @see com.amd.aparapi.Kernel#execute(int _globalSize)
 * 
 * @author gfrost
 *
 */
class KernelRunner{
   /**
    * Be careful changing the name/type of this field as it is referenced from JNI code.
    */
   public @interface UsedByJNICode {

   }

   private static Logger logger = Logger.getLogger(Config.getLoggerName());

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> represents a <code>boolean</code> type (array or primitive).
    * 
    * 
    * @see com.amd.aparapi.annotations.UsedByJNICode
    * @see com.amd.aparapi.KernelRunner.KernelArg
    * 
    * @author gfrost
    */
   @UsedByJNICode public static final int ARG_BOOLEAN = 1 << 0;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> represents a <code>byte</code> type (array or primitive).
    * 
    * 
    * @see com.amd.aparapi.annotations.UsedByJNICode
    * @see com.amd.aparapi.KernelRunner.KernelArg
    * 
    * @author gfrost
    */
   @UsedByJNICode public static final int ARG_BYTE = 1 << 1;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> represents a <code>float</code> type (array or primitive).
    * 
    * 
    * @see com.amd.aparapi.annotations.UsedByJNICode
    * @see com.amd.aparapi.KernelRunner.KernelArg
    * 
    * @author gfrost
    */
   @UsedByJNICode public static final int ARG_FLOAT = 1 << 2;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> represents a <code>int</code> type (array or primitive).
    * 
    * 
    * @see com.amd.aparapi.annotations.UsedByJNICode
    * @see com.amd.aparapi.KernelRunner.KernelArg
    * 
    * @author gfrost
    */
   @UsedByJNICode public static final int ARG_INT = 1 << 3;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> represents a <code>double</code> type (array or primitive).
    * 
    * 
    * @see com.amd.aparapi.annotations.UsedByJNICode
    * @see com.amd.aparapi.KernelRunner.KernelArg
    * 
    * @author gfrost
    */
   @UsedByJNICode public static final int ARG_DOUBLE = 1 << 4;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> represents a <code>long</code> type (array or primitive).
    * 
    * 
    * @see com.amd.aparapi.annotations.UsedByJNICode
    * @see com.amd.aparapi.KernelRunner.KernelArg
    * 
    * @author gfrost
    */
   @UsedByJNICode public static final int ARG_LONG = 1 << 5;

   /**
    * TODO:
    * 
    * @see com.amd.aparapi.annotations.UsedByJNICode
    * @see com.amd.aparapi.KernelRunner.KernelArg
    * 
    * @author gfrost
    */
   @UsedByJNICode public static final int ARG_SHORT = 1 << 6;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> represents an array.<br/>
    * So <code>ARG_ARRAY|ARG_INT</code> tells us this arg is an array of <code>int</code>.
    * 
    * 
    * @see com.amd.aparapi.annotations.UsedByJNICode
    * @see com.amd.aparapi.KernelRunner.KernelArg
    * 
    * @author gfrost
    */
   @UsedByJNICode public static final int ARG_ARRAY = 1 << 7;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> represents a primitive (non array).<br/>
    * So <code>ARG_PRIMITIVE|ARG_INT</code> tells us this arg is a primitive <code>int</code>.
    * 
    * 
    * @see com.amd.aparapi.annotations.UsedByJNICode
    * @see com.amd.aparapi.KernelRunner.KernelArg
    * 
    * @author gfrost
    */
   @UsedByJNICode public static final int ARG_PRIMITIVE = 1 << 8;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> is read by the Kernel (note from the Kernel's point of view).<br/>
    * So <code>ARG_ARRAY|ARG_INT|ARG_READ</code> tells us this arg is an array of int's that are read by the kernel.
    * 
    * 
    * @see com.amd.aparapi.annotations.UsedByJNICode
    * @see com.amd.aparapi.KernelRunner.KernelArg
    * 
    * @author gfrost
    */
   @UsedByJNICode public static final int ARG_READ = 1 << 9;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> is mutated by the Kernel (note from the Kernel's point of view).<br/>
    * So <code>ARG_ARRAY|ARG_INT|ARG_WRITE</code> tells us this arg is an array of int's that we expect the kernel to mutate.
    * 
    * 
    * @see com.amd.aparapi.annotations.UsedByJNICode
    * @see com.amd.aparapi.KernelRunner.KernelArg
    * 
    * @author gfrost
    */
   @UsedByJNICode public static final int ARG_WRITE = 1 << 10;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> resides in local memory in the generated OpenCL code.<br/>
    * 
    * 
    * @see com.amd.aparapi.annotations.UsedByJNICode
    * @see com.amd.aparapi.annotations.Experimental
    * @see com.amd.aparapi.KernelRunner.KernelArg
    * 
    * @author gfrost
    */
   @UsedByJNICode public static final int ARG_LOCAL = 1 << 11;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> resides in global memory in the generated OpenCL code.<br/>
    * 
    * 
    * @see com.amd.aparapi.annotations.UsedByJNICode
    * @see com.amd.aparapi.annotations.Experimental
    * @see com.amd.aparapi.KernelRunner.KernelArg
    * 
    * @author gfrost
    */
   @UsedByJNICode public static final int ARG_GLOBAL = 1 << 12;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> resides in constant memory in the generated OpenCL code.<br/>
    * 
    * 
    * @see com.amd.aparapi.annotations.UsedByJNICode
    * @see com.amd.aparapi.annotations.Experimental
    * @see com.amd.aparapi.KernelRunner.KernelArg
    * 
    * @author gfrost
    */
   @UsedByJNICode public static final int ARG_CONSTANT = 1 << 13;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> has it's length reference, in which case a synthetic arg is passed (name mangled) to the OpenCL kernel.<br/>
    * 
    * 
    * @see com.amd.aparapi.annotations.UsedByJNICode
    * @see com.amd.aparapi.KernelRunner.KernelArg
    * 
    * @author gfrost
    */
   @UsedByJNICode public static final int ARG_ARRAYLENGTH = 1 << 14;

   /**
    * TODO:
    * 
    * @see com.amd.aparapi.annotations.UsedByJNICode
    * @see com.amd.aparapi.KernelRunner.KernelArg
    * 
    * @author gfrost
    */
   @UsedByJNICode public static final int ARG_APARAPI_BUF = 1 << 15;

   /**
    * This 'bit' indicates that the arg has been explicitly marked for reading
    * 
    * @see com.amd.aparapi.annotations.UsedByJNICode
    * @see com.amd.aparapi.KernelRunner.KernelArg
    * 
    * @author gfrost
    */
   @UsedByJNICode public static final int ARG_EXPLICIT = 1 << 16;

   /**
    * This 'bit' indicates that the arg has been explicitly marked for writing
    * 
    * @see com.amd.aparapi.annotations.UsedByJNICode
    * @see com.amd.aparapi.KernelRunner.KernelArg
    * 
    * @author gfrost
    */
   @UsedByJNICode public static final int ARG_EXPLICIT_WRITE = 1 << 17;

   /**
    * TODO:
    * 
    * @see com.amd.aparapi.annotations.UsedByJNICode
    * @see com.amd.aparapi.KernelRunner.KernelArg
    * 
    * @author gfrost
    */
   @UsedByJNICode public static final int ARG_OBJ_ARRAY_STRUCT = 1 << 18;

   /**
    * TODO:
    * 
    * @see com.amd.aparapi.annotations.UsedByJNICode
    * @see com.amd.aparapi.KernelRunner.KernelArg
    * 
    * @author gfrost
    */
   // @UsedByJNICode public static final int ARG_APARAPI_BUF_HAS_ARRAY = 1 << 19;

   /**
    * TODO:
    * 
    * @see com.amd.aparapi.annotations.UsedByJNICode
    * @see com.amd.aparapi.KernelRunner.KernelArg
    * 
    * @author gfrost
    */
   // @UsedByJNICode public static final int ARG_APARAPI_BUF_IS_DIRECT = 1 << 20;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> represents a <code>char</code> type (array or primitive).
    * 
    * 
    * @see com.amd.aparapi.annotations.UsedByJNICode
    * @see com.amd.aparapi.KernelRunner.KernelArg
    * 
    * @author rlamothe
    */
   @UsedByJNICode public static final int ARG_CHAR = 1 << 21;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> represents a <code>static</code> field (array or primitive).
    * 
    * 
    * @see com.amd.aparapi.annotations.UsedByJNICode
    * @see com.amd.aparapi.KernelRunner.KernelArg
    * 
    * @author gfrost
    */
   @UsedByJNICode public static final int ARG_STATIC = 1 << 22;

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
    * @see com.amd.aparapi.annotations.UsedByJNICode
    * 
    * @author gfrost
    */
   //@UsedByJNICode public static final int JNI_FLAG_ENABLE_PROFILING = 1 << 0;

   /**
    * This 'bit' indicates that we wish to store profiling information in a CSV file from JNI code.
    * 
    * 
    * @see com.amd.aparapi.annotations.UsedByJNICode
    * 
    * @author gfrost
    */
   // @UsedByJNICode public static final int JNI_FLAG_ENABLE_PROFILING_CSV = 1 << 1;

   /**
    * This 'bit' indicates that we want to execute on the GPU.
    * 
    * 
    * Be careful changing final constants starting with JNI.<br/>
    * 
    * 
    * @see com.amd.aparapi.annotations.UsedByJNICode
    * 
    * @author gfrost
    */
   @UsedByJNICode public static final int JNI_FLAG_USE_GPU = 1 << 2;

   /**
    * This 'bit' indicates that we wish to enable verbose JNI layer messages to stderr.<br/>
    * 
    * @see com.amd.aparapi.annotations.UsedByJNICode
    * @see com.amd.aparapi.annotations.Experimental
    * 
    * @author gfrost
    */

   // @UsedByJNICode public static final int JNI_FLAG_ENABLE_VERBOSE_JNI = 1 << 3;

   /**
    * This 'bit' indicates that we wish to enable OpenCL resource tracking by JNI layer to be written to stderr.<br/>
    * 
    * @see com.amd.aparapi.annotations.UsedByJNICode
    * @see com.amd.aparapi.annotations.Experimental
    * 
    * @author gfrost
    */

   //  @UsedByJNICode @Annotations.Experimental public static final int JNI_FLAG_ENABLE_VERBOSE_JNI_OPENCL_RESOURCE_TRACKING = 1 << 4;

   /**
    * Each field (or captured field in the case of an anonymous inner class) referenced by any bytecode reachable from the users Kernel.run(), will
    * need to be represented as a <code>KernelArg</code>.
    * 
    * @see com.amd.aparapi.Kernel#execute(int _globalSize)
    * 
    * @author gfrost
    * 
    */
   static class KernelArg{

      /**
       * The type of this KernelArg. Created by oring appropriate flags
       * 
       * @see ARG_BOOLEAN
       * @see ARG_BYTE
       * @see ARG_CHAR
       * @see ARG_FLOAT
       * @see ARG_INT
       * @see ARG_DOUBLE
       * @see ARG_LONG
       * @see ARG_SHORT
       * @see ARG_ARRAY
       * @see ARG_PRIMITIVE
       * @see ARG_READ
       * @see ARG_WRITE
       * @see ARG_LOCAL
       * @see ARG_GLOBAL
       * @see ARG_CONSTANT
       * @see ARG_ARRAYLENGTH
       * @see ARG_APARAPI_BUF
       * @see ARG_EXPLICIT
       * @see ARG_EXPLICIT_WRITE
       * @see ARG_OBJ_ARRAY_STRUCT
       * @see ARG_APARAPI_BUF_HAS_ARRAY
       * @see ARG_APARAPI_BUF_IS_DIRECT
       */
      @UsedByJNICode public int type;

      /**
       * Name of the field
       */
      @UsedByJNICode public String name;

      /**
       * If this field represents a Java array then the instance will be captured here
       */
      @UsedByJNICode public Object javaArray;

      /**
       * If this is an array or a buffer then the size (in bytes) is held here
       */
      @UsedByJNICode public int sizeInBytes;

      /**
       * If this is an array buffer then the number of elements is stored here
       */
      @UsedByJNICode public int numElements;

      /**
       * If this is an array buffer then the number of elements is stored here.
       * 
       * At present only set for AparapiLocalBuffer objs, JNI multiplies this by localSize
       */
      //  @Annotations.Unused @UsedByJNICode public int bytesPerLocalWidth;

      /**
       * Only set for array objs, not used on JNI
       */
      @UsedByJNICode public Object array;

      /**
       * Field in fieldHolder object corresponding to this arg
       */
      @UsedByJNICode public Field field;
      
      /**
       * Field in fieldHolder object corresponding to this arg
       * For lambda use, args come from Block, KernelRunner and the 
       * lambda's own object
       */
      @UsedByJNICode public Object fieldHolder;
      

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
       * 
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
   
   
   enum MappedMethod {
      ACOS("acos", "D"),
      COS("cos", "D"),
      MAX_I("max", "I"),
      MAX_J("max", "J"),
      MAX_F("max", "F"),
      MAX_D("max", "D"),
      MIN_I("min", "I"),
      SQRT("sqrt", "D");
      
      private String mapping;
      private String returnType;
      
      private MappedMethod(String name, String ret) { 
         mapping = name;
         returnType = ret;
      }
      public String getName()        { return mapping; }
      public String getReturnType()  { return returnType; }
   };
   
   static boolean isMappedMethod(MethodReferenceEntry methodReferenceEntry) {
      boolean isMapped = false;
      for (MappedMethod mappedMethod : MappedMethod.values()) {
         if (methodReferenceEntry.getNameAndTypeEntry().getNameUTF8Entry().getUTF8().equals(mappedMethod.getName())) {

            // well they have the same name ;) 
            isMapped = true;
         }    
      }

      return (isMapped);
   }

   private static String descriptorToReturnTypeLetter(String desc) {
      // find the letter after the closed parenthesis
      return desc.substring(desc.lastIndexOf(')') + 1);
   }

   static String getMappedMethodName(MethodReferenceEntry _methodReferenceEntry) {
      String mappedName = null;
      String name = _methodReferenceEntry.getNameAndTypeEntry().getNameUTF8Entry().getUTF8();
      for (MappedMethod mappedMethod : MappedMethod.values()) {
         if (_methodReferenceEntry.getNameAndTypeEntry().getNameUTF8Entry().getUTF8().equals(mappedMethod.getName())
               && descriptorToReturnTypeLetter(_methodReferenceEntry.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8())
               .equals(mappedMethod.getReturnType())) {
            String mapTo = mappedMethod.getName();
            if (!mapTo.equals("")) {
               mappedName = mapTo;
            }    
         }    
      }    
      if (logger.isLoggable(Level.FINE)) {
         logger.fine("Selected mapped method " + mappedName);
      }
      return (mappedName);
   }
   
   
   class LambdaKernelCall {
      IntBlock block;
      String   lambdaKernelSource;
      String   lambdaMethodName;
      Field[] lambdaCapturedFields;
      Object[] lambdaCapturedArgs;
      String lambdaMethodSignature;
      
      public String     getLambdaKernelSource()    { return lambdaKernelSource; }
      public Object     getLambdaKernelThis()      { return lambdaCapturedArgs[0]; }
      public String     getLambdaMethodName()      { return lambdaMethodName; }
      public String     getLambdaMethodSignature()      { return lambdaMethodSignature; }
      //public Object[]   getLambdaCapturedArgs()    { return lambdaCapturedArgs; }
      //public Object[]   getLambdaReferenceArgs()   { return lambdaReferencedFields; }
      
      public String toString() { return getLambdaKernelThis().getClass().getName() + " " +
            getLambdaMethodName() + " " + getLambdaMethodSignature() + " from block: " +
            block;
      }
      
      public Field[]   getLambdaCapturedFields()    { return lambdaCapturedFields; }
      
      public LambdaKernelCall(IntBlock _block) throws AparapiException { 
         block = _block;
         
         // Try to do reflection on the block
         Class bc = block.getClass();
         System.out.println("# block class:" + bc);
         
         // The first field is "this" for the lambda call if the lambda
         // is not static, the later fields are captured values which will 
         // become lambda call parameters
         Field[] bcf = bc.getDeclaredFields();
         lambdaCapturedArgs = new Object[bcf.length];

         Field[] allBlockClassFields = block.getClass().getDeclaredFields();
         
         Field[] capturedFieldsWithoutThis = new Field[ allBlockClassFields.length - 1 ];
         for(int i=1; i<allBlockClassFields.length; i++) {
            capturedFieldsWithoutThis[i-1] = allBlockClassFields[i];
         }
         
         lambdaCapturedFields = capturedFieldsWithoutThis;
         
         try {
            for (int i=0; i<bcf.length; i++) {
               
               // Since Block members are private have to use Unsafe here
               Class currFieldType = bcf[i].getType();
               long offset = UnsafeWrapper.objectFieldOffset(bcf[i]);

               if (currFieldType.isPrimitive() == false) {
                  lambdaCapturedArgs[i] = UnsafeWrapper.getObject(block, offset);
               } else if (currFieldType.getName().equals("float")) {
                  lambdaCapturedArgs[i] = UnsafeWrapper.getFloat(block, offset);
               } else if (currFieldType.getName().equals("int")) {
                  lambdaCapturedArgs[i] = UnsafeWrapper.getInt(block, offset);
               } else if (currFieldType.getName().equals("long")) {
                  lambdaCapturedArgs[i] = UnsafeWrapper.getLong(block, offset);

                  // No getDouble ??   
                  //} else if (currFieldType.getName().equals("double")) {
                  //   lambdaArgs[i] = UnsafeWrapper.getDouble(block, offset);
               }

               //System.out.println("# Lambda arg type: " + currFieldType + "  " + bcf[i].getName() + " = " + lambdaCapturedArgs[i]);            
            }
         } catch (Exception e) {
            System.out.println("Problem getting Block args");
            e.printStackTrace();
         }
         
         // This is the Class containing the lambda method        
         Class lc = getLambdaKernelThis().getClass();

         // The class name is created with the "/" style delimiters
         String bcNameWithSlashes = bc.getName().replace('.', '/');
         ByteArrayInputStream blockClassStream = new ByteArrayInputStream(AparapiAgent.getBytes(bc));
         ClassModel blockModel = new ClassModel(blockClassStream);

         // We know we are calling an IntBlock lambda with signature "(I)V"
         MethodModel acceptModel = blockModel.getMethodModel("accept", "(I)V");

         List<MethodCall> acceptCallSites = acceptModel.getMethodCalls();
         assert acceptCallSites.size() == 1 : "Should only have one call site in this method";

         
         //VirtualMethodCall vCall = (VirtualMethodCall) acceptCallSites.get(0);
         MethodCall vCall = acceptCallSites.get(0);
         MethodEntry lambdaCallTarget = vCall.getConstantPoolMethodEntry();
         lambdaMethodName = lambdaCallTarget.getNameAndTypeEntry().getNameUTF8Entry().getUTF8();
         lambdaMethodSignature = lambdaCallTarget.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8();

         System.out.println("call target = " + 
             lambdaCallTarget.getClassEntry().getNameUTF8Entry().getUTF8() + 
             " " + lambdaMethodName + " " + lambdaMethodSignature);

         String lcNameWithSlashes = lc.getName().replace('.', '/');
         assert lcNameWithSlashes.equals(lambdaCallTarget.getClassEntry().getNameUTF8Entry().getUTF8()) : 
            "lambda target class name does not match arg in block object";
         
      }
   }

   private LambdaKernelCall lambdaKernelCall;  

   private long jniContextHandle = 0;

//   private Kernel kernel;

   private Entrypoint entryPoint;

   private int argc;

   /**
    * Create a KernelRunner for a specific Kernel instance.
    * 
    * @param _kernel
    */
//   KernelRunner(Kernel _kernel) {
//      kernel = _kernel;
//
//   }

//   KernelRunner() {
//      kernel = null;
//   }

   KernelRunner(IntBlock block) throws AparapiException {
      //kernel = null;
      lambdaKernelCall = new LambdaKernelCall(block);
      if (logger.isLoggable(Level.INFO)) {
         logger.info("New lambda call is = " + lambdaKernelCall);
      }
   }
   
   /**
    * <code>Kernel.dispose()</code> delegates to <code>KernelRunner.dispose()</code> which delegates to <code>disposeJNI()</code> to actually close JNI data structures.<br/>
    * 
    * @see KernelRunner#disposeJNI()
    */
   void dispose() {
      
      // Might need to revisit this for Superbowl
      
//      if (kernel.getExecutionMode().isOpenCL()) {
//         disposeJNI(jniContextHandle);
//      }
   }

   /**
    * TODO:
    * 
    * synchronized to avoid race in clGetPlatformIDs() in OpenCL lib problem should fixed in some future OpenCL version
    * 
    * @param _kernel
    * @param _flags
    * @param numProcessors
    * @param maxJTPLocalSize
    * @return
    */
   private native static synchronized long initJNI(Object _kernel, OpenCLDevice device, int _flags);

   private native long buildProgramJNI(long _jniContextHandle, String _source);

   private native int setArgsJNI(long _jniContextHandle, KernelArg[] _args, int argc);

   private native int updateLambdaBlockJNI(long _jniContextHandle, Object newHolder, int argc);

   private native int runKernelJNI(long _jniContextHandle, Range _range, boolean _needSync, int _passes);

   private native int disposeJNI(long _jniContextHandle);

   private native String getExtensionsJNI(long _jniContextHandle);

   //  private native @Deprecated int getMaxWorkGroupSizeJNI(long _jniContextHandle);

   // private native @Deprecated int getMaxWorkItemSizeJNI(long _jniContextHandle, int _index);

   // private native @Deprecated int getMaxComputeUnitsJNI(long _jniContextHandle);

   //private native @Deprecated int getMaxWorkItemDimensionsJNI(long _jniContextHandle);

   private synchronized native List<ProfileInfo> getProfileInfoJNI(long _jniContextHandle);

   private Set<String> capabilitiesSet;

   private long accumulatedExecutionTime = 0;

   private long conversionTime = 0;

   private long executionTime = 0;

   boolean hasFP64Support() {
      if (capabilitiesSet == null) {
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return (capabilitiesSet.contains(CL_KHR_FP64));
   }

   boolean hasSelectFPRoundingModeSupport() {
      if (capabilitiesSet == null) {
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(CL_KHR_SELECT_FPROUNDING_MODE);
   }

   boolean hasGlobalInt32BaseAtomicsSupport() {
      if (capabilitiesSet == null) {
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(CL_KHR_GLOBAL_INT32_BASE_ATOMICS);
   }

   boolean hasGlobalInt32ExtendedAtomicsSupport() {
      if (capabilitiesSet == null) {
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(CL_KHR_GLOBAL_INT32_EXTENDED_ATOMICS);
   }

   boolean hasLocalInt32BaseAtomicsSupport() {
      if (capabilitiesSet == null) {
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(CL_KHR_LOCAL_INT32_BASE_ATOMICS);
   }

   boolean hasLocalInt32ExtendedAtomicsSupport() {
      if (capabilitiesSet == null) {
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(CL_KHR_LOCAL_INT32_EXTENDED_ATOMICS);
   }

   boolean hasInt64BaseAtomicsSupport() {
      if (capabilitiesSet == null) {
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(CL_KHR_INT64_BASE_ATOMICS);
   }

   boolean hasInt64ExtendedAtomicsSupport() {
      if (capabilitiesSet == null) {
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(CL_KHR_INT64_EXTENDED_ATOMICS);
   }

   boolean has3DImageWritesSupport() {
      if (capabilitiesSet == null) {
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(CL_KHR_3D_IMAGE_WRITES);
   }

   boolean hasByteAddressableStoreSupport() {
      if (capabilitiesSet == null) {
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(CL_KHR_BYTE_ADDRESSABLE_SUPPORT);
   }

   boolean hasFP16Support() {
      if (capabilitiesSet == null) {
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(CL_KHR_FP16);
   }

   boolean hasGLSharingSupport() {
      if (capabilitiesSet == null) {
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(CL_KHR_GL_SHARING);
   }


   private static void await(CyclicBarrier _barrier) {
      try {
         _barrier.await();
      } catch (InterruptedException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (BrokenBarrierException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   private KernelArg[] args = null;

   private boolean usesOopConversion = false;

   /**
    * 
    * @param arg
    * @return
    * @throws AparapiException
    */
   private boolean prepareOopConversionBuffer(KernelArg arg) throws AparapiException {
      usesOopConversion = true;
      Class<?> arrayClass = arg.field.getType();
      ClassModel c = null;
      boolean didReallocate = false;

      if (arg.objArrayElementModel == null) {
         String tmp = arrayClass.getName().substring(2).replace("/", ".");
         String arrayClassInDotForm = tmp.substring(0, tmp.length() - 1);

         if (logger.isLoggable(Level.FINE)) {
            logger.fine("looking for type = " + arrayClassInDotForm);
         }

         // get ClassModel of obj array from entrypt.objectArrayFieldsClasses
         c = entryPoint.getObjectArrayFieldsClasses().get(arrayClassInDotForm);
         arg.objArrayElementModel = c;
      } else {
         c = arg.objArrayElementModel;
      }
      assert c != null : "should find class for elements " + arrayClass.getName();

      int arrayBaseOffset = UnsafeWrapper.arrayBaseOffset(arrayClass);
      int arrayScale = UnsafeWrapper.arrayIndexScale(arrayClass);

      if (logger.isLoggable(Level.FINEST)) {
         logger.finest("Syncing obj array type = " + arrayClass + " cvtd= " + c.getClassWeAreModelling().getName()
               + "arrayBaseOffset=" + arrayBaseOffset + " arrayScale=" + arrayScale);
      }

      int objArraySize = 0;
      Object newRef = null;
      try {
         newRef = arg.field.get(arg.fieldHolder);
         objArraySize = Array.getLength(newRef);
      } catch (IllegalAccessException e) {
         throw new AparapiException(e);
      }

      assert (newRef != null) && (objArraySize != 0) : "no data";

      int totalStructSize = c.getTotalStructSize();
      int totalBufferSize = objArraySize * totalStructSize;

      // allocate ByteBuffer if first time or array changed
      if ((arg.objArrayBuffer == null) || (newRef != arg.array)) {
         ByteBuffer structBuffer = ByteBuffer.allocate(totalBufferSize);
         arg.objArrayByteBuffer = structBuffer.order(ByteOrder.LITTLE_ENDIAN);
         arg.objArrayBuffer = arg.objArrayByteBuffer.array();
         didReallocate = true;
         if (logger.isLoggable(Level.FINEST)) {
            logger.finest("objArraySize = " + objArraySize + " totalStructSize= " + totalStructSize + " totalBufferSize="
                  + totalBufferSize);
         }
      } else {
         arg.objArrayByteBuffer.clear();
      }

      // copy the fields that the JNI uses
      arg.javaArray = arg.objArrayBuffer;
      arg.numElements = objArraySize;
      arg.sizeInBytes = totalBufferSize;

      for (int j = 0; j < objArraySize; j++) {
         int sizeWritten = 0;

         Object object = UnsafeWrapper.getObject(newRef, arrayBaseOffset + arrayScale * j);
         for (int i = 0; i < c.getStructMemberTypes().size(); i++) {
            TypeSpec t = c.getStructMemberTypes().get(i);
            long offset = c.getStructMemberOffsets().get(i);

            if (logger.isLoggable(Level.FINEST)) {
               logger.finest("name = " + c.getStructMembers().get(i).getNameAndTypeEntry().getNameUTF8Entry().getUTF8() + " t= "
                     + t);
            }

            switch (t) {
               case I: {
                  int x = UnsafeWrapper.getInt(object, offset);
                  arg.objArrayByteBuffer.putInt(x);
                  sizeWritten += t.getSize();
                  break;
               }
               case F: {
                  float x = UnsafeWrapper.getFloat(object, offset);
                  arg.objArrayByteBuffer.putFloat(x);
                  sizeWritten += t.getSize();
                  break;
               }
               case J: {
                  long x = UnsafeWrapper.getLong(object, offset);
                  arg.objArrayByteBuffer.putLong(x);
                  sizeWritten += t.getSize();
                  break;
               }
               case Z: {
                  boolean x = UnsafeWrapper.getBoolean(object, offset);
                  arg.objArrayByteBuffer.put(x == true ? (byte) 1 : (byte) 0);
                  // Booleans converted to 1 byte C chars for opencl
                  sizeWritten += TypeSpec.B.getSize();
                  break;
               }
               case B: {
                  byte x = UnsafeWrapper.getByte(object, offset);
                  arg.objArrayByteBuffer.put(x);
                  sizeWritten += t.getSize();
                  break;
               }
               case D: {
                  throw new AparapiException("Double not implemented yet");
               }
               default:
                  assert true == false : "typespec did not match anything";
                  throw new AparapiException("Unhandled type in buffer conversion");
            }
         }

         // add padding here if needed
         if (logger.isLoggable(Level.FINEST)) {
            logger.finest("sizeWritten = " + sizeWritten + " totalStructSize= " + totalStructSize);
         }

         assert sizeWritten <= totalStructSize : "wrote too much into buffer";

         while (sizeWritten < totalStructSize) {
            if (logger.isLoggable(Level.FINEST)) {
               logger.finest(arg.name + " struct pad byte = " + sizeWritten + " totalStructSize= " + totalStructSize);
            }
            arg.objArrayByteBuffer.put((byte) -1);
            sizeWritten++;
         }
      }

      assert arg.objArrayByteBuffer.arrayOffset() == 0 : "should be zero";

      return didReallocate;
   }

   private void extractOopConversionBuffer(KernelArg arg) throws AparapiException {
      Class<?> arrayClass = arg.field.getType();
      ClassModel c = arg.objArrayElementModel;
      assert c != null : "should find class for elements: " + arrayClass.getName();
      assert arg.array != null : "array is null";

      int arrayBaseOffset = UnsafeWrapper.arrayBaseOffset(arrayClass);
      int arrayScale = UnsafeWrapper.arrayIndexScale(arrayClass);
      if (logger.isLoggable(Level.FINEST)) {
         logger.finest("Syncing field:" + arg.name + ", bb=" + arg.objArrayByteBuffer + ", type = " + arrayClass);
      }

      int objArraySize = 0;
      try {
         objArraySize = Array.getLength(arg.field.get(arg.fieldHolder));
      } catch (IllegalAccessException e) {
         throw new AparapiException(e);
      }

      assert objArraySize > 0 : "should be > 0";

      int totalStructSize = c.getTotalStructSize();
      // int totalBufferSize = objArraySize * totalStructSize;
      // assert arg.objArrayBuffer.length == totalBufferSize : "size should match";

      arg.objArrayByteBuffer.rewind();

      for (int j = 0; j < objArraySize; j++) {
         int sizeWritten = 0;
         Object object = UnsafeWrapper.getObject(arg.array, arrayBaseOffset + arrayScale * j);
         for (int i = 0; i < c.getStructMemberTypes().size(); i++) {
            TypeSpec t = c.getStructMemberTypes().get(i);
            long offset = c.getStructMemberOffsets().get(i);
            switch (t) {
               case I: {
                  // read int value from buffer and store into obj in the array
                  int x = arg.objArrayByteBuffer.getInt();
                  if (logger.isLoggable(Level.FINEST)) {
                     logger.finest("fType = " + t.getShortName() + " x= " + x);
                  }
                  UnsafeWrapper.putInt(object, offset, x);
                  sizeWritten += t.getSize();
                  break;
               }
               case F: {
                  float x = arg.objArrayByteBuffer.getFloat();
                  if (logger.isLoggable(Level.FINEST)) {
                     logger.finest("fType = " + t.getShortName() + " x= " + x);
                  }
                  UnsafeWrapper.putFloat(object, offset, x);
                  sizeWritten += t.getSize();
                  break;
               }
               case J: {
                  long x = arg.objArrayByteBuffer.getLong();
                  if (logger.isLoggable(Level.FINEST)) {
                     logger.finest("fType = " + t.getShortName() + " x= " + x);
                  }
                  UnsafeWrapper.putLong(object, offset, x);
                  sizeWritten += t.getSize();
                  break;
               }
               case Z: {
                  byte x = arg.objArrayByteBuffer.get();
                  if (logger.isLoggable(Level.FINEST)) {
                     logger.finest("fType = " + t.getShortName() + " x= " + x);
                  }
                  UnsafeWrapper.putBoolean(object, offset, (x == 1 ? true : false));
                  // Booleans converted to 1 byte C chars for open cl
                  sizeWritten += TypeSpec.B.getSize();
                  break;
               }
               case B: {
                  byte x = arg.objArrayByteBuffer.get();
                  if (logger.isLoggable(Level.FINEST)) {
                     logger.finest("fType = " + t.getShortName() + " x= " + x);
                  }
                  UnsafeWrapper.putByte(object, offset, x);
                  sizeWritten += t.getSize();
                  break;
               }
               case D: {
                  throw new AparapiException("Double not implemented yet");
               }
               default:
                  assert true == false : "typespec did not match anything";
                  throw new AparapiException("Unhandled type in buffer conversion");
            }
         }

         // add padding here if needed
         if (logger.isLoggable(Level.FINEST)) {
            logger.finest("sizeWritten = " + sizeWritten + " totalStructSize= " + totalStructSize);
         }

         assert sizeWritten <= totalStructSize : "wrote too much into buffer";

         while (sizeWritten < totalStructSize) {
            // skip over pad bytes
            arg.objArrayByteBuffer.get();
            sizeWritten++;
         }
      }
   }

   private void restoreObjects() throws AparapiException {
      for (int i = 0; i < argc; i++) {
         KernelArg arg = args[i];
         if ((arg.type & ARG_OBJ_ARRAY_STRUCT) != 0) {
            extractOopConversionBuffer(arg);
         }
      }
   }

   private boolean updateKernelArrayRefs(Object lambdaObject) throws AparapiException {
      boolean needsSync = false;

      for (int i = 0; i < argc; i++) {
         KernelArg arg = args[i];
         try {
            if ((arg.type & ARG_ARRAY) != 0) {
               Object newArrayRef;
               newArrayRef = arg.field.get(lambdaObject);

               if (newArrayRef == null) {
                  throw new IllegalStateException("Cannot send null refs to kernel, reverting to java");
               }

               if ((arg.type & ARG_OBJ_ARRAY_STRUCT) != 0) {
                  prepareOopConversionBuffer(arg);
               } else {
                  // set up JNI fields for normal arrays
                  arg.javaArray = newArrayRef;
                  arg.numElements = Array.getLength(newArrayRef);
                  arg.sizeInBytes = arg.numElements * arg.primitiveSize;

                  if (((args[i].type & ARG_EXPLICIT) != 0) && puts.contains(newArrayRef)) {
                     args[i].type |= ARG_EXPLICIT_WRITE;
                     // System.out.println("detected an explicit write " + args[i].name);
                     puts.remove(newArrayRef);
                  }
               }
               if (newArrayRef != arg.array) {
                  needsSync = true;
                  if (logger.isLoggable(Level.FINE)) {
                     logger.fine("saw newArrayRef for " + arg.name + " = " + newArrayRef + ", newArrayLen = "
                           + Array.getLength(newArrayRef));
                  }
               }
               arg.array = newArrayRef;
               assert arg.array != null : "null array ref";
            }
         } catch (IllegalArgumentException e) {
            e.printStackTrace();
         } catch (IllegalAccessException e) {
            e.printStackTrace();
         }
      }
      return needsSync;
   }

      
//   /**
//    * There is a new Block for each invocation of the lambda
//    * @param callerBlock
//    */
//   private void updateCallerBlockParams(Object callerBlock) {
//      currentCallerBlock = callerBlock;
//      for(int i=0; i<argc; i++) {
//         if (args[i].fieldHolder instanceof IntBlock) {
//            if (logger.isLoggable(Level.FINE)) {
//               logger.fine("Updated Block for " + args[i].name + " old: " + args[i].fieldHolder + " new: " + callerBlock);
//            }
//            args[i].fieldHolder = callerBlock;
//         }
//      }
//   }

   private KernelRunner executeOpenCL(Object lambdaObject, Object callerBlock, final Range _range, final int _passes) throws AparapiException {
       assert args != null : "args should not be null";

      // Read the array refs after kernel may have changed them
      // We need to do this as input to computing the localSize
      boolean needSync = updateKernelArrayRefs(lambdaObject);
      if (needSync && logger.isLoggable(Level.FINE)) {
         logger.fine("Need to resync arrays on " + lambdaObject.getClass().getName());
      }
      
      // This will need work for captured array refs?
      updateLambdaBlockJNI(jniContextHandle, callerBlock, lambdaKernelCall.getLambdaCapturedFields().length);
      
      // native side will reallocate array buffers if necessary
      if (runKernelJNI(jniContextHandle, _range, needSync, _passes) != 0) {
         logger.warning("### CL exec seems to have failed. Trying to revert to Java ###");
         throw new AparapiException ("CL exec seems to have failed. Trying to revert to Java");         
      }

      if (usesOopConversion == true) {
         restoreObjects();
      }

      if (logger.isLoggable(Level.FINEST)) {
         logger.finest("executeOpenCL completed. " + _range);
      }
      return this;
   }
   
   /**
    * This is simply used to pass the iteration variable to the kernel
    * This can be removed with better lambda codegen
    */
   final int iterationVariable = 0;
   
   
   KernelArg prepareOneArg(Field field, Object holder) {
      KernelArg   currArg = new KernelArg();

      currArg.fieldHolder = holder;
      currArg.name = field.getName();
      currArg.field = field;
      if ((field.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
         currArg.type |= ARG_STATIC;
      }

      Class<?> type = field.getType();
      if (type.isArray()) {
         
         
         // None of this is used with Lambda kernels
         
//         if (field.getAnnotation(com.amd.aparapi.Kernel.Local.class) != null
//               || currArg.name.endsWith(Kernel.LOCAL_SUFFIX)) {
//            currArg.type |= ARG_LOCAL;
//         } else if (field.getAnnotation(com.amd.aparapi.Kernel.Constant.class) != null
//               || currArg.name.endsWith(Kernel.CONSTANT_SUFFIX)) {
//            currArg.type |= ARG_CONSTANT;
//         } else {
//            currArg.type |= ARG_GLOBAL;
//         }
         currArg.type |= ARG_GLOBAL;

         currArg.array = null; // will get updated in updateKernelArrayRefs
         currArg.type |= ARG_ARRAY;

//         if (isExplicit()) {
//            currArg.type |= ARG_EXPLICIT;
//         }

         // for now, treat all write arrays as read-write, see bugzilla issue 4859
         // we might come up with a better solution later
         currArg.type |= entryPoint.getArrayFieldAssignments().contains(field.getName()) ? (ARG_WRITE | ARG_READ)
               : 0;
         currArg.type |= entryPoint.getArrayFieldAccesses().contains(field.getName()) ? ARG_READ : 0;
         // currArg.type |= ARG_GLOBAL;
         currArg.type |= type.isAssignableFrom(float[].class) ? ARG_FLOAT : 0;

         currArg.type |= type.isAssignableFrom(int[].class) ? ARG_INT : 0;

         currArg.type |= type.isAssignableFrom(boolean[].class) ? ARG_BOOLEAN : 0;

         currArg.type |= type.isAssignableFrom(byte[].class) ? ARG_BYTE : 0;

         currArg.type |= type.isAssignableFrom(char[].class) ? ARG_CHAR : 0;

         currArg.type |= type.isAssignableFrom(double[].class) ? ARG_DOUBLE : 0;

         currArg.type |= type.isAssignableFrom(long[].class) ? ARG_LONG : 0;

         currArg.type |= type.isAssignableFrom(short[].class) ? ARG_SHORT : 0;

         // arrays whose length is used will have an int arg holding
         // the length as a kernel param
         if (entryPoint.getArrayFieldArrayLengthUsed().contains(currArg.name)) {
            currArg.type |= ARG_ARRAYLENGTH;
         }

         if (type.getName().startsWith("[L")) {
            currArg.type |= (ARG_OBJ_ARRAY_STRUCT | ARG_WRITE | ARG_READ);
            if (logger.isLoggable(Level.FINE)) {
               logger.fine("tagging " + currArg.name + " as (ARG_OBJ_ARRAY_STRUCT | ARG_WRITE | ARG_READ)");
            }
         }
      } else if (type.isAssignableFrom(float.class)) {
         currArg.type |= ARG_PRIMITIVE;
         currArg.type |= ARG_FLOAT;
      } else if (type.isAssignableFrom(int.class)) {
         currArg.type |= ARG_PRIMITIVE;
         currArg.type |= ARG_INT;
      } else if (type.isAssignableFrom(double.class)) {
         currArg.type |= ARG_PRIMITIVE;
         currArg.type |= ARG_DOUBLE;
      } else if (type.isAssignableFrom(long.class)) {
         currArg.type |= ARG_PRIMITIVE;
         currArg.type |= ARG_LONG;
      } else if (type.isAssignableFrom(boolean.class)) {
         currArg.type |= ARG_PRIMITIVE;
         currArg.type |= ARG_BOOLEAN;
      } else if (type.isAssignableFrom(byte.class)) {
         currArg.type |= ARG_PRIMITIVE;
         currArg.type |= ARG_BYTE;
      } else if (type.isAssignableFrom(char.class)) {
         currArg.type |= ARG_PRIMITIVE;
         currArg.type |= ARG_CHAR;
      } else if (type.isAssignableFrom(short.class)) {
         currArg.type |= ARG_PRIMITIVE;
         currArg.type |= ARG_SHORT;
      }
      // System.out.printf("in execute, arg %d %s %08x\n", i,currArg.name,currArg.type );

      currArg.primitiveSize = ((currArg.type & ARG_FLOAT) != 0 ? 4 : (currArg.type & ARG_INT) != 0 ? 4
            : (currArg.type & ARG_BYTE) != 0 ? 1 : (currArg.type & ARG_CHAR) != 0 ? 2
                  : (currArg.type & ARG_BOOLEAN) != 0 ? 1 : (currArg.type & ARG_SHORT) != 0 ? 2
                        : (currArg.type & ARG_LONG) != 0 ? 8 : (currArg.type & ARG_DOUBLE) != 0 ? 8 : 0);

      if (logger.isLoggable(Level.INFO)) {
         logger.info("prepareOneArg : " + currArg.name + ", type=" + Integer.toHexString(currArg.type)
               + ", primitiveSize=" + currArg.primitiveSize);
      }

      return currArg;
   }
   
   
   KernelArg[] prepareLambdaArgs(Object lambdaObject, Object callerBlock, Field[] callerCapturedFields) {
      List<KernelArg> argsList = new ArrayList<KernelArg>();

      // Add fields in this order: 
      // 1. captured args from block, 
      // 2. iteration variable,
      // 3. references from lambda's object
      try {
         
         for (Field field : callerCapturedFields) {
            field.setAccessible(true);
            argsList.add(prepareOneArg(field, callerBlock));            
         }

         argsList.add(prepareOneArg(this.getClass().getDeclaredField("iterationVariable"), this));

         for (Field field : entryPoint.getReferencedFields()) {
            field.setAccessible(true);
            argsList.add(prepareOneArg(field, lambdaObject));            
         }

      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }
      return argsList.toArray(new KernelArg[0]);
   }
   
   // Hope for the best!
   boolean runnable = true;
   public void setRunnable(boolean b) { runnable = b; }
   public boolean getRunnable() { return runnable; }


   synchronized boolean execute(Object callerBlock, final Range _range, final int _passes) throws AparapiException {

      long executeStartTime = System.currentTimeMillis();
      
      if (_range == null) {
         throw new IllegalStateException("range can't be null");
      }
      
      if (true) {
         // See if user supplied a Device
         Device device = _range.getDevice();

         if ((device == null) || (device instanceof OpenCLDevice)) {
            if (entryPoint == null) {

               assert lambdaKernelCall != null : "Should not be null";
               assert lambdaKernelCall.getLambdaKernelThis() != null : "Lambda This should not be null";
               Class lambdaClass = lambdaKernelCall.getLambdaKernelThis().getClass();
               ClassModel classModel = new ClassModel(lambdaClass);

               entryPoint = classModel.getEntrypoint(lambdaKernelCall.getLambdaMethodName(), 
                     lambdaKernelCall.getLambdaMethodSignature(), lambdaKernelCall.getLambdaKernelThis());

               if ((entryPoint != null) && !entryPoint.shouldFallback()) {
                  synchronized (KernelRunner.class) { // This seems to be needed because of a race condition uncovered with issue #68 http://code.google.com/p/aparapi/issues/detail?id=68
                     if (device != null && !(device instanceof OpenCLDevice)) {
                        throw new IllegalStateException("range's device is not suitable for OpenCL ");
                     }

                     OpenCLDevice openCLDevice = (OpenCLDevice) device; // still might be null! 

                     int jniFlags = 0;
                     if (openCLDevice == null) {
                        if (true) {
                           // We used to treat as before by getting first GPU device
                           // now we get the best GPU
                           openCLDevice = (OpenCLDevice) OpenCLDevice.best();
                           jniFlags |= JNI_FLAG_USE_GPU; // this flag might be redundant now. 
                        } else {
                           // We fetch the first CPU device 
                           openCLDevice = (OpenCLDevice) OpenCLDevice.firstCPU();
                           if (openCLDevice == null) {
                              throw new AparapiException ("CPU request can't be honored not CPU device");
                           }
                        }
                     } else {
                        if (openCLDevice.getType() == Device.TYPE.GPU) {
                           jniFlags |= JNI_FLAG_USE_GPU; // this flag might be redundant now. 
                        }
                     }

                     // synchronized(Kernel.class){
                     jniContextHandle = initJNI(lambdaKernelCall.getLambdaKernelThis(), openCLDevice, jniFlags); // openCLDevice will not be null here
                  } // end of synchronized! issue 68

                  if (jniContextHandle == 0) {
                     throw new AparapiException ("Can't create JNI context");
                  }

                  String extensions = getExtensionsJNI(jniContextHandle);
                  capabilitiesSet = new HashSet<String>();

                  StringTokenizer strTok = new StringTokenizer(extensions);
                  while (strTok.hasMoreTokens()) {
                     capabilitiesSet.add(strTok.nextToken());
                  }

                  if (logger.isLoggable(Level.FINE)) {
                     logger.fine("Capabilities initialized to :" + capabilitiesSet.toString());
                  }

                  if (entryPoint.requiresDoublePragma() && !hasFP64Support()) {
                     throw new AparapiException ("FP64 required but not supported");
                  }

                  if (entryPoint.requiresByteAddressableStorePragma() && !hasByteAddressableStoreSupport()) {
                     throw new AparapiException("Byte addressable stores required but not supported");
                  }

                  boolean all32AtomicsAvailable = hasGlobalInt32BaseAtomicsSupport() && hasGlobalInt32ExtendedAtomicsSupport()
                        && hasLocalInt32BaseAtomicsSupport() && hasLocalInt32ExtendedAtomicsSupport();

                  if (entryPoint.requiresAtomic32Pragma() && !all32AtomicsAvailable) {
                     throw new AparapiException("32 bit Atomics required but not supported");
                  }

                  entryPoint.setLambdaActualParamsCount(lambdaKernelCall.getLambdaCapturedFields().length);
                  String openCL = null;
                  //try {
                  openCL = KernelWriter.writeToString(entryPoint);
                  //} catch (CodeGenException codeGenException) {
                  //   return warnFallBackAndExecute(lambdaObject, _entrypointName, _entrypointSignature, capturedArgs, _range, _passes, codeGenException);
                  //}

                  if (Config.enableShowGeneratedOpenCL) {
                     System.out.println(openCL);
                  }

                  if (logger.isLoggable(Level.INFO)) {
                     logger.info(openCL);
                  }

                  // Send the string to OpenCL to compile it
                  if (buildProgramJNI(jniContextHandle, openCL) == 0) {
                     //return warnFallBackAndExecute(lambdaObject, _entrypointName, _entrypointSignature, capturedArgs, _range, _passes, 
                     //"OpenCL compile failed");
                     throw new AparapiException("OpenCL compile failed");
                  }

                  args = prepareLambdaArgs(lambdaKernelCall.getLambdaKernelThis(), callerBlock, lambdaKernelCall.getLambdaCapturedFields());

                  argc = args.length;

                  setArgsJNI(jniContextHandle, args, argc);

                  conversionTime = System.currentTimeMillis() - executeStartTime;

                  executeOpenCL(lambdaKernelCall.getLambdaKernelThis(), callerBlock, _range, _passes);

                  if (logger.isLoggable(Level.INFO)) {
                     logger.info("First run done. ");
                  }

               } else {
                  throw new AparapiException("failed to locate entrypoint");

               }
            } else {

               executeOpenCL(lambdaKernelCall.getLambdaKernelThis(), callerBlock, _range, _passes);
            }
         } else {
              throw new AparapiException("OpenCL was requested but Device supplied was not an OpenCLDevice");
         }
      }
      
      executionTime = System.currentTimeMillis() - executeStartTime;
      accumulatedExecutionTime += executionTime;

      return true;
   }

   private Set<Object> puts = new HashSet<Object>();

   private native int getJNI(long _jniContextHandle, Object _array);

   /**
    * Enqueue a request to return this array from the GPU. This method blocks until the array is available.
    * <br/>
    * Note that <code>Kernel.put(type [])</code> calls will delegate to this call.
    * <br/>
    * Package protected
    * 
    * @param array
    *          It is assumed that this parameter is indeed an array (of int, float, short etc).
    * 
    * @see Kernel#get(int[] arr)
    * @see Kernel#get(short[] arr)
    * @see Kernel#get(float[] arr)
    * @see Kernel#get(double[] arr)
    * @see Kernel#get(long[] arr)
    * @see Kernel#get(char[] arr)
    * @see Kernel#get(boolean[] arr)
    */
   
//   protected void get(Object array) {
//      if (explicit
//            && ((kernel.getExecutionMode() == Kernel.EXECUTION_MODE.GPU) || (kernel.getExecutionMode() == Kernel.EXECUTION_MODE.CPU))) {
//         // Only makes sense when we are using OpenCL
//         getJNI(jniContextHandle, array);
//      }
//   }

//   protected List<ProfileInfo> getProfileInfo() {
//      if (((kernel.getExecutionMode() == Kernel.EXECUTION_MODE.GPU) || (kernel.getExecutionMode() == Kernel.EXECUTION_MODE.CPU))) {
//         // Only makes sense when we are using OpenCL
//         return (getProfileInfoJNI(jniContextHandle));
//      } else {
//         return (null);
//      }
//   }

   /**
    * Tag this array so that it is explicitly enqueued before the kernel is executed. <br/>
    * Note that <code>Kernel.put(type [])</code> calls will delegate to this call. <br/>
    * Package protected
    * 
    * @param array
    *          It is assumed that this parameter is indeed an array (of int, float, short etc).
    * @see Kernel#put(int[] arr)
    * @see Kernel#put(short[] arr)
    * @see Kernel#put(float[] arr)
    * @see Kernel#put(double[] arr)
    * @see Kernel#put(long[] arr)
    * @see Kernel#put(char[] arr)
    * @see Kernel#put(boolean[] arr)
    */

//   protected void put(Object array) {
//      if (explicit
//            && ((kernel.getExecutionMode() == Kernel.EXECUTION_MODE.GPU) || (kernel.getExecutionMode() == Kernel.EXECUTION_MODE.CPU))) {
//         // Only makes sense when we are using OpenCL
//         puts.add(array);
//      }
//   }

//   private boolean explicit = false;
//
//   protected void setExplicit(boolean _explicit) {
//      explicit = _explicit;
//   }
//
//   protected boolean isExplicit() {
//      return (explicit);
//   }

   /**
    * Determine the time taken to convert bytecode to OpenCL for first Kernel.execute(range) call.
    * 
    * @return The time spent preparing the kernel for execution using GPU
    * 
    */
   public long getConversionTime() {
      return conversionTime;
   }

   /**
    * Determine the execution time of the previous Kernel.execute(range) call.
    * 
    * @return The time spent executing the kernel (ms)
    * 
    */
   public long getExecutionTime() {
      return executionTime;
   }

   /**
    * Determine the accumulated execution time of all previous Kernel.execute(range) calls.
    * 
    * @return The accumulated time spent executing this kernel (ms)
    * 
    */
   public long getAccumulatedExecutionTime() {
      return accumulatedExecutionTime;
   }

}
