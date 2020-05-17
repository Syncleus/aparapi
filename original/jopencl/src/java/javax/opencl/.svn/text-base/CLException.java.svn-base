package javax.opencl;

/**
 * OpenCL Exception
 * @author Johan Henriksson
 *
 */
public class CLException extends RuntimeException
	{
	private static final long serialVersionUID = 1L;
	
	private final int ret;
	
	private String msg;
	
	
  public final static int CL_DEVICE_NOT_FOUND = -1;
  public final static int CL_DEVICE_NOT_AVAILABLE = -2;
  public final static int CL_DEVICE_COMPILER_NOT_AVAILABLE = -3;

  public final static int CL_MEM_OBJECT_ALLOCATION_FAILURE = -4;
  public final static int CL_OUT_OF_RESOURCES = -5;
  public final static int CL_OUT_OF_HOST_MEMORY = -6;
  public final static int CL_PROFILING_INFO_NOT_AVAILABLE = -7;
  public final static int CL_MEM_COPY_OVERLAP = -8;
  public final static int CL_IMAGE_FORMAT_MISMATCH = -9;
  public final static int CL_IMAGE_FORMAT_NOT_SUPPORTED = -10;
  public final static int CL_BUILD_PROGRAM_FAILURE = -11;
  public final static int CL_MAP_FAILURE = -12;
	
  public final static int CL_INVALID_VALUE = -30;
  public final static int CL_INVALID_DEVICE_TYPE = -31;
  public final static int CL_INVALID_PLATFORM = -32;
  public final static int CL_INVALID_DEVICE = -33;
  public final static int CL_INVALID_CONTEXT = -34;
  public final static int CL_INVALID_QUEUE_PROPERTIES = -35;
  public final static int CL_INVALID_COMMAND_QUEUE = -36;
  public final static int CL_INVALID_HOST_PTR = -37;
  public final static int CL_INVALID_MEM_OBJECT = -38;
  public final static int CL_INVALID_IMAGE_FORMAT_DESCRIPTOR = -39;
  public final static int CL_INVALID_IMAGE_SIZE = -40;
  public final static int CL_INVALID_SAMPLER = -41;
  public final static int CL_INVALID_BINARY = -42;
  public final static int CL_INVALID_BUILD_OPTIONS = -43;
  public final static int CL_INVALID_PROGRAM = -44;
  public final static int CL_INVALID_PROGRAM_EXECUTABLE = -45;
  public final static int CL_INVALID_KERNEL_NAME = -46;
  public final static int CL_INVALID_KERNEL_DEFINITION = -47;
  public final static int CL_INVALID_KERNEL = -48;
  public final static int CL_INVALID_ARG_INDEX = -49;
  public final static int CL_INVALID_ARG_VALUE = -50;
  public final static int CL_INVALID_ARG_SIZE = -51;
  public final static int CL_INVALID_KERNEL_ARGS = -52;
  public final static int CL_INVALID_WORK_DIMENSION = -53;
  public final static int CL_INVALID_WORK_GROUP_SIZE = -54;
  public final static int CL_INVALID_WORK_ITEM_SIZE = -55;
  public final static int CL_INVALID_GLOBAL_OFFSET = -56;
  public final static int CL_INVALID_EVENT_WAIT_LIST = -57;
  public final static int CL_INVALID_EVENT = -58;
  public final static int CL_INVALID_OPERATION = -59;
  public final static int CL_INVALID_GL_OBJECT = -60;
  public final static int CL_INVALID_BUFFER_SIZE = -61;
  public final static int CL_INVALID_MIP_LEVEL = -62;
  

	

  
	public CLException(int ret)
		{
		this.ret=ret;
		this.msg="";
		}
	
	public CLException(String msg)
		{
		this.ret=666;
		}
	
	public int getCode()
		{
		return ret;
		}
	
	public String toString()
		{
		return "OpenCL error ("+ret+", "+getString()+")";
		}
	
	public String getString()
		{
		switch(ret)
			{
		  case CL_INVALID_VALUE:
		  return "invalid value";
		  case CL_INVALID_DEVICE_TYPE:
		  return "invalid device type";
		  case CL_INVALID_PLATFORM:
		  return "invalid platform";
		  case CL_INVALID_DEVICE:
		  return "invalid device";
		  case CL_INVALID_CONTEXT:
		  return "invalid context";
		  case CL_INVALID_QUEUE_PROPERTIES:
		  return "invalid queue properties";
		  case CL_INVALID_COMMAND_QUEUE:
		  return "invalid command queue";
		  case CL_INVALID_HOST_PTR:
		  return "invalid host pointer";
		  case CL_INVALID_MEM_OBJECT:
		  return "invalid mem object"; 
		  case CL_INVALID_IMAGE_FORMAT_DESCRIPTOR:
		  return "invalid image format descriptor";
		  case CL_INVALID_IMAGE_SIZE:
		  return "invalid image size";
		  case CL_INVALID_SAMPLER:
		  return "invalid sampler";
		  case CL_INVALID_BINARY:
		  return "invalid binary";
		  case CL_INVALID_BUILD_OPTIONS:
		  return "invalid build options";
		  case CL_INVALID_PROGRAM:
		  return "invalid program";
		  case CL_INVALID_PROGRAM_EXECUTABLE:
		  return "invalid program executable";
		  case CL_INVALID_KERNEL_NAME:
		  return "invalid kernel name";
		  case CL_INVALID_KERNEL_DEFINITION:
		  return "invalid kernel definition";
		  case CL_INVALID_KERNEL:
		  return "invalid kernel";
		  case CL_INVALID_ARG_INDEX:
		  return "invalid argument index";
		  case CL_INVALID_ARG_VALUE:
		  return "invalid argument value";
		  case CL_INVALID_ARG_SIZE:
		  return "invalid argument size";
		  case CL_INVALID_KERNEL_ARGS:
		  return "invalid kernel arguments";
		  case CL_INVALID_WORK_DIMENSION:
		  return "invalid work dimension";
		  case CL_INVALID_WORK_GROUP_SIZE:
		  return "invalid work group size";
		  case CL_INVALID_WORK_ITEM_SIZE:
		  return "invalid work item size";
		  case CL_INVALID_GLOBAL_OFFSET: 
		  return "invalid global offset";
		  case CL_INVALID_EVENT_WAIT_LIST:
		  return "invalid event wait list";
		  case CL_INVALID_EVENT: 
		  return "invalid event";
		  case CL_INVALID_OPERATION:
		  return "invalid operation";
		  case CL_INVALID_GL_OBJECT:
		  return "invalid gl object";
		  case CL_INVALID_BUFFER_SIZE:
		  return "invalid buffer size";
		  case CL_INVALID_MIP_LEVEL:
		  return "invalid MIP level";
		  
		  
		  case CL_MEM_OBJECT_ALLOCATION_FAILURE:
		  return "memory object allocation failure";
		  case CL_OUT_OF_RESOURCES:
		  return "out of resources";
		  case CL_OUT_OF_HOST_MEMORY:
		  return "out of host memory";
		  case CL_PROFILING_INFO_NOT_AVAILABLE:
		  return "profiling information not available";
		  case CL_MEM_COPY_OVERLAP:
		  return "memory copy overlap";
		  case CL_IMAGE_FORMAT_MISMATCH:
		  return "image format mismatch";
		  case CL_IMAGE_FORMAT_NOT_SUPPORTED:
		  return "image format not supported";
		  case CL_BUILD_PROGRAM_FAILURE:
		  return "build program failure";
		  case CL_MAP_FAILURE:
		  return "map failure";
		  
		  case CL_DEVICE_NOT_FOUND:
		  return "device not found";
		  case CL_DEVICE_NOT_AVAILABLE:
		  return "device not available";
		  case CL_DEVICE_COMPILER_NOT_AVAILABLE:
		  return "device compiler not available";


			}
		return msg;
		}
	
	
	}
