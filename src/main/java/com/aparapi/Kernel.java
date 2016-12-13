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

import com.aparapi.annotation.Experimental;
import com.aparapi.exception.DeprecatedException;
import com.aparapi.internal.model.CacheEnabler;
import com.aparapi.internal.model.ClassModel.ConstantPool.MethodReferenceEntry;
import com.aparapi.internal.model.ClassModel.ConstantPool.NameAndTypeEntry;
import com.aparapi.internal.model.ValueCache;
import com.aparapi.internal.model.ValueCache.ThrowingValueComputer;
import com.aparapi.internal.model.ValueCache.ValueComputer;
import com.aparapi.internal.opencl.OpenCLLoader;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Logger;

import com.aparapi.device.Device;
import com.aparapi.device.JavaDevice;
import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.kernel.KernelArg;
import com.aparapi.internal.kernel.KernelManager;
import com.aparapi.internal.kernel.KernelProfile;
import com.aparapi.internal.kernel.KernelRunner;
import com.aparapi.internal.util.Reflection;
import com.aparapi.internal.util.UnsafeWrapper;

/**
 * A <i>kernel</i> encapsulates a data parallel algorithm that will execute either on a GPU
 * (through conversion to OpenCL) or on a CPU via a Java Thread Pool.
 * <p>
 * To write a new kernel, a developer extends the <code>Kernel</code> class and overrides the <code>Kernel.run()</code> method.
 * To execute this kernel, the developer creates a new instance of it and calls <code>Kernel.execute(int globalSize)</code> with a suitable 'global size'. At runtime
 * Aparapi will attempt to convert the <code>Kernel.run()</code> method (and any method called directly or indirectly
 * by <code>Kernel.run()</code>) into OpenCL for execution on GPU devices made available via the OpenCL platform.
 * <p>
 * Note that <code>Kernel.run()</code> is not called directly. Instead,
 * the <code>Kernel.execute(int globalSize)</code> method will cause the overridden <code>Kernel.run()</code>
 * method to be invoked once for each value in the range <code>0...globalSize</code>.
 * <p>
 * On the first call to <code>Kernel.execute(int _globalSize)</code>, Aparapi will determine the EXECUTION_MODE of the kernel.
 * This decision is made dynamically based on two factors:
 * <ol>
 * <li>Whether OpenCL is available (appropriate drivers are installed and the OpenCL and Aparapi dynamic libraries are included on the system path).</li>
 * <li>Whether the bytecode of the <code>run()</code> method (and every method that can be called directly or indirectly from the <code>run()</code> method)
 *  can be converted into OpenCL.</li>
 * </ol>
 * <p>
 * Below is an example Kernel that calculates the square of a set of input values.
 * <p>
 * <blockquote><pre>
 *     class SquareKernel extends Kernel{
 *         private int values[];
 *         private int squares[];
 *         public SquareKernel(int values[]){
 *            this.values = values;
 *            squares = new int[values.length];
 *         }
 *         public void run() {
 *             int gid = getGlobalID();
 *             squares[gid] = values[gid]*values[gid];
 *         }
 *         public int[] getSquares(){
 *             return(squares);
 *         }
 *     }
 * </pre></blockquote>
 * <p>
 * To execute this kernel, first create a new instance of it and then call <code>execute(Range _range)</code>.
 * <p>
 * <blockquote><pre>
 *     int[] values = new int[1024];
 *     // fill values array
 *     Range range = Range.create(values.length); // create a range 0..1024
 *     SquareKernel kernel = new SquareKernel(values);
 *     kernel.execute(range);
 * </pre></blockquote>
 * <p>
 * When <code>execute(Range)</code> returns, all the executions of <code>Kernel.run()</code> have completed and the results are available in the <code>squares</code> array.
 * <p>
 * <blockquote><pre>
 *     int[] squares = kernel.getSquares();
 *     for (int i=0; i< values.length; i++){
 *        System.out.printf("%4d %4d %8d\n", i, values[i], squares[i]);
 *     }
 * </pre></blockquote>
 * <p>
 * A different approach to creating kernels that avoids extending Kernel is to write an anonymous inner class:
 * <p>
 * <blockquote><pre>
 *
 *     final int[] values = new int[1024];
 *     // fill the values array
 *     final int[] squares = new int[values.length];
 *     final Range range = Range.create(values.length);
 *
 *     Kernel kernel = new Kernel(){
 *         public void run() {
 *             int gid = getGlobalID();
 *             squares[gid] = values[gid]*values[gid];
 *         }
 *     };
 *     kernel.execute(range);
 *     for (int i=0; i< values.length; i++){
 *        System.out.printf("%4d %4d %8d\n", i, values[i], squares[i]);
 *     }
 *
 * </pre></blockquote>
 * <p>
 *
 * @author  gfrost AMD Javalabs
 * @version Alpha, 21/09/2010
 */
public abstract class Kernel implements Cloneable {

   private static Logger logger = Logger.getLogger(Config.getLoggerName());

   /**
    *  We can use this Annotation to 'tag' intended local buffers.
    *
    *  So we can either annotate the buffer
    *  <pre><code>
    *  &#64Local int[] buffer = new int[1024];
    *  </code></pre>
    *   Or use a special suffix
    *  <pre><code>
    *  int[] buffer_$local$ = new int[1024];
    *  </code></pre>
    *
    *  @see #LOCAL_SUFFIX
    *
    *
    */
   @Retention(RetentionPolicy.RUNTIME)
   public @interface Local {

   }

   /**
    *  We can use this Annotation to 'tag' intended constant buffers.
    *
    *  So we can either annotate the buffer
    *  <pre><code>
    *  &#64Constant int[] buffer = new int[1024];
    *  </code></pre>
    *   Or use a special suffix
    *  <pre><code>
    *  int[] buffer_$constant$ = new int[1024];
    *  </code></pre>
    *
    *  @see #LOCAL_SUFFIX
    *
    *
    */
   @Retention(RetentionPolicy.RUNTIME)
   public @interface Constant {

   }

