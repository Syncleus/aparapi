package example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.opencl.CLContext;
import javax.opencl.CLDevice;

public class Common
	{

	/**
	 * Read file into string
	 */
	public static String readFile(File file) throws IOException
		{
		StringBuffer bf=new StringBuffer();
		BufferedReader br=new BufferedReader(new FileReader(file));
		String line;
		while((line=br.readLine())!=null)
			{
			bf.append(line);
			bf.append("\n");
			}
		//TODO: should read file exactly as is. do not use readline!
		return bf.toString();
		}

	
	
	public static int shrRoundUp(int group_size, int global_size) 
		{
		int r = global_size % group_size;
		if(r == 0) 
			return global_size;
		else 
			return global_size + group_size - r;
		}

	
	
	public static CLDevice getFastestDevice(CLContext clc)
		{
		CLDevice[] devs=clc.getContextDevices();
		return devs[0];
		//TODO
		}
	
	/*
  size_t szParmDataBytes;
  cl_device_id* cdDevices;

  // get the list of GPU devices associated with context
  clGetContextInfo(cxGPUContext, CL_CONTEXT_DEVICES, 0, NULL, &szParmDataBytes);
  cdDevices = (cl_device_id*) malloc(szParmDataBytes);
  size_t device_count = szParmDataBytes / sizeof(cl_device_id);

  clGetContextInfo(cxGPUContext, CL_CONTEXT_DEVICES, szParmDataBytes, cdDevices, NULL);

  cl_device_id max_flops_device = cdDevices[0];
int max_flops = 0;

size_t current_device = 0;

  // CL_DEVICE_MAX_COMPUTE_UNITS
  cl_uint compute_units;
  clGetDeviceInfo(cdDevices[current_device], CL_DEVICE_MAX_COMPUTE_UNITS, sizeof(compute_units), &compute_units, NULL);

  // CL_DEVICE_MAX_CLOCK_FREQUENCY
  cl_uint clock_frequency;
  clGetDeviceInfo(cdDevices[current_device], CL_DEVICE_MAX_CLOCK_FREQUENCY, sizeof(clock_frequency), &clock_frequency, NULL);
  
max_flops = compute_units * clock_frequency;
++current_device;

while( current_device < device_count )
{
      // CL_DEVICE_MAX_COMPUTE_UNITS
      cl_uint compute_units;
      clGetDeviceInfo(cdDevices[current_device], CL_DEVICE_MAX_COMPUTE_UNITS, sizeof(compute_units), &compute_units, NULL);

      // CL_DEVICE_MAX_CLOCK_FREQUENCY
      cl_uint clock_frequency;
      clGetDeviceInfo(cdDevices[current_device], CL_DEVICE_MAX_CLOCK_FREQUENCY, sizeof(clock_frequency), &clock_frequency, NULL);
	
      int flops = compute_units * clock_frequency;
	if( flops > max_flops )
	{
		max_flops        = flops;
		max_flops_device = cdDevices[current_device];
	}
	++current_device;
}

  free(cdDevices);

return max_flops_device;
*/
	
	
	
	}
