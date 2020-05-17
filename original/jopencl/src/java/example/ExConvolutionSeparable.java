package example;

import java.io.File;
import java.io.IOException;

import javax.opencl.*;

/**
 * Convolve image using separable kernel
 * 
 * @author Johan Henriksson
 *
 */
public class ExConvolutionSeparable
	{
	private static int KERNEL_RADIUS = 8;
	private static int KERNEL_RADIUS_ALIGNED = 16;
	private static int KERNEL_LENGTH = 2 * KERNEL_RADIUS + 1;

	public static void main(String[] args)
		{
		
		try
			{
			int imageW = 2048;
			int imageH = 2048;
			
			float[] hKernel=new float[KERNEL_LENGTH];
			float[] hInput=new float[imageW*imageH];
			
			//CLContext clc=OpenCL.createContext(OpenCL.CL_DEVICE_TYPE_GPU);
			CLContext clc=OpenCL.createContext(OpenCL.CL_DEVICE_TYPE_CPU);
			CLDevice device=Common.getFastestDevice(clc);
			CLCommandQueue cq=clc.createCommandQueue(device); //should this be under device?
			
			//Allocate GPU memory
			CLMem dKernel=clc.createBuffer(OpenCL.CL_MEM_READ_ONLY | OpenCL.CL_MEM_COPY_HOST_PTR, hKernel);
			CLMem dInput=clc.createBuffer(OpenCL.CL_MEM_READ_ONLY | OpenCL.CL_MEM_COPY_HOST_PTR, hInput);
			CLMem dBuffer=clc.createBuffer(OpenCL.CL_MEM_READ_WRITE, Float.class, imageW * imageH);
			CLMem dOutput=clc.createBuffer(OpenCL.CL_MEM_WRITE_ONLY, Float.class, imageW * imageH);

			//Program
			CLProgram prog=clc.createProgram(Common.readFile(new File("example/ConvolutionSeparable.cl")));
			prog.build();
			CLKernel kernelRows=prog.createKernel("convolutionRows");
			CLKernel kernelColumns=prog.createKernel("convolutionColumns");
			
			//Convolve
			convolveRows(cq, kernelRows, dBuffer, dInput, dKernel, imageH, imageW);
			convolveColumns(cq, kernelColumns, dBuffer, dInput, dKernel, imageH, imageW);

			float[] hOutput=new float[imageW*imageH];
			cq.enqueueReadBuffer(dOutput, true, 0, imageW*imageH, hOutput, null);
			
			//Cleanup
			dOutput.release();
			dBuffer.release();
			dInput.release();
			dKernel.release();
			kernelColumns.release();
			kernelRows.release();
			prog.release();
			cq.release();
			clc.release();
			}
		catch (IOException e)
			{
			e.printStackTrace();
			}
		
		
		}
	
	
	public static int iDivUp(int dividend, int divisor)
		{
		return (dividend % divisor == 0) ? (dividend / divisor) : (dividend / divisor + 1);
		}

	public static void convolveRows(CLCommandQueue cq, CLKernel kernelRows, CLMem dDst, CLMem dSrc, CLMem dKernel, int imageH, int imageW)
		{
    int ROWS_OUTPUT_WIDTH = 128;
    
    kernelRows.setKernelArg(0, dDst);
    kernelRows.setKernelArg(1, dSrc);
    kernelRows.setKernelArg(2, dKernel);
    kernelRows.setKernelArg(3, Float.class, KERNEL_RADIUS_ALIGNED + ROWS_OUTPUT_WIDTH + KERNEL_RADIUS);
    kernelRows.setKernelArg(4, imageH);
    kernelRows.setKernelArg(5, imageW);
    
    int[] localWorkSize = new int[]{KERNEL_RADIUS_ALIGNED + ROWS_OUTPUT_WIDTH + KERNEL_RADIUS, 1};
    int[] globalWorkSize = new int[]{iDivUp(imageW, ROWS_OUTPUT_WIDTH) * localWorkSize[0], imageH};

    cq.enqueueNDRangeKernel(kernelRows, 2, null, globalWorkSize, localWorkSize, null);
		}
	
	
	public static void convolveColumns(CLCommandQueue cq, CLKernel kernelColumns, CLMem dDst, CLMem dSrc, CLMem dKernel, int imageH, int imageW)
		{
    int COLUMNS_BLOCKDIMX = 16;
    int COLUMNS_BLOCKDIMY = 16;
    int COLUMNS_OUTPUT_HEIGHT = 128;
    
    kernelColumns.setKernelArg(0, dDst);
    kernelColumns.setKernelArg(1, dSrc);
    kernelColumns.setKernelArg(2, dKernel);
    kernelColumns.setKernelArg(3, Float.class, (KERNEL_RADIUS + COLUMNS_OUTPUT_HEIGHT + KERNEL_RADIUS) * COLUMNS_BLOCKDIMX);
    kernelColumns.setKernelArg(4, imageH);
    kernelColumns.setKernelArg(5, imageW);
    
    int[] localWorkSize = new int[]{COLUMNS_BLOCKDIMX, COLUMNS_BLOCKDIMY};
    int[] globalWorkSize = new int[]{imageW, iDivUp(imageH, COLUMNS_OUTPUT_HEIGHT) * localWorkSize[1]};

    cq.enqueueNDRangeKernel(kernelColumns, 2, null, globalWorkSize, localWorkSize, null);
		}
	

	}
