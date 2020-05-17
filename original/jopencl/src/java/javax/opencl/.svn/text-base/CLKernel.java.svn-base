package javax.opencl;


/**
 * OpenCL Kernel
 * @author Johan Henriksson
 *
 */
public class CLKernel extends OpenCL
	{
	int id;
	
	/**
	 * Can only be created through Program
	 */
	CLKernel(CLProgram p, String kernelName)
		{
		int ret=_createKernel(p.id, kernelName);
		assertSuccess(ret);
		}
	
	
	public void retain()
		{
		int ret=_retainKernel(id);
		assertSuccess(ret);
		}
	
	public void release()
		{
		int ret=_releaseKernel(id);
		assertSuccess(ret);
		}
	

	public void setKernelArg(int index, CLMem mem)
		{
		int ret=_setKernelArg4(id, index, mem.id);
		assertSuccess(ret);
		}

	public void setKernelArg(int index, int i)
		{
		int ret=_setKernelArg4(id, index, i);
		assertSuccess(ret);
		}
   public void setKernelArg(int index, float f)
     {
     int ret=_setKernelArgFloat4(id, index, f);
     assertSuccess(ret);
     }
	public <E> void setKernelArg(int index, Class<E> cls, int numElem)
		{
		int s=sizeForType(cls);
		int ret=_setKernelArgNull(id, index, s*numElem);
		assertSuccess(ret);
		}

	
	public int getKernelNumArgs()
		{
		return _getKernelNumArgs(id);
		}

	public int getRefCount()
		{
		return _getRefCount(id);
		}
	
	

	private native int _createKernel(int pid, String kernelName);
	private native int _retainKernel(int kid);
	private native int _releaseKernel(int kid);
	private native int _setKernelArg4(int kid, int index, int value);
	private native int _setKernelArgFloat4(int kid, int index, float value);
	private native int _setKernelArgNull(int kid, int index, int size);
	private native int _getKernelNumArgs(int kid);
	private native int _getRefCount(int kid);
	
	
	
	/*

extern  cl_int 
clGetKernelInfo(cl_kernel       kernel,
               cl_kernel_info  param_name,
               size_t          param_value_size,
               void *          param_value,
               size_t *        param_value_size_ret) ;

extern  cl_int 
clGetKernelWorkGroupInfo(cl_kernel                  kernel,
                        cl_device_id               device,
                        cl_kernel_work_group_info  param_name,
                        size_t                     param_value_size,
                        void *                     param_value,
                        size_t *                   param_value_size_ret) ;

*/
	
	
	
	}
