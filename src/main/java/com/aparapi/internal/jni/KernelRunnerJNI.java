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

import com.aparapi.Kernel;
import com.aparapi.ProfileInfo;
import com.aparapi.Range;
import com.aparapi.annotation.Experimental;
import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.annotation.DocMe;
import com.aparapi.internal.annotation.UsedByJNICode;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * This class is intended to be used as a 'proxy' or 'facade' object for Java code to interact with JNI
 */
public abstract class KernelRunnerJNI{

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> represents a <code>boolean</code> type (array or primitive).
    * 
    * 
    * @see com.aparapi.internal.annotation.UsedByJNICode
    * 
    * @author gfrost
    */
   @UsedByJNICode protected static final int ARG_BOOLEAN = 1 << 0;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> represents a <code>byte</code> type (array or primitive).
    * 
    * 
    * @see com.aparapi.internal.annotation.UsedByJNICode
    * 
    * @author gfrost
    */
   @UsedByJNICode protected static final int ARG_BYTE = 1 << 1;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> represents a <code>float</code> type (array or primitive).
    * 
    * 
    * @see com.aparapi.internal.annotation.UsedByJNICode
    * 
    * @author gfrost
    */
   @UsedByJNICode protected static final int ARG_FLOAT = 1 << 2;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> represents a <code>int</code> type (array or primitive).
    * 
    * 
    * @see com.aparapi.internal.annotation.UsedByJNICode
    * 
    * @author gfrost
    */
   @UsedByJNICode protected static final int ARG_INT = 1 << 3;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> represents a <code>double</code> type (array or primitive).
    * 
    * 
    * @see com.aparapi.internal.annotation.UsedByJNICode
    * 
    * @author gfrost
    */
   @UsedByJNICode protected static final int ARG_DOUBLE = 1 << 4;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> represents a <code>long</code> type (array or primitive).
    * 
    * 
    * @see com.aparapi.internal.annotation.UsedByJNICode
    * 
    * @author gfrost
    */
   @UsedByJNICode protected static final int ARG_LONG = 1 << 5;

