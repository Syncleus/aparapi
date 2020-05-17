package javax.opencl;

/**
 * OpenCL program
 * @author Johan Henriksson
 *
 */
public class CLProgram extends OpenCL
	{
	int id;
	
	public CLProgram(CLContext context, String source)
		{
		int ret=_createProgram(context.id,source);
		if(ret!=CL_SUCCESS)
			throw new CLException(ret);
		}
	
	
	
	public CLKernel createKernel(String kernelName)
		{
		return new CLKernel(this, kernelName);
		}
	
	/*
  
//Program Object APIs
extern  cl_program 
clCreateProgramWithSource(cl_context        context,
                         cl_uint           count,
                         const char **     strings,
                         const size_t *    lengths,
                         cl_int *          errcode_ret) ;

extern  cl_program 
clCreateProgramWithBinary(cl_context                     context,
                         cl_uint                        num_devices,
                         const cl_device_id *           device_list,
                         const size_t *                 lengths,
                         const unsigned char **         binaries,
                         cl_int *                       binary_status,
                         cl_int *                       errcode_ret) ;
*/
	
	public void build()
		{
		int ret=_build(id);
		assertSuccess(ret);
		}
	
	public void retain()
		{
		int ret=_retain(id);
		assertSuccess(ret);
		}
	
	public void release()
		{
		int ret=_release(id);
		assertSuccess(ret);
		}
	

	private native int _createProgram(int context, String source);
	private native int _retain(int pid);
	private native int _release(int pid);
	private native int _build(int pid);
	
	
	public int getNumDevices()
		{
		return _getNumDevices(id);
		}
	
	private native int _getNumDevices(int pid);
	
	public int getBuildStatus(CLDevice device)
		{
		return _getBuildStatus(id,device.device_id);
		}
	
	private native int _getBuildStatus(int pid, int did);
	
	
	/*

extern  cl_int 
clUnloadCompiler(void) ;


extern  cl_int 
clGetProgramBuildInfo(cl_program            program,
                     cl_device_id          device,
                     cl_program_build_info param_name,
                     size_t                param_value_size,
                     void *                param_value,
                     size_t *              param_value_size_ret) ;
                       
                       
                       
*/

	}