   /**
    *
    *  We can use this Annotation to 'tag' __private (unshared) array fields. Data in the __private address space in OpenCL is accessible only from
    *  the current kernel instance.
    *
    *  To so mark a field with a buffer size of 99, we can either annotate the buffer
    *  <pre><code>
    *  &#64PrivateMemorySpace(99) int[] buffer = new int[99];
    *  </code></pre>
    *   Or use a special suffix
    *  <pre><code>
    *  int[] buffer_$private$99 = new int[99];
    *  </code></pre>
    *
    *  <p>Note that any code which must be runnable in {@link EXECUTION_MODE#JTP} will fail to work correctly if it uses such an
    *  array, as the array will be shared by all threads. The solution is to create a {@link NoCL} method called at the start of {@link #run()} which sets
    *  the field to an array returned from a static <code>ThreadLocal<foo[]></code></p>. Please see <code>MedianKernel7x7</code> in the samples for an example.
    *
    *  @see #PRIVATE_SUFFIX
    */
   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.FIELD})
   public @interface PrivateMemorySpace {
      /** Size of the array used as __private buffer. */
      int value();
   }

   /**
    * Annotation which can be applied to either a getter (with usual java bean naming convention relative to an instance field), or to any method
    * with void return type, which prevents both the method body and any calls to the method being emitted in the generated OpenCL. (In the case of a getter, the
    * underlying field is used in place of the NoCL getter method.) This allows for code specialization within a java/JTP execution path, for example to
    * allow logging/breakpointing when debugging, or to apply ThreadLocal processing (see {@link PrivateMemorySpace}) in java to simulate OpenCL __private
    * memory.
    */
   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.METHOD, ElementType.FIELD})
   public @interface NoCL {
      // empty
   }

   /**
    *  We can use this suffix to 'tag' intended local buffers.
    *
    *
    *  So either name the buffer
    *  <pre><code>
    *  int[] buffer_$local$ = new int[1024];
    *  </code></pre>
    *  Or use the Annotation form
    *  <pre><code>
    *  &#64Local int[] buffer = new int[1024];
    *  </code></pre>
    */
   public final static String LOCAL_SUFFIX = "_$local$";

   /**
    *  We can use this suffix to 'tag' intended constant buffers.
    *
    *
    *  So either name the buffer
    *  <pre><code>
    *  int[] buffer_$constant$ = new int[1024];
    *  </code></pre>
    *  Or use the Annotation form
    *  <pre><code>
    *  &#64Constant int[] buffer = new int[1024];
    *  </code></pre>
    */
   public final static String CONSTANT_SUFFIX = "_$constant$";

   /**
    *  We can use this suffix to 'tag' __private buffers.
    *
    *  <p>So either name the buffer
    *  <pre><code>
    *  int[] buffer_$private$32 = new int[32];
    *  </code></pre>
    *  Or use the Annotation form
    *  <pre><code>
    *  &#64PrivateMemorySpace(32) int[] buffer = new int[32];
    *  </code></pre>
    *
    *  @see PrivateMemorySpace for a more detailed usage summary
    */
   public final static String PRIVATE_SUFFIX = "_$private$";

   /**
    * This annotation is for internal use only
    */
   @Retention(RetentionPolicy.RUNTIME)
   protected @interface OpenCLDelegate {

   }

   /**
    * This annotation is for internal use only
    */
   @Retention(RetentionPolicy.RUNTIME)
   protected @interface OpenCLMapping {
      String mapTo() default "";

      boolean atomic32() default false;

      boolean atomic64() default false;
   }

   public abstract class Entry {
      public abstract void run();

      public Kernel execute(Range _range) {
         return (Kernel.this.execute("foo", _range, 1));
      }
   }

   /**
    * @deprecated It is no longer recommended that {@code EXECUTION_MODE}s are used, as a more sophisticated {@link com.aparapi.device.Device}
    * preference mechanism is in place, see {@link com.aparapi.internal.kernel.KernelManager}. Though {@link #setExecutionMode(EXECUTION_MODE)}
    * is still honored, the default EXECUTION_MODE is now {@link EXECUTION_MODE#AUTO}, which indicates that the KernelManager
    * will determine execution behaviours.
    *
    * <p>
    * The <i>execution mode</i> ENUM enumerates the possible modes of executing a kernel.
    * One can request a mode of execution using the values below, and query a kernel after it first executes to
    * determine how it executed.
    *
    * <p>
    * Aparapi supports 5 execution modes. Default is GPU.
    * <ul>
    * <table>
    * <tr><th align="left">Enum value</th><th align="left">Execution</th></tr>
    * <tr><td><code><b>GPU</b></code></td><td>Execute using OpenCL on first available GPU device</td></tr>
    * <tr><td><code><b>ACC</b></code></td><td>Execute using OpenCL on first available Accelerator device</td></tr>
    * <tr><td><code><b>CPU</b></code></td><td>Execute using OpenCL on first available CPU device</td></tr>
    * <tr><td><code><b>JTP</b></code></td><td>Execute using a Java Thread Pool (one thread spawned per available core)</td></tr>
    * <tr><td><code><b>SEQ</b></code></td><td>Execute using a single loop. This is useful for debugging but will be less
    * performant than the other modes</td></tr>
    * </table>
    * </ul>
    * <p>
    * To request that a kernel is executed in a specific mode, call <code>Kernel.setExecutionMode(EXECUTION_MODE)</code> before the
    *  kernel first executes.
    * <p>
    * <blockquote><pre>
    *     int[] values = new int[1024];
    *     // fill values array
    *     SquareKernel kernel = new SquareKernel(values);
    *     kernel.setExecutionMode(Kernel.EXECUTION_MODE.JTP);
    *     kernel.execute(values.length);
    * </pre></blockquote>
    * <p>
    * Alternatively, the property <code>com.aparapi.executionMode</code> can be set to one of <code>JTP,GPU,ACC,CPU,SEQ</code>
    * when an application is launched. 
    * <p><blockquote><pre>
    *    java -classpath ....;aparapi.jar -Dcom.aparapi.executionMode=GPU MyApplication
    * </pre></blockquote><p>
    * Generally setting the execution mode is not recommended (it is best to let Aparapi decide automatically) but the option
    * provides a way to compare a kernel's performance under multiple execution modes.
    *
    * @author  gfrost AMD Javalabs
    * @version Alpha, 21/09/2010
    */
   @Deprecated
   public static enum EXECUTION_MODE {
      /**
       *
       */
      AUTO,
      /**
       * A dummy value to indicate an unknown state.
       */
      NONE,
      /**
       * The value representing execution on a GPU device via OpenCL.
       */
      GPU,
      /**
       * The value representing execution on a CPU device via OpenCL.
       * <p>
       * <b>Note</b> not all OpenCL implementations support OpenCL compute on the CPU.
       */
      CPU,
      /**
       * The value representing execution on a Java Thread Pool.
       * <p>
       * By default one Java thread is started for each available core and each core will execute <code>globalSize/cores</code> work items.
       * This creates a total of <code>globalSize%cores</code> threads to complete the work.
       * Choose suitable values for <code>globalSize</code> to minimize the number of threads that are spawned.
       */
      JTP,
      /**
       * The value representing execution sequentially in a single loop.
       * <p>
       * This is meant to be used for debugging a kernel.
       */
      SEQ,
      /**
       * The value representing execution on an accelerator device (Xeon Phi) via OpenCL.
       */
      ACC;

      /**
       * @deprecated See {@link EXECUTION_MODE}.
       */
      @Deprecated
      static LinkedHashSet<EXECUTION_MODE> getDefaultExecutionModes() {
         LinkedHashSet<EXECUTION_MODE> defaultExecutionModes = new LinkedHashSet<EXECUTION_MODE>();

         if (OpenCLLoader.isOpenCLAvailable()) {
            defaultExecutionModes.add(GPU);
            defaultExecutionModes.add(JTP);
         } else {
            defaultExecutionModes.add(JTP);
         }

         final String executionMode = Config.executionMode;

         if (executionMode != null) {
            try {
               LinkedHashSet<EXECUTION_MODE> requestedExecutionModes;
               requestedExecutionModes = EXECUTION_MODE.getExecutionModeFromString(executionMode);
               logger.fine("requested execution mode =");
               for (final EXECUTION_MODE mode : requestedExecutionModes) {
                  logger.fine(" " + mode);
               }
               if ((OpenCLLoader.isOpenCLAvailable() && EXECUTION_MODE.anyOpenCL(requestedExecutionModes))
                     || !EXECUTION_MODE.anyOpenCL(requestedExecutionModes)) {
                  defaultExecutionModes = requestedExecutionModes;
               }
            } catch (final Throwable t) {
               // we will take the default
            }
         }

         logger.info("default execution modes = " + defaultExecutionModes);

         for (final EXECUTION_MODE e : defaultExecutionModes) {
            logger.info("SETTING DEFAULT MODE: " + e.toString());
         }

         return (defaultExecutionModes);
      }

      static LinkedHashSet<EXECUTION_MODE> getExecutionModeFromString(String executionMode) {
         final LinkedHashSet<EXECUTION_MODE> executionModes = new LinkedHashSet<EXECUTION_MODE>();
         for (final String mode : executionMode.split(",")) {
            executionModes.add(valueOf(mode.toUpperCase()));
         }
         return executionModes;
      }

      static EXECUTION_MODE getFallbackExecutionMode() {
         final EXECUTION_MODE defaultFallbackExecutionMode = JTP;
         logger.info("fallback execution mode = " + defaultFallbackExecutionMode);
         return (defaultFallbackExecutionMode);
      }

      static boolean anyOpenCL(LinkedHashSet<EXECUTION_MODE> _executionModes) {
         for (final EXECUTION_MODE mode : _executionModes) {
            if ((mode == GPU) || (mode == ACC) || (mode == CPU)) {
               return true;
            }
         }
         return false;
      }

      public boolean isOpenCL() {
         return (this == GPU) || (this == ACC) || (this == CPU);
      }
   };

   ////////////////////
   // !!! oren change -> add source/binary flow support to kernel 
   ////////////////////
   public static enum FlowType
   {
	   // flow type list
	   SOURCE(com.aparapi.internal.jni.KernelRunnerJNI.JNI_FLAG_SOURCE_FLOW),
	   BINARY(com.aparapi.internal.jni.KernelRunnerJNI.JNI_FLAG_BINARY_FLOW),
	   DEFAULT(com.aparapi.internal.jni.KernelRunnerJNI.JNI_FLAG_DEFAULT_FLOW);

	   // data store
	   int flowType;

	   FlowType(int flowType)
	   {
		   setValue(flowType);
	   }

	   FlowType(String flowTypeStr)
	   {
		   this.flowType = strToFlowType(flowTypeStr).getValue();
	   }

	   public int getValue()
	   {
		   return this.flowType;
	   }

	   private void setValue(int flowType)
	   {
		   this.flowType = flowType;
	   }

	   public static FlowType getDefaultFlowType() 
	   {
		   // if set by user try get value else set to default 
		   FlowType flowType = (Config.flowType==null) ? DEFAULT : strToFlowType(Config.flowType);
		   return flowType;
	   }

	   public static FlowType strToFlowType(final String flowTypeStr)
	   {
		   try 
		   {
			   FlowType flowType = valueOf(flowTypeStr.toUpperCase());
			   return flowType;
		   }
		   catch (Exception e)
		   {
			   logger.info("!!! bad flow type => (" + flowTypeStr + ") => reverting to default platform flow!");
			   throw e;
		   }
	   }

   }
   
   public FlowType getFlowType() {
		return kernelFlowType;
	}


	public void setFlowType(FlowType kernelFlowType) {
		this.kernelFlowType = kernelFlowType;
	}

	public void setFlowType(String flowTypeStr) {
		this.kernelFlowType = FlowType.strToFlowType(flowTypeStr);
	}
   
   private FlowType kernelFlowType = FlowType.getDefaultFlowType();
   
     
   ////////////////////
   
   private KernelRunner kernelRunner = null;

   private boolean autoCleanUpArrays = false;

   private KernelState kernelState = new KernelState();

   /**
    * This class is for internal Kernel state management<p>
    * NOT INTENDED FOR USE BY USERS
    */
   public final class KernelState {

      private int[] globalIds = new int[] {0, 0, 0};

      private int[] localIds = new int[] {0, 0, 0};

      private int[] groupIds = new int[] {0, 0, 0};

      private Range range;

      private int passId;

      private volatile CyclicBarrier localBarrier;

      private boolean localBarrierDisabled;

      /**
       * Default constructor
       */
      protected KernelState() {

      }

      /**
       * Copy constructor
       */
      protected KernelState(KernelState kernelState) {
         globalIds = kernelState.getGlobalIds();
         localIds = kernelState.getLocalIds();
         groupIds = kernelState.getGroupIds();
         range = kernelState.getRange();
         passId = kernelState.getPassId();
         localBarrier = kernelState.getLocalBarrier();
      }

      /**
       * @return the globalIds
       */
      public int[] getGlobalIds() {
         return globalIds;
      }

      /**
       * @param globalIds the globalIds to set
       */
      public void setGlobalIds(int[] globalIds) {
         this.globalIds = globalIds;
      }

      /**
       * Set a specific index value
       *
       * @param _index
       * @param value
       */
      public void setGlobalId(int _index, int value) {
         globalIds[_index] = value;
      }

      /**
       * @return the localIds
       */
      public int[] getLocalIds() {
         return localIds;
      }

      /**
       * @param localIds the localIds to set
       */
      public void setLocalIds(int[] localIds) {
         this.localIds = localIds;
      }

      /**
       * Set a specific index value
       *
       * @param _index
       * @param value
       */
      public void setLocalId(int _index, int value) {
         localIds[_index] = value;
      }

      /**
       * @return the groupIds
       */
      public int[] getGroupIds() {
         return groupIds;
      }

      /**
       * @param groupIds the groupIds to set
       */
      public void setGroupIds(int[] groupIds) {
         this.groupIds = groupIds;
      }

      /**
       * Set a specific index value
       *
       * @param _index
       * @param value
       */
      public void setGroupId(int _index, int value) {
         groupIds[_index] = value;
      }

      /**
       * @return the range
       */
      public Range getRange() {
         return range;
      }

      /**
       * @param range the range to set
       */
      public void setRange(Range range) {
         this.range = range;
      }

      /**
       * @return the passId
       */
      public int getPassId() {
         return passId;
      }

      /**
       * @param passId the passId to set
       */
      public void setPassId(int passId) {
         this.passId = passId;
      }

      /**
       * @return the localBarrier
       */
      public CyclicBarrier getLocalBarrier() {
         return localBarrier;
      }

      /**
       * @param localBarrier the localBarrier to set
       */
      public void setLocalBarrier(CyclicBarrier localBarrier) {
         this.localBarrier = localBarrier;
      }

      public void awaitOnLocalBarrier() {
         if (!localBarrierDisabled) {
            try {
               kernelState.getLocalBarrier().await();
            } catch (final InterruptedException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            } catch (final BrokenBarrierException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
         }
      }

      public void disableLocalBarrier() {
         localBarrierDisabled = true;
      }
   }

   /**
    * Determine the globalId of an executing kernel.
    * <p>
    * The kernel implementation uses the globalId to determine which of the executing kernels (in the global domain space) this invocation is expected to deal with.
    * <p>
    * For example in a <code>SquareKernel</code> implementation:
    * <p>
    * <blockquote><pre>
    *     class SquareKernel extends Kernel{
    *         private int values[];
    *         private int squares[];
    *         public SquareKernel(int values[]){
    *            this.values = values;
    *            squares = new int[values.length];
    *         }
    *         public void run() {
    *             int gid = getGlobalID();
    *             squares[gid] = values[gid]*values[gid];
    *         }
    *         public int[] getSquares(){
    *             return(squares);
    *         }
    *     }
    * </pre></blockquote>
    * <p>
    * Each invocation of <code>SquareKernel.run()</code> retrieves it's globalId by calling <code>getGlobalId()</code>, and then computes the value of <code>square[gid]</code> for a given value of <code>value[gid]</code>.
    * <p>
    * @return The globalId for the Kernel being executed
    *
    * @see #getLocalId()
    * @see #getGroupId()
    * @see #getGlobalSize()
    * @see #getNumGroups()
    * @see #getLocalSize()
    */

   @OpenCLDelegate
   protected final int getGlobalId() {
      return getGlobalId(0);
   }


@OpenCLDelegate
   protected final int getGlobalId(int _dim) {
      return kernelState.getGlobalIds()[_dim];
   }

   /*
      @OpenCLDelegate protected final int getGlobalX() {
         return (getGlobalId(0));
      }

      @OpenCLDelegate protected final int getGlobalY() {
         return (getGlobalId(1));
      }

      @OpenCLDelegate protected final int getGlobalZ() {
         return (getGlobalId(2));
      }
   */
   /**
    * Determine the groupId of an executing kernel.
    * <p>
    * When a <code>Kernel.execute(int globalSize)</code> is invoked for a particular kernel, the runtime will break the work into various 'groups'.
    * <p>
    * A kernel can use <code>getGroupId()</code> to determine which group a kernel is currently
    * dispatched to
    * <p>
    * The following code would capture the groupId for each kernel and map it against globalId.
    * <blockquote><pre>
    *     final int[] groupIds = new int[1024];
    *     Kernel kernel = new Kernel(){
    *         public void run() {
    *             int gid = getGlobalId();
    *             groupIds[gid] = getGroupId();
    *         }
    *     };
    *     kernel.execute(groupIds.length);
    *     for (int i=0; i< values.length; i++){
    *        System.out.printf("%4d %4d\n", i, groupIds[i]);
    *     }
    * </pre></blockquote>
    *
    * @see #getLocalId()
    * @see #getGlobalId()
    * @see #getGlobalSize()
    * @see #getNumGroups()
    * @see #getLocalSize()
    *
    * @return The groupId for this Kernel being executed
    */
   @OpenCLDelegate
   protected final int getGroupId() {
      return getGroupId(0);
   }

   @OpenCLDelegate
   protected final int getGroupId(int _dim) {
      return kernelState.getGroupIds()[_dim];
   }

   /*
      @OpenCLDelegate protected final int getGroupX() {
         return (getGroupId(0));
      }

      @OpenCLDelegate protected final int getGroupY() {
         return (getGroupId(1));
      }

      @OpenCLDelegate protected final int getGroupZ() {
         return (getGroupId(2));
      }
   */
   /**
    * Determine the passId of an executing kernel.
    * <p>
    * When a <code>Kernel.execute(int globalSize, int passes)</code> is invoked for a particular kernel, the runtime will break the work into various 'groups'.
    * <p>
    * A kernel can use <code>getPassId()</code> to determine which pass we are in.  This is ideal for 'reduce' type phases
    *
    * @see #getLocalId()
    * @see #getGlobalId()
    * @see #getGlobalSize()
    * @see #getNumGroups()
    * @see #getLocalSize()
    *
    * @return The groupId for this Kernel being executed
    */
   @OpenCLDelegate
   protected final int getPassId() {
      return kernelState.getPassId();
   }

   /**
    * Determine the local id of an executing kernel.
    * <p>
    * When a <code>Kernel.execute(int globalSize)</code> is invoked for a particular kernel, the runtime will break the work into
    * various 'groups'.
    * <code>getLocalId()</code> can be used to determine the relative id of the current kernel within a specific group.
    * <p>
    * The following code would capture the groupId for each kernel and map it against globalId.
    * <blockquote><pre>
    *     final int[] localIds = new int[1024];
    *     Kernel kernel = new Kernel(){
    *         public void run() {
    *             int gid = getGlobalId();
    *             localIds[gid] = getLocalId();
    *         }
    *     };
    *     kernel.execute(localIds.length);
    *     for (int i=0; i< values.length; i++){
    *        System.out.printf("%4d %4d\n", i, localIds[i]);
    *     }
    * </pre></blockquote>
    *
    * @see #getGroupId()
    * @see #getGlobalId()
    * @see #getGlobalSize()
    * @see #getNumGroups()
    * @see #getLocalSize()
    *
    * @return The local id for this Kernel being executed
    */
   @OpenCLDelegate
   protected final int getLocalId() {
      return getLocalId(0);
   }

   @OpenCLDelegate
   protected final int getLocalId(int _dim) {
      return kernelState.getLocalIds()[_dim];
   }

   /*
      @OpenCLDelegate protected final int getLocalX() {
         return (getLocalId(0));
      }

      @OpenCLDelegate protected final int getLocalY() {
         return (getLocalId(1));
      }

      @OpenCLDelegate protected final int getLocalZ() {
         return (getLocalId(2));
      }
   */
   /**
    * Determine the size of the group that an executing kernel is a member of.
    * <p>
    * When a <code>Kernel.execute(int globalSize)</code> is invoked for a particular kernel, the runtime will break the work into
    * various 'groups'. <code>getLocalSize()</code> allows a kernel to determine the size of the current group.
    * <p>
    * Note groups may not all be the same size. In particular, if <code>(global size)%(# of compute devices)!=0</code>, the runtime can choose to dispatch kernels to
    * groups with differing sizes.
    *
    * @see #getGroupId()
    * @see #getGlobalId()
    * @see #getGlobalSize()
    * @see #getNumGroups()
    * @see #getLocalSize()
    *
    * @return The size of the currently executing group.
    */
   @OpenCLDelegate
   protected final int getLocalSize() {
      return kernelState.getRange().getLocalSize(0);
   }

   @OpenCLDelegate
   protected final int getLocalSize(int _dim) {
      return kernelState.getRange().getLocalSize(_dim);
   }

   /*
      @OpenCLDelegate protected final int getLocalWidth() {
         return (range.getLocalSize(0));
      }

      @OpenCLDelegate protected final int getLocalHeight() {
         return (range.getLocalSize(1));
      }

      @OpenCLDelegate protected final int getLocalDepth() {
         return (range.getLocalSize(2));
      }
   */
   /**
    * Determine the value that was passed to <code>Kernel.execute(int globalSize)</code> method.
    *
    * @see #getGroupId()
    * @see #getGlobalId()
    * @see #getNumGroups()
    * @see #getLocalSize()
    *
    * @return The value passed to <code>Kernel.execute(int globalSize)</code> causing the current execution.
    */
   @OpenCLDelegate
   protected final int getGlobalSize() {
      return kernelState.getRange().getGlobalSize(0);
   }

   @OpenCLDelegate
   protected final int getGlobalSize(int _dim) {
      return kernelState.getRange().getGlobalSize(_dim);
   }

   /*
      @OpenCLDelegate protected final int getGlobalWidth() {
         return (range.getGlobalSize(0));
      }

      @OpenCLDelegate protected final int getGlobalHeight() {
         return (range.getGlobalSize(1));
      }

      @OpenCLDelegate protected final int getGlobalDepth() {
         return (range.getGlobalSize(2));
      }
   */
   /**
    * Determine the number of groups that will be used to execute a kernel
    * <p>
    * When <code>Kernel.execute(int globalSize)</code> is invoked, the runtime will split the work into
    * multiple 'groups'. <code>getNumGroups()</code> returns the total number of groups that will be used.
    *
    * @see #getGroupId()
    * @see #getGlobalId()
    * @see #getGlobalSize()
    * @see #getNumGroups()
    * @see #getLocalSize()
    *
    * @return The number of groups that kernels will be dispatched into.
    */
   @OpenCLDelegate
   protected final int getNumGroups() {
      return kernelState.getRange().getNumGroups(0);
   }

   @OpenCLDelegate
   protected final int getNumGroups(int _dim) {
      return kernelState.getRange().getNumGroups(_dim);
   }

   /*
      @OpenCLDelegate protected final int getNumGroupsWidth() {
         return (range.getGroups(0));
      }

      @OpenCLDelegate protected final int getNumGroupsHeight() {
         return (range.getGroups(1));
      }

      @OpenCLDelegate protected final int getNumGroupsDepth() {
         return (range.getGroups(2));
      }
   */
   /**
    * The entry point of a kernel.
    *
    * <p>
    * Every kernel must override this method.
    */
   public abstract void run();

   /** False by default. In the event that all preferred devices fail to execute a kernel, it is possible to supply an alternate (possibly non-parallel)
    * execution algorithm by overriding this method to return true, and overriding {@link #executeFallbackAlgorithm(Range, int)} with the alternate
    * algorithm.
    */
   public boolean hasFallbackAlgorithm() {
      return false;
   }

   /** If {@link #hasFallbackAlgorithm()} has been overriden to return true, this method should be overriden so as to
    * apply a single pass of the kernel's logic to the entire _range.
    *
    * <p>
    * This is not normally required, as fallback to {@link JavaDevice#THREAD_POOL} will implement the algorithm in parallel. However
    * in the event that thread pool execution may be prohibitively slow, this method might implement a "quick and dirty" approximation
    * to the desired result (for example, a simple box-blur as opposed to a gaussian blur in an image processing application).
    */
   public void executeFallbackAlgorithm(Range _range, int _passId) {
      // nothing
   }

   /**
    * Invoking this method flags that once the current pass is complete execution should be abandoned. Due to the complexity of intercommunication
    * between java (or C) and executing OpenCL, this is the best we can do for general cancellation of execution at present. OpenCL 2.0 should introduce
    * pipe mechanisms which will support mid-pass cancellation easily.
    *
    * <p>
    * Note that in the case of thread-pool/pure java execution we could do better already, using Thread.interrupt() (and/or other means) to abandon
    * execution mid-pass. However at present this is not attempted.
    *
    * @see #execute(int, int)
    * @see #execute(Range, int)
    * @see #execute(String, Range, int)
    */
   public void cancelMultiPass() {
      if (kernelRunner == null) {
         return;
      }
      kernelRunner.cancelMultiPass();
   }

   public int getCancelState() {
      return kernelRunner == null ? KernelRunner.CANCEL_STATUS_FALSE : kernelRunner.getCancelState();
   }

   /**
    * @see KernelRunner#getCurrentPass()
    */
   public int getCurrentPass() {
      if (kernelRunner == null) {
         return KernelRunner.PASS_ID_COMPLETED_EXECUTION;
      }
      return kernelRunner.getCurrentPass();
   }

   /**
    * @see KernelRunner#isExecuting()
    */
   public boolean isExecuting() {
      if (kernelRunner == null) {
         return false;
      }
      return kernelRunner.isExecuting();
   }

   /**
    * When using a Java Thread Pool Aparapi uses clone to copy the initial instance to each thread.
    *
    * <p>
    * If you choose to override <code>clone()</code> you are responsible for delegating to <code>super.clone();</code>
    */
   @Override
   public Kernel clone() {
      try {
         final Kernel worker = (Kernel) super.clone();

         // We need to be careful to also clone the KernelState
         worker.kernelState = worker.new KernelState(kernelState); // Qualified copy constructor

         worker.kernelState.setGroupIds(new int[] {0, 0, 0});

         worker.kernelState.setLocalIds(new int[] {0, 0, 0});

         worker.kernelState.setGlobalIds(new int[] {0, 0, 0});

         return worker;
      } catch (final CloneNotSupportedException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
         return (null);
      }
   }

   
   /**
    * Init a kernel from an existing one. used in caching mechanisems to improve startup time (ex. SparkCL). 
    *  
    */
   public void init(Kernel kernel) {

	 // create and init a copy of the kernel runner
	 kernelRunner = new KernelRunner(this);
         if(kernel.kernelRunner!=null)
	   kernelRunner.init(kernel.kernelRunner);
         // We need to be careful to also clone the KernelState
         kernelState = new KernelState(kernel.kernelState); // Qualified copy constructor
   }


/**
    * Delegates to either {@link java.lang.Math#acos(double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/acos.html">acos(float)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param a value to delegate to {@link java.lang.Math#acos(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/acos.html">acos(float)</a></code>
     * @return {@link java.lang.Math#acos(double)} casted to float/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/acos.html">acos(float)</a></code>
     *
     * @see java.lang.Math#acos(double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/acos.html">acos(float)</a></code>
     */
   @OpenCLMapping(mapTo = "acos")
   protected float acos(float a) {
      return (float) Math.acos(a);
   }

   /**
   * Delegates to either {@link java.lang.Math#acos(double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/acos.html">acos(double)</a></code> (OpenCL).
    *
    * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
    *
    * @param a value to delegate to {@link java.lang.Math#acos(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/acos.html">acos(double)</a></code>
    * @return {@link java.lang.Math#acos(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/acos.html">acos(double)</a></code>
    *
    * @see java.lang.Math#acos(double)
    * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/acos.html">acos(double)</a></code>
    */
   @OpenCLMapping(mapTo = "acos")
   protected double acos(double a) {
      return Math.acos(a);
   }

   /**
    * Delegates to either {@link java.lang.Math#asin(double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/asin.html">asin(float)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _f value to delegate to {@link java.lang.Math#asin(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/asin.html">asin(float)</a></code>
     * @return {@link java.lang.Math#asin(double)} casted to float/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/asin.html">asin(float)</a></code>
     *
     * @see java.lang.Math#asin(double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/asin.html">asin(float)</a></code>
     */
   @OpenCLMapping(mapTo = "asin")
   protected float asin(float _f) {
      return (float) Math.asin(_f);
   }

   /**
    * Delegates to either {@link java.lang.Math#asin(double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/asin.html">asin(double)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _d value to delegate to {@link java.lang.Math#asin(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/asin.html">asin(double)</a></code>
     * @return {@link java.lang.Math#asin(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/asin.html">asin(double)</a></code>
     *
     * @see java.lang.Math#asin(double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/asin.html">asin(double)</a></code>
     */
   @OpenCLMapping(mapTo = "asin")
   protected double asin(double _d) {
      return Math.asin(_d);
   }

   /**
    * Delegates to either {@link java.lang.Math#atan(double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/atan.html">atan(float)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _f value to delegate to {@link java.lang.Math#atan(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/atan.html">atan(float)</a></code>
     * @return {@link java.lang.Math#atan(double)} casted to float/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/atan.html">atan(float)</a></code>
     *
     * @see java.lang.Math#atan(double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/atan.html">atan(float)</a></code>
     */
   @OpenCLMapping(mapTo = "atan")
   protected float atan(float _f) {
      return (float) Math.atan(_f);
   }

   /**
    * Delegates to either {@link java.lang.Math#atan(double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/atan.html">atan(double)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _d value to delegate to {@link java.lang.Math#atan(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/atan.html">atan(double)</a></code>
     * @return {@link java.lang.Math#atan(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/atan.html">atan(double)</a></code>
     *
     * @see java.lang.Math#atan(double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/atan.html">atan(double)</a></code>
     */
   @OpenCLMapping(mapTo = "atan")
   protected double atan(double _d) {
      return Math.atan(_d);
   }

   /**
    * Delegates to either {@link java.lang.Math#atan2(double, double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/atan.html">atan2(float, float)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _f1 value to delegate to first argument of {@link java.lang.Math#atan2(double, double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/atan.html">atan2(float, float)</a></code>
     * @param _f2 value to delegate to second argument of {@link java.lang.Math#atan2(double, double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/atan.html">atan2(float, float)</a></code>
     * @return {@link java.lang.Math#atan2(double, double)} casted to float/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/atan.html">atan2(float, float)</a></code>
     *
     * @see java.lang.Math#atan2(double, double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/atan.html">atan2(float, float)</a></code>
     */
   @OpenCLMapping(mapTo = "atan2")
   protected float atan2(float _f1, float _f2) {
      return (float) Math.atan2(_f1, _f2);
   }

   /**
    * Delegates to either {@link java.lang.Math#atan2(double, double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/atan.html">atan2(double, double)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _d1 value to delegate to first argument of {@link java.lang.Math#atan2(double, double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/atan.html">atan2(double, double)</a></code>
     * @param _d2 value to delegate to second argument of {@link java.lang.Math#atan2(double, double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/atan.html">atan2(double, double)</a></code>
     * @return {@link java.lang.Math#atan2(double, double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/atan.html">atan2(double, double)</a></code>
     *
     * @see java.lang.Math#atan2(double, double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/atan.html">atan2(double, double)</a></code>
     */
   @OpenCLMapping(mapTo = "atan2")
   protected double atan2(double _d1, double _d2) {
      return Math.atan2(_d1, _d2);
   }

   /**
    * Delegates to either {@link java.lang.Math#ceil(double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/ceil.html">ceil(float)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _f value to delegate to {@link java.lang.Math#ceil(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/ceil.html">ceil(float)</a></code>
     * @return {@link java.lang.Math#ceil(double)} casted to float/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/ceil.html">ceil(float)</a></code>
     *
     * @see java.lang.Math#ceil(double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/ceil.html">ceil(float)</a></code>
     */
   @OpenCLMapping(mapTo = "ceil")
   protected float ceil(float _f) {
      return (float) Math.ceil(_f);
   }

   /**
    * Delegates to either {@link java.lang.Math#ceil(double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/ceil.html">ceil(double)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _d value to delegate to {@link java.lang.Math#ceil(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/ceil.html">ceil(double)</a></code>
     * @return {@link java.lang.Math#ceil(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/ceil.html">ceil(double)</a></code>
     *
     * @see java.lang.Math#ceil(double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/ceil.html">ceil(double)</a></code>
     */
   @OpenCLMapping(mapTo = "ceil")
   protected double ceil(double _d) {
      return Math.ceil(_d);
   }

   /**
    * Delegates to either {@link java.lang.Math#cos(double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/cos.html">cos(float)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _f value to delegate to {@link java.lang.Math#cos(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/cos.html">cos(float)</a></code>
     * @return {@link java.lang.Math#cos(double)} casted to float/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/cos.html">cos(float)</a></code>
     *
     * @see java.lang.Math#cos(double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/cos.html">cos(float)</a></code>
     */
   @OpenCLMapping(mapTo = "cos")
   protected float cos(float _f) {
      return (float) Math.cos(_f);
   }

   /**
    * Delegates to either {@link java.lang.Math#cos(double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/cos.html">cos(double)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _d value to delegate to {@link java.lang.Math#cos(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/cos.html">cos(double)</a></code>
     * @return {@link java.lang.Math#cos(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/cos.html">cos(double)</a></code>
     *
     * @see java.lang.Math#cos(double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/cos.html">cos(double)</a></code>
     */
   @OpenCLMapping(mapTo = "cos")
   protected double cos(double _d) {
      return Math.cos(_d);
   }

   /**
    * Delegates to either {@link java.lang.Math#exp(double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/exp.html">exp(float)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _f value to delegate to {@link java.lang.Math#exp(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/exp.html">exp(float)</a></code>
     * @return {@link java.lang.Math#exp(double)} casted to float/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/exp.html">exp(float)</a></code>
     *
     * @see java.lang.Math#exp(double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/exp.html">exp(float)</a></code>
     */
   @OpenCLMapping(mapTo = "exp")
   protected float exp(float _f) {
      return (float) Math.exp(_f);
   }

   /**
    * Delegates to either {@link java.lang.Math#exp(double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/exp.html">exp(double)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _d value to delegate to {@link java.lang.Math#exp(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/exp.html">exp(double)</a></code>
     * @return {@link java.lang.Math#exp(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/exp.html">exp(double)</a></code>
     *
     * @see java.lang.Math#exp(double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/exp.html">exp(double)</a></code>
     */
   @OpenCLMapping(mapTo = "exp")
   protected double exp(double _d) {
      return Math.exp(_d);
   }

   /**
    * Delegates to either {@link java.lang.Math#abs(float)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/fabs.html">fabs(float)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _f value to delegate to {@link java.lang.Math#abs(float)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/fabs.html">fabs(float)</a></code>
     * @return {@link java.lang.Math#abs(float)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/fabs.html">fabs(float)</a></code>
     *
     * @see java.lang.Math#abs(float)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/fabs.html">fabs(float)</a></code>
     */
   @OpenCLMapping(mapTo = "fabs")
   protected float abs(float _f) {
      return Math.abs(_f);
   }

   /**
    * Delegates to either {@link java.lang.Math#abs(double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/fabs.html">fabs(double)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _d value to delegate to {@link java.lang.Math#abs(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/fabs.html">fabs(double)</a></code>
     * @return {@link java.lang.Math#abs(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/fabs.html">fabs(double)</a></code>
     *
     * @see java.lang.Math#abs(double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/fabs.html">fabs(double)</a></code>
     */
   @OpenCLMapping(mapTo = "fabs")
   protected double abs(double _d) {
      return Math.abs(_d);
   }

   /**
    * Delegates to either {@link java.lang.Math#abs(int)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/abs.html">abs(int)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param n value to delegate to {@link java.lang.Math#abs(int)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/abs.html">abs(int)</a></code>
     * @return {@link java.lang.Math#abs(int)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/abs.html">abs(int)</a></code>
     *
     * @see java.lang.Math#abs(int)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/abs.html">abs(int)</a></code>
     */
   @OpenCLMapping(mapTo = "abs")
   protected int abs(int n) {
      return Math.abs(n);
   }

   /**
    * Delegates to either {@link java.lang.Math#abs(long)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/abs.html">abs(long)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param n value to delegate to {@link java.lang.Math#abs(long)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/abs.html">abs(long)</a></code>
     * @return {@link java.lang.Math#abs(long)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/fabs.html">abs(long)</a></code>
     *
     * @see java.lang.Math#abs(long)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/abs.html">abs(long)</a></code>
     */
   @OpenCLMapping(mapTo = "abs")
   protected long abs(long n) {
      return Math.abs(n);
   }

   /**
    * Delegates to either {@link java.lang.Math#floor(double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/abs.html">floor(float)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _f value to delegate to {@link java.lang.Math#floor(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/floor.html">floor(float)</a></code>
     * @return {@link java.lang.Math#floor(double)} casted to float/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/floor.html">floor(float)</a></code>
     *
     * @see java.lang.Math#floor(double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/floor.html">floor(float)</a></code>
     */
   @OpenCLMapping(mapTo = "floor")
   protected float floor(float _f) {
      return (float) Math.floor(_f);
   }

   /**
    * Delegates to either {@link java.lang.Math#floor(double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/abs.html">floor(double)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _d value to delegate to {@link java.lang.Math#floor(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/floor.html">floor(double)</a></code>
     * @return {@link java.lang.Math#floor(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/floor.html">floor(double)</a></code>
     *
     * @see java.lang.Math#floor(double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/floor.html">floor(double)</a></code>
     */
   @OpenCLMapping(mapTo = "floor")
   protected double floor(double _d) {
      return Math.floor(_d);
   }

   /**
    * Delegates to either {@link java.lang.Math#max(float, float)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/fmax.html">fmax(float, float)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _f1 value to delegate to first argument of {@link java.lang.Math#max(float, float)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/fmax.html">fmax(float, float)</a></code>
     * @param _f2 value to delegate to second argument of {@link java.lang.Math#max(float, float)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/fmax.html">fmax(float, float)</a></code>
     * @return {@link java.lang.Math#max(float, float)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/fmax.html">fmax(float, float)</a></code>
     *
     * @see java.lang.Math#max(float, float)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/fmax.html">fmax(float, float)</a></code>
     */
   @OpenCLMapping(mapTo = "fmax")
   protected float max(float _f1, float _f2) {
      return Math.max(_f1, _f2);
   }

   /**
    * Delegates to either {@link java.lang.Math#max(double, double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/fmax.html">fmax(double, double)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _d1 value to delegate to first argument of {@link java.lang.Math#max(double, double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/fmax.html">fmax(double, double)</a></code>
     * @param _d2 value to delegate to second argument of {@link java.lang.Math#max(double, double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/fmax.html">fmax(double, double)</a></code>
     * @return {@link java.lang.Math#max(double, double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/fmax.html">fmax(double, double)</a></code>
     *
     * @see java.lang.Math#max(double, double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/fmax.html">fmax(double, double)</a></code>
     */
   @OpenCLMapping(mapTo = "fmax")
   protected double max(double _d1, double _d2) {
      return Math.max(_d1, _d2);
   }

   /**
    * Delegates to either {@link java.lang.Math#max(int, int)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/integerMax.html">max(int, int)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param n1 value to delegate to {@link java.lang.Math#max(int, int)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/integerMax.html">max(int, int)</a></code>
     * @param n2 value to delegate to {@link java.lang.Math#max(int, int)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/integerMax.html">max(int, int)</a></code>
     * @return {@link java.lang.Math#max(int, int)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/integerMax.html">max(int, int)</a></code>
     *
     * @see java.lang.Math#max(int, int)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/integerMax.html">max(int, int)</a></code>
     */
   @OpenCLMapping(mapTo = "max")
   protected int max(int n1, int n2) {
      return Math.max(n1, n2);
   }

   /**
    * Delegates to either {@link java.lang.Math#max(long, long)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/integerMax.html">max(long, long)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param n1 value to delegate to first argument of {@link java.lang.Math#max(long, long)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/integerMax.html">max(long, long)</a></code>
     * @param n2 value to delegate to second argument of {@link java.lang.Math#max(long, long)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/integerMax.html">max(long, long)</a></code>
     * @return {@link java.lang.Math#max(long, long)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/integerMax.html">max(long, long)</a></code>
     *
     * @see java.lang.Math#max(long, long)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/integerMax.html">max(long, long)</a></code>
     */
   @OpenCLMapping(mapTo = "max")
   protected long max(long n1, long n2) {
      return Math.max(n1, n2);
   }

   /**
    * Delegates to either {@link java.lang.Math#min(float, float)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/fmin.html">fmin(float, float)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _f1 value to delegate to first argument of {@link java.lang.Math#min(float, float)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/fmin.html">fmin(float, float)</a></code>
     * @param _f2 value to delegate to second argument of {@link java.lang.Math#min(float, float)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/fmin.html">fmin(float, float)</a></code>
     * @return {@link java.lang.Math#min(float, float)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/fmin.html">fmin(float, float)</a></code>
     *
     * @see java.lang.Math#min(float, float)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/fmin.html">fmin(float, float)</a></code>
     */
   @OpenCLMapping(mapTo = "fmin")
   protected float min(float _f1, float _f2) {
      return Math.min(_f1, _f2);
   }

   /**
    * Delegates to either {@link java.lang.Math#min(double, double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/fmin.html">fmin(double, double)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _d1 value to delegate to first argument of {@link java.lang.Math#min(double, double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/fmin.html">fmin(double, double)</a></code>
     * @param _d2 value to delegate to second argument of {@link java.lang.Math#min(double, double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/fmin.html">fmin(double, double)</a></code>
     * @return {@link java.lang.Math#min(double, double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/fmin.html">fmin(double, double)</a></code>
     *
     * @see java.lang.Math#min(double, double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/fmin.html">fmin(double, double)</a></code>
     */
   @OpenCLMapping(mapTo = "fmin")
   protected double min(double _d1, double _d2) {
      return Math.min(_d1, _d2);
   }

   /**
    * Delegates to either {@link java.lang.Math#min(int, int)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/integerMax.html">min(int, int)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param n1 value to delegate to first argument of {@link java.lang.Math#min(int, int)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/integerMax.html">min(int, int)</a></code>
     * @param n2 value to delegate to second argument of {@link java.lang.Math#min(int, int)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/integerMax.html">min(int, int)</a></code>
     * @return {@link java.lang.Math#min(int, int)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/integerMax.html">min(int, int)</a></code>
     *
     * @see java.lang.Math#min(int, int)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/integerMax.html">min(int, int)</a></code>
     */
   @OpenCLMapping(mapTo = "min")
   protected int min(int n1, int n2) {
      return Math.min(n1, n2);
   }

   /**
    * Delegates to either {@link java.lang.Math#min(long, long)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/integerMax.html">min(long, long)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param n1 value to delegate to first argument of {@link java.lang.Math#min(long, long)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/integerMax.html">min(long, long)</a></code>
     * @param n2 value to delegate to second argument of {@link java.lang.Math#min(long, long)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/integerMax.html">min(long, long)</a></code>
     * @return {@link java.lang.Math#min(long, long)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/integerMax.html">min(long, long)</a></code>
     *
     * @see java.lang.Math#min(long, long)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/integerMax.html">min(long, long)</a></code>
     */
   @OpenCLMapping(mapTo = "min")
   protected long min(long n1, long n2) {
      return Math.min(n1, n2);
   }

   /**
    * Delegates to either {@link java.lang.Math#log(double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/log.html">log(float)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _f value to delegate to {@link java.lang.Math#log(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/log.html">log(float)</a></code>
     * @return {@link java.lang.Math#log(double)} casted to float/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/log.html">log(float)</a></code>
     *
     * @see java.lang.Math#log(double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/log.html">log(float)</a></code>
     */
   @OpenCLMapping(mapTo = "log")
   protected float log(float _f) {
      return (float) Math.log(_f);
   }

   /**
    * Delegates to either {@link java.lang.Math#log(double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/log.html">log(double)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _d value to delegate to {@link java.lang.Math#log(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/log.html">log(double)</a></code>
     * @return {@link java.lang.Math#log(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/log.html">log(double)</a></code>
     *
     * @see java.lang.Math#log(double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/log.html">log(double)</a></code>
     */
   @OpenCLMapping(mapTo = "log")
   protected double log(double _d) {
      return Math.log(_d);
   }

   /**
    * Delegates to either {@link java.lang.Math#pow(double, double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/pow.html">pow(float, float)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _f1 value to delegate to first argument of {@link java.lang.Math#pow(double, double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/pow.html">pow(float, float)</a></code>
     * @param _f2 value to delegate to second argument of {@link java.lang.Math#pow(double, double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/pow.html">pow(float, float)</a></code>
     * @return {@link java.lang.Math#pow(double, double)} casted to float/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/pow.html">pow(float, float)</a></code>
     *
     * @see java.lang.Math#pow(double, double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/pow.html">pow(float, float)</a></code>
     */
   @OpenCLMapping(mapTo = "pow")
   protected float pow(float _f1, float _f2) {
      return (float) Math.pow(_f1, _f2);
   }

   /**
    * Delegates to either {@link java.lang.Math#pow(double, double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/pow.html">pow(double, double)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _d1 value to delegate to first argument of {@link java.lang.Math#pow(double, double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/pow.html">pow(double, double)</a></code>
     * @param _d2 value to delegate to second argument of {@link java.lang.Math#pow(double, double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/pow.html">pow(double, double)</a></code>
     * @return {@link java.lang.Math#pow(double, double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/pow.html">pow(double, double)</a></code>
     *
     * @see java.lang.Math#pow(double, double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/pow.html">pow(double, double)</a></code>
     */
   @OpenCLMapping(mapTo = "pow")
   protected double pow(double _d1, double _d2) {
      return Math.pow(_d1, _d2);
   }

   /**
    * Delegates to either {@link java.lang.Math#IEEEremainder(double, double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/remainder.html">remainder(float, float)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _f1 value to delegate to first argument of {@link java.lang.Math#IEEEremainder(double, double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/remainder.html">remainder(float, float)</a></code>
     * @param _f2 value to delegate to second argument of {@link java.lang.Math#IEEEremainder(double, double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/remainder.html">remainder(float, float)</a></code>
     * @return {@link java.lang.Math#IEEEremainder(double, double)} casted to float/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/remainder.html">remainder(float, float)</a></code>
     *
     * @see java.lang.Math#IEEEremainder(double, double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/remainder.html">remainder(float, float)</a></code>
     */
   @OpenCLMapping(mapTo = "remainder")
   protected float IEEEremainder(float _f1, float _f2) {
      return (float) Math.IEEEremainder(_f1, _f2);
   }

   /**
    * Delegates to either {@link java.lang.Math#IEEEremainder(double, double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/remainder.html">remainder(double, double)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _d1 value to delegate to first argument of {@link java.lang.Math#IEEEremainder(double, double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/remainder.html">remainder(double, double)</a></code>
     * @param _d2 value to delegate to second argument of {@link java.lang.Math#IEEEremainder(double, double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/remainder.html">remainder(double, double)</a></code>
     * @return {@link java.lang.Math#IEEEremainder(double, double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/remainder.html">remainder(double, double)</a></code>
     *
     * @see java.lang.Math#IEEEremainder(double, double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/remainder.html">remainder(double, double)</a></code>
     */
   @OpenCLMapping(mapTo = "remainder")
   protected double IEEEremainder(double _d1, double _d2) {
      return Math.IEEEremainder(_d1, _d2);
   }

   /**
    * Delegates to either {@link java.lang.Math#toRadians(double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/radians.html">radians(float)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _f value to delegate to {@link java.lang.Math#toRadians(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/radians.html">radians(float)</a></code>
     * @return {@link java.lang.Math#toRadians(double)} casted to float/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/radians.html">radians(float)</a></code>
     *
     * @see java.lang.Math#toRadians(double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/radians.html">radians(float)</a></code>
     */
   @OpenCLMapping(mapTo = "radians")
   protected float toRadians(float _f) {
      return (float) Math.toRadians(_f);
   }

   /**
    * Delegates to either {@link java.lang.Math#toRadians(double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/radians.html">radians(double)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _d value to delegate to {@link java.lang.Math#toRadians(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/radians.html">radians(double)</a></code>
     * @return {@link java.lang.Math#toRadians(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/radians.html">radians(double)</a></code>
     *
     * @see java.lang.Math#toRadians(double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/radians.html">radians(double)</a></code>
     */
   @OpenCLMapping(mapTo = "radians")
   protected double toRadians(double _d) {
      return Math.toRadians(_d);
   }

   /**
    * Delegates to either {@link java.lang.Math#toDegrees(double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/degrees.html">degrees(float)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _f value to delegate to {@link java.lang.Math#toDegrees(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/degrees.html">degrees(float)</a></code>
     * @return {@link java.lang.Math#toDegrees(double)} casted to float/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/degrees.html">degrees(float)</a></code>
     *
     * @see java.lang.Math#toDegrees(double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/degrees.html">degrees(float)</a></code>
     */
   @OpenCLMapping(mapTo = "degrees")
   protected float toDegrees(float _f) {
      return (float) Math.toDegrees(_f);
   }

   /**
    * Delegates to either {@link java.lang.Math#toDegrees(double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/degrees.html">degrees(double)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _d value to delegate to {@link java.lang.Math#toDegrees(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/degrees.html">degrees(double)</a></code>
     * @return {@link java.lang.Math#toDegrees(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/degrees.html">degrees(double)</a></code>
     *
     * @see java.lang.Math#toDegrees(double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/degrees.html">degrees(double)</a></code>
     */
   @OpenCLMapping(mapTo = "degrees")
   protected double toDegrees(double _d) {
      return Math.toDegrees(_d);
   }

   /**
    * Delegates to either {@link java.lang.Math#rint(double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/rint.html">rint(float)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _f value to delegate to {@link java.lang.Math#rint(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/rint.html">rint(float)</a></code>
     * @return {@link java.lang.Math#rint(double)} casted to float/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/rint.html">rint(float)</a></code>
     *
     * @see java.lang.Math#rint(double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/rint.html">rint(float)</a></code>
     */
   @OpenCLMapping(mapTo = "rint")
   protected float rint(float _f) {
      return (float) Math.rint(_f);
   }

   /**
    * Delegates to either {@link java.lang.Math#rint(double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/rint.html">rint(double)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _d value to delegate to {@link java.lang.Math#rint(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/rint.html">rint(double)</a></code>
     * @return {@link java.lang.Math#rint(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/rint.html">rint(double)</a></code>
     *
     * @see java.lang.Math#rint(double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/rint.html">rint(double)</a></code>
     */
   @OpenCLMapping(mapTo = "rint")
   protected double rint(double _d) {
      return Math.rint(_d);
   }

   /**
    * Delegates to either {@link java.lang.Math#round(float)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/round.html">round(float)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _f value to delegate to {@link java.lang.Math#round(float)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/round.html">round(float)</a></code>
     * @return {@link java.lang.Math#round(float)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/round.html">round(float)</a></code>
     *
     * @see java.lang.Math#round(float)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/round.html">round(float)</a></code>
     */
   @OpenCLMapping(mapTo = "round")
   protected int round(float _f) {
      return Math.round(_f);
   }

   /**
    * Delegates to either {@link java.lang.Math#round(double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/round.html">round(double)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _d value to delegate to {@link java.lang.Math#round(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/round.html">round(double)</a></code>
     * @return {@link java.lang.Math#round(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/round.html">round(double)</a></code>
     *
     * @see java.lang.Math#round(double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/round.html">round(double)</a></code>
     */
   @OpenCLMapping(mapTo = "round")
   protected long round(double _d) {
      return Math.round(_d);
   }

   /**
    * Delegates to either {@link java.lang.Math#sin(double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/sin.html">sin(float)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _f value to delegate to {@link java.lang.Math#sin(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/sin.html">sin(float)</a></code>
     * @return {@link java.lang.Math#sin(double)} casted to float/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/sin.html">sin(float)</a></code>
     *
     * @see java.lang.Math#sin(double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/sin.html">sin(float)</a></code>
     */
   @OpenCLMapping(mapTo = "sin")
   protected float sin(float _f) {
      return (float) Math.sin(_f);
   }

   /**
    * Delegates to either {@link java.lang.Math#sin(double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/sin.html">sin(double)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _d value to delegate to {@link java.lang.Math#sin(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/sin.html">sin(double)</a></code>
     * @return {@link java.lang.Math#sin(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/sin.html">sin(double)</a></code>
     *
     * @see java.lang.Math#sin(double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/sin.html">sin(double)</a></code>
     */
   @OpenCLMapping(mapTo = "sin")
   protected double sin(double _d) {
      return Math.sin(_d);
   }

   /**
    * Delegates to either {@link java.lang.Math#sqrt(double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/sqrt.html">sqrt(float)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _f value to delegate to {@link java.lang.Math#sqrt(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/sqrt.html">sqrt(float)</a></code>
     * @return {@link java.lang.Math#sqrt(double)} casted to float/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/sqrt.html">sqrt(float)</a></code>
     *
     * @see java.lang.Math#sqrt(double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/sqrt.html">sqrt(float)</a></code>
     */
   @OpenCLMapping(mapTo = "sqrt")
   protected float sqrt(float _f) {
      return (float) Math.sqrt(_f);
   }

   /**
    * Delegates to either {@link java.lang.Math#sqrt(double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/sqrt.html">sqrt(double)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _d value to delegate to {@link java.lang.Math#sqrt(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/sqrt.html">sqrt(double)</a></code>
     * @return {@link java.lang.Math#sqrt(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/sqrt.html">sqrt(double)</a></code>
     *
     * @see java.lang.Math#sqrt(double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/sqrt.html">sqrt(double)</a></code>
     */
   @OpenCLMapping(mapTo = "sqrt")
   protected double sqrt(double _d) {
      return Math.sqrt(_d);
   }

   /**
    * Delegates to either {@link java.lang.Math#tan(double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/tan.html">tan(float)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _f value to delegate to {@link java.lang.Math#tan(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/tan.html">tan(float)</a></code>
     * @return {@link java.lang.Math#tan(double)} casted to float/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/tan.html">tan(float)</a></code>
     *
     * @see java.lang.Math#tan(double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/tan.html">tan(float)</a></code>
     */
   @OpenCLMapping(mapTo = "tan")
   protected float tan(float _f) {
      return (float) Math.tan(_f);
   }

   /**
    * Delegates to either {@link java.lang.Math#tan(double)} (Java) or <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/tan.html">tan(double)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _d value to delegate to {@link java.lang.Math#tan(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/tan.html">tan(double)</a></code>
     * @return {@link java.lang.Math#tan(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/tan.html">tan(double)</a></code>
     *
     * @see java.lang.Math#tan(double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/tan.html">tan(double)</a></code>
     */
   @OpenCLMapping(mapTo = "tan")
   protected double tan(double _d) {
      return Math.tan(_d);
   }

   // the following rsqrt and native_sqrt and native_rsqrt don't exist in java Math
   // but added them here for nbody testing, not sure if we want to expose them
   /**
    * Computes  inverse square root using {@link java.lang.Math#sqrt(double)} (Java) or delegates to <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/sqrt.html">rsqrt(double)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _f value to delegate to {@link java.lang.Math#sqrt(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/sqrt.html">rsqrt(double)</a></code>
     * @return <code>( 1.0f / {@link java.lang.Math#sqrt(double)} casted to float )</code>/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/sqrt.html">rsqrt(double)</a></code>
     *
     * @see java.lang.Math#sqrt(double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/sqrt.html">rsqrt(double)</a></code>
     */
   @OpenCLMapping(mapTo = "rsqrt")
   protected float rsqrt(float _f) {
      return (1.0f / (float) Math.sqrt(_f));
   }

   /**
    * Computes  inverse square root using {@link java.lang.Math#sqrt(double)} (Java) or delegates to <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/sqrt.html">rsqrt(double)</a></code> (OpenCL).
     *
     * User should note the differences in precision between Java and OpenCL's implementation of arithmetic functions to determine whether the difference in precision is acceptable.
     *
     * @param _d value to delegate to {@link java.lang.Math#sqrt(double)}/<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/sqrt.html">rsqrt(double)</a></code>
     * @return <code>( 1.0f / {@link java.lang.Math#sqrt(double)} )</code> /<code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/sqrt.html">rsqrt(double)</a></code>
     *
     * @see java.lang.Math#sqrt(double)
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/sqrt.html">rsqrt(double)</a></code>
     */
   @OpenCLMapping(mapTo = "rsqrt")
   protected double rsqrt(double _d) {
      return (1.0 / Math.sqrt(_d));
   }

   @OpenCLMapping(mapTo = "native_sqrt")
   private float native_sqrt(float _f) {
      int j = Float.floatToIntBits(_f);
      j = ((1 << 29) + (j >> 1)) - (1 << 22) - 0x4c00;
      return (Float.intBitsToFloat(j));
      // could add more precision using one iteration of newton's method, use the following
   }

   @OpenCLMapping(mapTo = "native_rsqrt")
   private float native_rsqrt(float _f) {
      int j = Float.floatToIntBits(_f);
      j = 0x5f3759df - (j >> 1);
      final float x = (Float.intBitsToFloat(j));
      return x;
      // if want more precision via one iteration of newton's method, use the following
      // float fhalf = 0.5f*_f;
      // return (x *(1.5f - fhalf * x * x));
   }

   // Hacked from AtomicIntegerArray.getAndAdd(i, delta)
   /**
    * Atomically adds <code>_delta</code> value to <code>_index</code> element of array <code>_arr</code> (Java) or delegates to <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/atomic_add.html">atomic_add(volatile int*, int)</a></code> (OpenCL).
     *
     *
     * @param _arr array for which an element value needs to be atomically incremented by <code>_delta</code>
     * @param _index index of the <code>_arr</code> array that needs to be atomically incremented by <code>_delta</code>
     * @param _delta value by which <code>_index</code> element of <code>_arr</code> array needs to be atomically incremented
     * @return previous value of <code>_index</code> element of <code>_arr</code> array
     *
     * @see <code><a href="http://www.khronos.org/registry/cl/sdk/1.1/docs/man/xhtml/atomic_add.html">atomic_add(volatile int*, int)</a></code>
     */
   @OpenCLMapping(atomic32 = true)
   protected int atomicAdd(int[] _arr, int _index, int _delta) {
      if (!Config.disableUnsafe) {
         return UnsafeWrapper.atomicAdd(_arr, _index, _delta);
      } else {
         synchronized (_arr) {
            final int previous = _arr[_index];
            _arr[_index] += _delta;
            return previous;
         }
      }
   }

   /**
    * Wait for all kernels in the current group to rendezvous at this call before continuing execution.
    *
    * @annotion Experimental
    */
   @OpenCLDelegate
   @Experimental
   protected final void localBarrier() {
      kernelState.awaitOnLocalBarrier();
   }

   /**
    * Wait for all kernels in the current group to rendezvous at this call before continuing execution.
    *
    *
    * Java version is identical to localBarrier()
    *
    * @annotion Experimental
    * @deprecated
    */
   @OpenCLDelegate
   @Experimental
   @Deprecated
   protected final void globalBarrier() throws DeprecatedException {
      throw new DeprecatedException(
            "Kernel.globalBarrier() has been deprecated. It was based an incorrect understanding of OpenCL functionality.");
   }

   @OpenCLMapping(mapTo = "hypot")
   protected float hypot(final float a, final float b) {
      return (float) Math.hypot(a, b);
   }

   @OpenCLMapping(mapTo = "hypot")
   protected double hypot(final double a, final double b) {
      return Math.hypot(a, b);
   }

   public KernelState getKernelState() {
      return kernelState;
   }

   private KernelRunner prepareKernelRunner() {
      if (kernelRunner == null) {
         kernelRunner = new KernelRunner(this);
      }
      return kernelRunner;
   }

   /**
    * Determine the execution time of the previous Kernel.execute(range) call.
    *
    * Note that for the first call this will include the conversion time.
    *
    * @return The time spent executing the kernel (ms)
    *
    * @see #getConversionTime();
    * @see #getAccumulatedExecutionTime();
    *
    */
   public double getExecutionTime() {
      KernelProfile profile = KernelManager.instance().getProfile(getClass());
      synchronized (profile) {
         return profile.getLastExecutionTime();
      }
   }

   /**
    * Determine the total execution time of all previous Kernel.execute(range) calls.
    *
    * Note that this will include the initial conversion time.
    *
    * @return The total time spent executing the kernel (ms)
    *
    * @see #getExecutionTime();
    * @see #getConversionTime();
    *
    */
   public double getAccumulatedExecutionTime() {
      KernelProfile profile = KernelManager.instance().getProfile(getClass());
      synchronized (profile) {
         return profile.getAccumulatedTotalTime();
      }
   }

   /**
    * Determine the time taken to convert bytecode to OpenCL for first Kernel.execute(range) call.
    * @return The time spent preparing the kernel for execution using GPU
    *
    * @see #getExecutionTime();
    * @see #getAccumulatedExecutionTime();
    */
   public double getConversionTime() {
      KernelProfile profile = KernelManager.instance().getProfile(getClass());
      synchronized (profile) {
         return profile.getLastConversionTime();
      }
   }

   /**
    * Start execution of <code>_range</code> kernels.
    * <p>
    * When <code>kernel.execute(globalSize)</code> is invoked, Aparapi will schedule the execution of <code>globalSize</code> kernels. If the execution mode is GPU then
    * the kernels will execute as OpenCL code on the GPU device. Otherwise, if the mode is JTP, the kernels will execute as a pool of Java threads on the CPU.
    * <p>
    * @param _range The number of Kernels that we would like to initiate.
    * @returnThe Kernel instance (this) so we can chain calls to put(arr).execute(range).get(arr)
    *
    */
   public synchronized Kernel execute(Range _range) {
      return (execute(_range, 1));
   }

   @Override
   @SuppressWarnings("deprecation")
   public String toString() {
      if (executionMode == EXECUTION_MODE.AUTO) {
         List<Device> preferredDevices = KernelManager.instance().getPreferences(this).getPreferredDevices(this);
         StringBuilder preferredDevicesSummary = new StringBuilder("{");
         for (int i = 0; i < preferredDevices.size(); ++i) {
            Device device = preferredDevices.get(i);
            preferredDevicesSummary.append(device.getShortDescription());
            if (i < preferredDevices.size() - 1) {
               preferredDevicesSummary.append("|");
            }
         }
         preferredDevicesSummary.append("}");
         return Reflection.getSimpleName(getClass()) + ", devices=" + preferredDevicesSummary.toString();
      } else {
         return Reflection.getSimpleName(getClass()) + ", modes=" + executionModes + ", current = " + executionMode;
      }
   }

   /**
    * Start execution of <code>_range</code> kernels.
    * <p>
    * When <code>kernel.execute(_range)</code> is 1invoked, Aparapi will schedule the execution of <code>_range</code> kernels. If the execution mode is GPU then
    * the kernels will execute as OpenCL code on the GPU device. Otherwise, if the mode is JTP, the kernels will execute as a pool of Java threads on the CPU.
    * <p>
    * Since adding the new <code>Range class</code> this method offers backward compatibility and merely defers to <code> return (execute(Range.create(_range), 1));</code>.
    * @param _range The number of Kernels that we would like to initiate.
    * @returnThe Kernel instance (this) so we can chain calls to put(arr).execute(range).get(arr)
    *
    */
   public synchronized Kernel execute(int _range) {
      return (execute(createRange(_range), 1));
   }

   @SuppressWarnings("deprecation")
   protected Range createRange(int _range) {
      if (executionMode.equals(EXECUTION_MODE.AUTO)) {
         Device device = getTargetDevice();
         Range range = Range.create(device, _range);
         return range;
      } else {
         return Range.create(null, _range);
      }
   }

   /**
    * Start execution of <code>_passes</code> iterations of <code>_range</code> kernels.
    * <p>
    * When <code>kernel.execute(_range, _passes)</code> is invoked, Aparapi will schedule the execution of <code>_reange</code> kernels. If the execution mode is GPU then
    * the kernels will execute as OpenCL code on the GPU device. Otherwise, if the mode is JTP, the kernels will execute as a pool of Java threads on the CPU.
    * <p>
    * @param _passes The number of passes to make
    * @return The Kernel instance (this) so we can chain calls to put(arr).execute(range).get(arr)
    *
    */
   public synchronized Kernel execute(Range _range, int _passes) {
      return (execute("run", _range, _passes));
   }

   /**
    * Start execution of <code>_passes</code> iterations over the <code>_range</code> of kernels.
    * <p>
    * When <code>kernel.execute(_range)</code> is invoked, Aparapi will schedule the execution of <code>_range</code> kernels. If the execution mode is GPU then
    * the kernels will execute as OpenCL code on the GPU device. Otherwise, if the mode is JTP, the kernels will execute as a pool of Java threads on the CPU.
    * <p>
    * Since adding the new <code>Range class</code> this method offers backward compatibility and merely defers to <code> return (execute(Range.create(_range), 1));</code>.
    * @param _range The number of Kernels that we would like to initiate.
    * @returnThe Kernel instance (this) so we can chain calls to put(arr).execute(range).get(arr)
    *
    */
   public synchronized Kernel execute(int _range, int _passes) {
      return (execute(createRange(_range), _passes));
   }

   /**
    * Start execution of <code>globalSize</code> kernels for the given entrypoint.
    * <p>
    * When <code>kernel.execute("entrypoint", globalSize)</code> is invoked, Aparapi will schedule the execution of <code>globalSize</code> kernels. If the execution mode is GPU then
    * the kernels will execute as OpenCL code on the GPU device. Otherwise, if the mode is JTP, the kernels will execute as a pool of Java threads on the CPU.
    * <p>
    * @param _entrypoint is the name of the method we wish to use as the entrypoint to the kernel
    * @return The Kernel instance (this) so we can chain calls to put(arr).execute(range).get(arr)
    *
    */
   public synchronized Kernel execute(String _entrypoint, Range _range) {
      return (execute(_entrypoint, _range, 1));
   }

   /**
    * Start execution of <code>globalSize</code> kernels for the given entrypoint.
    * <p>
    * When <code>kernel.execute("entrypoint", globalSize)</code> is invoked, Aparapi will schedule the execution of <code>globalSize</code> kernels. If the execution mode is GPU then
    * the kernels will execute as OpenCL code on the GPU device. Otherwise, if the mode is JTP, the kernels will execute as a pool of Java threads on the CPU.
    * <p>
    * @param _entrypoint is the name of the method we wish to use as the entrypoint to the kernel
    * @return The Kernel instance (this) so we can chain calls to put(arr).execute(range).get(arr)
    *
    */
   public synchronized Kernel execute(String _entrypoint, Range _range, int _passes) {
      return prepareKernelRunner().execute(_entrypoint, _range, _passes);
   }

   public boolean isAutoCleanUpArrays() {
      return autoCleanUpArrays;
   }

   /**
    * Property which if true enables automatic calling of {@link #cleanUpArrays()} following each execution.
    */
   public void setAutoCleanUpArrays(boolean autoCleanUpArrays) {
      this.autoCleanUpArrays = autoCleanUpArrays;
   }

   /**
    * Frees the bulk of the resources used by this kernel, by setting array sizes in non-primitive {@link KernelArg}s to 1 (0 size is prohibited) and invoking kernel
    * execution on a zero size range. Unlike {@link #dispose()}, this does not prohibit further invocations of this kernel, as sundry resources such as OpenCL queues are
    * <b>not</b> freed by this method.
    *
    * <p>This allows a "dormant" Kernel to remain in existence without undue strain on GPU resources, which may be strongly preferable to disposing a Kernel and
    * recreating another one later, as creation/use of a new Kernel (specifically creation of its associated OpenCL context) is expensive.</p>
    *
    * <p>Note that where the underlying array field is declared final, for obvious reasons it is not resized to zero.</p>
    */
   public synchronized void cleanUpArrays() {
      if (kernelRunner != null) {
         kernelRunner.cleanUpArrays();
      }
   }

   /**
    * Release any resources associated with this Kernel.
    * <p>
    * When the execution mode is <code>CPU</code> or <code>GPU</code>, Aparapi stores some OpenCL resources in a data structure associated with the kernel instance.  The
    * <code>dispose()</code> method must be called to release these resources.
    * <p>
    * If <code>execute(int _globalSize)</code> is called after <code>dispose()</code> is called the results are undefined.
    */
   public synchronized void dispose() {
      if (kernelRunner != null) {
         kernelRunner.dispose();
         kernelRunner = null;
      }
   }

   public boolean isRunningCL() {
      return getTargetDevice() instanceof OpenCLDevice;
   }

   public final Device getTargetDevice() {
      return KernelManager.instance().getPreferences(this).getPreferredDevice(this);
   }

   /** @return true by default, may be overriden to allow vetoing of a device or devices by a given Kernel instance. */
   public boolean isAllowDevice(Device _device) {
      return true;
   }

   /**
    * @deprecated See {@link EXECUTION_MODE}
    * <p>
    * Return the current execution mode.
    *
    * Before a Kernel executes, this return value will be the execution mode as determined by the setting of
    * the EXECUTION_MODE enumeration. By default, this setting is either <b>GPU</b>
    * if OpenCL is available on the target system, or <b>JTP</b> otherwise. This default setting can be
    * changed by calling setExecutionMode().
    *
    * <p>
    * After a Kernel executes, the return value will be the mode in which the Kernel actually executed.
    *
    * @return The current execution mode.
    *
    * @see #setExecutionMode(EXECUTION_MODE)
    */
   @Deprecated
   public EXECUTION_MODE getExecutionMode() {
      return (executionMode);
   }

   /**
    * @deprecated See {@link EXECUTION_MODE}
    * <p>
    * Set the execution mode.
    * <p>
    * This should be regarded as a request. The real mode will be determined at runtime based on the availability of OpenCL and the characteristics of the workload.
    *
    * @param _executionMode the requested execution mode.
    *
    * @see #getExecutionMode()
    */
   @Deprecated
   public void setExecutionMode(EXECUTION_MODE _executionMode) {
      executionMode = _executionMode;
   }

   public void setExecutionModeWithoutFallback(EXECUTION_MODE _executionMode) {
     executionModes.clear();
     executionModes.add(_executionMode);
     currentMode = executionModes.iterator();
     executionMode = currentMode.next();  }

   /**
    * @deprecated See {@link EXECUTION_MODE}
    */
   @Deprecated
   public void setFallbackExecutionMode() {
      executionMode = EXECUTION_MODE.getFallbackExecutionMode();
   }

   final static Map<String, String> typeToLetterMap = new HashMap<String, String>();

   static {
      // only primitive types for now
      typeToLetterMap.put("double", "D");
      typeToLetterMap.put("float", "F");
      typeToLetterMap.put("int", "I");
      typeToLetterMap.put("long", "J");
      typeToLetterMap.put("boolean", "Z");
      typeToLetterMap.put("byte", "B");
      typeToLetterMap.put("char", "C");
      typeToLetterMap.put("short", "S");
      typeToLetterMap.put("void", "V");
   }

   private static String descriptorToReturnTypeLetter(String desc) {
      // find the letter after the closed parenthesis
      return desc.substring(desc.lastIndexOf(')') + 1);
   }

   private static String getReturnTypeLetter(Method meth) {
      return toClassShortNameIfAny(meth.getReturnType());
   }

   private static String toClassShortNameIfAny(final Class<?> retClass) {
      if (retClass.isArray()) {
         return "[" + toClassShortNameIfAny(retClass.getComponentType());
      }
      final String strRetClass = retClass.toString();
      final String mapping = typeToLetterMap.get(strRetClass);
      // System.out.println("strRetClass = <" + strRetClass + ">, mapping = " + mapping);
      if (mapping == null)
         return "[" + retClass.getName() + ";";
      return mapping;
   }

   public static String getMappedMethodName(MethodReferenceEntry _methodReferenceEntry) {
      if (CacheEnabler.areCachesEnabled())
         return getProperty(mappedMethodNamesCache, _methodReferenceEntry, null);
      String mappedName = null;
      final String name = _methodReferenceEntry.getNameAndTypeEntry().getNameUTF8Entry().getUTF8();
      Class<?> currentClass = _methodReferenceEntry.getOwnerClassModel().getClassWeAreModelling();
      while (currentClass != Object.class) {
         for (final Method kernelMethod : currentClass.getDeclaredMethods()) {
            if (kernelMethod.isAnnotationPresent(OpenCLMapping.class)) {
               // ultimately, need a way to constrain this based upon signature (to disambiguate abs(float) from abs(int);
               // for Alpha, we will just disambiguate based on the return type
               if (false) {
                  System.out.println("kernelMethod is ... " + kernelMethod.toGenericString());
                  System.out.println("returnType = " + kernelMethod.getReturnType());
                  System.out.println("returnTypeLetter = " + getReturnTypeLetter(kernelMethod));
                  System.out.println("kernelMethod getName = " + kernelMethod.getName());
                  System.out.println("methRefName = " + name + " descriptor = "
                        + _methodReferenceEntry.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8());
                  System.out.println("descToReturnTypeLetter = "
                        + descriptorToReturnTypeLetter(_methodReferenceEntry.getNameAndTypeEntry().getDescriptorUTF8Entry()
                              .getUTF8()));
               }
               if (toSignature(_methodReferenceEntry).equals(toSignature(kernelMethod))) {
                  final OpenCLMapping annotation = kernelMethod.getAnnotation(OpenCLMapping.class);
                  final String mapTo = annotation.mapTo();
                  if (!mapTo.equals("")) {
                     mappedName = mapTo;
                     // System.out.println("mapTo = " + mapTo);
                  }
               }
            }
         }
         if (mappedName != null)
            break;
         currentClass = currentClass.getSuperclass();
      }
      // System.out.println("... in getMappedMethodName, returning = " + mappedName);
      return (mappedName);
   }

   public static boolean isMappedMethod(MethodReferenceEntry methodReferenceEntry) {
      if (CacheEnabler.areCachesEnabled())
         return getBoolean(mappedMethodFlags, methodReferenceEntry);
      boolean isMapped = false;
      for (final Method kernelMethod : Kernel.class.getDeclaredMethods()) {
         if (kernelMethod.isAnnotationPresent(OpenCLMapping.class)) {
            if (methodReferenceEntry.getNameAndTypeEntry().getNameUTF8Entry().getUTF8().equals(kernelMethod.getName())) {

               // well they have the same name ;)
               isMapped = true;
            }
         }
      }
      return (isMapped);
   }

   public static boolean isOpenCLDelegateMethod(MethodReferenceEntry methodReferenceEntry) {
      if (CacheEnabler.areCachesEnabled())
         return getBoolean(openCLDelegateMethodFlags, methodReferenceEntry);
      boolean isMapped = false;
      for (final Method kernelMethod : Kernel.class.getDeclaredMethods()) {
         if (kernelMethod.isAnnotationPresent(OpenCLDelegate.class)) {
            if (methodReferenceEntry.getNameAndTypeEntry().getNameUTF8Entry().getUTF8().equals(kernelMethod.getName())) {

               // well they have the same name ;)
               isMapped = true;
            }
         }
      }
      return (isMapped);
   }

   public static boolean usesAtomic32(MethodReferenceEntry methodReferenceEntry) {
      if (CacheEnabler.areCachesEnabled())
         return getProperty(atomic32Cache, methodReferenceEntry, false);
      for (final Method kernelMethod : Kernel.class.getDeclaredMethods()) {
         if (kernelMethod.isAnnotationPresent(OpenCLMapping.class)) {
            if (methodReferenceEntry.getNameAndTypeEntry().getNameUTF8Entry().getUTF8().equals(kernelMethod.getName())) {
               final OpenCLMapping annotation = kernelMethod.getAnnotation(OpenCLMapping.class);
               return annotation.atomic32();
            }
         }
      }
      return (false);
   }

   // For alpha release atomic64 is not supported
   public static boolean usesAtomic64(MethodReferenceEntry methodReferenceEntry) {
      //      if (CacheEnabler.areCachesEnabled())
      //      return getProperty(atomic64Cache, methodReferenceEntry, false);
      //for (java.lang.reflect.Method kernelMethod : Kernel.class.getDeclaredMethods()) {
      //   if (kernelMethod.isAnnotationPresent(Kernel.OpenCLMapping.class)) {
      //      if (methodReferenceEntry.getNameAndTypeEntry().getNameUTF8Entry().getUTF8().equals(kernelMethod.getName())) {
      //         OpenCLMapping annotation = kernelMethod.getAnnotation(Kernel.OpenCLMapping.class);
      //           return annotation.atomic64();
      //      }
      //   }
      //}
      return (false);
   }

   // the flag useNullForLocalSize is useful for testing that what we compute for localSize is what OpenCL
   // would also compute if we passed in null.  In non-testing mode, we just call execute with the
   // same localSize that we computed in getLocalSizeJNI.  We don't want do publicize these of course.
   // GRF we can't access this from test classes without exposing in in javadoc so I left the flag but made the test/set of the flag reflectively
   boolean useNullForLocalSize = false;

   // Explicit memory management API's follow

   /**
    * For dev purposes (we should remove this for production) allow us to define that this Kernel uses explicit memory management
    * @param _explicit (true if we want explicit memory management)
    */
   public void setExplicit(boolean _explicit) {
      prepareKernelRunner().setExplicit(_explicit);
   }

   /**
    * For dev purposes (we should remove this for production) determine whether this Kernel uses explicit memory management
    * @return  (true if we kernel is using explicit memory management)
    */
   public boolean isExplicit() {
      return prepareKernelRunner().isExplicit();
   }

   /**
    * Tag this array so that it is explicitly enqueued before the kernel is executed
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel put(long[] array) {
      prepareKernelRunner().put(array);
      return (this);
   }

   /**
    * Tag this array so that it is explicitly enqueued before the kernel is executed
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel put(long[][] array) {
      prepareKernelRunner().put(array);
      return (this);
   }

   /**
    * Tag this array so that it is explicitly enqueued before the kernel is executed
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel put(long[][][] array) {
      prepareKernelRunner().put(array);
      return (this);
   }

   /**
    * Tag this array so that it is explicitly enqueued before the kernel is executed
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel put(double[] array) {
      prepareKernelRunner().put(array);
      return (this);
   }

   /**
    * Tag this array so that it is explicitly enqueued before the kernel is executed
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel put(double[][] array) {
      prepareKernelRunner().put(array);
      return (this);
   }

   /**
    * Tag this array so that it is explicitly enqueued before the kernel is executed
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel put(double[][][] array) {
      prepareKernelRunner().put(array);
      return (this);
   }

   /**
    * Tag this array so that it is explicitly enqueued before the kernel is executed
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel put(float[] array) {
      prepareKernelRunner().put(array);
      return (this);
   }

   /**
    * Tag this array so that it is explicitly enqueued before the kernel is executed
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel put(float[][] array) {
      prepareKernelRunner().put(array);
      return (this);
   }

   /**
    * Tag this array so that it is explicitly enqueued before the kernel is executed
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel put(float[][][] array) {
      prepareKernelRunner().put(array);
      return (this);
   }

   /**
    * Tag this array so that it is explicitly enqueued before the kernel is executed
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel put(int[] array) {
      prepareKernelRunner().put(array);
      return (this);
   }

   /**
    * Tag this array so that it is explicitly enqueued before the kernel is executed
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel put(int[][] array) {
      prepareKernelRunner().put(array);
      return (this);
   }

   /**
    * Tag this array so that it is explicitly enqueued before the kernel is executed
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel put(int[][][] array) {
      prepareKernelRunner().put(array);
      return (this);
   }

   /**
    * Tag this array so that it is explicitly enqueued before the kernel is executed
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel put(byte[] array) {
      prepareKernelRunner().put(array);
      return (this);
   }

   /**
    * Tag this array so that it is explicitly enqueued before the kernel is executed
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel put(byte[][] array) {
      prepareKernelRunner().put(array);
      return (this);
   }

   /**
    * Tag this array so that it is explicitly enqueued before the kernel is executed
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel put(byte[][][] array) {
      prepareKernelRunner().put(array);
      return (this);
   }

   /**
     * Tag this array so that it is explicitly enqueued before the kernel is executed
     * @param array
     * @return This kernel so that we can use the 'fluent' style API
     */
   public Kernel put(char[] array) {
      prepareKernelRunner().put(array);
      return (this);
   }

   /**
    * Tag this array so that it is explicitly enqueued before the kernel is executed
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel put(char[][] array) {
      prepareKernelRunner().put(array);
      return (this);
   }

   /**
    * Tag this array so that it is explicitly enqueued before the kernel is executed
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel put(char[][][] array) {
      prepareKernelRunner().put(array);
      return (this);
   }

   /**
    * Tag this array so that it is explicitly enqueued before the kernel is executed
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel put(boolean[] array) {
      prepareKernelRunner().put(array);
      return (this);
   }

   /**
    * Tag this array so that it is explicitly enqueued before the kernel is executed
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel put(boolean[][] array) {
      prepareKernelRunner().put(array);
      return (this);
   }

   /**
    * Tag this array so that it is explicitly enqueued before the kernel is executed
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel put(boolean[][][] array) {
      prepareKernelRunner().put(array);
      return (this);
   }

   /**
    * Enqueue a request to return this buffer from the GPU. This method blocks until the array is available.
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel get(long[] array) {
      prepareKernelRunner().get(array);
      return (this);
   }

   /**
    * Enqueue a request to return this buffer from the GPU. This method blocks until the array is available.
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel get(long[][] array) {
      prepareKernelRunner().get(array);
      return (this);
   }

   /**
    * Enqueue a request to return this buffer from the GPU. This method blocks until the array is available.
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel get(long[][][] array) {
      prepareKernelRunner().get(array);
      return (this);
   }

   /**
    * Enqueue a request to return this buffer from the GPU. This method blocks until the array is available.
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel get(double[] array) {
      prepareKernelRunner().get(array);
      return (this);
   }

   /**
    * Enqueue a request to return this buffer from the GPU. This method blocks until the array is available.
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel get(double[][] array) {
      prepareKernelRunner().get(array);
      return (this);
   }

   /**
    * Enqueue a request to return this buffer from the GPU. This method blocks until the array is available.
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel get(double[][][] array) {
      prepareKernelRunner().get(array);
      return (this);
   }

   /**
    * Enqueue a request to return this buffer from the GPU. This method blocks until the array is available.
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel get(float[] array) {
      prepareKernelRunner().get(array);
      return (this);
   }

   /**
    * Enqueue a request to return this buffer from the GPU. This method blocks until the array is available.
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel get(float[][] array) {
      prepareKernelRunner().get(array);
      return (this);
   }

   /**
    * Enqueue a request to return this buffer from the GPU. This method blocks until the array is available.
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel get(float[][][] array) {
      prepareKernelRunner().get(array);
      return (this);
   }

   /**
    * Enqueue a request to return this buffer from the GPU. This method blocks until the array is available.
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel get(int[] array) {
      prepareKernelRunner().get(array);
      return (this);
   }

   /**
    * Enqueue a request to return this buffer from the GPU. This method blocks until the array is available.
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel get(int[][] array) {
      prepareKernelRunner().get(array);
      return (this);
   }

   /**
    * Enqueue a request to return this buffer from the GPU. This method blocks until the array is available.
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel get(int[][][] array) {
      prepareKernelRunner().get(array);
      return (this);
   }

   /**
    * Enqueue a request to return this buffer from the GPU. This method blocks until the array is available.
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel get(byte[] array) {
      prepareKernelRunner().get(array);
      return (this);
   }

   /**
    * Enqueue a request to return this buffer from the GPU. This method blocks until the array is available.
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel get(byte[][] array) {
      prepareKernelRunner().get(array);
      return (this);
   }

   /**
    * Enqueue a request to return this buffer from the GPU. This method blocks until the array is available.
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel get(byte[][][] array) {
      prepareKernelRunner().get(array);
      return (this);
   }

   /**
    * Enqueue a request to return this buffer from the GPU. This method blocks until the array is available.
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel get(char[] array) {
      prepareKernelRunner().get(array);
      return (this);
   }

   /**
    * Enqueue a request to return this buffer from the GPU. This method blocks until the array is available.
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel get(char[][] array) {
      prepareKernelRunner().get(array);
      return (this);
   }

   /**
    * Enqueue a request to return this buffer from the GPU. This method blocks until the array is available.
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel get(char[][][] array) {
      prepareKernelRunner().get(array);
      return (this);
   }

   /**
    * Enqueue a request to return this buffer from the GPU. This method blocks until the array is available.
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel get(boolean[] array) {
      prepareKernelRunner().get(array);
      return (this);
   }

   /**
    * Enqueue a request to return this buffer from the GPU. This method blocks until the array is available.
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel get(boolean[][] array) {
      prepareKernelRunner().get(array);
      return (this);
   }

   /**
    * Enqueue a request to return this buffer from the GPU. This method blocks until the array is available.
    * @param array
    * @return This kernel so that we can use the 'fluent' style API
    */
   public Kernel get(boolean[][][] array) {
      prepareKernelRunner().get(array);
      return (this);
   }

   /**
    * Get the profiling information from the last successful call to Kernel.execute().
    * @return A list of ProfileInfo records
    */
   public List<ProfileInfo> getProfileInfo() {
      return prepareKernelRunner().getProfileInfo();
   }

   /**
    * @deprecated See {@link EXECUTION_MODE}.
    */
   @Deprecated
  private final LinkedHashSet<EXECUTION_MODE> executionModes = (Config.executionMode != null) ? EXECUTION_MODE.getDefaultExecutionModes() :  new LinkedHashSet<>(Collections.singleton(EXECUTION_MODE.AUTO));

   /**
    * @deprecated See {@link EXECUTION_MODE}.
    */
   @Deprecated
  private Iterator<EXECUTION_MODE> currentMode = executionModes.iterator();

   /**
    * @deprecated See {@link EXECUTION_MODE}.
    */
   @Deprecated
  private EXECUTION_MODE executionMode = currentMode.next();

   /**
    * @deprecated See {@link EXECUTION_MODE}.
    * <p>
    * set possible fallback path for execution modes.
    * for example setExecutionFallbackPath(GPU,CPU,JTP) will try to use the GPU
    * if it fails it will fall back to OpenCL CPU and finally it will try JTP.
    */
   @Deprecated
  public void addExecutionModes(EXECUTION_MODE... platforms) {
      executionModes.addAll(Arrays.asList(platforms));
      currentMode = executionModes.iterator();
      executionMode = currentMode.next();
   }

   /**
    * @deprecated See {@link EXECUTION_MODE}.
    * @return is there another execution path we can try
    */
   @Deprecated
  public boolean hasNextExecutionMode() {
      return currentMode.hasNext();
   }

   /**
    * @deprecated See {@link EXECUTION_MODE}.
    * try the next execution path in the list if there aren't any more than give up
    */
   @Deprecated
  public void tryNextExecutionMode() {
      if (currentMode.hasNext()) {
         executionMode = currentMode.next();
      }
   }

   private static final ValueCache<Class<?>, Map<String, Boolean>, RuntimeException> mappedMethodFlags = markedWith(OpenCLMapping.class);

   private static final ValueCache<Class<?>, Map<String, Boolean>, RuntimeException> openCLDelegateMethodFlags = markedWith(OpenCLDelegate.class);

   private static final ValueCache<Class<?>, Map<String, Boolean>, RuntimeException> atomic32Cache = cacheProperty(new ValueComputer<Class<?>, Map<String, Boolean>>() {
      @Override
      public Map<String, Boolean> compute(Class<?> key) {
         Map<String, Boolean> properties = new HashMap<>();
         for (final Method method : key.getDeclaredMethods()) {
            if (isRelevant(method) && method.isAnnotationPresent(OpenCLMapping.class)) {
               properties.put(toSignature(method), method.getAnnotation(OpenCLMapping.class).atomic32());
            }
         }
         return properties;
      }
   });

   private static final ValueCache<Class<?>, Map<String, Boolean>, RuntimeException> atomic64Cache = cacheProperty(new ValueComputer<Class<?>, Map<String, Boolean>>() {
      @Override
      public Map<String, Boolean> compute(Class<?> key) {
         Map<String, Boolean> properties = new HashMap<>();
         for (final Method method : key.getDeclaredMethods()) {
            if (isRelevant(method) && method.isAnnotationPresent(OpenCLMapping.class)) {
               properties.put(toSignature(method), method.getAnnotation(OpenCLMapping.class).atomic64());
            }
         }
         return properties;
      }
   });

   private static boolean getBoolean(ValueCache<Class<?>, Map<String, Boolean>, RuntimeException> methodNamesCache,
         MethodReferenceEntry methodReferenceEntry) {
      return getProperty(methodNamesCache, methodReferenceEntry, false);
   }

   private static <A extends Annotation> ValueCache<Class<?>, Map<String, Boolean>, RuntimeException> markedWith(
         final Class<A> annotationClass) {
      return cacheProperty(new ValueComputer<Class<?>, Map<String, Boolean>>() {
         @Override
         public Map<String, Boolean> compute(Class<?> key) {
            Map<String, Boolean> markedMethodNames = new HashMap<>();
            for (final Method method : key.getDeclaredMethods()) {
               markedMethodNames.put(toSignature(method), method.isAnnotationPresent(annotationClass));
            }
            return markedMethodNames;
         }
      });
   }

   static String toSignature(Method method) {
      return method.getName() + getArgumentsLetters(method) + getReturnTypeLetter(method);
   }

   private static String getArgumentsLetters(Method method) {
      StringBuilder sb = new StringBuilder("(");
      for (Class<?> parameterClass : method.getParameterTypes()) {
         sb.append(toClassShortNameIfAny(parameterClass));
      }
      sb.append(")");
      return sb.toString();
   }

   private static boolean isRelevant(Method method) {
      return !method.isSynthetic() && !method.isBridge();
   }

   private static <V, T extends Throwable> V getProperty(ValueCache<Class<?>, Map<String, V>, T> cache,
         MethodReferenceEntry methodReferenceEntry, V defaultValue) throws T {
      Map<String, V> map = cache.computeIfAbsent(methodReferenceEntry.getOwnerClassModel().getClassWeAreModelling());
      String key = toSignature(methodReferenceEntry);
      if (map.containsKey(key))
         return map.get(key);
      return defaultValue;
   }

   private static String toSignature(MethodReferenceEntry methodReferenceEntry) {
      NameAndTypeEntry nameAndTypeEntry = methodReferenceEntry.getNameAndTypeEntry();
      return nameAndTypeEntry.getNameUTF8Entry().getUTF8() + nameAndTypeEntry.getDescriptorUTF8Entry().getUTF8();
   }

   private static final ValueCache<Class<?>, Map<String, String>, RuntimeException> mappedMethodNamesCache = cacheProperty(new ValueComputer<Class<?>, Map<String, String>>() {
      @Override
      public Map<String, String> compute(Class<?> key) {
         Map<String, String> properties = new HashMap<>();
         for (final Method method : key.getDeclaredMethods()) {
            if (isRelevant(method) && method.isAnnotationPresent(OpenCLMapping.class)) {
               // ultimately, need a way to constrain this based upon signature (to disambiguate abs(float) from abs(int);
               final OpenCLMapping annotation = method.getAnnotation(OpenCLMapping.class);
               final String mapTo = annotation.mapTo();
               if (mapTo != null && !mapTo.equals("")) {
                  properties.put(toSignature(method), mapTo);
               }
            }
         }
         return properties;
      }
   });

   private static <K, V, T extends Throwable> ValueCache<Class<?>, Map<K, V>, T> cacheProperty(
         final ThrowingValueComputer<Class<?>, Map<K, V>, T> throwingValueComputer) {
      return ValueCache.on(new ThrowingValueComputer<Class<?>, Map<K, V>, T>() {
         @Override
         public Map<K, V> compute(Class<?> key) throws T {
            Map<K, V> properties = new HashMap<>();
            Deque<Class<?>> superclasses = new ArrayDeque<>();
            Class<?> currentSuperClass = key;
            do {
               superclasses.push(currentSuperClass);
               currentSuperClass = currentSuperClass.getSuperclass();
            } while (currentSuperClass != Object.class);
            for (Class<?> clazz : superclasses) {
               // Overwrite property values for shadowed/overriden methods
               properties.putAll(throwingValueComputer.compute(clazz));
            }
            return properties;
         }
      });
   }

   public static void invalidateCaches() {
      atomic32Cache.invalidate();
      atomic64Cache.invalidate();
      mappedMethodFlags.invalidate();
      mappedMethodNamesCache.invalidate();
      openCLDelegateMethodFlags.invalidate();
   }
}