   /**
    * TODO:
    * 
    * @see com.aparapi.internal.annotation.UsedByJNICode
    * 
    * @author gfrost
    */
   @UsedByJNICode protected static final int ARG_SHORT = 1 << 6;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> represents an array.<br/>
    * So <code>ARG_ARRAY|ARG_INT</code> tells us this arg is an array of <code>int</code>.
    * 
    * 
    * @see com.aparapi.internal.annotation.UsedByJNICode
    * 
    * @author gfrost
    */
   @UsedByJNICode protected static final int ARG_ARRAY = 1 << 7;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> represents a primitive (non array).<br/>
    * So <code>ARG_PRIMITIVE|ARG_INT</code> tells us this arg is a primitive <code>int</code>.
    * 
    * 
    * @see com.aparapi.internal.annotation.UsedByJNICode
    * 
    * @author gfrost
    */
   @UsedByJNICode protected static final int ARG_PRIMITIVE = 1 << 8;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> is read by the Kernel (note from the Kernel's point of view).<br/>
    * So <code>ARG_ARRAY|ARG_INT|ARG_READ</code> tells us this arg is an array of int's that are read by the kernel.
    * 
    * 
    * @see com.aparapi.internal.annotation.UsedByJNICode
    * 
    * @author gfrost
    */
   @UsedByJNICode protected static final int ARG_READ = 1 << 9;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> is mutated by the Kernel (note from the Kernel's point of view).<br/>
    * So <code>ARG_ARRAY|ARG_INT|ARG_WRITE</code> tells us this arg is an array of int's that we expect the kernel to mutate.
    * 
    * 
    * @see com.aparapi.internal.annotation.UsedByJNICode
    * 
    * @author gfrost
    */
   @UsedByJNICode protected static final int ARG_WRITE = 1 << 10;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> resides in local memory in the generated OpenCL code.<br/>
    * 
    * 
    * @see com.aparapi.internal.annotation.UsedByJNICode
    * @see com.aparapi.annotation.Experimental
    * 
    * @author gfrost
    */
   @Experimental @UsedByJNICode protected static final int ARG_LOCAL = 1 << 11;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> resides in global memory in the generated OpenCL code.<br/>
    * 
    * 
    * @see com.aparapi.internal.annotation.UsedByJNICode
    * @see com.aparapi.annotation.Experimental
    * 
    * @author gfrost
    */
   @Experimental @UsedByJNICode protected static final int ARG_GLOBAL = 1 << 12;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> resides in constant memory in the generated OpenCL code.<br/>
    * 
    * 
    * @see com.aparapi.internal.annotation.UsedByJNICode
    * @see com.aparapi.annotation.Experimental
    * 
    * @author gfrost
    */
   @Experimental @UsedByJNICode protected static final int ARG_CONSTANT = 1 << 13;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> has it's length reference, in which case a synthetic arg is passed (name mangled) to the OpenCL kernel.<br/>
    * 
    * 
    * @see com.aparapi.internal.annotation.UsedByJNICode
    * 
    * @author gfrost
    */
   @UsedByJNICode protected static final int ARG_ARRAYLENGTH = 1 << 14;

   /**
    * TODO:
    * 
    * @see com.aparapi.internal.annotation.UsedByJNICode
    * 
    * @author gfrost
    */
   @UsedByJNICode protected static final int ARG_APARAPI_BUFFER = 1 << 15;

   /**
    * This 'bit' indicates that the arg has been explicitly marked for reading
    * 
    * @see com.aparapi.internal.annotation.UsedByJNICode
    * 
    * @author gfrost
    */
   @UsedByJNICode protected static final int ARG_EXPLICIT = 1 << 16;

   /**
    * This 'bit' indicates that the arg has been explicitly marked for writing
    * 
    * @see com.aparapi.internal.annotation.UsedByJNICode
    * 
    * @author gfrost
    */
   @UsedByJNICode protected static final int ARG_EXPLICIT_WRITE = 1 << 17;

   /**
    * TODO:
    * 
    * @see com.aparapi.internal.annotation.UsedByJNICode
    * 
    * @author gfrost
    */
   @UsedByJNICode protected static final int ARG_OBJ_ARRAY_STRUCT = 1 << 18;


   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> represents a <code>char</code> type (array or primitive).
    * 
    * 
    * @see com.aparapi.internal.annotation.UsedByJNICode
    * 
    * @author rlamothe
    */
   @UsedByJNICode protected static final int ARG_CHAR = 1 << 21;

   /**
    * This 'bit' indicates that a particular <code>KernelArg</code> represents a <code>static</code> field (array or primitive).
    * 
    * 
    * @see com.aparapi.internal.annotation.UsedByJNICode
    * 
    * @author gfrost
    */
   @UsedByJNICode protected static final int ARG_STATIC = 1 << 22;

   /**
    * This 'bit' indicates that we wish to enable profiling from the JNI code.
    * 
    * @see com.aparapi.annotations.UsedByJNICode
    * 
    * @author gfrost
    */
   //@UsedByJNICode protected static final int JNI_FLAG_ENABLE_PROFILING = 1 << 0;

   /**
    * This 'bit' indicates that we wish to store profiling information in a CSV file from JNI code.
    * 
    * @see com.aparapi.annotations.UsedByJNICode
    * 
    * @author gfrost
    */
   // @UsedByJNICode protected static final int JNI_FLAG_ENABLE_PROFILING_CSV = 1 << 1;

   /**
    * This 'bit' indicates that we want to execute on the GPU.
    * 
    * Be careful changing final constants starting with JNI.<br/>
    * 
    * @see com.aparapi.internal.annotation.UsedByJNICode
    * 
    * @author gfrost
    */
   @UsedByJNICode protected static final int JNI_FLAG_USE_GPU = 1 << 2;

   /**
    * This 'bit' indicates that we wish to enable verbose JNI layer messages to stderr.<br/>
    * 
    * @see com.aparapi.annotations.UsedByJNICode
    * 
    * @author gfrost
    */
   // @UsedByJNICode protected static final int JNI_FLAG_ENABLE_VERBOSE_JNI = 1 << 3;

   /**
    * This 'bit' indicates that we wish to enable OpenCL resource tracking by JNI layer to be written to stderr.<br/>
    * 
    * @see com.aparapi.annotations.UsedByJNICode
    * @see com.aparapi.annotations.Experimental
    * 
    * @author gfrost
    */
   //  @UsedByJNICode @Annotations.Experimental protected static final int JNI_FLAG_ENABLE_VERBOSE_JNI_OPENCL_RESOURCE_TRACKING = 1 << 4;
   
   /**
    * This 'bit' indicates that we want to execute on the Acceleratr.
    * 
    * Be careful changing final constants starting with JNI.<br/>
    * 
    * @see com.aparapi.internal.annotation.UsedByJNICode
    * 
    * @author ekasit
    */
   @UsedByJNICode protected static final int JNI_FLAG_USE_ACC = 1 << 5;


   /*
    * Native methods
    */

   /**
    * TODO:
    * 
    * synchronized to avoid race in clGetPlatformIDs() in OpenCL lib problem should fixed in some future OpenCL version
    * 
    * @param _kernel
    * @param _device
    * @param _flags
    * @return
    */
   @DocMe protected native synchronized long initJNI(Kernel _kernel, OpenCLDevice _device, int _flags);

   protected native int getJNI(long _jniContextHandle, Object _array);

   /**
    * @param _source The OpenCL source code to compile, which may be sent empty if the binary for that source code is known to be cached on the JNI side
    *                under the key {@code _binaryKey}.
    * @param _binaryKey A key which embodies a Kernel class and a Device, under which the JNI side will cache the compiled binary corresponding to that Kernel/Device
    *                   pair. Once a certain _binaryKey has been passed to this method once, further calls to this method with that key will ignore the _source (which
    *                   can be passed empty) andused the cached binary.
    *                   <p>By passing an empty String as the _binaryKey, the entire JNI-side binary caching apparatus can be disabled.
    */
   protected native long buildProgramJNI(long _jniContextHandle, String _source, String _binaryKey);

   protected native int setArgsJNI(long _jniContextHandle, KernelArgJNI[] _args, int argc);

   protected native int runKernelJNI(long _jniContextHandle, Range _range, boolean _needSync, int _passes, ByteBuffer _inBuffer, ByteBuffer _outBuffer);

   protected native int disposeJNI(long _jniContextHandle);

   protected native String getExtensionsJNI(long _jniContextHandle);

   protected native synchronized List<ProfileInfo> getProfileInfoJNI(long _jniContextHandle);
}
