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
import static org.junit.Assume.assumeTrue;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.device.JavaDevice;
import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.kernel.KernelManager;

/**
 * Advanced tests for validation of the correctness of the atomics implementation both on Java and on OpenCL.
 * @author CodeRasurae
 */
public class AtomicsSupportAdvTest {

    private static OpenCLDevice openCLDevice = null;

    private static final int SIZE = 100;
	private final static int LOCK_IDX = 3;
	private final static int MAX_VAL_IDX = 0;
	private final static int MAX_POS_LEFT_IDX = 1;
	private final static int MAX_POS_RIGHT_IDX = 2;

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
    
    @Before
    public void setUpBeforeClass() throws Exception {
    	KernelManager.setKernelManager(new CLKernelManager());
        Device device = KernelManager.instance().bestDevice();
        assumeTrue (device != null && device instanceof OpenCLDevice);
        openCLDevice = (OpenCLDevice) device;
    }

    @Test
    public void testOpenCLExplicit() {
    	final int in[] = new int[SIZE];
    	
    	final int[] out = new int[3];
    	for (int i = 0; i < SIZE/2; i++) {
    		in[i] = i;
    		in[i + SIZE/2] = SIZE - i;
    	}
    	in[10] = SIZE;
    	
        final AtomicKernel kernel = new AtomicKernel(in, out);
        try {
	        final Range range = openCLDevice.createRange(SIZE/2, SIZE/2);
	        kernel.setExplicit(true);
	        kernel.put(in);
	        kernel.execute(range);
	        kernel.get(out);
        } finally {
        	kernel.dispose();
        }

        assertEquals("Max value doesn't match", 100, out[0]);
        assertTrue("Left max found at unexpected position: " + out[MAX_POS_LEFT_IDX], out[MAX_POS_LEFT_IDX] == 10 || out[MAX_POS_LEFT_IDX] == 50);
        assertTrue("Right max found at unexpected position: " + out[MAX_POS_RIGHT_IDX], out[MAX_POS_RIGHT_IDX] == 100-10 || out[MAX_POS_RIGHT_IDX] == 100-50);
    }
    
    @Test
    public void testOpenCL() {
    	final int in[] = new int[SIZE];
    	
    	final int[] out = new int[3];
    	for (int i = 0; i < SIZE/2; i++) {
    		in[i] = i;
    		in[i + SIZE/2] = SIZE - i;
    	}
    	in[10] = SIZE;
    	
        final AtomicKernel kernel = new AtomicKernel(in, out);
        try {
	        final Range range = openCLDevice.createRange(SIZE/2, SIZE/2);
	        kernel.execute(range);
        } finally {
        	kernel.dispose();
        }

        assertEquals("Max value doesn't match", 100, out[0]);
        assertTrue("Left max found at unexpected position: " + out[MAX_POS_LEFT_IDX], out[MAX_POS_LEFT_IDX] == 10 || out[MAX_POS_LEFT_IDX] == 50);
        assertTrue("Right max found at unexpected position: " + out[MAX_POS_RIGHT_IDX], out[MAX_POS_RIGHT_IDX] == 100-10 || out[MAX_POS_RIGHT_IDX] == 100-50);
    }
    
    @Test
    public void testJTP() {
    	KernelManager.setKernelManager(new JTPKernelManager());
    	Device device = KernelManager.instance().bestDevice();
    	final int in[] = new int[SIZE];
    	
    	final int[] out = new int[3];
    	for (int i = 0; i < SIZE/2; i++) {
    		in[i] = i;
    		in[i + SIZE/2] = SIZE - i;
    	}
    	in[10] = SIZE;
    	
        final AtomicKernel kernel = new AtomicKernel(in, out);
        try {
	        final Range range = device.createRange(SIZE/2, SIZE/2);
	        kernel.execute(range);
        } finally {
        	kernel.dispose();
        }
        assertEquals("Max value doesn't match", 100, out[0]);
        assertTrue("Left max found at unexpected position: " + out[MAX_POS_LEFT_IDX], out[MAX_POS_LEFT_IDX] == 10 || out[MAX_POS_LEFT_IDX] == 50);
        assertTrue("Right max found at unexpected position: " + out[MAX_POS_RIGHT_IDX], out[MAX_POS_RIGHT_IDX] == 100-10 || out[MAX_POS_RIGHT_IDX] == 100-50);
    }
    
