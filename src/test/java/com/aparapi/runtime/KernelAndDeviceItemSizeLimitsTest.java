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
import com.aparapi.exception.QueryFailedException;
import com.aparapi.internal.kernel.KernelManager;

public class KernelAndDeviceItemSizeLimitsTest {
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
   public void testKernelLocalMemSizeInUseAndExecuteOnOpenCL() {
      SimpleKernel myKernel = new SimpleKernel();
      
      long maxLocalMemSize = -10;
      try  {
         maxLocalMemSize = myKernel.getKernelLocalMemSizeInUse(openCLDevice);
      } catch(QueryFailedException e) {
         fail("This shouldn't happen");
      }
      
      assertTrue("Max Local Mem Size should be greater or equal to 0", maxLocalMemSize >= 0);
      
      Range r = Range.create(openCLDevice, SIZE, SIZE);
      myKernel.execute(r);
      
      int[] results = myKernel.getResults();
      for (int i = 0; i < SIZE; i++) {
         assertEquals("There is an error in the computed kernel result at index=" + i, i, results[i]);
      }
      
      assertTrue("Aparapi should have taken some time to compile and execute the kernel", 
            myKernel.getAccumulatedExecutionTimeCurrentThread(openCLDevice) > 0);
   }
   
   @Test
   public void testKernelMinimumPrivateMemSizeInUsePerWorkItemAndExecuteOnOpenCL() {
      SimpleKernel myKernel = new SimpleKernel();
      
      long maxPrivateMemSize = -10;
      try  {
         maxPrivateMemSize = myKernel.getKernelMinimumPrivateMemSizeInUsePerWorkItem(openCLDevice);
      } catch(QueryFailedException e) {
         fail("This shouldn't happen");
      }
      
      assertTrue("Max Private Mem Size should be greater than 0", maxPrivateMemSize >= 0);
      
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
   public void testKernelMaxWorkGroupSizeAndExecuteOnOpenCL() {
      SimpleKernel myKernel = new SimpleKernel();
      
      int maxWorkGroupSize = -10;
      try  {
         maxWorkGroupSize = myKernel.getKernelMaxWorkGroupSize(openCLDevice);
      } catch(QueryFailedException e) {
         fail("This shouldn't happen");
      }
      
      assertTrue("Max Kernel Workgroup Size should be greater than 0", maxWorkGroupSize > 0);
      
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
   public void testPreferredKernelWorkGroupSizeMultipleAndExecuteOnOpenCL() {
      SimpleKernel myKernel = new SimpleKernel();
      
      int preferredWorkGroupSizeMultiple = -10;
      try  {
         preferredWorkGroupSizeMultiple = myKernel.getKernelPreferredWorkGroupSizeMultiple(openCLDevice);
      } catch(QueryFailedException e) {
         fail("This shouldn't happen");
      }
      
      assertTrue("Preferred Kernel Workgroup Size Multiple should be greater than 0", preferredWorkGroupSizeMultiple > 0);
      
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
   public void testKernelCompileWorkGroupSizeAndExecuteOnOpenCL() {
      SimpleKernel myKernel = new SimpleKernel();
      
      int maxWorkItemSize[] = null;
      try  {
         maxWorkItemSize = myKernel.getKernelCompileWorkGroupSize(openCLDevice);
      } catch(QueryFailedException e) {
         fail("This shouldn't happen");
      }
      
      assertTrue("Kernel Compile Work Group Size should not be null", maxWorkItemSize != null);
      assertTrue("Kernel Compile Work Group Size should have a dimension greater than 0", maxWorkItemSize.length > 0);
      for (int i = 0; i < maxWorkItemSize.length; i++) {
         assertTrue("Kernel Compile Work Group Size should be greater or equal than zero at index=" + i, maxWorkItemSize[i] >= 0);
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

   /////////
   ///JTP///
   /////////
   @Test
   public void testKernelLocalMemSizeInUseAndExecuteOnJTP() {
      KernelManager.setKernelManager(new JTPKernelManager());
      Device device = KernelManager.instance().bestDevice();
      assumeTrue (device != null && device instanceof JavaDevice);
      
      SimpleKernel myKernel = new SimpleKernel();
      
      long maxLocalMemSize = -10;
      try  {
         maxLocalMemSize = myKernel.getKernelLocalMemSizeInUse(device);
      } catch(QueryFailedException e) {
         fail("This shouldn't happen");
      }
      
      assertTrue("Max Local Mem Size should be equal or greater to 0", maxLocalMemSize >= 0);
      
      Range r = Range.create(device, SIZE, SIZE);
      myKernel.execute(r);
      
      int[] results = myKernel.getResults();
      for (int i = 0; i < SIZE; i++) {
         assertEquals("There is an error in the computed kernel result at index=" + i, i, results[i]);
      }
      
      assertTrue("Aparapi should have taken some time to compile and execute the kernel", 
            myKernel.getAccumulatedExecutionTimeCurrentThread(device) > 0);
   }
   
   @Test
   public void testKernelMinimumPrivateMemSizeInUsePerWorkItemAndExecuteOnJTP() {
      KernelManager.setKernelManager(new JTPKernelManager());
      Device device = KernelManager.instance().bestDevice();
      assumeTrue (device != null && device instanceof JavaDevice);
      
      SimpleKernel myKernel = new SimpleKernel();
      
      long maxPrivateMemSize = -10;
      try  {
         maxPrivateMemSize = myKernel.getKernelMinimumPrivateMemSizeInUsePerWorkItem(device);
      } catch(QueryFailedException e) {
         fail("This shouldn't happen");
      }
      
      assertTrue("Max Private Mem Size should be equal or greater to 0", maxPrivateMemSize >= 0);
      
      Range r = Range.create(device, SIZE, SIZE);
      myKernel.execute(r);
      
      int[] results = myKernel.getResults();
      for (int i = 0; i < SIZE; i++) {
         assertEquals("There is an error in the computed kernel result at index=" + i, i, results[i]);
      }
      
      assertTrue("Aparapi should have taken some time to compile and execute the kernel", 
            myKernel.getAccumulatedExecutionTimeCurrentThread(device) > 0);
   }
   
   @Test
   public void testKernelMaxWorkGroupSizeAndExecuteOnJTP() {
      KernelManager.setKernelManager(new JTPKernelManager());
      Device device = KernelManager.instance().bestDevice();
      assumeTrue (device != null && device instanceof JavaDevice);
      
      SimpleKernel myKernel = new SimpleKernel();
      
      int maxWorkGroupSize = 0;
      try  {
         maxWorkGroupSize = myKernel.getKernelMaxWorkGroupSize(device);
      } catch(QueryFailedException e) {
         fail("This shouldn't happen");
      }
      
      assertTrue("Max Kernel Workgroup Size should be equal or greater than 0", maxWorkGroupSize >= 0);
      
      Range r = Range.create(device, SIZE, SIZE);
      myKernel.execute(r);
      
      int[] results = myKernel.getResults();
      for (int i = 0; i < SIZE; i++) {
         assertEquals("There is an error in the computed kernel result at index=" + i, i, results[i]);
      }
      
      assertTrue("Aparapi should have taken some time to compile and execute the kernel", 
            myKernel.getAccumulatedExecutionTimeCurrentThread(device) > 0);
   }
   
   @Test
   public void testPreferredKernelWorkGroupSizeMultipleAndExecuteOnJTP() {
      KernelManager.setKernelManager(new JTPKernelManager());
      Device device = KernelManager.instance().bestDevice();
      assumeTrue (device != null && device instanceof JavaDevice);
      
      SimpleKernel myKernel = new SimpleKernel();
      
      int preferredWorkGroupSizeMultiple = -10;
      try  {
         preferredWorkGroupSizeMultiple = myKernel.getKernelPreferredWorkGroupSizeMultiple(device);
      } catch(QueryFailedException e) {
         fail("This shouldn't happen");
      }
      
      assertTrue("Preferred Kernel Workgroup Size Multiple should be equal to 1", preferredWorkGroupSizeMultiple == 1);
      
      Range r = Range.create(device, SIZE, SIZE);
      myKernel.execute(r);
      
      int[] results = myKernel.getResults();
      for (int i = 0; i < SIZE; i++) {
         assertEquals("There is an error in the computed kernel result at index=" + i, i, results[i]);
      }
      
      assertTrue("Aparapi should have taken some time to compile and execute the kernel", 
            myKernel.getAccumulatedExecutionTimeCurrentThread(device) > 0);
   }

   @Test
   public void testKernelCompileWorkGroupSizeAndExecuteOnJTP() {
      KernelManager.setKernelManager(new JTPKernelManager());
      Device device = KernelManager.instance().bestDevice();
      assumeTrue (device != null && device instanceof JavaDevice);
      
      SimpleKernel myKernel = new SimpleKernel();
      
      int maxWorkItemSize[] = null;
      try  {
         maxWorkItemSize = myKernel.getKernelCompileWorkGroupSize(device);
      } catch(QueryFailedException e) {
         fail("This shouldn't happen");
      }
      
      assertTrue("Kernel Compile Work Group Size should not be null", maxWorkItemSize != null);
      assertTrue("Kernel Compile Work Group Size should have a dimension greater than 0", maxWorkItemSize.length > 0);
      for (int i = 0; i < maxWorkItemSize.length; i++) {
         assertTrue("Kernel Compile Work Group Size should be greater or equal than zero at index=" + i, maxWorkItemSize[i] >= 0);
      }
      
      Range r = Range.create(device, SIZE, SIZE);
      myKernel.execute(r);
      
      int[] results = myKernel.getResults();
      for (int i = 0; i < SIZE; i++) {
         assertEquals("There is an error in the computed kernel result at index=" + i, i, results[i]);
      }
      
      assertTrue("Aparapi should have taken some time to compile and execute the kernel", 
                 myKernel.getAccumulatedExecutionTimeCurrentThread(device) > 0);
   }

   
   private static class SimpleKernel extends Kernel {
      private int resultArray[] = new int[SIZE];
      
      @Local
      private int myArray[] =  new int[32];
      
      @NoCL
      public int[] getResults() {
         return resultArray;
      }
      
      @Override
      public void run() {
         int id = getLocalId();
         int me = id + 1;
         me--;
         resultArray[id] = me;
      }
   }
}
