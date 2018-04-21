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

import static org.junit.Assume.assumeTrue;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.aparapi.Kernel;
import com.aparapi.ProfileReport;
import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.device.JavaDevice;
import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.kernel.KernelManager;

/**
 * Provides integration tests to help assure backwards compatibility under single thread per kernel per device
 * execution environments. 
 * 
 * @author CoreRasurae
 *
 */
public class ProfileReportBackwardsCompatTest {
	private static OpenCLDevice openCLDevice;
	
	@Rule 
	public TestName name = new TestName();

	
    private class CLKernelManager extends KernelManager {
    	@Override
    	protected List<Device.TYPE> getPreferredDeviceTypes() {
    		return Arrays.asList(Device.TYPE.ACC, Device.TYPE.GPU, Device.TYPE.CPU);
    	}
    }
    
    private class JTPKernelManager extends KernelManager {
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
    
    public void setUpBefore() throws Exception {
    	KernelManager.setKernelManager(new CLKernelManager());
        Device device = KernelManager.instance().bestDevice();
        if (device == null || !(device instanceof OpenCLDevice)) {
        	System.out.println("!!!No OpenCLDevice available for running the integration test - test will be skipped");
        }
        assumeTrue (device != null && device instanceof OpenCLDevice);
        openCLDevice = (OpenCLDevice) device;
    }


    /**
     * This integration test validates that previous Kernel methods for retrieving profiling data
     * are still consistent in the new implementation and with the new profile reports. 
     * @throws Exception 
     */
    @Test
    public void sequentialSingleThreadOpenCLTest() throws Exception {
    	setUpBefore();
    	System.out.println("Test " + name.getMethodName() + " - Executing on device: " + openCLDevice.getShortDescription() + " - " + openCLDevice.getName());
    	assertTrue(sequentialSingleThreadTestHelper(openCLDevice, 128));
    }

    /**
     * This integration test validates that previous Kernel methods for retrieving profiling data
     * are still consistent in the new implementation and with the new profile reports. 
     */
    @Test
    public void sequentialSingleThreadJTPTest() {
    	KernelManager.setKernelManager(new JTPKernelManager());
    	Device device = KernelManager.instance().bestDevice();
    	assertTrue(sequentialSingleThreadTestHelper(device, 32));
    }

    
    public boolean sequentialSingleThreadTestHelper(Device device, int size) {
    	final int runs = 100;
    	final int inputArray[] = new int[size];
    	double accumulatedExecutionTime = 0.0;
    	double lastExecutionTime = 0.0;
    	double lastConversionTime = 0.0;
    	final Basic1Kernel kernel = new Basic1Kernel();
    	
    	int[] outputArray = null;
    	Range range = device.createRange(size, size);
    	long startOfExecution = System.currentTimeMillis();
    	try {
    		for (int i = 0; i < runs; i++) {
    			outputArray = Arrays.copyOf(inputArray, inputArray.length);
    			kernel.setInputOuputArray(outputArray);
    			kernel.execute(range);
    			lastExecutionTime = kernel.getExecutionTime();
    			accumulatedExecutionTime += lastExecutionTime;
    			lastConversionTime = kernel.getConversionTime();
    		}
    		long runTime = System.currentTimeMillis() - startOfExecution;
    		WeakReference<ProfileReport> reportRef = kernel.getProfileReportLastThread(device);
    		ProfileReport report = reportRef.get();
    		assertEquals("Number of profiling reports doesn't match the expected", runs, report.getReportId());
    		assertEquals("Aparapi Accumulated execution time doesn't match", accumulatedExecutionTime, kernel.getAccumulatedExecutionTime(), 1e-10);
    		assertEquals("Aparapi last execution time doesn't match last report", lastExecutionTime, report.getExecutionTime(), 1e-10);
    		assertEquals("Aparapi last conversion time doesn't match last report", lastConversionTime, report.getConversionTime(), 1e-10);
    		assertEquals("Test estimated accumulated time doesn't match within 100ms window", runTime, accumulatedExecutionTime, 100);
    		assertTrue(validateBasic1Kernel(inputArray, outputArray));
    	} finally {
    		kernel.dispose();
    	}
    	
    	return true;
    }

    private class TestData {
    	private int[] outputArray;
    	private double accumulatedExecutionTime = 0.0;
    	private double lastExecutionTime = 0.0;
    	private double lastConversionTime = 0.0;
    	private long startOfExecution = 0;
    	private long runTime = 0;
    }
    
    /**
     * This test executes two threads one for each kernel on an OpenCL device and checks that the traditional Aparapi profiling interfaces work. 
     * @throws Exception 
     */
    @Test
    public void threadedSingleThreadPerKernelOpenCLTest() throws Exception {
    	setUpBefore();
    	System.out.println("Test " + name.getMethodName() + " - Executing on device: " + openCLDevice.getShortDescription() + " - " + openCLDevice.getName());
    	assertTrue(threadedSingleThreadPerKernelTestHelper(openCLDevice, 128));
    }
    
    /**
     * This test executes two threads one for each kernel on Java Thread Pool and checks that the traditional Aparapi profiling interfaces work.
     */
    @Test
    public void threadedSingleThreadPerKernelJTPTest() {
    	KernelManager.setKernelManager(new JTPKernelManager());
    	Device device = KernelManager.instance().bestDevice();
    	assertTrue(threadedSingleThreadPerKernelTestHelper(device, 16));
    }
    
