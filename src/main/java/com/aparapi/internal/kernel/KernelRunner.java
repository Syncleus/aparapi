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
package com.aparapi.internal.kernel;

import com.aparapi.*;
import com.aparapi.Kernel.Constant;
import com.aparapi.Kernel.*;
import com.aparapi.device.*;
import com.aparapi.internal.annotation.*;
import com.aparapi.internal.exception.*;
import com.aparapi.internal.instruction.InstructionSet.*;
import com.aparapi.internal.jni.*;
import com.aparapi.internal.model.*;
import com.aparapi.internal.util.*;
import com.aparapi.internal.writer.*;
import com.aparapi.opencl.*;

import java.lang.reflect.*;
import java.nio.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.ForkJoinPool.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.*;

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
 * @see com.aparapi.Kernel#execute(int _globalSize)
 * 
 * @author gfrost
 *
 */
public class KernelRunner extends KernelRunnerJNI{

   public static boolean BINARY_CACHING_DISABLED = false;

   private static final int MINIMUM_ARRAY_SIZE = 1;

   /** @see #getCurrentPass() */
   @UsedByJNICode public static final int PASS_ID_PREPARING_EXECUTION = -2;
   /** @see #getCurrentPass() */
   @UsedByJNICode public static final int PASS_ID_COMPLETED_EXECUTION = -1;
   @UsedByJNICode public static final int CANCEL_STATUS_FALSE = 0;
   @UsedByJNICode public static final int CANCEL_STATUS_TRUE = 1;
   private static final String CODE_GEN_ERROR_MARKER = CodeGenException.class.getName();

   private static Logger logger = Logger.getLogger(Config.getLoggerName());

   private long jniContextHandle = 0;

   private final Kernel kernel;

   private Entrypoint entryPoint;

   private int argc;

   // may be read by a thread other than the control thread, hence volatile
   private volatile boolean executing;

   // may be read by a thread other than the control thread, hence volatile
   private volatile int passId = PASS_ID_PREPARING_EXECUTION;

   /**
    * A direct ByteBuffer used for asynchronous intercommunication between java and JNI C code.
    *
    * <p>
    * At present this is a 4 byte buffer to be interpreted as an int[1], used for passing from java to C a single integer interpreted as a cancellation indicator.
    */
   private final ByteBuffer inBufferRemote;
   private final IntBuffer inBufferRemoteInt;

   /** A direct ByteBuffer used for asynchronous intercommunication between java and JNI C code.
    * <p>
    * At present this is a 4 byte buffer to be interpreted as an int[1], used for passing from C to java a single integer interpreted as a
    * the current pass id.
    */
   private final ByteBuffer outBufferRemote;
   private final IntBuffer outBufferRemoteInt;

   private boolean isFallBack = false; // If isFallBack, rebuild the kernel (necessary?)

