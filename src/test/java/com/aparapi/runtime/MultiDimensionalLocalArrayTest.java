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

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.device.JavaDevice;
import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.kernel.KernelManager;

/**
 * This class provides tests for Issue #51
 */
public class MultiDimensionalLocalArrayTest
{
	
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

    @After
    public void classTeardown() {
    	Util.resetKernelManager();
    }
    
    @Before
    public void setUpBeforeClass() throws Exception {
    	KernelManager.setKernelManager(new CLKernelManager());
        Device device = KernelManager.instance().bestDevice();
        if (device == null || !(device instanceof OpenCLDevice)) {
        	System.out.println("!!!No OpenCLDevice available for running the integration test");
        }
        assumeTrue (device != null && device instanceof OpenCLDevice);
        openCLDevice = (OpenCLDevice) device;
    }
    
    private Device getDevice() {
    	boolean openCL = true;
    	if (openCL) {
    		return openCLDevice;
    	} else {
        	KernelManager.setKernelManager(new JTPKernelManager());
        	return KernelManager.instance().bestDevice(); 
    	}
    }
    
    @Test
    public void singleDimensionTest()
    {
    	final Device device = getDevice();
        final int SIZE = 16;
        final float[] RESULT = new float[2];
        Kernel kernel = new Kernel()
        {
            @Local final float[] localArray = new float[SIZE*SIZE];

            @Override
            public void run()
            {
                int row = getGlobalId(0);
                int column = getGlobalId(1);
                localArray[row + column*SIZE] = row + column;
                localBarrier();
                float value = 0;
                for (int x = 0; x < SIZE; x++)
                {
                    for (int y = 0; y < SIZE; y++)
                    {
                        value += localArray[x + y*SIZE];
                    }
                }
                RESULT[0] = value;
            }
        };
        try {
        	kernel.execute(Range.create2D(device, SIZE, SIZE, SIZE, SIZE));
        } finally {
        	kernel.dispose();
        }
        assertEquals(3840, RESULT[0], 1E-6F);
    }

    @Test
    public void singleDimensionMultipleExecutionTest()
    {
    	final Device device = getDevice();
        final int SIZE = 16;
        final float[] RESULT = new float[2];
        Kernel kernel = new Kernel()
        {
            @Local final float[] localArray = new float[SIZE*SIZE];

            @Override
            public void run()
            {
                int row = getGlobalId(0);
                int column = getGlobalId(1);
                localArray[row + column*SIZE] = row + column;
                localBarrier();
                float value = 0;
                for (int x = 0; x < SIZE; x++)
                {
                    for (int y = 0; y < SIZE; y++)
                    {
                        value += localArray[x + y*SIZE];
                    }
                }
                RESULT[0] = value;
            }
        };
        try {
        	kernel.execute(Range.create2D(device, SIZE, SIZE, SIZE, SIZE));
        	assertEquals(3840, RESULT[0], 1E-6F);
        	kernel.execute(Range.create2D(device, SIZE, SIZE, SIZE, SIZE));
        	assertEquals(3840, RESULT[0], 1E-6F);
        } finally {
        	kernel.dispose();
        }
    }

    @Test
    public void twoDimensionTest()
    {
    	final Device device = getDevice();
        final int SIZE = 16;
        final float[][] RESULT = new float[2][2];
        Kernel kernel = new Kernel()
        {
            @Local final float[][] localArray = new float[SIZE][SIZE];

            @Override
            public void run()
            {
                int row = getGlobalId(0);
                int column = getGlobalId(1);
                localArray[row][column] = row + column;
                localBarrier();
                float value = 0;
                for (int x = 0; x < SIZE; x++)
                {
                    for (int y = 0; y < SIZE; y++)
                    {
                        value += localArray[x][y];
                    }
                }                
                RESULT[0][0] = value;
            }
        };
        try {
        	kernel.execute(Range.create2D(device, SIZE, SIZE, SIZE, SIZE));
        } finally {
        	kernel.dispose();
        }
        assertEquals(3840, RESULT[0][0], 1E-6F);        
    }
    
