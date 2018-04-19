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
 * Base tests for validation of the correctness of the atomics function computations, both on Java and on OpenCL.
 * @author CodeRasurae
 */
public class AtomicsSupportTest {

    private static OpenCLDevice openCLDevice = null;

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
    public void issue81AtomicAddOpenCLExplicit() {
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

    	@Local
    	private AtomicInteger atomicValues[];
    	
    	public AtomicAdd(int[] in, int out[]) {
    		this.in = in;
    		this.out = out;
    		atomicValues = new AtomicInteger[2];
    		atomicValues[0] = new AtomicInteger(0);
    		atomicValues[1] = new AtomicInteger(0);
    	}
    	
		@Override
		public void run() {
			atomicSet(atomicValues[0], in[0]);
			out[0] = atomicAdd(atomicValues[0], in[1]);
			out[1] = atomicGet(atomicValues[0]);
		}
    }

    @Test
    public void issue81AtomicSubOpenCLExplicit() {
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

    	@Local
    	private AtomicInteger atomicValues[];

    	public AtomicSub(int[] in, int out[]) {
    		this.in = in;
    		this.out = out;
    		atomicValues = new AtomicInteger[2];
    		atomicValues[0] = new AtomicInteger(0);
    		atomicValues[1] = new AtomicInteger(0);
    	}
    	
		@Override
		public void run() {
			atomicSet(atomicValues[0], in[0]);
			out[0] = atomicSub(atomicValues[0], in[1]);
			out[1] = atomicGet(atomicValues[0]);			
		}

    }

    @Test
    public void issue81AtomicXchgOpenCLExplicit() {
    	
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

    	@Local
    	private AtomicInteger atomicValues[];

    	public AtomicXchg(int[] in, int out[]) {
    		this.in = in;
    		this.out = out;
    		atomicValues = new AtomicInteger[2];
    		atomicValues[0] = new AtomicInteger(0);
    		atomicValues[1] = new AtomicInteger(0);
    	}
    	
		@Override
		public void run() {
			atomicSet(atomicValues[0], in[0]);
			out[0] = atomicXchg(atomicValues[0], in[1]);
			out[1] = atomicGet(atomicValues[0]);
		}

    }
    
    @Test
    public void issue81AtomicIncOpenCLExplicit() {
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

    	@Local
    	private AtomicInteger atomicValues[];
    	
    	public AtomicInc(int[] in, int out[]) {
    		this.in = in;
    		this.out = out;
    		atomicValues = new AtomicInteger[2];
    		atomicValues[0] = new AtomicInteger(0);
    		atomicValues[1] = new AtomicInteger(0);
    	}
    	
		@Override
		public void run() {
			atomicSet(atomicValues[0], in[0]);
			out[0] = atomicInc(atomicValues[0]);
			out[1] = atomicGet(atomicValues[0]);
		}

    }

    @Test
    public void issue81AtomicDecOpenCLExplicit() {
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

    	@Local
    	private AtomicInteger atomicValues[];

    	public AtomicDec(int[] in, int out[]) {
    		this.in = in;
    		this.out = out;
    		atomicValues = new AtomicInteger[2];
    		atomicValues[0] = new AtomicInteger(0);
    		atomicValues[1] = new AtomicInteger(0);
    	}
    	
		@Override
		public void run() {
			atomicSet(atomicValues[0], in[0]);
			out[0] = atomicDec(atomicValues[0]);
			out[1] = atomicGet(atomicValues[0]);
		}

    }

    @Test
    public void issue81AtomicCmpXchg1OpenCLExplicit() {
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
    	
    	@Local
    	private AtomicInteger atomicValues[];

    	public AtomicCmpXchg(int[] in, int out[]) {
    		this.in = in;
    		this.out = out;
    		atomicValues = new AtomicInteger[2];
    		atomicValues[0] = new AtomicInteger(0);
    		atomicValues[1] = new AtomicInteger(0);
    	}
    	
		@Override
		public void run() {
			atomicSet(atomicValues[0], in[0]);
			out[0] = atomicCmpXchg(atomicValues[0], in[1], in[2]);
			out[1] = atomicGet(atomicValues[0]);
		}

    }

    @Test
    public void issue81AtomicMin1OpenCLExplicit() {
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

    	@Local
    	private AtomicInteger atomicValues[];

    	public AtomicMin(int[] in, int out[]) {
    		this.in = in;
    		this.out = out;
    		atomicValues = new AtomicInteger[2];
    		atomicValues[0] = new AtomicInteger(0);
    		atomicValues[1] = new AtomicInteger(0);
    	}
    	
		@Override
		public void run() {
			atomicSet(atomicValues[0], in[0]);
			out[0] = atomicMin(atomicValues[0], in[1]);
			out[1] = atomicGet(atomicValues[0]);			
		}

    }

    @Test
    public void issue81AtomicMax1OpenCLExplicit() {
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

    	@Local
    	private AtomicInteger atomicValues[];

    	public AtomicMax(int[] in, int out[]) {
    		this.in = in;
    		this.out = out;
    		atomicValues = new AtomicInteger[2];
    		atomicValues[0] = new AtomicInteger(0);
    		atomicValues[1] = new AtomicInteger(0);
    	}
    	
		@Override
		public void run() {
			atomicSet(atomicValues[0], in[0]);
			out[0] = atomicMax(atomicValues[0], in[1]);
			out[1] = atomicGet(atomicValues[0]);
		}

    }

    @Test
    public void issue81AtomicAndOpenCLExplicit() {
    	
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

    	@Local
    	private AtomicInteger atomicValues[];

    	public AtomicAnd(int[] in, int out[]) {
    		this.in = in;
    		this.out = out;
    		atomicValues = new AtomicInteger[2];
    		atomicValues[0] = new AtomicInteger(0);
    		atomicValues[1] = new AtomicInteger(0);
    	}
    	
		@Override
		public void run() {
			atomicSet(atomicValues[0], in[0]);
			out[0] = atomicAnd(atomicValues[0], in[1]);
			out[1] = atomicGet(atomicValues[0]);
		}

    }

    @Test
    public void issue81AtomicOrOpenCLExplicit() {
    	
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

    	@Local
    	private AtomicInteger atomicValues[];

    	public AtomicOr(int[] in, int out[]) {
    		this.in = in;
    		this.out = out;
    		atomicValues = new AtomicInteger[2];
    		atomicValues[0] = new AtomicInteger(0);
    		atomicValues[1] = new AtomicInteger(0);
    	}
    	
		@Override
		public void run() {
			atomicSet(atomicValues[0], in[0]);
			out[0] = atomicOr(atomicValues[0], in[1]);
			out[1] = atomicGet(atomicValues[0]);
		}

    }

    @Test
    public void issue81AtomicXorOpenCLExplicit() {
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

    	@Local
    	private AtomicInteger atomicValues[];

    	public AtomicXor(int[] in, int out[]) {
    		this.in = in;
    		this.out = out;
    		atomicValues = new AtomicInteger[2];
    		atomicValues[0] = new AtomicInteger(0);
    		atomicValues[1] = new AtomicInteger(0);
    	}
    	
		@Override
		public void run() {
			atomicSet(atomicValues[0], in[0]);
			out[0] = atomicXor(atomicValues[0], in[1]);
			out[1] = atomicGet(atomicValues[0]);
		}
    }    
}
