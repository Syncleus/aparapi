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

public class Issue81AtomicsSupportTest {

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
    public void issue81OpenCLExplicit() {
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
    public void issue81OpenCL() {
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
    public void issue81JTP() {
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
    public void issue81BOpenCL() {
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
    public void issue81BJTP() {
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

    
    @Test
    public void issue81AtomicAddOpenCLExplicit() {
    	Device openCLDevice = KernelManager.instance().bestDevice();
    	final int in[] = new int[2];
    	final int[] out = new int[2];
    	in[0] = 10;
    	in[1] = 20;
    	
    	final AtomicAdd kernel = new AtomicAdd(in, out);
    	try {
	    	final Range range = openCLDevice.createRange(1,1);
	        kernel.setExplicit(true);
	        kernel.put(in);
	        kernel.execute(range);
	        kernel.get(out);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[0] + in[1], out[1]);
    }

    @Test
    public void issue81AtomicAddOpenCL() {
    	Device openCLDevice = KernelManager.instance().bestDevice();
    	final int in[] = new int[2];
    	final int[] out = new int[2];
    	in[0] = 10;
    	in[1] = 20;
    	
    	final AtomicAdd kernel = new AtomicAdd(in, out);
    	try {
	    	final Range range = openCLDevice.createRange(1,1);
	        kernel.execute(range);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[0] + in[1], out[1]);
    }

    @Test
    public void issue81AtomicAddJTP() {
    	KernelManager.setKernelManager(new JTPKernelManager());
    	Device device = KernelManager.instance().bestDevice();
    	final int in[] = new int[2];
    	final int[] out = new int[2];
    	in[0] = 10;
    	in[1] = 20;
    	
    	final AtomicAdd kernel = new AtomicAdd(in, out);
    	try {
	    	final Range range = device.createRange(1,1);
	    	kernel.execute(range);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[0] + in[1], out[1]);
    }

    /**
     * Kernel for single threaded validation of atomicAdd.
     * Validates that a add operation is actually performed.
     * @author lpnm
     *
     */
    private static final class AtomicAdd extends Kernel {
    	private int in[];
    	private int out[];
    	
    	public AtomicAdd(int[] in, int out[]) {
    		this.in = in;
    		this.out = out;
    		atomicValues = new AtomicInteger[2];
    		atomicValues[0] = new AtomicInteger(0);
    		atomicValues[1] = new AtomicInteger(0);
    	}

    	@Local
    	private AtomicInteger atomicValues[];
    	
		@Override
		public void run() {
			atomicSet(atomicValues[0], in[0]);
			out[0] = atomicAdd(atomicValues[0], in[1]);
			out[1] = atomicGet(atomicValues[0]);
		}
    }

    @Test
    public void issue81AtomicSubOpenCLExplicit() {
    	Device openCLDevice = KernelManager.instance().bestDevice();
    	final int in[] = new int[2];
    	final int[] out = new int[2];
    	in[0] = 10;
    	in[1] = 20;
    	
    	final AtomicSub kernel = new AtomicSub(in, out);
    	try {
	    	final Range range = openCLDevice.createRange(1,1);
	        kernel.setExplicit(true);
	        kernel.put(in);
	        kernel.execute(range);
	        kernel.get(out);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[0] - in[1], out[1]);
    }

    @Test
    public void issue81AtomicSubOpenCL() {
    	Device openCLDevice = KernelManager.instance().bestDevice();
    	final int in[] = new int[2];
    	final int[] out = new int[2];
    	in[0] = 10;
    	in[1] = 20;
    	
    	final AtomicSub kernel = new AtomicSub(in, out);
    	try {
	    	final Range range = openCLDevice.createRange(1,1);
	        kernel.execute(range);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[0] - in[1], out[1]);
    }

    @Test
    public void issue81AtomicSubJTP() {
    	KernelManager.setKernelManager(new JTPKernelManager());
    	Device device = KernelManager.instance().bestDevice();
    	final int in[] = new int[2];
    	final int[] out = new int[2];
    	in[0] = 10;
    	in[1] = 20;
    	
    	final AtomicSub kernel = new AtomicSub(in, out);
    	try {
	    	final Range range = device.createRange(1,1);
	    	kernel.execute(range);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[0] - in[1], out[1]);
    }
    
    /**
     * Kernel for single threaded validation of atomicSub.
     * Validates that a subtraction operation is actually performed.
     * @author lpnm
     *
     */
    private static final class AtomicSub extends Kernel {
    	private int in[];
    	private int out[];
    	
    	public AtomicSub(int[] in, int out[]) {
    		this.in = in;
    		this.out = out;
    		atomicValues = new AtomicInteger[2];
    		atomicValues[0] = new AtomicInteger(0);
    		atomicValues[1] = new AtomicInteger(0);
    	}

    	@Local
    	private AtomicInteger atomicValues[];
    	
		@Override
		public void run() {
			atomicSet(atomicValues[0], in[0]);
			out[0] = atomicSub(atomicValues[0], in[1]);
			out[1] = atomicGet(atomicValues[0]);			
		}

    }

    @Test
    public void issue81AtomicXchgOpenCLExplicit() {
    	Device openCLDevice = KernelManager.instance().bestDevice();
    	final int in[] = new int[2];
    	final int[] out = new int[2];
    	in[0] = 10;
    	in[1] = 20;
    	
    	final AtomicXchg kernel = new AtomicXchg(in, out);
    	try {
	    	final Range range = openCLDevice.createRange(1,1);
	        kernel.setExplicit(true);
	        kernel.put(in);
	        kernel.execute(range);
	        kernel.get(out);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[1], out[1]);
    }

    @Test
    public void issue81AtomicXchgOpenCL() {
    	Device openCLDevice = KernelManager.instance().bestDevice();
    	final int in[] = new int[2];
    	final int[] out = new int[2];
    	in[0] = 10;
    	in[1] = 20;
    	
    	final AtomicXchg kernel = new AtomicXchg(in, out);
    	try {
	    	final Range range = openCLDevice.createRange(1,1);
	        kernel.execute(range);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[1], out[1]);
    }
    
    @Test
    public void issue81AtomicXchgJTP() {
    	KernelManager.setKernelManager(new JTPKernelManager());
    	Device device = KernelManager.instance().bestDevice();
    	final int in[] = new int[2];
    	final int[] out = new int[2];
    	in[0] = 10;
    	in[1] = 20;
    	
    	final AtomicXchg kernel = new AtomicXchg(in, out);
    	try {
	    	final Range range = device.createRange(1,1);
	    	kernel.execute(range);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[1], out[1]);
    }

    /**
     * Kernel for single threaded validation of atomicXchg.
     * Validates that a value exchange operation is actually performed.
     * @author lpnm
     *
     */
    private static final class AtomicXchg extends Kernel {
    	private int in[];
    	private int out[];
    	
    	public AtomicXchg(int[] in, int out[]) {
    		this.in = in;
    		this.out = out;
    		atomicValues = new AtomicInteger[2];
    		atomicValues[0] = new AtomicInteger(0);
    		atomicValues[1] = new AtomicInteger(0);
    	}

    	@Local
    	private AtomicInteger atomicValues[];
    	
		@Override
		public void run() {
			atomicSet(atomicValues[0], in[0]);
			out[0] = atomicXchg(atomicValues[0], in[1]);
			out[1] = atomicGet(atomicValues[0]);
		}

    }
    
    @Test
    public void issue81AtomicIncOpenCLExplicit() {
    	Device openCLDevice = KernelManager.instance().bestDevice();
    	final int in[] = new int[1];
    	final int[] out = new int[2];
    	in[0] = 50;
    	
    	final AtomicInc kernel = new AtomicInc(in, out);
    	try {
	    	final Range range = openCLDevice.createRange(1,1);
	        kernel.setExplicit(true);
	        kernel.put(in);
	        kernel.execute(range);
	        kernel.get(out);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[0] + 1, out[1]);
    }

    @Test
    public void issue81AtomicIncOpenCL() {
    	Device openCLDevice = KernelManager.instance().bestDevice();
    	final int in[] = new int[1];
    	final int[] out = new int[2];
    	in[0] = 50;
    	
    	final AtomicInc kernel = new AtomicInc(in, out);
    	try {
	    	final Range range = openCLDevice.createRange(1,1);
	        kernel.execute(range);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[0] + 1, out[1]);
    }
    
    @Test
    public void issue81AtomicInc() {
    	KernelManager.setKernelManager(new JTPKernelManager());
    	Device device = KernelManager.instance().bestDevice();
    	final int in[] = new int[1];
    	final int[] out = new int[2];
    	in[0] = 50;
    	
    	final AtomicInc kernel = new AtomicInc(in, out);
    	try {
	    	final Range range = device.createRange(1,1);
	    	kernel.execute(range);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[0] + 1, out[1]);
    }

    /**
     * Kernel for single threaded validation of atomicInc.
     * Validates that an increment operation is actually performed.
     * @author lpnm
     *
     */
    private static final class AtomicInc extends Kernel {
    	private int in[];
    	private int out[];
    	
    	public AtomicInc(int[] in, int out[]) {
    		this.in = in;
    		this.out = out;
    		atomicValues = new AtomicInteger[2];
    		atomicValues[0] = new AtomicInteger(0);
    		atomicValues[1] = new AtomicInteger(0);
    	}

    	@Local
    	private AtomicInteger atomicValues[];
    	
		@Override
		public void run() {
			atomicSet(atomicValues[0], in[0]);
			out[0] = atomicInc(atomicValues[0]);
			out[1] = atomicGet(atomicValues[0]);
		}

    }

    @Test
    public void issue81AtomicDecOpenCLExplicit() {
    	Device openCLDevice = KernelManager.instance().bestDevice();
    	final int in[] = new int[1];
    	final int[] out = new int[2];
    	in[0] = 50;
    	
    	final AtomicDec kernel = new AtomicDec(in, out);
    	try {
	    	final Range range = openCLDevice.createRange(1,1);
	        kernel.setExplicit(true);
	        kernel.put(in);
	        kernel.execute(range);
	        kernel.get(out);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[0] - 1, out[1]);
    }

    @Test
    public void issue81AtomicDecOpenCL() {
    	Device openCLDevice = KernelManager.instance().bestDevice();
    	final int in[] = new int[1];
    	final int[] out = new int[2];
    	in[0] = 50;
    	
    	final AtomicDec kernel = new AtomicDec(in, out);
    	try {
	    	final Range range = openCLDevice.createRange(1,1);
	        kernel.execute(range);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[0] - 1, out[1]);
    }

    @Test
    public void issue81AtomicDecJTP() {
    	KernelManager.setKernelManager(new JTPKernelManager());
    	Device device = KernelManager.instance().bestDevice();
    	final int in[] = new int[1];
    	final int[] out = new int[2];
    	in[0] = 50;
    	
    	final AtomicDec kernel = new AtomicDec(in, out);
    	try {
	    	final Range range = device.createRange(1,1);
	    	kernel.execute(range);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[0] - 1, out[1]);
    }

    /**
     * Kernel for single threaded validation of atomicDec.
     * Validates that a decrement operation is actually performed.
     * @author lpnm
     *
     */
    private static final class AtomicDec extends Kernel {
    	private int in[];
    	private int out[];
    	
    	public AtomicDec(int[] in, int out[]) {
    		this.in = in;
    		this.out = out;
    		atomicValues = new AtomicInteger[2];
    		atomicValues[0] = new AtomicInteger(0);
    		atomicValues[1] = new AtomicInteger(0);
    	}

    	@Local
    	private AtomicInteger atomicValues[];
    	
		@Override
		public void run() {
			atomicSet(atomicValues[0], in[0]);
			out[0] = atomicDec(atomicValues[0]);
			out[1] = atomicGet(atomicValues[0]);
		}

    }

    @Test
    public void issue81AtomicCmpXchg1OpenCLExplicit() {
    	Device openCLDevice = KernelManager.instance().bestDevice();
    	final int in[] = new int[3];
    	final int[] out = new int[2];
    	in[0] = 50;
    	in[1] = 50;
    	in[2] = 100;
    	
    	final AtomicCmpXchg kernel = new AtomicCmpXchg(in, out);
    	try {
	    	final Range range = openCLDevice.createRange(1,1);
	        kernel.setExplicit(true);
	        kernel.put(in);
	        kernel.execute(range);
	        kernel.get(out);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[2], out[1]);
    }

    @Test
    public void issue81AtomicCmpXchg1OpenCL() {
    	Device openCLDevice = KernelManager.instance().bestDevice();
    	final int in[] = new int[3];
    	final int[] out = new int[2];
    	in[0] = 50;
    	in[1] = 50;
    	in[2] = 100;
    	
    	final AtomicCmpXchg kernel = new AtomicCmpXchg(in, out);
    	try {
	    	final Range range = openCLDevice.createRange(1,1);
	        kernel.execute(range);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[2], out[1]);
    }

    @Test
    public void issue81AtomicCmpXchg1JTP() {
    	KernelManager.setKernelManager(new JTPKernelManager());
    	Device device = KernelManager.instance().bestDevice();
    	final int in[] = new int[3];
    	final int[] out = new int[2];
    	in[0] = 50;
    	in[1] = 50;
    	in[2] = 100;
    	
    	final AtomicCmpXchg kernel = new AtomicCmpXchg(in, out);
    	try {
	    	final Range range = device.createRange(1,1);
	    	kernel.execute(range);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[2], out[1]);
    }

    @Test
    public void issue81AtomicCmpXchg2OpenCLExplicit() {
    	Device openCLDevice = KernelManager.instance().bestDevice();
    	final int in[] = new int[3];
    	final int[] out = new int[2];
    	in[0] = 50;
    	in[1] = 51;
    	in[2] = 100;
    	
    	final AtomicCmpXchg kernel = new AtomicCmpXchg(in, out);
    	try {
	    	final Range range = openCLDevice.createRange(1,1);
	        kernel.setExplicit(true);
	        kernel.put(in);
	        kernel.execute(range);
	        kernel.get(out);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[0], out[1]);
    }

    @Test
    public void issue81AtomicCmpXchg2OpenCL() {
    	Device openCLDevice = KernelManager.instance().bestDevice();
    	final int in[] = new int[3];
    	final int[] out = new int[2];
    	in[0] = 50;
    	in[1] = 51;
    	in[2] = 100;
    	
    	final AtomicCmpXchg kernel = new AtomicCmpXchg(in, out);
    	try {
	    	final Range range = openCLDevice.createRange(1,1);
	        kernel.execute(range);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[0], out[1]);
    }

    @Test
    public void issue81AtomicCmpXchg2JTP() {
    	KernelManager.setKernelManager(new JTPKernelManager());
    	Device device = KernelManager.instance().bestDevice();
    	final int in[] = new int[3];
    	final int[] out = new int[2];
    	in[0] = 50;
    	in[1] = 51;
    	in[2] = 100;
    	
    	final AtomicCmpXchg kernel = new AtomicCmpXchg(in, out);
    	try {
	    	final Range range = device.createRange(1,1);
	    	kernel.execute(range);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[0], out[1]);
    }

    /**
     * Kernel for single threaded validation of atomicCmpXchg.
     * Validates that a cmpXchg operation is actually performed.
     * @author lpnm
     *
     */
    private static final class AtomicCmpXchg extends Kernel {
    	private int in[];
    	private int out[];
    	
    	public AtomicCmpXchg(int[] in, int out[]) {
    		this.in = in;
    		this.out = out;
    		atomicValues = new AtomicInteger[2];
    		atomicValues[0] = new AtomicInteger(0);
    		atomicValues[1] = new AtomicInteger(0);
    	}

    	@Local
    	private AtomicInteger atomicValues[];
    	
		@Override
		public void run() {
			atomicSet(atomicValues[0], in[0]);
			out[0] = atomicCmpXchg(atomicValues[0], in[1], in[2]);
			out[1] = atomicGet(atomicValues[0]);
		}

    }

    @Test
    public void issue81AtomicMin1OpenCLExplicit() {
    	Device openCLDevice = KernelManager.instance().bestDevice();
    	final int in[] = new int[2];
    	final int[] out = new int[2];
    	in[0] = 50;
    	in[1] = 49;
    	
    	final AtomicMin kernel = new AtomicMin(in, out);
    	try {
	    	final Range range = openCLDevice.createRange(1,1);
	        kernel.setExplicit(true);
	        kernel.put(in);
	        kernel.execute(range);
	        kernel.get(out);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[1], out[1]);
    }

    @Test
    public void issue81AtomicMin1OpenCL() {
    	Device openCLDevice = KernelManager.instance().bestDevice();
    	final int in[] = new int[2];
    	final int[] out = new int[2];
    	in[0] = 50;
    	in[1] = 49;
    	
    	final AtomicMin kernel = new AtomicMin(in, out);
    	try {
	    	final Range range = openCLDevice.createRange(1,1);
	        kernel.execute(range);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[1], out[1]);
    }

    @Test
    public void issue81AtomicMin1JTP() {
    	KernelManager.setKernelManager(new JTPKernelManager());
    	Device device = KernelManager.instance().bestDevice();
    	final int in[] = new int[2];
    	final int[] out = new int[2];
    	in[0] = 50;
    	in[1] = 49;
    	
    	final AtomicMin kernel = new AtomicMin(in, out);
    	try {
	    	final Range range = device.createRange(1,1);
	    	kernel.execute(range);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[1], out[1]);
    }

    @Test
    public void issue81AtomicMin2OpenCLExplicit() {
    	Device openCLDevice = KernelManager.instance().bestDevice();
    	final int in[] = new int[2];
    	final int[] out = new int[2];
    	in[0] = 50;
    	in[1] = 51;
    	
    	final AtomicMin kernel = new AtomicMin(in, out);
    	try {
	    	final Range range = openCLDevice.createRange(1,1);
	        kernel.setExplicit(true);
	        kernel.put(in);
	        kernel.execute(range);
	        kernel.get(out);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[0], out[1]);
    }

    @Test
    public void issue81AtomicMin2OpenCL() {
    	Device openCLDevice = KernelManager.instance().bestDevice();
    	final int in[] = new int[2];
    	final int[] out = new int[2];
    	in[0] = 50;
    	in[1] = 51;
    	
    	final AtomicMin kernel = new AtomicMin(in, out);
    	try {
	    	final Range range = openCLDevice.createRange(1,1);
	        kernel.execute(range);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[0], out[1]);
    }

    @Test
    public void issue81AtomicMin2JTP() {
    	KernelManager.setKernelManager(new JTPKernelManager());
    	Device device = KernelManager.instance().bestDevice();
    	final int in[] = new int[2];
    	final int[] out = new int[2];
    	in[0] = 50;
    	in[1] = 51;
    	
    	final AtomicMin kernel = new AtomicMin(in, out);
    	try {
	    	final Range range = device.createRange(1,1);
	    	kernel.execute(range);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[0], out[1]);
    }

    /**
     * Kernel for single threaded validation of atomicMin.
     * Validates that a min operation is actually performed.
     * @author lpnm
     *
     */
    private static final class AtomicMin extends Kernel {
    	private int in[];
    	private int out[];
    	
    	public AtomicMin(int[] in, int out[]) {
    		this.in = in;
    		this.out = out;
    		atomicValues = new AtomicInteger[2];
    		atomicValues[0] = new AtomicInteger(0);
    		atomicValues[1] = new AtomicInteger(0);
    	}

    	@Local
    	private AtomicInteger atomicValues[];
    	
		@Override
		public void run() {
			atomicSet(atomicValues[0], in[0]);
			out[0] = atomicMin(atomicValues[0], in[1]);
			out[1] = atomicGet(atomicValues[0]);			
		}

    }

    @Test
    public void issue81AtomicMax1OpenCLExplicit() {
    	Device openCLDevice = KernelManager.instance().bestDevice();
    	final int in[] = new int[2];
    	final int[] out = new int[2];
    	in[0] = 50;
    	in[1] = 51;
    	
    	final AtomicMax kernel = new AtomicMax(in, out);
    	try {
	    	final Range range = openCLDevice.createRange(1,1);
	        kernel.setExplicit(true);
	        kernel.put(in);
	        kernel.execute(range);
	        kernel.get(out);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[1], out[1]);
    }

    @Test
    public void issue81AtomicMax1OpenCL() {
    	Device openCLDevice = KernelManager.instance().bestDevice();
    	final int in[] = new int[2];
    	final int[] out = new int[2];
    	in[0] = 50;
    	in[1] = 51;
    	
    	final AtomicMax kernel = new AtomicMax(in, out);
    	try {
	    	final Range range = openCLDevice.createRange(1,1);
	        kernel.execute(range);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[1], out[1]);
    }
    
    @Test
    public void issue81AtomicMax1JTP() {
    	KernelManager.setKernelManager(new JTPKernelManager());
    	Device device = KernelManager.instance().bestDevice();
    	final int in[] = new int[2];
    	final int[] out = new int[2];
    	in[0] = 50;
    	in[1] = 51;
    	
    	final AtomicMax kernel = new AtomicMax(in, out);
    	try {
	    	final Range range = device.createRange(1,1);
	    	kernel.execute(range);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[1], out[1]);
    }

    @Test
    public void issue81AtomicMax2OpenCLExplicit() {
    	Device openCLDevice = KernelManager.instance().bestDevice();
    	final int in[] = new int[2];
    	final int[] out = new int[2];
    	in[0] = 50;
    	in[1] = 49;
    	
    	final AtomicMax kernel = new AtomicMax(in, out);
    	try {
	    	final Range range = openCLDevice.createRange(1,1);
	        kernel.setExplicit(true);
	        kernel.put(in);
	        kernel.execute(range);
	        kernel.get(out);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[0], out[1]);
    }

    @Test
    public void issue81AtomicMax2OpenCL() {
    	Device openCLDevice = KernelManager.instance().bestDevice();
    	final int in[] = new int[2];
    	final int[] out = new int[2];
    	in[0] = 50;
    	in[1] = 49;
    	
    	final AtomicMax kernel = new AtomicMax(in, out);
    	try {
	    	final Range range = openCLDevice.createRange(1,1);
	        kernel.execute(range);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[0], out[1]);
    }

    @Test
    public void issue81AtomicMax2JTP() {
    	KernelManager.setKernelManager(new JTPKernelManager());
    	Device device = KernelManager.instance().bestDevice();
    	final int in[] = new int[2];
    	final int[] out = new int[2];
    	in[0] = 50;
    	in[1] = 49;
    	
    	final AtomicMax kernel = new AtomicMax(in, out);
    	try {
	    	final Range range = device.createRange(1,1);
	    	kernel.execute(range);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", in[0], out[1]);
    }

    /**
     * Kernel for single threaded validation of atomicMax.
     * Validates that a max operation is actually performed.
     * @author lpnm
     *
     */
    private static final class AtomicMax extends Kernel {
    	private int in[];
    	private int out[];
    	
    	public AtomicMax(int[] in, int out[]) {
    		this.in = in;
    		this.out = out;
    		atomicValues = new AtomicInteger[2];
    		atomicValues[0] = new AtomicInteger(0);
    		atomicValues[1] = new AtomicInteger(0);
    	}

    	@Local
    	private AtomicInteger atomicValues[];
    	
		@Override
		public void run() {
			atomicSet(atomicValues[0], in[0]);
			out[0] = atomicMax(atomicValues[0], in[1]);
			out[1] = atomicGet(atomicValues[0]);
		}

    }

    @Test
    public void issue81AtomicAndOpenCLExplicit() {
    	Device openCLDevice = KernelManager.instance().bestDevice();
    	final int in[] = new int[2];
    	final int[] out = new int[2];
    	in[0] = 0xf1;
    	in[1] = 0x8f;
    	
    	final AtomicAnd kernel = new AtomicAnd(in, out);
    	try {
	    	final Range range = openCLDevice.createRange(1,1);
	        kernel.setExplicit(true);
	        kernel.put(in);
	        kernel.execute(range);
	        kernel.get(out);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", 0x81, out[1]);
    }

    @Test
    public void issue81AtomicAndOpenCL() {
    	Device openCLDevice = KernelManager.instance().bestDevice();
    	final int in[] = new int[2];
    	final int[] out = new int[2];
    	in[0] = 0xf1;
    	in[1] = 0x8f;
    	
    	final AtomicAnd kernel = new AtomicAnd(in, out);
    	try {
	    	final Range range = openCLDevice.createRange(1,1);
	        kernel.execute(range);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", 0x81, out[1]);
    }
    
    @Test
    public void issue81AtomicAndJTP() {
    	KernelManager.setKernelManager(new JTPKernelManager());
    	Device device = KernelManager.instance().bestDevice();
    	final int in[] = new int[2];
    	final int[] out = new int[2];
    	in[0] = 0xf1;
    	in[1] = 0x8f;
    	
    	final AtomicAnd kernel = new AtomicAnd(in, out);
    	try {
	    	final Range range = device.createRange(1,1);
	    	kernel.execute(range);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", 0x81, out[1]);
    }
    
    /**
     * Kernel for single threaded validation of atomicXor.
     * Validates that an and operation is actually performed.
     * @author lpnm
     *
     */
    private static final class AtomicAnd extends Kernel {
    	private int in[];
    	private int out[];
    	
    	public AtomicAnd(int[] in, int out[]) {
    		this.in = in;
    		this.out = out;
    		atomicValues = new AtomicInteger[2];
    		atomicValues[0] = new AtomicInteger(0);
    		atomicValues[1] = new AtomicInteger(0);
    	}

    	@Local
    	private AtomicInteger atomicValues[];
    	
		@Override
		public void run() {
			atomicSet(atomicValues[0], in[0]);
			out[0] = atomicAnd(atomicValues[0], in[1]);
			out[1] = atomicGet(atomicValues[0]);
		}

    }

    @Test
    public void issue81AtomicOrOpenCLExplicit() {
    	Device openCLDevice = KernelManager.instance().bestDevice();
    	final int in[] = new int[2];
    	final int[] out = new int[2];
    	in[0] = 0x80;
    	in[1] = 0x02;
    	
    	final AtomicOr kernel = new AtomicOr(in, out);
    	try {
	    	final Range range = openCLDevice.createRange(1,1);
	        kernel.setExplicit(true);
	        kernel.put(in);
	        kernel.execute(range);
	        kernel.get(out);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", 0x82, out[1]);
    }

    @Test
    public void issue81AtomicOrOpenCL() {
    	Device openCLDevice = KernelManager.instance().bestDevice();
    	final int in[] = new int[2];
    	final int[] out = new int[2];
    	in[0] = 0x80;
    	in[1] = 0x02;
    	
    	final AtomicOr kernel = new AtomicOr(in, out);
    	try {
	    	final Range range = openCLDevice.createRange(1,1);
	        kernel.execute(range);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", 0x82, out[1]);
    }

    @Test
    public void issue81AtomicOrJTP() {
    	KernelManager.setKernelManager(new JTPKernelManager());
    	Device device = KernelManager.instance().bestDevice();
    	final int in[] = new int[2];
    	final int[] out = new int[2];
    	in[0] = 0x80;
    	in[1] = 0x02;
    	
    	final AtomicOr kernel = new AtomicOr(in, out);
    	try {
	    	final Range range = device.createRange(1,1);
	    	kernel.execute(range);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", 0x82, out[1]);
    }
    
    /**
     * Kernel for single threaded validation of atomicOr.
     * Validates that an or operation is actually performed.
     * @author lpnm
     *
     */
    private static final class AtomicOr extends Kernel {
    	private int in[];
    	private int out[];
    	
    	public AtomicOr(int[] in, int out[]) {
    		this.in = in;
    		this.out = out;
    		atomicValues = new AtomicInteger[2];
    		atomicValues[0] = new AtomicInteger(0);
    		atomicValues[1] = new AtomicInteger(0);
    	}

    	@Local
    	private AtomicInteger atomicValues[];
    	
		@Override
		public void run() {
			atomicSet(atomicValues[0], in[0]);
			out[0] = atomicOr(atomicValues[0], in[1]);
			out[1] = atomicGet(atomicValues[0]);
		}

    }

    @Test
    public void issue81AtomicXorOpenCLExplicit() {
    	Device openCLDevice = KernelManager.instance().bestDevice();
    	final int in[] = new int[2];
    	final int[] out = new int[2];
    	in[0] = 0xf1;
    	in[1] = 0x8f;
    	
    	final AtomicXor kernel = new AtomicXor(in, out);
    	try {
	    	final Range range = openCLDevice.createRange(1,1);
	        kernel.setExplicit(true);
	        kernel.put(in);
	        kernel.execute(range);
	        kernel.get(out);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", 0x7e, out[1]);
    }

    @Test
    public void issue81AtomicXorOpenCL() {
    	Device openCLDevice = KernelManager.instance().bestDevice();
    	final int in[] = new int[2];
    	final int[] out = new int[2];
    	in[0] = 0xf1;
    	in[1] = 0x8f;
    	
    	final AtomicXor kernel = new AtomicXor(in, out);
    	try {
	    	final Range range = openCLDevice.createRange(1,1);
	        kernel.execute(range);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", 0x7e, out[1]);
    }
    
    @Test
    public void issue81AtomicXorJTP() {
    	KernelManager.setKernelManager(new JTPKernelManager());
    	Device device = KernelManager.instance().bestDevice();
    	final int in[] = new int[2];
    	final int[] out = new int[2];
    	in[0] = 0xf1;
    	in[1] = 0x8f;
    	
    	final AtomicXor kernel = new AtomicXor(in, out);
    	try {
	    	final Range range = device.createRange(1,1);
	    	kernel.execute(range);
    	} finally {
    		kernel.dispose();
    	}
    	assertEquals("Old value doesn't match", in[0], out[0]);
    	assertEquals("Final value doesn't match", 0x7e, out[1]);
    }
    
    /**
     * Kernel for single threaded validation of atomicXor.
     * Validates that a xor operation is actually performed.
     * @author lpnm
     *
     */
    private static final class AtomicXor extends Kernel {
    	private int in[];
    	private int out[];
    	
    	public AtomicXor(int[] in, int out[]) {
    		this.in = in;
    		this.out = out;
    		atomicValues = new AtomicInteger[2];
    		atomicValues[0] = new AtomicInteger(0);
    		atomicValues[1] = new AtomicInteger(0);
    	}

    	@Local
    	private AtomicInteger atomicValues[];
    	
		@Override
		public void run() {
			atomicSet(atomicValues[0], in[0]);
			out[0] = atomicXor(atomicValues[0], in[1]);
			out[1] = atomicGet(atomicValues[0]);
		}
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
        	if (maxValue == localMaxVal) {
        		//Only the threads that have the max value will reach this point, however the max value, may
        		//occur at multiple indices of the input array.
        		if (atomicXchg(maxs[LOCK_IDX], 0xff) == 0) {
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
        	if (maxValue == localMaxVal) {
        		//Only the threads that have the max value will reach this point, however the max value, may
        		//occur at multiple indices of the input array.
        		if (atomicXchg(maxs[LOCK_IDX], 0xff) == 0) {
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

}