    public boolean threadedSingleThreadPerKernelTestHelper(Device device, final int size) {
    	final int runs = 100;
    	final int inputArray[] = new int[size];
    	
    	final Basic1Kernel kernel1 = new Basic1Kernel();
    	final Basic1Kernel kernel2 = new Basic2Kernel();
    	List<Basic1Kernel> kernels = new ArrayList<Basic1Kernel>(2);
    	kernels.add(kernel1);
    	kernels.add(kernel2);
    	
    	final TestData[] results = new TestData[2];
    	results[0] = new TestData();
    	results[1] = new TestData();
    	
    	boolean terminatedOk = false;
    	try {
	    	ExecutorService executorService = Executors.newFixedThreadPool(2);
	    	try {
				kernels.forEach(k -> executorService.submit(() -> {
					results[k.getId() - 1].startOfExecution = System.currentTimeMillis();
					for (int i = 0; i < runs; i++) {
						results[k.getId() - 1].outputArray = Arrays.copyOf(inputArray, inputArray.length);
						k.setInputOuputArray(results[k.getId() - 1].outputArray);
	    				k.execute(Range.create(device, size, size));
	    				results[k.getId() - 1].lastExecutionTime = k.getExecutionTime();
	    				results[k.getId() - 1].accumulatedExecutionTime += results[k.getId() - 1].lastExecutionTime;
	    				results[k.getId() - 1].lastConversionTime = k.getConversionTime();
					}
					results[k.getId() - 1].runTime = System.currentTimeMillis() - results[k.getId() - 1].startOfExecution;
				}));
	    	} finally {
	    		executorService.shutdown();
	    		try {
	    			terminatedOk = executorService.awaitTermination(5, TimeUnit.MINUTES);
	    		} catch (InterruptedException ex) {
	    			//For the purposes of the test this suffices
	    			terminatedOk = false;
	    		}
	            if (!terminatedOk) {
	            	executorService.shutdownNow();
	            }
	    	}
	    	
	    	assertTrue(terminatedOk);
	 
	    	//Validate kernel1 reports
    		WeakReference<ProfileReport> reportRef = kernel1.getProfileReportLastThread(device);
    		ProfileReport report = reportRef.get();
    		assertEquals("Number of profiling reports doesn't match the expected", runs, report.getReportId());
    		assertEquals("Aparapi Accumulated execution time doesn't match", results[0].accumulatedExecutionTime, kernel1.getAccumulatedExecutionTime(), 1e-10);
    		assertEquals("Aparapi last execution time doesn't match last report", results[0].lastExecutionTime, report.getExecutionTime(), 1e-10);
    		assertEquals("Aparapi last conversion time doesn't match last report", results[0].lastConversionTime, report.getConversionTime(), 1e-10);
    		assertEquals("Test estimated accumulated time doesn't match within 100ms window", results[0].runTime, results[0].accumulatedExecutionTime, 100);
    		assertTrue(validateBasic1Kernel(inputArray, results[0].outputArray));
    		
    		//Validate kernel2 reports
    		reportRef = kernel2.getProfileReportLastThread(device);
    		report = reportRef.get();
    		assertEquals("Number of profiling reports doesn't match the expected", runs, report.getReportId());
    		assertEquals("Aparapi Accumulated execution time doesn't match", results[1].accumulatedExecutionTime, kernel2.getAccumulatedExecutionTime(), 1e-10);
    		assertEquals("Aparapi last execution time doesn't match last report", results[1].lastExecutionTime, report.getExecutionTime(), 1e-10);
    		assertEquals("Aparapi last conversion time doesn't match last report", results[1].lastConversionTime, report.getConversionTime(), 1e-10);
    		assertEquals("Test estimated accumulated time doesn't match within 100ms window", results[1].runTime, results[1].accumulatedExecutionTime, 100);
    		assertTrue(validateBasic2Kernel(inputArray, results[1].outputArray));
    	} finally {
    		kernel1.dispose();
    		kernel2.dispose();
    	}
    	
    	return true;
    }
    
    private boolean validateBasic1Kernel(final int[] inputArray, final int[] resultArray) {
    	int[] expecteds = Arrays.copyOf(inputArray, inputArray.length);
    	for (int threadId = 0; threadId < inputArray.length; threadId++) {
    		expecteds[threadId] += threadId;
    	}
    	
    	assertArrayEquals(expecteds, resultArray);
    	
    	return true;
    }

    private boolean validateBasic2Kernel(final int[] inputArray, final int[] resultArray) {
    	int[] expecteds = Arrays.copyOf(inputArray, inputArray.length);
    	for (int threadId = 0; threadId < inputArray.length; threadId++) {
    		expecteds[threadId] += threadId+1;
    	}
    	
    	assertArrayEquals(expecteds, resultArray);
    	
    	return true;
    }

    private class Basic1Kernel extends Kernel {
    	protected int[] workArray;
    	
    	@NoCL
    	public void setInputOuputArray(int[] array) {
    		workArray = array;
    	}

    	@NoCL
    	public int getId() {
    		return 1;
    	}
    	
		@Override
		public void run() {
			int id = getLocalId();
			
			workArray[id]+=id;
		}
    }
    
    private class Basic2Kernel extends Basic1Kernel {
    	@Override
    	@NoCL
    	public int getId() {
    		return 2;
    	}

    	@Override 
    	public void run() {
    		int id = getLocalId();
			
			workArray[id]+=id+1;
    	}
    }
	
}