    @Test
    public void twoDimensionMultipleExecutionTest()
    {
    	final Device device = getDevice();
        final int SIZE = 16;
        final float[][] RESULT = new float[2][2];
        Kernel kernel = new Kernel()
        {
            @Local final float[][] localArray = new float[SIZE][SIZE];

            @Override
            public void run()
            {
                int row = getGlobalId(0);
                int column = getGlobalId(1);
                localArray[row][column] = row + (float)column;
                localBarrier();
                float value = 0;
                for (int x = 0; x < SIZE; x++)
                {
                    for (int y = 0; y < SIZE; y++)
                    {
                        value += localArray[x][y];
                    }
                }                
                RESULT[0][0] = value;
            }
        };
        
        try {
	        kernel.execute(Range.create2D(device, SIZE, SIZE, SIZE, SIZE));
	        assertEquals(3840, RESULT[0][0], 1E-6F);
	        kernel.execute(Range.create2D(device, SIZE, SIZE, SIZE, SIZE));
	        assertEquals(3840, RESULT[0][0], 1E-6F);
        } finally {
        	kernel.dispose();
        }
    }
    
    private class Resizable1DKernel extends Kernel {
    	private int size;
    	private float[] result;
    	
    	@Local 
        private float[] localArray;

    	@NoCL
    	public void setResult(float[] result) {
    		this.result = result;
    	}
    	
    	@NoCL
        public void setArray(int size, float[] array) {
    		this.size = size;
        	localArray = array;
        }
        
        @Override
        public void run()
        {
            int row = getGlobalId(0);
            int column = getGlobalId(1);
            localArray[row + column*size] = row + column;
            localBarrier();
            float value = 0;
            for (int x = 0; x < size; x++)
            {
                for (int y = 0; y < size; y++)
                {
                    value += localArray[x + y*size];
                }
            }
            result[0] = value;
        }    	
    }

    private class Resizable2DKernel extends Kernel {
    	private int size;
    	private float[] result;
    	
    	@Local 
        private float[][] localArray;

    	@NoCL
    	public void setResult(float[] result) {
    		this.result = result;
    	}
    	
    	@NoCL
        public void setArray(int size, float[][] array) {
    		this.size = size;
        	localArray = array;
        }
        
        @Override
        public void run()
        {
            int row = getGlobalId(0);
            int column = getGlobalId(1);
            localArray[row][column] = row + (float)column;
            localBarrier();
            float value = 0;
            for (int x = 0; x < size; x++)
            {
                for (int y = 0; y < size; y++)
                {
                    value += localArray[x][y];
                }
            }                
            result[0] = value;
        }    	
    }

    @Test
    public void resizableOneDimensionTest()
    {
    	final Device device = getDevice();
        final int SIZE = 8;
        final float[] RESULT = new float[2];
        
        Resizable1DKernel kernel = new Resizable1DKernel();
        try {
        	kernel.setResult(RESULT);
        	kernel.setArray(SIZE, new float[SIZE*SIZE]);
        	kernel.execute(Range.create2D(device, SIZE, SIZE, SIZE, SIZE));
        	assertEquals(448, RESULT[0], 1E-6F);
        	kernel.setArray(2*SIZE, new float[2*SIZE*2*SIZE]);
        	kernel.execute(Range.create2D(device, 2*SIZE, 2*SIZE, 2*SIZE, 2*SIZE));
        	assertTrue("Result is not greater than 448", RESULT[0]>448);
        } finally {
        	kernel.dispose();
        }
        
    }
    
    @Test
    public void resizableTwoDimensionTest()
    {
    	final Device device = getDevice();
        final int SIZE = 8;
        final float[] RESULT = new float[2];
        Resizable2DKernel kernel = new Resizable2DKernel();
        try {
        	kernel.setResult(RESULT);
        	kernel.setArray(SIZE, new float[SIZE][SIZE]);
	        kernel.execute(Range.create2D(device, SIZE, SIZE, SIZE, SIZE));
	        assertEquals(448, RESULT[0], 1E-6F);
	        kernel.setArray(2*SIZE, new float[2*SIZE][2*SIZE]);
	        kernel.execute(Range.create2D(device, 2*SIZE, 2*SIZE, 2*SIZE, 2*SIZE));
	        assertTrue("Result is not greater than 448", RESULT[0]>448);
        } finally {
        	kernel.dispose();
        }
    }
}