package example;

import java.io.File;
import java.io.IOException;

import javax.opencl.CLCommandQueue;
import javax.opencl.CLContext;
import javax.opencl.CLDevice;
import javax.opencl.CLKernel;
import javax.opencl.CLMem;
import javax.opencl.CLProgram;
import javax.opencl.OpenCL;


/**
 * Taken after bla bla
 * 
 * @author Johan Henriksson
 *
 */
public class ExTranspose
	{
	private static int BLOCK_DIM=16;
	
	
	
	public static void main(String[] args)
		{
		
		try
			{
			int size_x = 256;
			int size_y = 4096;

			// size of memory required to store the matrix
			int mem_size = size_x * size_y;

			//CLContext clc=OpenCL.createContext(OpenCL.CL_DEVICE_TYPE_GPU);
			CLContext clc=OpenCL.createContext(OpenCL.CL_DEVICE_TYPE_CPU);
			CLDevice device=Common.getFastestDevice(clc);
			CLCommandQueue cq=clc.createCommandQueue(device); //should this be under device?
			
			// allocate and initalize host memory
			float[] h_idata=new float[size_x*size_y];
			//TODO fill with garbage

			// allocate device memory and copy host to device memory
			CLMem d_idata = clc.createBuffer(OpenCL.CL_MEM_READ_ONLY | OpenCL.CL_MEM_COPY_HOST_PTR, h_idata);
			CLMem d_odata = clc.createBuffer(OpenCL.CL_MEM_WRITE_ONLY, Float.class, mem_size);
			
			CLProgram prog=clc.createProgram(Common.readFile(new File("example/transpose.cl")));
			prog.build();
			CLKernel kernelBad=prog.createKernel("transpose_naive");
			CLKernel kernelGood=prog.createKernel("transpose");
			
			//Kernel arguments: naive
			kernelBad.setKernelArg(0, d_odata);
			kernelBad.setKernelArg(1, d_idata);
			kernelBad.setKernelArg(2, size_x);
			kernelBad.setKernelArg(3, size_y);
			
			//Kernel arguments: optimized
			kernelGood.setKernelArg(0, d_odata);
			kernelGood.setKernelArg(1, d_idata);
			kernelGood.setKernelArg(2, size_x);
			kernelGood.setKernelArg(3, size_y);
//			ciErrNum |= clSetKernelArg(ckKernel, 4, sizeof(float) * BLOCK_DIM * (BLOCK_DIM+1), NULL ); //TODO wtf
			kernelGood.setKernelArg(4, Float.class, BLOCK_DIM * (BLOCK_DIM+1));
			
			// setup execution parameters
			int[] szLocalWorkSize=new int[]{BLOCK_DIM,BLOCK_DIM};
			int[] szGlobalWorkSize=new int[]{Common.shrRoundUp(size_x, BLOCK_DIM),Common.shrRoundUp(size_y, BLOCK_DIM)};
			                               
			// warmup so we don't time driver startup
			cq.enqueueNDRangeKernel(kernelBad, 2, null, szGlobalWorkSize, szLocalWorkSize, null);
			cq.enqueueNDRangeKernel(kernelGood, 2, null, szGlobalWorkSize, szLocalWorkSize, null);

			//Benchmark 1
			int numit=10;
			long startTime=System.currentTimeMillis();
			for(int i=0;i<numit;++i)
				cq.enqueueNDRangeKernel(kernelBad, 2, null, szGlobalWorkSize, szLocalWorkSize, null);
			long durBad=(System.currentTimeMillis()-startTime)/numit;
			
			//Benchmark 2
			startTime=System.currentTimeMillis();
			for(int i=0;i<numit;++i)
				cq.enqueueNDRangeKernel(kernelGood, 2, null, szGlobalWorkSize, szLocalWorkSize, null);
			long durGood=(System.currentTimeMillis()-startTime)/numit;

			cq.finish();
			
			System.out.println("Naive time "+durBad);
			System.out.println("Optimized time "+durGood);
			
			// copy result from device to host
			float[] h_odata=new float[size_x*size_y];
			cq.enqueueReadBuffer(d_odata, true, 0, mem_size, h_odata, null);
			
			// cleanup OpenCL
			d_idata.release();
			d_odata.release();
			kernelGood.release();
			kernelBad.release();
			prog.release();
			cq.release();
			clc.release();
			
			}
		catch (IOException e)
			{
			e.printStackTrace();
			}
		
		
		
		
		
		}
	}
