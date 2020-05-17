package javax.opencl;

/**
 * OpenCL sampler
 * @author Johan Henriksson
 *
 */
public class CLSampler extends OpenCL
	{
	int id;
	
	CLSampler(CLContext con, boolean normalized, int adressingMode, int filterMode)
		{
		int ret=_createSampler(con.id, normalized, adressingMode, filterMode);
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
	
	
	
	private native int _createSampler(int cid, boolean normalized, int adressingMode, int filterMode);
	private native int _retain(int mid);
	private native int _release(int mid);
	
	/*
extern  cl_int 
clGetSamplerInfo(cl_sampler         sampler,
                 cl_sampler_info    param_name,
                 size_t             param_value_size,
                 void *             param_value,
                 size_t *           param_value_size_ret) ;
	 */
	}
