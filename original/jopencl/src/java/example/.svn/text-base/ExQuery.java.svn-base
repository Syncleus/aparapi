package example;


import javax.opencl.*;

/**
 * Example: querying devices
 * 
 * @author Johan Henriksson
 *
 */
public class ExQuery
	{
	
	
	public static void main(String[] args)
		{
	
		//CLContext clc=OpenCL.createContext(OpenCL.CL_DEVICE_TYPE_GPU);		
		CLContext clc=OpenCL.createContext(OpenCL.CL_DEVICE_TYPE_CPU);		
	
		// Then we can get the list of GPU devices associated with this context
		CLDevice[] devices=clc.getContextDevices();
	
		for(CLDevice dev:devices)
			{
			System.out.println("device "+dev);
			System.out.println("  dev name "+dev.getDeviceName());
			}
		
		for(CLPlatform pl:OpenCL.getPlatforms())
			{
			System.out.println("platform: "+pl);
			System.out.println("  vendor: "+pl.getVendor());
			}
		
		}
	
	}
