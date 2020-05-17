package javax.opencl;

/**
 * OpenCL memory object
 * 
 * @author Johan Henriksson
 *
 */
public class CLMem extends OpenCL
	{
	int id;
	final CLContext context;
	
	CLMem(CLContext context)
		{
		this.context=context;
		}
	
	static CLMem createBuffer(CLContext context, int memFlags, int[] initData)
		{
		assertNotNull(context);
		CLMem mem=new CLMem(context);
		int ret=mem._createBufferFromInt(context.id, memFlags, initData);
		assertSuccess(ret);
		return mem;
		}

	static CLMem createBuffer(CLContext context, int memFlags, float[] initData)
		{
		assertNotNull(context);
		CLMem mem=new CLMem(context);
		int ret=mem._createBufferFromFloat(context.id, memFlags, initData);
		assertSuccess(ret);
		return mem;
		}
	
	static CLMem createBuffer(CLContext context, int memFlags, Class<?> c, int numElem)
		{
		assertNotNull(context);
		CLMem mem=new CLMem(context);
		int elsize=sizeForType(c);
		int ret=mem._createBuffer(context.id, memFlags, elsize*numElem);
		assertSuccess(ret);
		return mem;
		}
	
	static CLMem createImage2D(CLContext con, int memFlags, CLImageFormat imageFormat, int imageW, int imageH, int imageRowPitch, byte[] buffer)
		{
		assertNotNull(con,buffer,imageFormat);
		CLMem mem=new CLMem(con);
		mem._createImage2Dbyte(con.id, memFlags, imageFormat, imageW, imageH, imageRowPitch, buffer);
		return mem;
		}

	static CLMem createImage2D(CLContext con, int memFlags, CLImageFormat imageFormat, int imageW, int imageH, int imageRowPitch, float[] buffer)
		{
		assertNotNull(con,buffer,imageFormat);
		CLMem mem=new CLMem(con);
		mem._createImage2Dfloat(con.id, memFlags, imageFormat, imageW, imageH, imageRowPitch, buffer);
		return mem;
		}

	static CLMem createImage3D(CLContext con, int memFlags, CLImageFormat imageFormat, int imageW, int imageH, int imageD, int imageRowPitch, int imageSlicePitch, byte[] buffer)
		{
		assertNotNull(con,buffer,imageFormat);
		CLMem mem=new CLMem(con);
		mem._createImage3Dbyte(con.id, memFlags, imageFormat, imageW, imageH, imageD, imageRowPitch, imageSlicePitch, buffer);
		return mem;
		}

	
	
	private native int _createBufferFromInt(int contextID, int memFlags, int[] initData);
	private native int _createBufferFromFloat(int contextID, int memFlags, float[] initData);
	private native int _createBuffer(int contextID, int memFlags, int size);

	private native int _createImage2Dbyte(int cid, int memFlags, CLImageFormat imageFormat, int imageW, int imageH, int imageRowPitch, byte[] buffer);
	private native int _createImage2Dfloat(int cid, int memFlags, CLImageFormat imageFormat, int imageW, int imageH, int imageRowPitch, float[] buffer);
	private native int _createImage3Dbyte(int cid, int memFlags, CLImageFormat imageFormat, int imageW, int imageH, int imageD,
			int imageRowPitch, int imageSlicePitch, byte[] buffer);

/*
                      
extern  cl_int 
clGetSupportedImageFormats(cl_context           context,
                          cl_mem_flags         flags,
                          cl_mem_object_type   image_type,
                          cl_uint              num_entries,
                          cl_image_format *    image_formats,
                          cl_uint *            num_image_formats) ;
                                  

extern  cl_int 
clGetImageInfo(cl_mem           image,
              cl_image_info    param_name, 
              size_t           param_value_size,
              void *           param_value,
              size_t *         param_value_size_ret) ;

*/
	
	
	public int getMemObjectSize()
		{
		return _getSize(id);
		}
	
	private native int _getSize(int mid);
	
	
	public void retain()
		{
		int ret=_retainMem(id);
		assertSuccess(ret);
		}
	
	public void release()
		{
		int ret=_releaseMem(id);
		assertSuccess(ret);
		}
	
	
	
	private native int _retainMem(int mid);
	private native int _releaseMem(int mid);
	
	}