    @Test
    public void testBOpenCL() {
    	final int in[] = new int[SIZE];
    	final AtomicInteger[] out = new AtomicInteger[3];
    	for (int i = 0; i < out.length; i++) {
    		out[i] = new AtomicInteger(0);
    	}
    	for (int i = 0; i < SIZE/2; i++) {
    		in[i] = i;
    		in[i + SIZE/2] = SIZE - i;
    	}
    	in[10] = SIZE;
    	
        final AtomicBKernel kernel = new AtomicBKernel(in, out);
        try {
	        final Range range = openCLDevice.createRange(SIZE/2, SIZE/2);
	        kernel.execute(range);
        } finally {
        	kernel.dispose();
        }

        assertEquals("Max value doesn't match", 100, out[0].get());
        assertTrue("Left max found at unexpected position: " + out[MAX_POS_LEFT_IDX], out[MAX_POS_LEFT_IDX].get() == 10 || out[MAX_POS_LEFT_IDX].get() == 50);
        assertTrue("Right max found at unexpected position: " + out[MAX_POS_RIGHT_IDX], out[MAX_POS_RIGHT_IDX].get() == 100-10 || out[MAX_POS_RIGHT_IDX].get() == 100-50);
    }
        
    @Test
    public void testBJTP() {
    	KernelManager.setKernelManager(new JTPKernelManager());
    	Device device = KernelManager.instance().bestDevice();
    	final int in[] = new int[SIZE];
    	final AtomicInteger[] out = new AtomicInteger[3];
    	for (int i = 0; i < out.length; i++) {
    		out[i] = new AtomicInteger(0);
    	}
    	for (int i = 0; i < SIZE/2; i++) {
    		in[i] = i;
    		in[i + SIZE/2] = SIZE - i;
    	}
    	in[10] = SIZE;
    	
        final AtomicBKernel kernel = new AtomicBKernel(in, out);
        try {
	        final Range range = device.createRange(SIZE/2, SIZE/2);
	        kernel.execute(range);
        } finally {
        	kernel.dispose();
        }
        assertEquals("Max value doesn't match", 100, out[0].get());
        assertTrue("Left max found at unexpected position: " + out[MAX_POS_LEFT_IDX], out[MAX_POS_LEFT_IDX].get() == 10 || out[MAX_POS_LEFT_IDX].get() == 50);
        assertTrue("Right max found at unexpected position: " + out[MAX_POS_RIGHT_IDX], out[MAX_POS_RIGHT_IDX].get() == 100-10 || out[MAX_POS_RIGHT_IDX].get() == 100-50);
    }    
    
    private static final class AtomicKernel extends Kernel {    	
    	private int in[];
    	private int out[];
    	
    	@Local
    	private final AtomicInteger maxs[] = new AtomicInteger[4];
    	    	
    	public AtomicKernel(int[] in, int[] out) {
    		this.in = in;
    		this.out = out;
    		for (int idx = 0; idx < 4; idx++) {
    			maxs[idx] = new AtomicInteger(0);
    		}
    	}
    	
