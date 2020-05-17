package javax.opencl;

/**
 * OpenCL platform
 * @author Johan Henriksson
 *
 */
public class CLPlatform extends OpenCL
	{
	int id;
	
	CLPlatform(int id)
		{
		this.id=id;
		}
	
	
	public String getProfile()
		{
		return _getPlatformInfoString(id, CL_PLATFORM_PROFILE);
		}
	
	public String getVersion()
		{
		return _getPlatformInfoString(id, CL_PLATFORM_VERSION);
		}
	
	public String getName()
		{
		return _getPlatformInfoString(id, CL_PLATFORM_NAME);
		}
	
	public String getVendor()
		{
		return _getPlatformInfoString(id, CL_PLATFORM_VENDOR);
		}
	
	public String getExtensions()
		{
		return _getPlatformInfoString(id, CL_PLATFORM_EXTENSIONS);
		}
	
	private native String _getPlatformInfoString(int plid, int param_name);
	
	
	/*

//Device APIs
extern  cl_int 
clGetDeviceIDs(cl_platform_id   platform,
              cl_device_type   device_type, 
              cl_uint          num_entries, 
              cl_device_id *   devices, 
              cl_uint *        num_devices) ;

*/

	
	}