   private static final ForkJoinWorkerThreadFactory lowPriorityThreadFactory = new ForkJoinWorkerThreadFactory(){
      @Override public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
         ForkJoinWorkerThread newThread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
         newThread.setPriority(Thread.MIN_PRIORITY);
         return newThread;
      }
   };

   private static final ForkJoinPool threadPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors(),
         lowPriorityThreadFactory, null, false);
   private static HashMap<Class<? extends Kernel>, String> openCLCache = new HashMap<>();
   private static LinkedHashSet<String> seenBinaryKeys = new LinkedHashSet<>();

   /**
    * Create a KernelRunner for a specific Kernel instance.
    * 
    * @param _kernel
    */
   public KernelRunner(Kernel _kernel) {
      kernel = _kernel;

      inBufferRemote = ByteBuffer.allocateDirect(4);
      outBufferRemote = ByteBuffer.allocateDirect(4);

      inBufferRemote.order(ByteOrder.nativeOrder());
      outBufferRemote.order(ByteOrder.nativeOrder());

      inBufferRemoteInt = inBufferRemote.asIntBuffer();
      outBufferRemoteInt = outBufferRemote.asIntBuffer();

      KernelManager.instance(); // ensures static initialization of KernelManager
   }

   /**
    * @see Kernel#cleanUpArrays().
    */
   public void cleanUpArrays() {
      if (args != null && kernel.isRunningCL()) {
         for (KernelArg arg : args) {
            if ((arg.getType() & KernelRunnerJNI.ARG_ARRAY) != 0) {
               Field field = arg.getField();
               if (field != null && field.getType().isArray() && !Modifier.isFinal(field.getModifiers())) {
                  field.setAccessible(true);
                  Class<?> componentType = field.getType().getComponentType();
                  Object newValue = Array.newInstance(componentType, MINIMUM_ARRAY_SIZE);
                  try {
                     field.set(kernel, newValue);
                  }
                  catch (IllegalAccessException e) {
                     throw new RuntimeException(e);
                  }
               }
            }
         }
         kernel.execute(0);
      } else if (kernel.isRunningCL()) {
         logger.log(Level.SEVERE, "KernelRunner#cleanUpArrays() could not execute as no args available (Kernel has not been executed?)");
      }
   }

   /**
    * <code>Kernel.dispose()</code> delegates to <code>KernelRunner.dispose()</code> which delegates to <code>disposeJNI()</code> to actually close JNI data structures.<br/>
    * 
    * @see KernelRunnerJNI#disposeJNI(long)
    */
   public synchronized void dispose() {
      if (kernel.isRunningCL()) {
         disposeJNI(jniContextHandle);
         seenBinaryKeys.clear();
      }
      // We are using a shared pool, so there's no need no shutdown it when kernel is disposed
      //      threadPool.shutdownNow();
   }

   private Set<String> capabilitiesSet;

   boolean hasFP64Support() {
      if (capabilitiesSet == null) {
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return (capabilitiesSet.contains(OpenCL.CL_KHR_FP64));
   }

   boolean hasSelectFPRoundingModeSupport() {
      if (capabilitiesSet == null) {
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(OpenCL.CL_KHR_SELECT_FPROUNDING_MODE);
   }

   boolean hasGlobalInt32BaseAtomicsSupport() {
      if (capabilitiesSet == null) {
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(OpenCL.CL_KHR_GLOBAL_INT32_BASE_ATOMICS);
   }

   boolean hasGlobalInt32ExtendedAtomicsSupport() {
      if (capabilitiesSet == null) {
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(OpenCL.CL_KHR_GLOBAL_INT32_EXTENDED_ATOMICS);
   }

   boolean hasLocalInt32BaseAtomicsSupport() {
      if (capabilitiesSet == null) {
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(OpenCL.CL_KHR_LOCAL_INT32_BASE_ATOMICS);
   }

   boolean hasLocalInt32ExtendedAtomicsSupport() {
      if (capabilitiesSet == null) {
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(OpenCL.CL_KHR_LOCAL_INT32_EXTENDED_ATOMICS);
   }

   boolean hasInt64BaseAtomicsSupport() {
      if (capabilitiesSet == null) {
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(OpenCL.CL_KHR_INT64_BASE_ATOMICS);
   }

   boolean hasInt64ExtendedAtomicsSupport() {
      if (capabilitiesSet == null) {
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(OpenCL.CL_KHR_INT64_EXTENDED_ATOMICS);
   }

   boolean has3DImageWritesSupport() {
      if (capabilitiesSet == null) {
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(OpenCL.CL_KHR_3D_IMAGE_WRITES);
   }

   boolean hasByteAddressableStoreSupport() {
      if (capabilitiesSet == null) {
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(OpenCL.CL_KHR_BYTE_ADDRESSABLE_SUPPORT);
   }

   boolean hasFP16Support() {
      if (capabilitiesSet == null) {
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(OpenCL.CL_KHR_FP16);
   }

   boolean hasGLSharingSupport() {
      if (capabilitiesSet == null) {
         throw new IllegalStateException("Capabilities queried before they were initialized");
      }
      return capabilitiesSet.contains(OpenCL.CL_KHR_GL_SHARING);
   }

   private static final class FJSafeCyclicBarrier extends CyclicBarrier{
      FJSafeCyclicBarrier(final int threads) {
         super(threads);
      }

      @Override public int await() throws InterruptedException, BrokenBarrierException {
         class Awaiter implements ManagedBlocker{
            private int value;

            private boolean released;

            @Override public boolean block() throws InterruptedException {
               try {
                  value = superAwait();
                  released = true;
                  return true;
               } catch (final BrokenBarrierException e) {
                  throw new RuntimeException(e);
               }
            }

            @Override public boolean isReleasable() {
               return released;
            }

            int getValue() {
               return value;
            }
         }
         final Awaiter awaiter = new Awaiter();
         ForkJoinPool.managedBlock(awaiter);
         return awaiter.getValue();
      }

      int superAwait() throws InterruptedException, BrokenBarrierException {
         return super.await();
      }
   }

   //   @FunctionalInterface
   private interface ThreadIdSetter{
      void set(KernelState kernelState, int globalGroupId, int threadId);
   }

   /**
    * Execute using a Java thread pool, or sequentially, or using an alternative algorithm, usually as a result of failing to compile or execute OpenCL
    */
   @SuppressWarnings("deprecation")
   protected void executeJava(ExecutionSettings _settings, Device device) {
      if (logger.isLoggable(Level.FINE)) {
         logger.fine("executeJava: range = " + _settings.range + ", device = " + device);
      }
      boolean legacySequentialMode = kernel.getExecutionMode().equals(Kernel.EXECUTION_MODE.SEQ);

      passId = PASS_ID_PREPARING_EXECUTION;
      _settings.profile.onEvent(ProfilingEvent.PREPARE_EXECUTE);

      try {
         if (device == JavaDevice.ALTERNATIVE_ALGORITHM) {
            if (kernel.hasFallbackAlgorithm()) {
               for (passId = 0; passId < _settings.passes; ++passId) {
                  kernel.executeFallbackAlgorithm(_settings.range, passId);
               }
            } else {
               boolean silently = true; // not having an alternative algorithm is the normal state, and does not need reporting
               fallBackToNextDevice(_settings, (Exception) null, silently);
            }
         } else {
            final int localSize0 = _settings.range.getLocalSize(0);
            final int localSize1 = _settings.range.getLocalSize(1);
            final int localSize2 = _settings.range.getLocalSize(2);
            final int globalSize1 = _settings.range.getGlobalSize(1);
            if (legacySequentialMode || device == JavaDevice.SEQUENTIAL) {
               /**
                * SEQ mode is useful for testing trivial logic, but kernels which use SEQ mode cannot be used if the
                * product of localSize(0..3) is >1.  So we can use multi-dim ranges but only if the local size is 1 in all dimensions.
                *
                * As a result of this barrier is only ever 1 work item wide and probably should be turned into a no-op.
                *
                * So we need to check if the range is valid here. If not we have no choice but to punt.
                */
               if ((localSize0 * localSize1 * localSize2) > 1) {
                  throw new IllegalStateException("Can't run range with group size >1 sequentially. Barriers would deadlock!");
               }

               final Kernel kernelClone = kernel.clone();
               final KernelState kernelState = kernelClone.getKernelState();

               kernelState.setRange(_settings.range);
               kernelState.setGroupId(0, 0);
               kernelState.setGroupId(1, 0);
               kernelState.setGroupId(2, 0);
               kernelState.setLocalId(0, 0);
               kernelState.setLocalId(1, 0);
               kernelState.setLocalId(2, 0);
               kernelState.setLocalBarrier(new FJSafeCyclicBarrier(1));

               for (passId = 0; passId < _settings.passes; passId++) {
                  if (getCancelState() == CANCEL_STATUS_TRUE) {
                     break;
                  }
                  kernelState.setPassId(passId);

                  if (_settings.range.getDims() == 1) {
                     for (int id = 0; id < _settings.range.getGlobalSize(0); id++) {
                        kernelState.setGlobalId(0, id);
                        kernelClone.run();
                     }
                  }
                  else if (_settings.range.getDims() == 2) {
                     for (int x = 0; x < _settings.range.getGlobalSize(0); x++) {
                        kernelState.setGlobalId(0, x);

                        for (int y = 0; y < globalSize1; y++) {
                           kernelState.setGlobalId(1, y);
                           kernelClone.run();
                        }
                     }
                  }
                  else if (_settings.range.getDims() == 3) {
                     for (int x = 0; x < _settings.range.getGlobalSize(0); x++) {
                        kernelState.setGlobalId(0, x);

                        for (int y = 0; y < globalSize1; y++) {
                           kernelState.setGlobalId(1, y);

                           for (int z = 0; z < _settings.range.getGlobalSize(2); z++) {
                              kernelState.setGlobalId(2, z);
                              kernelClone.run();
                           }

                           kernelClone.run();
                        }
                     }
                  }
               }
               passId = PASS_ID_COMPLETED_EXECUTION;
            }
            else {
               if (device != JavaDevice.THREAD_POOL && kernel.getExecutionMode() != Kernel.EXECUTION_MODE.JTP) {
                  throw new AssertionError("unexpected JavaDevice or EXECUTION_MODE");
               }
               final int threads = localSize0 * localSize1 * localSize2;
               final int numGroups0 = _settings.range.getNumGroups(0);
               final int numGroups1 = _settings.range.getNumGroups(1);
               final int globalGroups = numGroups0 * numGroups1 * _settings.range.getNumGroups(2);
               /**
                * This joinBarrier is the barrier that we provide for the kernel threads to rendezvous with the current dispatch thread.
                * So this barrier is threadCount+1 wide (the +1 is for the dispatch thread)
                */
               final CyclicBarrier joinBarrier = new FJSafeCyclicBarrier(threads + 1);

               /**
                * This localBarrier is only ever used by the kernels.  If the kernel does not use the barrier the threads
                * can get out of sync, we promised nothing in JTP mode.
                *
                * As with OpenCL all threads within a group must wait at the barrier or none.  It is a user error (possible deadlock!)
                * if the barrier is in a conditional that is only executed by some of the threads within a group.
                *
                * Kernel developer must understand this.
                *
                * This barrier is threadCount wide.  We never hit the barrier from the dispatch thread.
                */
               final CyclicBarrier localBarrier = new FJSafeCyclicBarrier(threads);

               final ThreadIdSetter threadIdSetter;

               if (_settings.range.getDims() == 1) {
                  threadIdSetter = new ThreadIdSetter() {
                     @Override
                     public void set(KernelState kernelState, int globalGroupId, int threadId) {
                        //                   (kernelState, globalGroupId, threadId) ->{
                        kernelState.setLocalId(0, (threadId % localSize0));
                        kernelState.setGlobalId(0, (threadId + (globalGroupId * threads)));
                        kernelState.setGroupId(0, globalGroupId);
                     }
                  };
               }
               else if (_settings.range.getDims() == 2) {

                  /**
                   * Consider a 12x4 grid of 4*2 local groups
                   * <pre>
                   *                                             threads = 4*2 = 8
                   *                                             localWidth=4
                   *                                             localHeight=2
                   *                                             globalWidth=12
                   *                                             globalHeight=4
                   *
                   *    00 01 02 03 | 04 05 06 07 | 08 09 10 11
                   *    12 13 14 15 | 16 17 18 19 | 20 21 22 23
                   *    ------------+-------------+------------
                   *    24 25 26 27 | 28 29 30 31 | 32 33 34 35
                   *    36 37 38 39 | 40 41 42 43 | 44 45 46 47
                   *
                   *    00 01 02 03 | 00 01 02 03 | 00 01 02 03  threadIds : [0..7]*6
                   *    04 05 06 07 | 04 05 06 07 | 04 05 06 07
                   *    ------------+-------------+------------
                   *    00 01 02 03 | 00 01 02 03 | 00 01 02 03
                   *    04 05 06 07 | 04 05 06 07 | 04 05 06 07
                   *
                   *    00 00 00 00 | 01 01 01 01 | 02 02 02 02  groupId[0] : 0..6
                   *    00 00 00 00 | 01 01 01 01 | 02 02 02 02
                   *    ------------+-------------+------------
                   *    00 00 00 00 | 01 01 01 01 | 02 02 02 02
                   *    00 00 00 00 | 01 01 01 01 | 02 02 02 02
                   *
                   *    00 00 00 00 | 00 00 00 00 | 00 00 00 00  groupId[1] : 0..6
                   *    00 00 00 00 | 00 00 00 00 | 00 00 00 00
                   *    ------------+-------------+------------
                   *    01 01 01 01 | 01 01 01 01 | 01 01 01 01
                   *    01 01 01 01 | 01 01 01 01 | 01 01 01 01
                   *
                   *    00 01 02 03 | 08 09 10 11 | 16 17 18 19  globalThreadIds == threadId + groupId * threads;
                   *    04 05 06 07 | 12 13 14 15 | 20 21 22 23
                   *    ------------+-------------+------------
                   *    24 25 26 27 | 32[33]34 35 | 40 41 42 43
                   *    28 29 30 31 | 36 37 38 39 | 44 45 46 47
                   *
                   *    00 01 02 03 | 00 01 02 03 | 00 01 02 03  localX = threadId % localWidth; (for globalThreadId 33 = threadId = 01 : 01%4 =1)
                   *    00 01 02 03 | 00 01 02 03 | 00 01 02 03
                   *    ------------+-------------+------------
                   *    00 01 02 03 | 00[01]02 03 | 00 01 02 03
                   *    00 01 02 03 | 00 01 02 03 | 00 01 02 03
                   *
                   *    00 00 00 00 | 00 00 00 00 | 00 00 00 00  localY = threadId /localWidth  (for globalThreadId 33 = threadId = 01 : 01/4 =0)
                   *    01 01 01 01 | 01 01 01 01 | 01 01 01 01
                   *    ------------+-------------+------------
                   *    00 00 00 00 | 00[00]00 00 | 00 00 00 00
                   *    01 01 01 01 | 01 01 01 01 | 01 01 01 01
                   *
                   *    00 01 02 03 | 04 05 06 07 | 08 09 10 11  globalX=
                   *    00 01 02 03 | 04 05 06 07 | 08 09 10 11     groupsPerLineWidth=globalWidth/localWidth (=12/4 =3)
                   *    ------------+-------------+------------     groupInset =groupId%groupsPerLineWidth (=4%3 = 1)
                   *    00 01 02 03 | 04[05]06 07 | 08 09 10 11
                   *    00 01 02 03 | 04 05 06 07 | 08 09 10 11     globalX = groupInset*localWidth+localX (= 1*4+1 = 5)
                   *
                   *    00 00 00 00 | 00 00 00 00 | 00 00 00 00  globalY
                   *    01 01 01 01 | 01 01 01 01 | 01 01 01 01
                   *    ------------+-------------+------------
                   *    02 02 02 02 | 02[02]02 02 | 02 02 02 02
                   *    03 03 03 03 | 03 03 03 03 | 03 03 03 03
                   *
                   * </pre>
                   * Assume we are trying to locate the id's for #33
                   *
                   */
                  threadIdSetter = new ThreadIdSetter() {
                     @Override
                     public void set(KernelState kernelState, int globalGroupId, int threadId) {
                        //                   (kernelState, globalGroupId, threadId) ->{
                        kernelState.setLocalId(0, (threadId % localSize0)); // threadId % localWidth =  (for 33 = 1 % 4 = 1)
                        kernelState.setLocalId(1, (threadId / localSize0)); // threadId / localWidth = (for 33 = 1 / 4 == 0)

                        final int groupInset = globalGroupId % numGroups0; // 4%3 = 1
                        kernelState.setGlobalId(0, ((groupInset * localSize0) + kernelState.getLocalIds()[0])); // 1*4+1=5

                        final int completeLines = (globalGroupId / numGroups0) * localSize1;// (4/3) * 2
                        kernelState.setGlobalId(1, (completeLines + kernelState.getLocalIds()[1])); // 2+0 = 2
                        kernelState.setGroupId(0, (globalGroupId % numGroups0));
                        kernelState.setGroupId(1, (globalGroupId / numGroups0));
                     }
                  };
               }
               else if (_settings.range.getDims() == 3) {
                  //Same as 2D actually turns out that localId[0] is identical for all three dims so could be hoisted out of conditional code
                  threadIdSetter = new ThreadIdSetter() {
                     @Override
                     public void set(KernelState kernelState, int globalGroupId, int threadId) {
                        //                   (kernelState, globalGroupId, threadId) ->{
                        kernelState.setLocalId(0, (threadId % localSize0));

                        kernelState.setLocalId(1, ((threadId / localSize0) % localSize1));

                        // the thread id's span WxHxD so threadId/(WxH) should yield the local depth
                        kernelState.setLocalId(2, (threadId / (localSize0 * localSize1)));

                        kernelState.setGlobalId(0, (((globalGroupId % numGroups0) * localSize0) + kernelState.getLocalIds()[0]));

                        kernelState.setGlobalId(1,
                        ((((globalGroupId / numGroups0) * localSize1) % globalSize1) + kernelState.getLocalIds()[1]));

                        kernelState.setGlobalId(2,
                        (((globalGroupId / (numGroups0 * numGroups1)) * localSize2) + kernelState.getLocalIds()[2]));

                        kernelState.setGroupId(0, (globalGroupId % numGroups0));
                        kernelState.setGroupId(1, ((globalGroupId / numGroups0) % numGroups1));
                        kernelState.setGroupId(2, (globalGroupId / (numGroups0 * numGroups1)));
                     }
                  };
               }
               else
                  throw new IllegalArgumentException("Expected 1,2 or 3 dimensions, found " + _settings.range.getDims());
               for (passId = 0; passId < _settings.passes; passId++) {
                  if (getCancelState() == CANCEL_STATUS_TRUE) {
                     break;
                  }
                  /**
                   * Note that we emulate OpenCL by creating one thread per localId (across the group).
                   *
                   * So threadCount == range.getLocalSize(0)*range.getLocalSize(1)*range.getLocalSize(2);
                   *
                   * For a 1D range of 12 groups of 4 we create 4 threads. One per localId(0).
                   *
                   * We also clone the kernel 4 times. One per thread.
                   *
                   * We create local barrier which has a width of 4
                   *
                   *    Thread-0 handles localId(0) (global 0,4,8)
                   *    Thread-1 handles localId(1) (global 1,5,7)
                   *    Thread-2 handles localId(2) (global 2,6,10)
                   *    Thread-3 handles localId(3) (global 3,7,11)
                   *
                   * This allows all threads to synchronize using the local barrier.
                   *
                   * Initially the use of local buffers seems broken as the buffers appears to be per Kernel.
                   * Thankfully Kernel.clone() performs a shallow clone of all buffers (local and global)
                   * So each of the cloned kernels actually still reference the same underlying local/global buffers.
                   *
                   * If the kernel uses local buffers but does not use barriers then it is possible for different groups
                   * to see mutations from each other (unlike OpenCL), however if the kernel does not us barriers then it
                   * cannot assume any coherence in OpenCL mode either (the failure mode will be different but still wrong)
                   *
                   * So even JTP mode use of local buffers will need to use barriers. Not for the same reason as OpenCL but to keep groups in lockstep.
                   *
                   **/
                  for (int id = 0; id < threads; id++) {
                     final int threadId = id;

                     /**
                      *  We clone one kernel for each thread.
                      *
                      *  They will all share references to the same range, localBarrier and global/local buffers because the clone is shallow.
                      *  We need clones so that each thread can assign 'state' (localId/globalId/groupId) without worrying
                      *  about other threads.
                      */
                     final Kernel kernelClone = kernel.clone();
                     final KernelState kernelState = kernelClone.getKernelState();
                     kernelState.setRange(_settings.range);
                     kernelState.setPassId(passId);

                     if (threads == 1) {
                        kernelState.disableLocalBarrier();
                     }
                     else {
                        kernelState.setLocalBarrier(localBarrier);
                     }

                     threadPool.submit(
                     //                     () -> {
                     new Runnable() {
                        public void run() {
                           try {
                              for (int globalGroupId = 0; globalGroupId < globalGroups; globalGroupId++) {
                                 threadIdSetter.set(kernelState, globalGroupId, threadId);
                                 kernelClone.run();
                              }
                           }
                           catch (RuntimeException | Error e) {
                              logger.log(Level.SEVERE, "Execution failed", e);
                           }
                           finally {
                              await(joinBarrier); // This thread will rendezvous with dispatch thread here. This is effectively a join.
                           }
                        }
                     });
                  }

                  await(joinBarrier); // This dispatch thread waits for all worker threads here.
               }
               passId = PASS_ID_COMPLETED_EXECUTION;
            } // execution mode == JTP
         }
      } finally {
         passId = PASS_ID_COMPLETED_EXECUTION;
      }
   }

   private static void await(CyclicBarrier _barrier) {
      try {
         _barrier.await();
      } catch (final InterruptedException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final BrokenBarrierException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   private KernelArg[] args = null;

   private boolean usesOopConversion = false;

   /**
    * Helper method to retrieve the class model from a kernel argument. 
    * @param arg the kernel argument
    * @param arrayClass the array Java class for the argument 
    * @return the Aparapi ClassModel instance.
    */
   private ClassModel getClassModelFromArg(KernelArg arg, final Class<?> arrayClass) {
	  ClassModel c = null;
      if (arg.getObjArrayElementModel() == null) {
          final String tmp = arrayClass.getName().substring(2).replace('/', '.');
          final String arrayClassInDotForm = tmp.substring(0, tmp.length() - 1);

          if (logger.isLoggable(Level.FINE)) {
             logger.fine("looking for type = " + arrayClassInDotForm);
          }

          // get ClassModel of obj array from entrypt.objectArrayFieldsClasses
          c = entryPoint.getObjectArrayFieldsClasses().get(arrayClassInDotForm);
          arg.setObjArrayElementModel(c);
       } else {
          c = arg.getObjArrayElementModel();
       }
       assert c != null : "should find class for elements " + arrayClass.getName();
       
       return c;
   }
   
   /**
    * Helper method that manages the memory allocation for storing the kernel argument data,
    * so that the data can be exchanged between the host and the OpenCL device.  
    * @param arg the kernel argument
    * @param newRef the actual Java data instance
    * @param objArraySize the number of elements in the Java array
    * @param totalStructSize  the size of each target array element
    * @param totalBufferSize the total buffer size including memory alignment
    * @return <ul><li>true, if internal buffer had to be allocated or reallocated holding the data</li>
    * <li>false, if buffer didn't change and is already allocated</li></ul>
    */
   public boolean allocateArrayBufferIfFirstTimeOrArrayChanged(KernelArg arg, Object newRef, 
		   			final int objArraySize, final int totalStructSize, final int totalBufferSize) {
	   boolean didReallocate = false;
	   
	   if ((arg.getObjArrayBuffer() == null) || (newRef != arg.getArray())) {
	      final ByteBuffer structBuffer = ByteBuffer.allocate(totalBufferSize);
	      arg.setObjArrayByteBuffer(structBuffer.order(ByteOrder.LITTLE_ENDIAN));
	      arg.setObjArrayBuffer(arg.getObjArrayByteBuffer().array());
	      didReallocate = true;
	      if (logger.isLoggable(Level.FINEST)) {
	         logger.finest("objArraySize = " + objArraySize + " totalStructSize= " + totalStructSize + " totalBufferSize="
	               + totalBufferSize);
	      }
	   } else {
	      arg.getObjArrayByteBuffer().clear();
	   }
	   
	   return didReallocate;
   }

   /**
    * 
    * @param arg
    * @return
    * @throws AparapiException
    */
   private boolean prepareOopConversionBuffer(KernelArg arg) throws AparapiException {
      usesOopConversion = true;
      final Class<?> arrayClass = arg.getField().getType();
      ClassModel c = getClassModelFromArg(arg, arrayClass);

      final int arrayBaseOffset = UnsafeWrapper.arrayBaseOffset(arrayClass);
      final int arrayScale = UnsafeWrapper.arrayIndexScale(arrayClass);

      if (logger.isLoggable(Level.FINEST)) {
         logger.finest("Syncing obj array type = " + arrayClass + " cvtd= " + c.getClassWeAreModelling().getName()
               + "arrayBaseOffset=" + arrayBaseOffset + " arrayScale=" + arrayScale);
      }

      int objArraySize = 0;
      Object newRef = null;
      try {
         newRef = arg.getField().get(kernel);
         objArraySize = Array.getLength(newRef);
      } catch (final IllegalAccessException e) {
         throw new AparapiException(e);
      }

      assert (newRef != null) && (objArraySize != 0) : "no data";

      final int totalStructSize = c.getTotalStructSize();
      final int totalBufferSize = objArraySize * totalStructSize;

      // allocate ByteBuffer if first time or array changed
      boolean didReallocate = allocateArrayBufferIfFirstTimeOrArrayChanged(arg, newRef, objArraySize, totalStructSize, totalBufferSize);

      // copy the fields that the JNI uses
      arg.setJavaArray(arg.getObjArrayBuffer());
      arg.setNumElements(objArraySize);
      arg.setSizeInBytes(totalBufferSize);

      for (int j = 0; j < objArraySize; j++) {
         int sizeWritten = 0;

         final Object object = UnsafeWrapper.getObject(newRef, arrayBaseOffset + (arrayScale * j));
         for (int i = 0; i < c.getStructMemberTypes().size(); i++) {
            final TypeSpec t = c.getStructMemberTypes().get(i);
            final long offset = c.getStructMemberOffsets().get(i);

            if (logger.isLoggable(Level.FINEST)) {
               logger.finest("name = " + c.getStructMembers().get(i).getNameAndTypeEntry().getNameUTF8Entry().getUTF8() + " t= "
                     + t);
            }

            switch (t) {
               case I: {
                  final int x = UnsafeWrapper.getInt(object, offset);
                  arg.getObjArrayByteBuffer().putInt(x);
                  sizeWritten += t.getSize();
                  break;
               }
               case F: {
                  final float x = UnsafeWrapper.getFloat(object, offset);
                  arg.getObjArrayByteBuffer().putFloat(x);
                  sizeWritten += t.getSize();
                  break;
               }
               case J: {
                  final long x = UnsafeWrapper.getLong(object, offset);
                  arg.getObjArrayByteBuffer().putLong(x);
                  sizeWritten += t.getSize();
                  break;
               }
               case Z: {
                  final boolean x = UnsafeWrapper.getBoolean(object, offset);
                  arg.getObjArrayByteBuffer().put(x == true ? (byte) 1 : (byte) 0);
                  // Booleans converted to 1 byte C chars for opencl
                  sizeWritten += TypeSpec.B.getSize();
                  break;
               }
               case B: {
                  final byte x = UnsafeWrapper.getByte(object, offset);
                  arg.getObjArrayByteBuffer().put(x);
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
               logger.finest(arg.getName() + " struct pad byte = " + sizeWritten + " totalStructSize= " + totalStructSize);
            }
            arg.getObjArrayByteBuffer().put((byte) -1);
            sizeWritten++;
         }
      }

      assert arg.getObjArrayByteBuffer().arrayOffset() == 0 : "should be zero";

      return didReallocate;
   }

   private void extractOopConversionBuffer(KernelArg arg) throws AparapiException {
      final Class<?> arrayClass = arg.getField().getType();
      final ClassModel c = arg.getObjArrayElementModel();
      assert c != null : "should find class for elements: " + arrayClass.getName();
      assert arg.getArray() != null : "array is null";

      final int arrayBaseOffset = UnsafeWrapper.arrayBaseOffset(arrayClass);
      final int arrayScale = UnsafeWrapper.arrayIndexScale(arrayClass);
      if (logger.isLoggable(Level.FINEST)) {
         logger.finest("Syncing field:" + arg.getName() + ", bb=" + arg.getObjArrayByteBuffer() + ", type = " + arrayClass);
      }

      int objArraySize = 0;
      try {
         objArraySize = Array.getLength(arg.getField().get(kernel));
      } catch (final IllegalAccessException e) {
         throw new AparapiException(e);
      }

      assert objArraySize > 0 : "should be > 0";

      final int totalStructSize = c.getTotalStructSize();
      // int totalBufferSize = objArraySize * totalStructSize;
      // assert arg.objArrayBuffer.length == totalBufferSize : "size should match";

      arg.getObjArrayByteBuffer().rewind();

      for (int j = 0; j < objArraySize; j++) {
         int sizeWritten = 0;
         final Object object = UnsafeWrapper.getObject(arg.getArray(), arrayBaseOffset + (arrayScale * j));
         for (int i = 0; i < c.getStructMemberTypes().size(); i++) {
            final TypeSpec t = c.getStructMemberTypes().get(i);
            final long offset = c.getStructMemberOffsets().get(i);
            switch (t) {
               case I: {
                  // read int value from buffer and store into obj in the array
                  final int x = arg.getObjArrayByteBuffer().getInt();
                  if (logger.isLoggable(Level.FINEST)) {
                     logger.finest("fType = " + t.getShortName() + " x= " + x);
                  }
                  UnsafeWrapper.putInt(object, offset, x);
                  sizeWritten += t.getSize();
                  break;
               }
               case F: {
                  final float x = arg.getObjArrayByteBuffer().getFloat();
                  if (logger.isLoggable(Level.FINEST)) {
                     logger.finest("fType = " + t.getShortName() + " x= " + x);
                  }
                  UnsafeWrapper.putFloat(object, offset, x);
                  sizeWritten += t.getSize();
                  break;
               }
               case J: {
                  final long x = arg.getObjArrayByteBuffer().getLong();
                  if (logger.isLoggable(Level.FINEST)) {
                     logger.finest("fType = " + t.getShortName() + " x= " + x);
                  }
                  UnsafeWrapper.putLong(object, offset, x);
                  sizeWritten += t.getSize();
                  break;
               }
               case Z: {
                  final byte x = arg.getObjArrayByteBuffer().get();
                  if (logger.isLoggable(Level.FINEST)) {
                     logger.finest("fType = " + t.getShortName() + " x= " + x);
                  }
                  UnsafeWrapper.putBoolean(object, offset, (x == 1 ? true : false));
                  // Booleans converted to 1 byte C chars for open cl
                  sizeWritten += TypeSpec.B.getSize();
                  break;
               }
               case B: {
                  final byte x = arg.getObjArrayByteBuffer().get();
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
            arg.getObjArrayByteBuffer().get();
            sizeWritten++;
         }
      }
   }

   private void restoreObjects() throws AparapiException {
      for (int i = 0; i < argc; i++) {
         final KernelArg arg = args[i];
         if (arg.getField().getType() == AtomicInteger[].class) {
            extractAtomicIntegerConversionBuffer(arg); 
         } else if ((arg.getType() & ARG_OBJ_ARRAY_STRUCT) != 0) {
            extractOopConversionBuffer(arg);
         }
      }
   }
   
   private boolean prepareAtomicIntegerConversionBuffer(KernelArg arg) throws AparapiException {
      usesOopConversion = true;
      final Class<?> arrayClass = arg.getField().getType();
      ClassModel c = getClassModelFromArg(arg, arrayClass);


      if (logger.isLoggable(Level.FINEST)) {
         logger.finest("Syncing obj array type = " + arrayClass + " cvtd= " + c.getClassWeAreModelling().getName());
      }

      int objArraySize = 0;
      Object newRef = null;
      try {
         newRef = arg.getField().get(kernel);
         objArraySize = Array.getLength(newRef);
      } catch (final IllegalAccessException e) {
         throw new AparapiException(e);
      }

      assert (newRef != null) && (objArraySize != 0) : "no data";

      final int totalStructSize = Integer.BYTES;
      final int totalBufferSize = objArraySize * totalStructSize;

      // allocate ByteBuffer if first time or array changed
      boolean didReallocate = allocateArrayBufferIfFirstTimeOrArrayChanged(arg, newRef, objArraySize, totalStructSize, totalBufferSize);

      AtomicInteger[] atomic = (AtomicInteger[])newRef;
      
      // copy the fields that the JNI uses
      arg.setJavaArray(arg.getObjArrayBuffer());
      arg.setNumElements(objArraySize);
      arg.setSizeInBytes(totalBufferSize);

      int sizeWritten = 0;
      for (int j = 0; j < objArraySize; j++) {
         arg.getObjArrayByteBuffer().putInt(atomic[j].get());
         sizeWritten += Integer.BYTES;

         // add padding here if needed
         if (logger.isLoggable(Level.FINEST)) {
            logger.finest("sizeWritten = " + sizeWritten + " totalStructSize= " + totalStructSize);
         }
      }
      assert sizeWritten <= totalBufferSize : "wrote too much into buffer";

      while (sizeWritten < totalBufferSize) {
         if (logger.isLoggable(Level.FINEST)) {
            logger.finest(arg.getName() + " struct pad byte = " + sizeWritten + " totalStructSize= " + totalStructSize);
          }
          arg.getObjArrayByteBuffer().put((byte) -1);
          sizeWritten++;
      }

      assert arg.getObjArrayByteBuffer().arrayOffset() == 0 : "should be zero";

      return didReallocate;	
   }

   private void extractAtomicIntegerConversionBuffer(KernelArg arg) throws AparapiException {
      final Class<?> arrayClass = arg.getField().getType();
      final ClassModel c = arg.getObjArrayElementModel();
      assert c != null : "should find class for elements: " + arrayClass.getName();
      assert arg.getArray() != null : "array is null";

      if (logger.isLoggable(Level.FINEST)) {
         logger.finest("Syncing field:" + arg.getName() + ", bb=" + arg.getObjArrayByteBuffer() + ", type = " + arrayClass);
      }

      int objArraySize = 0;
      try {
         objArraySize = Array.getLength(arg.getField().get(kernel));
      } catch (final IllegalAccessException e) {
         throw new AparapiException(e);
      }

      assert objArraySize > 0 : "should be > 0";

      final int totalStructSize = Integer.BYTES;
      final int totalBufferSize = objArraySize * totalStructSize;
      // assert arg.objArrayBuffer.length == totalBufferSize : "size should match";

      arg.getObjArrayByteBuffer().rewind();
      
      AtomicInteger[] atomics = (AtomicInteger[])arg.getArray();

      int sizeWritten = 0;
      for (int j = 0; j < objArraySize; j++) {
         // read int value from buffer and store into obj in the array
         final int x = arg.getObjArrayByteBuffer().getInt();
         atomics[j].set(x);
         
         sizeWritten += Integer.BYTES;

         // add padding here if needed
         if (logger.isLoggable(Level.FINEST)) {
            logger.finest("sizeWritten = " + sizeWritten + " totalStructSize= " + totalStructSize);
         }
      }
      assert sizeWritten <= totalBufferSize : "wrote too much into buffer";

      while (sizeWritten < totalBufferSize) {
         // skip over pad bytes
         arg.getObjArrayByteBuffer().get();
         sizeWritten++;
      }
   }

   private boolean updateKernelArrayRefs() throws AparapiException {
      boolean needsSync = false;

      for (int i = 0; i < argc; i++) {
         final KernelArg arg = args[i];
         try {
            if ((arg.getType() & ARG_ARRAY) != 0) {
               Object newArrayRef;
               newArrayRef = arg.getField().get(kernel);

               if (newArrayRef == null) {
                  throw new IllegalStateException("Cannot send null refs to kernel, reverting to java");
               }

               String fieldName = arg.getField().getName();
               int arrayLength = Array.getLength(newArrayRef);
               Integer privateMemorySize = ClassModel.getPrivateMemorySizeFromField(arg.getField());
               if (privateMemorySize == null) {
                  privateMemorySize = ClassModel.getPrivateMemorySizeFromFieldName(fieldName);
               }
               if (privateMemorySize != null) {
                  if (arrayLength > privateMemorySize) {
                     throw new IllegalStateException("__private array field " + fieldName + " has illegal length " + arrayLength
                           + " > " + privateMemorySize);
                  }
               }

               if (arg.getField().getType() == AtomicInteger[].class) {
            	  prepareAtomicIntegerConversionBuffer(arg);
               } else if ((arg.getType() & ARG_OBJ_ARRAY_STRUCT) != 0) {
                  prepareOopConversionBuffer(arg);
               } else {
                  // set up JNI fields for normal arrays
                  arg.setJavaArray(newArrayRef);
                  arg.setNumElements(arrayLength);
                  arg.setSizeInBytes(arg.getNumElements() * arg.getPrimitiveSize());

                  if (((args[i].getType() & ARG_EXPLICIT) != 0) && puts.contains(newArrayRef)) {
                     args[i].setType(args[i].getType() | ARG_EXPLICIT_WRITE);
                     // System.out.println("detected an explicit write " + args[i].name);
                     puts.remove(newArrayRef);
                  }
               }

               if (newArrayRef != arg.getArray()) {
                  needsSync = true;

                  if (logger.isLoggable(Level.FINE)) {
                     logger.fine("saw newArrayRef for " + arg.getName() + " = " + newArrayRef + ", newArrayLen = "
                           + Array.getLength(newArrayRef));
                  }
               }

               arg.setArray(newArrayRef);
               assert arg.getArray() != null : "null array ref";
            } else if ((arg.getType() & ARG_APARAPI_BUFFER) != 0) {
               // TODO: check if the 2D/3D array is changed. 
               //   can Arrays.equals help?
               needsSync = true; // Always need syn
               Object buffer = new Object();
               try {
                  buffer = arg.getField().get(kernel);
               } catch (IllegalAccessException e) {
                  e.printStackTrace();
               }
               int numDims = arg.getNumDims();
               Object subBuffer = buffer;
               int[] dims = new int[numDims];
               for (int d = 0; d < numDims - 1; d++) {
                  dims[d] = Array.getLength(subBuffer);
                  subBuffer = Array.get(subBuffer, 0);
               }
               dims[numDims - 1] = Array.getLength(subBuffer);
               arg.setDims(dims);

               int primitiveSize = getPrimitiveSize(arg.getType());
               int totalElements = 1;
               for (int d = 0; d < numDims; d++) {
                  totalElements *= dims[d];
               }
               arg.setJavaBuffer(buffer);
               arg.setSizeInBytes(totalElements * primitiveSize);
               arg.setArray(buffer);
            }
         } catch (final IllegalArgumentException e) {
            e.printStackTrace();
         } catch (final IllegalAccessException e) {
            e.printStackTrace();
         }
      }
      return needsSync;
   }

   @SuppressWarnings("deprecation")
   private Kernel executeOpenCL(ExecutionSettings _settings) throws AparapiException {

      // Read the array refs after kernel may have changed them
      // We need to do this as input to computing the localSize
      assert args != null : "args should not be null";
      final boolean needSync = updateKernelArrayRefs();
      if (needSync && logger.isLoggable(Level.FINE)) {
         logger.fine("Need to resync arrays on " + kernel);
      }

      // native side will reallocate array buffers if necessary
      int returnValue = runKernelJNI(jniContextHandle, _settings.range, needSync, _settings.passes, inBufferRemote, outBufferRemote);
      if (returnValue != 0) {
         String reason = "OpenCL execution seems to have failed (runKernelJNI returned " + returnValue + ")";
         return fallBackToNextDevice(_settings, new AparapiException(reason));
      }

      if (usesOopConversion == true) {
         restoreObjects();
      }

      if (logger.isLoggable(Level.FINE)) {
         logger.fine("executeOpenCL completed. " + _settings.range);
      }

      return kernel;
   }

   @SuppressWarnings("deprecation")
   synchronized private Kernel fallBackByExecutionMode(ExecutionSettings _settings) {
      isFallBack = true;
      if (kernel.hasNextExecutionMode()) {
         kernel.tryNextExecutionMode();
         if (logger.isLoggable(Level.WARNING)) {
            logger.warning("Trying next execution mode " + kernel.getExecutionMode());
         }
      } else {
         kernel.setFallbackExecutionMode();
      }
      recreateRange(_settings);
      return executeInternalInner(_settings);
   }

   private void recreateRange(ExecutionSettings _settings) {
      if (_settings.range.isLocalIsDerived() && !_settings.legacyExecutionMode) {
         Device device = kernel.getTargetDevice();
         Range result;
         switch (_settings.range.getDims()) {
            case 1: {
               result = Range.create(device, _settings.range.getGlobalSize_0());
               break;
            }
            case 2: {
               result = Range.create2D(device, _settings.range.getGlobalSize_0(), _settings.range.getGlobalSize_1());
               break;
            }
            case 3: {
               result = Range.create3D(device, _settings.range.getGlobalSize_0(), _settings.range.getGlobalSize_1(), _settings.range.getGlobalSize_2());
               break;
            }
            default: {
               throw new AssertionError("Range.getDims() = " + _settings.range.getDims());
            }
         }
         _settings.range = result;
      }
   }

   private Kernel fallBackToNextDevice(ExecutionSettings _settings, String _reason) {
      return fallBackToNextDevice(_settings, new AparapiException(_reason));
   }

   @SuppressWarnings("deprecation")
   synchronized private Kernel fallBackToNextDevice(ExecutionSettings _settings, Exception _exception) {
      return fallBackToNextDevice(_settings, _exception, false);
   }

   @SuppressWarnings("deprecation")
   synchronized private Kernel fallBackToNextDevice(ExecutionSettings _settings, Exception _exception, boolean _silently) {
      isFallBack = true;
      _settings.profile.onEvent(ProfilingEvent.EXECUTED);
      if (_settings.legacyExecutionMode) {
         if (!_silently && logger.isLoggable(Level.WARNING)) {
            logger.warning("Execution mode " + kernel.getExecutionMode() + " failed for " + kernel + ": " + _exception.getMessage());
             _exception.printStackTrace();
          }
          return fallBackByExecutionMode(_settings);
      } else {
         KernelPreferences preferences = KernelManager.instance().getPreferences(kernel);
         if (!_silently && logger.isLoggable(Level.WARNING)) {
            logger.warning("Device failed for " + kernel + ": " + _exception.getMessage());
         }

         preferences.markPreferredDeviceFailed();

//         Device nextDevice = preferences.getPreferredDevice(kernel);
//
//         if (nextDevice == null) {
//            if (!_silently && logger.isLoggable(Level.SEVERE)) {
//               logger.severe("No Devices left to try, giving up");
//            }
//            throw new RuntimeException(_exception);
//         }
         if (!_silently && logger.isLoggable(Level.WARNING)) {
            _exception.printStackTrace();
            logger.warning("Trying next device: " + describeDevice());
         }
      }

      recreateRange(_settings);
      return executeInternalInner(_settings);
   }

   @SuppressWarnings("deprecation")
   public synchronized Kernel execute(String _entrypoint, final Range _range, final int _passes) {
      executing = true;
      try {
         clearCancelMultiPass();
         KernelProfile profile = KernelManager.instance().getProfile(kernel.getClass());
         KernelPreferences preferences = KernelManager.instance().getPreferences(kernel);
         boolean legacyExecutionMode = kernel.getExecutionMode() != Kernel.EXECUTION_MODE.AUTO;

         ExecutionSettings settings = new ExecutionSettings(preferences, profile, _entrypoint, _range, _passes, legacyExecutionMode);
         // Two Kernels of the same class share the same KernelPreferences object, and since failure (fallback) generally mutates
         // the preferences object, we must lock it. Note this prevents two Kernels of the same class executing simultaneously.
         synchronized (preferences) {
            return executeInternalOuter(settings);
         }
      } finally {
         executing = false;
         clearCancelMultiPass();
      }
   }

   private synchronized Kernel executeInternalOuter(ExecutionSettings _settings) {
      try {
         return executeInternalInner(_settings);
      } finally {
         if (kernel.isAutoCleanUpArrays() &&_settings.range.getGlobalSize_0() != 0) {
            cleanUpArrays();
         }
      }
   }

   @SuppressWarnings("deprecation")
   private synchronized Kernel executeInternalInner(ExecutionSettings _settings) {

      if (_settings.range == null) {
         throw new IllegalStateException("range can't be null");
      }

      EXECUTION_MODE requestedExecutionMode = kernel.getExecutionMode();

      if (requestedExecutionMode.isOpenCL() && _settings.range.getDevice() != null && !(_settings.range.getDevice() instanceof OpenCLDevice)) {
         fallBackToNextDevice(_settings, "OpenCL EXECUTION_MODE was requested but Device supplied was not an OpenCLDevice");
      }

      Device device = _settings.range.getDevice();
      boolean userSpecifiedDevice = true;
      if (device == null) {
         userSpecifiedDevice = false;
         if (!_settings.legacyExecutionMode) {
            device = _settings.preferences.getPreferredDevice(kernel);
            if (device == null) {
               // the default fallback when KernelPreferences has run out of options is JTP
               device = JavaDevice.THREAD_POOL;
            }
         } else {
            if (requestedExecutionMode == EXECUTION_MODE.JTP) {
               device = JavaDevice.THREAD_POOL;
            } else if (requestedExecutionMode == EXECUTION_MODE.SEQ) {
               device = JavaDevice.SEQUENTIAL;
            }
         }
      } else {
         boolean compatible = isDeviceCompatible(device);
         if (!compatible) {
            throw new AssertionError("user supplied Device incompatible with current EXECUTION_MODE or getTargetDevice(); device = "
                    + device.getShortDescription() + "; kernel = " + kernel);
         }
      }

      try {
         OpenCLDevice openCLDevice = device instanceof OpenCLDevice ? (OpenCLDevice) device : null;

         int jniFlags = 0;
         // for legacy reasons use old logic where Kernel.EXECUTION_MODE is not AUTO
         if (_settings.legacyExecutionMode && !userSpecifiedDevice && requestedExecutionMode.isOpenCL()) {
            if (requestedExecutionMode.equals(EXECUTION_MODE.GPU)) {
               // Get the best GPU
               openCLDevice = (OpenCLDevice) KernelManager.DeprecatedMethods.bestGPU();
               jniFlags |= JNI_FLAG_USE_GPU; // this flag might be redundant now.
               if (openCLDevice == null) {
                  return fallBackToNextDevice(_settings, "GPU request can't be honored, no GPU device");
               }
            } else if (requestedExecutionMode.equals(EXECUTION_MODE.ACC)) {
               // Get the best ACC
               openCLDevice = (OpenCLDevice) KernelManager.DeprecatedMethods.bestACC();
               jniFlags |= JNI_FLAG_USE_ACC; // this flag might be redundant now.
               if (openCLDevice == null) {
                  return fallBackToNextDevice(_settings, "ACC request can't be honored, no ACC device");
               }
            } else {
               // We fetch the first CPU device
               openCLDevice = (OpenCLDevice) KernelManager.DeprecatedMethods.firstDevice(Device.TYPE.CPU);
               if (openCLDevice == null) {
                  return fallBackToNextDevice(_settings, "CPU request can't be honored, no CPU device");
               }
            }
         } else {
            if (device.getType() == Device.TYPE.GPU) {
               jniFlags |= JNI_FLAG_USE_GPU; // this flag might be redundant now.
            } else if (device.getType() == Device.TYPE.ACC) {
               jniFlags |= JNI_FLAG_USE_ACC; // this flag might be redundant now.
            }
         }
         if (device == null && openCLDevice != null) {
            device = openCLDevice;
         }
         assert device != null : "No device available";
         _settings.profile.onStart(device);
         /* for backward compatibility reasons we still honor execution mode */
         boolean isOpenCl = requestedExecutionMode.isOpenCL() || device instanceof OpenCLDevice;
         if (isOpenCl) {
            if ((entryPoint == null) || (isFallBack)) {
               if (entryPoint == null) {
                  try {
                     final ClassModel classModel = ClassModel.createClassModel(kernel.getClass());
                     entryPoint = classModel.getEntrypoint(_settings.entrypoint, kernel);
                     _settings.profile.onEvent(ProfilingEvent.CLASS_MODEL_BUILT);
                  } catch (final Exception exception) {
                     _settings.profile.onEvent(ProfilingEvent.CLASS_MODEL_BUILT);
                     return fallBackToNextDevice(_settings, exception);
                  }
               }

               if ((entryPoint != null)) {
                  synchronized (Kernel.class) { // This seems to be needed because of a race condition uncovered with issue #68 http://code.google.com/p/aparapi/issues/detail?id=68

                     //  jniFlags |= (Config.enableProfiling ? JNI_FLAG_ENABLE_PROFILING : 0);
                     //  jniFlags |= (Config.enableProfilingCSV ? JNI_FLAG_ENABLE_PROFILING_CSV | JNI_FLAG_ENABLE_PROFILING : 0);
                     //  jniFlags |= (Config.enableVerboseJNI ? JNI_FLAG_ENABLE_VERBOSE_JNI : 0);
                     // jniFlags |= (Config.enableVerboseJNIOpenCLResourceTracking ? JNI_FLAG_ENABLE_VERBOSE_JNI_OPENCL_RESOURCE_TRACKING :0);
                     // jniFlags |= (kernel.getExecutionMode().equals(Kernel.EXECUTION_MODE.GPU) ? JNI_FLAG_USE_GPU : 0);
                     // Init the device to check capabilities before emitting the
                     // code that requires the capabilities.
                     jniContextHandle = initJNI(kernel, openCLDevice, jniFlags); // openCLDevice will not be null here
                     _settings.profile.onEvent(ProfilingEvent.INIT_JNI);
                  } // end of synchronized! issue 68

                  if (jniContextHandle == 0) {
                     return fallBackToNextDevice(_settings, "initJNI failed to return a valid handle");
                  }

                  final String extensions = getExtensionsJNI(jniContextHandle);
                  capabilitiesSet = new HashSet<String>();

                  final StringTokenizer strTok = new StringTokenizer(extensions);
                  while (strTok.hasMoreTokens()) {
                     capabilitiesSet.add(strTok.nextToken());
                  }

                  if (logger.isLoggable(Level.FINE)) {
                     logger.fine("Capabilities initialized to :" + capabilitiesSet.toString());
                  }

                  if (entryPoint.requiresDoublePragma() && !hasFP64Support()) {
                     return fallBackToNextDevice(_settings, "FP64 required but not supported");
                  }

                  if (entryPoint.requiresByteAddressableStorePragma() && !hasByteAddressableStoreSupport()) {
                     return fallBackToNextDevice(_settings, "Byte addressable stores required but not supported");
                  }

                  final boolean all32AtomicsAvailable = hasGlobalInt32BaseAtomicsSupport()
                        && hasGlobalInt32ExtendedAtomicsSupport() && hasLocalInt32BaseAtomicsSupport()
                        && hasLocalInt32ExtendedAtomicsSupport();

                  if (entryPoint.requiresAtomic32Pragma() && !all32AtomicsAvailable) {

                     return fallBackToNextDevice(_settings, "32 bit Atomics required but not supported");
                  }

                  String openCL;
                  synchronized (openCLCache) {
                     openCL = openCLCache.get(kernel.getClass());
                     if (openCL == null) {
                        try {
                           openCL = KernelWriter.writeToString(entryPoint);
                           if (logger.isLoggable(Level.INFO)) {
                              logger.info(openCL);
                           }
                           else if (Config.enableShowGeneratedOpenCL) {
                              System.out.println(openCL);
                           }
                           _settings.profile.onEvent(ProfilingEvent.OPENCL_GENERATED);
                           openCLCache.put(kernel.getClass(), openCL);
                        }
                        catch (final CodeGenException codeGenException) {
                           openCLCache.put(kernel.getClass(), CODE_GEN_ERROR_MARKER);
                           _settings.profile.onEvent(ProfilingEvent.OPENCL_GENERATED);
                           return fallBackToNextDevice(_settings, codeGenException);
                        }
                     }
                     else {
                        if (openCL.equals(CODE_GEN_ERROR_MARKER)) {
                           _settings.profile.onEvent(ProfilingEvent.OPENCL_GENERATED);
                           boolean silently = true; // since we must have already reported the CodeGenException
                           return fallBackToNextDevice(_settings, null, silently);
                        }
                     }
                  }

                  // Send the string to OpenCL to compile it, or if the compiled binary is already cached on JNI side just empty string to use cached binary
                  long handle;
                  if (BINARY_CACHING_DISABLED) {
                     handle = buildProgramJNI(jniContextHandle, openCL, "");
                  } else {
                     synchronized (seenBinaryKeys) {
                        String binaryKey = kernel.getClass().getName() + ":" + device.getDeviceId();
                        if (seenBinaryKeys.contains(binaryKey)) {
                           // use cached binary
                           logger.log(Level.INFO, "reusing cached binary for " + binaryKey);
                           handle = buildProgramJNI(jniContextHandle, "", binaryKey);
                        }
                        else {
                           // create and cache binary
                           logger.log(Level.INFO, "compiling new binary for " + binaryKey);
                           handle = buildProgramJNI(jniContextHandle, openCL, binaryKey);
                           seenBinaryKeys.add(binaryKey);
                        }
                     }
                  }
                  _settings.profile.onEvent(ProfilingEvent.OPENCL_COMPILED);
                  if (handle == 0) {
                     return fallBackToNextDevice(_settings, "OpenCL compile failed");
                  }

                  args = new KernelArg[entryPoint.getReferencedFields().size()];
                  int i = 0;

                  for (final Field field : entryPoint.getReferencedFields()) {
                     try {
                        field.setAccessible(true);
                        args[i] = new KernelArg();
                        args[i].setName(field.getName());
                        args[i].setField(field);
                        if ((field.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
                           args[i].setType(args[i].getType() | ARG_STATIC);
                        }

                        final Class<?> type = field.getType();
                        if (type.isArray()) {

                           if (field.getAnnotation(Local.class) != null || args[i].getName().endsWith(Kernel.LOCAL_SUFFIX)) {
                              args[i].setType(args[i].getType() | ARG_LOCAL);
                           } else if ((field.getAnnotation(Constant.class) != null)
                                 || args[i].getName().endsWith(Kernel.CONSTANT_SUFFIX)) {
                              args[i].setType(args[i].getType() | ARG_CONSTANT);
                           } else {
                              args[i].setType(args[i].getType() | ARG_GLOBAL);
                           }
                           if (isExplicit()) {
                              args[i].setType(args[i].getType() | ARG_EXPLICIT);
                           }
                           // for now, treat all write arrays as read-write, see bugzilla issue 4859
                           // we might come up with a better solution later
                           args[i].setType(args[i].getType()
                                 | (entryPoint.getArrayFieldAssignments().contains(field.getName()) ? (ARG_WRITE | ARG_READ) : 0));
                           args[i].setType(args[i].getType()
                                 | (entryPoint.getArrayFieldAccesses().contains(field.getName()) ? ARG_READ : 0));
                           // args[i].type |= ARG_GLOBAL;

                           if (type.getName().startsWith("[L")) {
                              args[i].setArray(null); // will get updated in updateKernelArrayRefs
                              args[i].setType(args[i].getType()
                                    | (ARG_ARRAY | ARG_OBJ_ARRAY_STRUCT | ARG_WRITE | ARG_READ));

                              if (logger.isLoggable(Level.FINE)) {
                                 logger.fine("tagging " + args[i].getName() + " as (ARG_ARRAY | ARG_OBJ_ARRAY_STRUCT | ARG_WRITE | ARG_READ)");
                              }
                           } else if (type.getName().startsWith("[[")) {

                              try {
                                 setMultiArrayType(args[i], type);
                              } catch (AparapiException e) {
                                 return fallBackToNextDevice(_settings, "failed to set kernel arguement "
                                       + args[i].getName() + ".  Aparapi only supports 2D and 3D arrays.");
                              }
                           } else {

                              args[i].setArray(null); // will get updated in updateKernelArrayRefs
                              args[i].setType(args[i].getType() | ARG_ARRAY);

                              args[i].setType(args[i].getType() | (type.isAssignableFrom(float[].class) ? ARG_FLOAT : 0));
                              args[i].setType(args[i].getType() | (type.isAssignableFrom(int[].class) ? ARG_INT : 0));
                              args[i].setType(args[i].getType() | (type.isAssignableFrom(boolean[].class) ? ARG_BOOLEAN : 0));
                              args[i].setType(args[i].getType() | (type.isAssignableFrom(byte[].class) ? ARG_BYTE : 0));
                              args[i].setType(args[i].getType() | (type.isAssignableFrom(char[].class) ? ARG_CHAR : 0));
                              args[i].setType(args[i].getType() | (type.isAssignableFrom(double[].class) ? ARG_DOUBLE : 0));
                              args[i].setType(args[i].getType() | (type.isAssignableFrom(long[].class) ? ARG_LONG : 0));
                              args[i].setType(args[i].getType() | (type.isAssignableFrom(short[].class) ? ARG_SHORT : 0));

                              // arrays whose length is used will have an int arg holding
                              // the length as a kernel param
                              if (entryPoint.getArrayFieldArrayLengthUsed().contains(args[i].getName())) {
                                 args[i].setType(args[i].getType() | ARG_ARRAYLENGTH);
                              }

                              if (type.getName().startsWith("[L")) {
                                 args[i].setType(args[i].getType() | (ARG_OBJ_ARRAY_STRUCT | ARG_WRITE | ARG_READ));
                                 if (logger.isLoggable(Level.FINE)) {
                                    logger.fine("tagging " + args[i].getName()
                                          + " as (ARG_OBJ_ARRAY_STRUCT | ARG_WRITE | ARG_READ)");
                                 }
                              }
                           }
                        } else if (type.isAssignableFrom(float.class)) {
                           args[i].setType(args[i].getType() | ARG_PRIMITIVE);
                           args[i].setType(args[i].getType() | ARG_FLOAT);
                        } else if (type.isAssignableFrom(int.class)) {
                           args[i].setType(args[i].getType() | ARG_PRIMITIVE);
                           args[i].setType(args[i].getType() | ARG_INT);
                        } else if (type.isAssignableFrom(double.class)) {
                           args[i].setType(args[i].getType() | ARG_PRIMITIVE);
                           args[i].setType(args[i].getType() | ARG_DOUBLE);
                        } else if (type.isAssignableFrom(long.class)) {
                           args[i].setType(args[i].getType() | ARG_PRIMITIVE);
                           args[i].setType(args[i].getType() | ARG_LONG);
                        } else if (type.isAssignableFrom(boolean.class)) {
                           args[i].setType(args[i].getType() | ARG_PRIMITIVE);
                           args[i].setType(args[i].getType() | ARG_BOOLEAN);
                        } else if (type.isAssignableFrom(byte.class)) {
                           args[i].setType(args[i].getType() | ARG_PRIMITIVE);
                           args[i].setType(args[i].getType() | ARG_BYTE);
                        } else if (type.isAssignableFrom(char.class)) {
                           args[i].setType(args[i].getType() | ARG_PRIMITIVE);
                           args[i].setType(args[i].getType() | ARG_CHAR);
                        } else if (type.isAssignableFrom(short.class)) {
                           args[i].setType(args[i].getType() | ARG_PRIMITIVE);
                           args[i].setType(args[i].getType() | ARG_SHORT);
                        }
                        // System.out.printf("in execute, arg %d %s %08x\n", i,args[i].name,args[i].type );
                     } catch (final IllegalArgumentException e) {
                        e.printStackTrace();
                     }

                     args[i].setPrimitiveSize(getPrimitiveSize(args[i].getType()));

                     if (logger.isLoggable(Level.FINE)) {
                        logger.fine("arg " + i + ", " + args[i].getName() + ", type=" + Integer.toHexString(args[i].getType())
                              + ", primitiveSize=" + args[i].getPrimitiveSize());
                     }

                     i++;
                  }

                  // at this point, i = the actual used number of arguments
                  // (private buffers do not get treated as arguments)

                  argc = i;

                  setArgsJNI(jniContextHandle, args, argc);
                  _settings.profile.onEvent(ProfilingEvent.PREPARE_EXECUTE);
                  try {
                     executeOpenCL(_settings);
                     isFallBack = false;
                  } catch (final AparapiException e) {
                     fallBackToNextDevice(_settings, e);
                  }
               } else { // (entryPoint != null) && !entryPoint.shouldFallback()
                  fallBackToNextDevice(_settings, "failed to locate entrypoint");
               }
            } else { // (entryPoint == null) || (isFallBack)
               try {
                  executeOpenCL(_settings);
                  isFallBack = false;
               } catch (final AparapiException e) {
                  fallBackToNextDevice(_settings, e);
               }
            }
         } else { // isOpenCL
            if (!(device instanceof JavaDevice)) {
               fallBackToNextDevice(_settings, "Non-OpenCL Kernel.EXECUTION_MODE requested but device is not a JavaDevice ");
            }
            executeJava(_settings, (JavaDevice) device);
         }

         if (Config.enableExecutionModeReporting) {
            System.out.println("execution complete: " + kernel);
         }

         return kernel;
      }
      finally {
         _settings.profile.onEvent(ProfilingEvent.EXECUTED);
         maybeReportProfile(_settings);
      }
   }

   @Override
   public String toString() {
      return "KernelRunner{" + kernel + "}";
   }

   private String describeDevice() {
      Device device = KernelManager.instance().getPreferences(kernel).getPreferredDevice(kernel);
      return (device == null) ? "<default fallback>" : device.getShortDescription();
   }

   private void maybeReportProfile(ExecutionSettings _settings) {
      if (Config.dumpProfileOnExecution) {
         StringBuilder report = new StringBuilder();
         report.append(KernelDeviceProfile.getTableHeader()).append('\n');
         report.append(_settings.profile.getLastDeviceProfile().getLastAsTableRow());
         System.out.println(report);
      }
   }

   @SuppressWarnings("deprecation")
   private boolean isDeviceCompatible(Device device) {
      Kernel.EXECUTION_MODE mode = kernel.getExecutionMode();
      if (mode != Kernel.EXECUTION_MODE.AUTO) {
         switch (device.getType()) {
            case GPU:
               return mode == Kernel.EXECUTION_MODE.GPU;
            case CPU:
               return mode == Kernel.EXECUTION_MODE.CPU;
            case JTP:
               return mode == Kernel.EXECUTION_MODE.JTP;
            case SEQ:
               return mode == Kernel.EXECUTION_MODE.SEQ;
            case ACC:
               return mode == Kernel.EXECUTION_MODE.ACC;
            default:
               return false;
         }
      } else {
         return (device == kernel.getTargetDevice());
      }
   }

   public int getCancelState() {
      return inBufferRemoteInt.get(0);
   }

   public void cancelMultiPass() {
      inBufferRemoteInt.put(0, CANCEL_STATUS_TRUE);
   }

   private void clearCancelMultiPass() {
      inBufferRemoteInt.put(0, CANCEL_STATUS_FALSE);
   }

   /**
    * Returns the index of the current pass, or one of two special constants with negative values to indicate special progress states. Those constants are
    * {@link #PASS_ID_PREPARING_EXECUTION} to indicate that the Kernel has started executing but not reached the initial pass, or
    * {@link #PASS_ID_COMPLETED_EXECUTION} to indicate that execution is complete (possibly due to early termination via {@link #cancelMultiPass()}), i.e. the Kernel
    * is idle. {@link #PASS_ID_COMPLETED_EXECUTION} is also returned before the first execution has been invoked.
    *
    * <p>This can be used, for instance, to update a visual progress bar.
    *
    * @see #execute(String, Range, int)
    */
   public int getCurrentPass() {
      if (!executing) {
         return PASS_ID_COMPLETED_EXECUTION;
      }

      if (kernel.isRunningCL()) {
         return getCurrentPassRemote();
      } else {
         return getCurrentPassLocal();
      }
   }

   /**
    * True while any of the {@code execute()} methods are in progress.
    */
   public boolean isExecuting() {
      return executing;
   }

   protected int getCurrentPassRemote() {
      return outBufferRemoteInt.get(0);
   }

   private int getCurrentPassLocal() {
      return passId;
   }

   private int getPrimitiveSize(int type) {
      if ((type & ARG_FLOAT) != 0) {
         return 4;
      } else if ((type & ARG_INT) != 0) {
         return 4;
      } else if ((type & ARG_BYTE) != 0) {
         return 1;
      } else if ((type & ARG_CHAR) != 0) {
         return 2;
      } else if ((type & ARG_BOOLEAN) != 0) {
         return 1;
      } else if ((type & ARG_SHORT) != 0) {
         return 2;
      } else if ((type & ARG_LONG) != 0) {
         return 8;
      } else if ((type & ARG_DOUBLE) != 0) {
         return 8;
      }
      return 0;
   }

   private void setMultiArrayType(KernelArg arg, Class<?> type) throws AparapiException {
      arg.setType(arg.getType() | (ARG_WRITE | ARG_READ | ARG_APARAPI_BUFFER));
      int numDims = 0;
      while (type.getName().startsWith("[[[[")) {
         throw new AparapiException("Aparapi only supports 2D and 3D arrays.");
      }
      arg.setType(arg.getType() | ARG_ARRAYLENGTH);
      while (type.getName().charAt(numDims) == '[') {
         numDims++;
      }
      arg.setNumDims(numDims);
      arg.setJavaBuffer(null); // will get updated in updateKernelArrayRefs
      arg.setArray(null); // will get updated in updateKernelArrayRefs

      Class<?> elementType = arg.getField().getType();
      while (elementType.isArray()) {
         elementType = elementType.getComponentType();
      }

      if (elementType.isAssignableFrom(float.class)) {
         arg.setType(arg.getType() | ARG_FLOAT);
      } else if (elementType.isAssignableFrom(int.class)) {
         arg.setType(arg.getType() | ARG_INT);
      } else if (elementType.isAssignableFrom(boolean.class)) {
         arg.setType(arg.getType() | ARG_BOOLEAN);
      } else if (elementType.isAssignableFrom(byte.class)) {
         arg.setType(arg.getType() | ARG_BYTE);
      } else if (elementType.isAssignableFrom(char.class)) {
         arg.setType(arg.getType() | ARG_CHAR);
      } else if (elementType.isAssignableFrom(double.class)) {
         arg.setType(arg.getType() | ARG_DOUBLE);
      } else if (elementType.isAssignableFrom(long.class)) {
         arg.setType(arg.getType() | ARG_LONG);
      } else if (elementType.isAssignableFrom(short.class)) {
         arg.setType(arg.getType() | ARG_SHORT);
      }
   }

   private final Set<Object> puts = new HashSet<Object>();

   /**
    * Enqueue a request to return this array from the GPU. This method blocks until the array is available.
    * <br/>
    * Note that <code>Kernel.put(type [])</code> calls will delegate to this call.
    * <br/>
    * Package public
    * 
    * @param array
    *          It is assumed that this parameter is indeed an array (of int, float, short etc).
    * 
    * @see Kernel#get(int[] arr)
    * @see Kernel#get(float[] arr)
    * @see Kernel#get(double[] arr)
    * @see Kernel#get(long[] arr)
    * @see Kernel#get(char[] arr)
    * @see Kernel#get(boolean[] arr)
    */
   public void get(Object array) {
      if (explicit && (kernel.isRunningCL())) {
        // Only makes sense when we are using OpenCL
         getJNI(jniContextHandle, array);
      }
   }

   public List<ProfileInfo> getProfileInfo() {
      if (explicit && (kernel.isRunningCL())) {
         // Only makes sense when we are using OpenCL
         return (getProfileInfoJNI(jniContextHandle));
      } else {
         return (null);
      }
   }

   /**
    * Tag this array so that it is explicitly enqueued before the kernel is executed. <br/>
    * Note that <code>Kernel.put(type [])</code> calls will delegate to this call. <br/>
    * Package public
    * 
    * @param array
    *          It is assumed that this parameter is indeed an array (of int, float, short etc).
    * @see Kernel#put(int[] arr)
    * @see Kernel#put(float[] arr)
    * @see Kernel#put(double[] arr)
    * @see Kernel#put(long[] arr)
    * @see Kernel#put(char[] arr)
    * @see Kernel#put(boolean[] arr)
    */

   public void put(Object array) {
      if (explicit && (kernel.isRunningCL())) {
         // Only makes sense when we are using OpenCL
         puts.add(array);
      }
   }

   private boolean explicit = false;

   public void setExplicit(boolean _explicit) {
      explicit = _explicit;
   }

   public boolean isExplicit() {
      return (explicit);
   }

   private static class ExecutionSettings {
      final KernelPreferences preferences;
      final KernelProfile profile;
      final String entrypoint;
      Range range;
      final int passes;
      final boolean legacyExecutionMode;

      private ExecutionSettings(KernelPreferences preferences, KernelProfile profile, String entrypoint, Range range, int passes, boolean legacyExecutionMode) {
         this.preferences = preferences;
         this.profile = profile;
         this.entrypoint = entrypoint;
         this.range = range;
         this.passes = passes;
         this.legacyExecutionMode = legacyExecutionMode;
      }

      @Override
      public String toString() {
         return "ExecutionSettings{" +
                 "preferences=" + preferences +
                 ", profile=" + profile +
                 ", entrypoint='" + entrypoint + '\'' +
                 ", range=" + range +
                 ", passes=" + passes +
                 ", legacyExecutionMode=" + legacyExecutionMode +
                 '}';
      }
   }
}
