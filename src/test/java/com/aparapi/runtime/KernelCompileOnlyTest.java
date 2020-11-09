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
package com.aparapi.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.device.JavaDevice;
import com.aparapi.device.OpenCLDevice;
import com.aparapi.exception.CompileFailedException;
import com.aparapi.internal.kernel.KernelManager;

public class KernelCompileOnlyTest {
   protected static OpenCLDevice openCLDevice = null;
   protected int[] targetArray;
   protected static final int SIZE = 100;

   protected class CLKernelManager extends KernelManager {
     @Override
     protected List<Device.TYPE> getPreferredDeviceTypes() {
        return Arrays.asList(Device.TYPE.ACC, Device.TYPE.GPU, Device.TYPE.CPU);
     }
   }

   protected class JTPKernelManager extends KernelManager {
     private JTPKernelManager() {
        LinkedHashSet<Device> preferredDevices = new LinkedHashSet<Device>(1);
        preferredDevices.add(JavaDevice.THREAD_POOL);
        setDefaultPreferredDevices(preferredDevices);
     }
     @Override
     protected List<Device.TYPE> getPreferredDeviceTypes() {
        return Arrays.asList(Device.TYPE.JTP);
     }
   }

   @AfterClass
   public static void classTeardown() {
     Util.resetKernelManager();
   }

   @Before
   public void setUpBefore() throws Exception {
      KernelManager.setKernelManager(new CLKernelManager());
      Device device = KernelManager.instance().bestDevice();
      assumeTrue (device != null && device instanceof OpenCLDevice);
      openCLDevice = (OpenCLDevice) device;
   }
   
   
   @Test
   public void testCompileOnlyOnOpenCL() {
      SimpleKernel myKernel = new SimpleKernel();
      
      try  {
         myKernel.compile(openCLDevice);
      } catch(CompileFailedException e) {
         fail("This shouldn't happen");
      }
      
      assertTrue("Aparapi should have taken some time to compile the kernel", 
                 myKernel.getAccumulatedExecutionTimeCurrentThread(openCLDevice) > 0);
   }
   
   @Test
   public void testCompileFollowedByExecuteOnOpenCL() {
      SimpleKernel myKernel = new SimpleKernel();
      
      try  {
         myKernel.compile(openCLDevice);
      } catch(CompileFailedException e) {
         fail("This shouldn't happen");
      }
      
      Range r = Range.create(openCLDevice, SIZE);
      myKernel.execute(r);
      
      int[] results = myKernel.getResults();
      for (int i = 0; i < SIZE; i++) {
         assertEquals("There is an error in the computed kernel result at index=" + i, i, results[i]);
      }
      
      assertTrue("Aparapi should have taken some time to compile and execute the kernel", 
            myKernel.getAccumulatedExecutionTimeCurrentThread(openCLDevice) > 0);
   }

   @Test
   public void testCompileCompileFollowedByExecuteOnOpenCL() {
      SimpleKernel myKernel = new SimpleKernel();
      
      try  {
         myKernel.compile(openCLDevice);
      } catch(CompileFailedException e) {
         fail("This shouldn't happen");
      }
      
      try  {
         myKernel.compile(openCLDevice);
      } catch(CompileFailedException e) {
         fail("This shouldn't happen");
      }
      
      Range r = Range.create(openCLDevice, SIZE);
      myKernel.execute(r);
      
      int[] results = myKernel.getResults();
      for (int i = 0; i < SIZE; i++) {
         assertEquals("There is an error in the computed kernel result at index=" + i, i, results[i]);
      }
   }

   @Test
   public void testCompileFollowedByExecuteFollowedByCompileOnOpenCL() {
      SimpleKernel myKernel = new SimpleKernel();
      
      try  {
         myKernel.compile(openCLDevice);
      } catch(CompileFailedException e) {
         fail("This shouldn't happen");
      }
      
      
      Range r = Range.create(openCLDevice, SIZE);
      myKernel.execute(r);
      
      int[] results = myKernel.getResults();
      for (int i = 0; i < SIZE; i++) {
         assertEquals("There is an error in the computed kernel result at index=" + i, i, results[i]);
      }
      
      try  {
         myKernel.compile(openCLDevice);
      } catch(CompileFailedException e) {
         fail("This shouldn't happen");
      }
      
      myKernel.execute(r);
      
      int[] resultsB = myKernel.getResults();
      for (int i = 0; i < SIZE; i++) {
         assertEquals("There is an error in the computed kernel result at index=" + i, i, resultsB[i]);
      }
   }

   @Test(expected = CompileFailedException.class)
   public void testCompilationFailedExceptionCompilingOnNone() throws CompileFailedException {
      NotCompilableKernel myKernel = new NotCompilableKernel();
      myKernel.compile(null);
      fail("Compilation should fail");
   }

   @Test(expected = CompileFailedException.class)
   public void testCompilationFailedExceptionCompilingOnOpenCL() throws CompileFailedException {
      NotCompilableKernel myKernel = new NotCompilableKernel();
      myKernel.compile(openCLDevice);
      fail("Compilation should fail");
   }

   @Test
   public void testCompilationOnJTP() {
      KernelManager.setKernelManager(new JTPKernelManager());
      Device device = KernelManager.instance().bestDevice();
      assumeTrue (device != null && device instanceof JavaDevice);

      SimpleKernel myKernel = new SimpleKernel();
      try  {
         myKernel.compile(device);
      } catch(CompileFailedException e) {
         fail("This shouldn't happen");
      }
   }
   
   @Test
   public void testCompilationFollowedByExecutionOnJTP() {
      KernelManager.setKernelManager(new JTPKernelManager());
      Device device = KernelManager.instance().bestDevice();
      assumeTrue (device != null && device instanceof JavaDevice);

      SimpleKernel myKernel = new SimpleKernel();
      try  {
         myKernel.compile(device);
      } catch(CompileFailedException e) {
         fail("This shouldn't happen");
      }
      
      Range r = Range.create(device, SIZE, SIZE);
      myKernel.execute(r);
      
      int[] resultsB = myKernel.getResults();
      for (int i = 0; i < SIZE; i++) {
         assertEquals("There is an error in the computed kernel result at index=" + i, i, resultsB[i]);
      }
   }

   private static class SimpleKernel extends Kernel {
      private int resultArray[] = new int[SIZE];
      
      @NoCL
      public int[] getResults() {
         return resultArray;
      }
      
      @Override
      public void run() {
         int id = getLocalId();
         resultArray[id] = id;
      }
   }
   
   private static class NotCompilableKernel extends Kernel {
      private int resultArray[];
      
      @NoCL
      public int[] getResults() {
         return resultArray;
      }
      
      @Override
      public void run() {
         int id = getLocalId();
         int[] myArray = new int[SIZE];
         myArray[id] = id;
         resultArray = myArray;
      }
   }
}