        @Override
        public void run() {
        	final int localId = getLocalId(0);
        	
        	//Ensure that initial values are initialized... this must be enforced for OpenCL, otherwise they may contain
        	//random values, as for Java, it is not needed, as they are already initialized in AtomicInteger constructor.
        	//Since this is Aparapi, it must be initialized on both platforms. 
        	if (localId == 0) {
	        	atomicSet(maxs[MAX_VAL_IDX], 0);
	        	atomicSet(maxs[LOCK_IDX], 0);
        	}
        	//Ensure all threads start with the initialized atomic max value and lock.
        	localBarrier();
        	
        	final int offset = localId * 2;
    		int localMaxVal = 0;
    		int localMaxPosFromLeft = 0;
    		int localMaxPosFromRight = 0;
    		for (int i = 0; i < 2; i++) {
    			localMaxVal = max(in[offset + i], localMaxVal);
    			if (localMaxVal == in[offset + i]) {
    				localMaxPosFromLeft = offset + i;
    				localMaxPosFromRight = SIZE - (offset + i);
    			}
    		}
    		
        	atomicMax(maxs[MAX_VAL_IDX], localMaxVal);
    		//Ensure all threads have updated the atomic maxs[MAX_VAL_IDX]
        	localBarrier();
        	
        	int maxValue = atomicGet(maxs[MAX_VAL_IDX]);
    		//Only the threads that have the max value will reach this point, however the max value, may
    		//occur at multiple indices of the input array.
        	if (maxValue == localMaxVal && atomicXchg(maxs[LOCK_IDX], 0xff) == 0) {
    			//Only one of the threads with the max value will get here, thus ensuring consistent update of
    			//maxPosFromRight and maxPosFromLeft.
    			atomicSet(maxs[MAX_POS_LEFT_IDX], localMaxPosFromLeft);
    			atomicSet(maxs[MAX_POS_RIGHT_IDX], localMaxPosFromRight);
    			out[MAX_VAL_IDX] = maxValue;
    			out[MAX_POS_LEFT_IDX] = atomicGet(maxs[MAX_POS_LEFT_IDX]);
    			out[MAX_POS_RIGHT_IDX] = localMaxPosFromRight;
        	}
        }
    }
    
    private static final class AtomicBKernel extends Kernel {    	
    	private int in[];
    	private AtomicInteger out[];
    	
    	@Local
    	private final AtomicInteger maxs[] = new AtomicInteger[4];
    	    	
    	public AtomicBKernel(int[] in, AtomicInteger[] out) {
    		this.in = in;
    		this.out = out;
    		for (int idx = 0; idx < 4; idx++) {
    			maxs[idx] = new AtomicInteger(0);
    		}
    	}
    	
        @Override
        public void run() {
        	final int localId = getLocalId(0);
        	
        	//Ensure that initial values are initialized... this must be enforced for OpenCL, otherwise they may contain
        	//random values, as for Java, it is not needed, as they are already initialized in AtomicInteger constructor.
        	//Since this is Aparapi, it must be initialized on both platforms. 
        	if (localId == 0) {
	        	atomicSet(maxs[MAX_VAL_IDX], 0);
	        	atomicSet(maxs[LOCK_IDX], 0);
        	}
        	//Ensure all threads start with the initialized atomic max value and lock.
        	localBarrier();
        	
        	final int offset = localId * 2;
    		int localMaxVal = 0;
    		int localMaxPosFromLeft = 0;
    		int localMaxPosFromRight = 0;
    		for (int i = 0; i < 2; i++) {
    			localMaxVal = max(in[offset + i], localMaxVal);
    			if (localMaxVal == in[offset + i]) {
    				localMaxPosFromLeft = offset + i;
    				localMaxPosFromRight = SIZE - (offset + i);
    			}
    		}
    		
        	atomicMax(maxs[MAX_VAL_IDX], localMaxVal);
    		//Ensure all threads have updated the atomic maxs[MAX_VAL_IDX]
        	localBarrier();
        	
        	int maxValue = atomicGet(maxs[MAX_VAL_IDX]);
    		//Only the threads that have the max value will reach this point, however the max value, may
    		//occur at multiple indices of the input array.
        	if (maxValue == localMaxVal && atomicXchg(maxs[LOCK_IDX], 0xff) == 0) {
    			//Only one of the threads with the max value will get here, thus ensuring consistent update of
    			//maxPosFromRight and maxPosFromLeft.
    			atomicSet(maxs[MAX_POS_LEFT_IDX], localMaxPosFromLeft);
    			atomicSet(maxs[MAX_POS_RIGHT_IDX], localMaxPosFromRight);
    			atomicSet(out[MAX_VAL_IDX], maxValue);
    			atomicSet(out[MAX_POS_LEFT_IDX], atomicGet(maxs[MAX_POS_LEFT_IDX]));
    			atomicSet(out[MAX_POS_RIGHT_IDX], localMaxPosFromRight);
        	}
        }
    }

}
