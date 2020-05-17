package javax.opencl;

/**
 * Java binding for OpenCL
 * @author Johan Henriksson
 *
 */
public class OpenCL
	{
	//Load the native code
  static
  	{
    System.loadLibrary("jopencl");
  	}

	public final static int CL_SUCCESS = 0;
	
  
  public final static int CL_VERSION_1_0 = 1;
  
  public final static int CL_FALSE = 0;
  public final static int CL_TRUE = 1;
  
  public final static int CL_PLATFORM_PROFILE = 0x0900;
  public final static int CL_PLATFORM_VERSION = 0x0901;
  public final static int CL_PLATFORM_NAME = 0x0902;
  public final static int CL_PLATFORM_VENDOR = 0x0903;
  public final static int CL_PLATFORM_EXTENSIONS = 0x0904;
  
  public final static int CL_DEVICE_TYPE_DEFAULT = (1 << 0);
  public final static int CL_DEVICE_TYPE_CPU = (1 << 1);
  public final static int CL_DEVICE_TYPE_GPU = (1 << 2);
  public final static int CL_DEVICE_TYPE_ACCELERATOR = (1 << 3);
  public final static int CL_DEVICE_TYPE_ALL = 0xFFFFFFFF;
  public final static int CL_DEVICE_TYPE = 0x1000;
  
  public final static int CL_DEVICE_VENDOR_ID = 0x1001;
  
  public final static int CL_DEVICE_MAX_COMPUTE_UNITS = 0x1002;
  public final static int CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS = 0x1003;
  public final static int CL_DEVICE_MAX_WORK_GROUP_SIZE = 0x1004;
  public final static int CL_DEVICE_MAX_WORK_ITEM_SIZES = 0x1005;
  public final static int CL_DEVICE_PREFERRED_VECTOR_WIDTH_CHAR = 0x1006;
  public final static int CL_DEVICE_PREFERRED_VECTOR_WIDTH_SHORT = 0x1007;
  public final static int CL_DEVICE_PREFERRED_VECTOR_WIDTH_INT = 0x1008;
  public final static int CL_DEVICE_PREFERRED_VECTOR_WIDTH_LONG = 0x1009;
  public final static int CL_DEVICE_PREFERRED_VECTOR_WIDTH_FLOAT = 0x100A;
  public final static int CL_DEVICE_PREFERRED_VECTOR_WIDTH_DOUBLE = 0x100B;
  public final static int CL_DEVICE_MAX_CLOCK_FREQUENCY = 0x100C;
  public final static int CL_DEVICE_ADDRESS_BITS = 0x100D;
  public final static int CL_DEVICE_MAX_READ_IMAGE_ARGS = 0x100E;
  public final static int CL_DEVICE_MAX_WRITE_IMAGE_ARGS = 0x100F;
  public final static int CL_DEVICE_MAX_MEM_ALLOC_SIZE = 0x1010;
  public final static int CL_DEVICE_IMAGE2D_MAX_WIDTH = 0x1011;
  public final static int CL_DEVICE_IMAGE2D_MAX_HEIGHT = 0x1012;
  public final static int CL_DEVICE_IMAGE3D_MAX_WIDTH = 0x1013;
  public final static int CL_DEVICE_IMAGE3D_MAX_HEIGHT = 0x1014;
  public final static int CL_DEVICE_IMAGE3D_MAX_DEPTH = 0x1015;
  public final static int CL_DEVICE_IMAGE_SUPPORT = 0x1016;
  public final static int CL_DEVICE_MAX_PARAMETER_SIZE = 0x1017;
  public final static int CL_DEVICE_MAX_SAMPLERS = 0x1018;
  public final static int CL_DEVICE_MEM_BASE_ADDR_ALIGN = 0x1019;
  public final static int CL_DEVICE_MIN_DATA_TYPE_ALIGN_SIZE = 0x101A;
  public final static int CL_DEVICE_SINGLE_FP_CONFIG = 0x101B;
  public final static int CL_DEVICE_GLOBAL_MEM_CACHE_TYPE = 0x101C;
  public final static int CL_DEVICE_GLOBAL_MEM_CACHELINE_SIZE = 0x101D;
  public final static int CL_DEVICE_GLOBAL_MEM_CACHE_SIZE = 0x101E;
  public final static int CL_DEVICE_GLOBAL_MEM_SIZE = 0x101F;
  public final static int CL_DEVICE_MAX_CONSTANT_BUFFER_SIZE = 0x1020;
  public final static int CL_DEVICE_MAX_CONSTANT_ARGS = 0x1021;
  public final static int CL_DEVICE_LOCAL_MEM_TYPE = 0x1022;
  public final static int CL_DEVICE_LOCAL_MEM_SIZE = 0x1023;
  public final static int CL_DEVICE_ERROR_CORRECTION_SUPPORT = 0x1024;
  public final static int CL_DEVICE_PROFILING_TIMER_RESOLUTION = 0x1025;
  public final static int CL_DEVICE_ENDIAN_LITTLE = 0x1026;
  public final static int CL_DEVICE_AVAILABLE = 0x1027;
  public final static int CL_DEVICE_COMPILER_AVAILABLE = 0x1028;
  public final static int CL_DEVICE_EXECUTION_CAPABILITIES = 0x1029;
  public final static int CL_DEVICE_QUEUE_PROPERTIES = 0x102A;
  public final static int CL_DEVICE_NAME = 0x102B;
  public final static int CL_DEVICE_VENDOR = 0x102C;
  public final static int CL_DRIVER_VERSION = 0x102D;
  public final static int CL_DEVICE_PROFILE = 0x102E;
  public final static int CL_DEVICE_VERSION = 0x102F;
  public final static int CL_DEVICE_EXTENSIONS = 0x1030;
  public final static int CL_DEVICE_PLATFORM = 0x1031;
  public final static int CL_DEVICE_ADDRESS_32_BITS = (1 << 0);
  public final static int CL_DEVICE_ADDRESS_64_BITS = (1 << 1);
  
  public final static int CL_FP_DENORM = (1 << 0);
  public final static int CL_FP_INF_NAN = (1 << 1);
  public final static int CL_FP_ROUND_TO_NEAREST = (1 << 2);
  public final static int CL_FP_ROUND_TO_ZERO = (1 << 3);
  public final static int CL_FP_ROUND_TO_INF = (1 << 4);
  public final static int CL_FP_FMA = (1 << 5);
  public final static int CL_NONE = 0x0;
  public final static int CL_READ_ONLY_CACHE = 0x1;
  public final static int CL_READ_WRITE_CACHE = 0x2;
  public final static int CL_LOCAL = 0x1;
  public final static int CL_GLOBAL = 0x2;
  public final static int CL_EXEC_KERNEL = (1 << 0);
  public final static int CL_EXEC_NATIVE_KERNEL = (1 << 1);
  public final static int CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE = (1 << 0);
  public final static int CL_QUEUE_PROFILING_ENABLE = (1 << 1);
  public final static int CL_CONTEXT_REFERENCE_COUNT = 0x1080;
  public final static int CL_CONTEXT_NUM_DEVICES = 0x1081;
  public final static int CL_CONTEXT_DEVICES = 0x1082;
  public final static int CL_CONTEXT_PROPERTIES = 0x1083;
  public final static int CL_CONTEXT_PLATFORM = 0x1084;
  public final static int CL_QUEUE_CONTEXT = 0x1090;
  public final static int CL_QUEUE_DEVICE = 0x1091;
  public final static int CL_QUEUE_REFERENCE_COUNT = 0x1092;
  public final static int CL_QUEUE_PROPERTIES = 0x1093;
  public final static int CL_MEM_READ_WRITE = (1 << 0);
  public final static int CL_MEM_WRITE_ONLY = (1 << 1);
  public final static int CL_MEM_READ_ONLY = (1 << 2);
  public final static int CL_MEM_USE_HOST_PTR = (1 << 3);
  public final static int CL_MEM_ALLOC_HOST_PTR = (1 << 4);
  public final static int CL_MEM_COPY_HOST_PTR = (1 << 5);
  public final static int CL_R = 0x10B0;
  public final static int CL_A = 0x10B1;
  public final static int CL_RG = 0x10B2;
  public final static int CL_RA = 0x10B3;
  public final static int CL_RGB = 0x10B4;
  public final static int CL_RGBA = 0x10B5;
  public final static int CL_BGRA = 0x10B6;
  public final static int CL_ARGB = 0x10B7;
  public final static int CL_INTENSITY = 0x10B8;
  public final static int CL_LUMINANCE = 0x10B9;
  public final static int CL_SNORM_INT8 = 0x10D0;
  public final static int CL_SNORM_INT16 = 0x10D1;
  public final static int CL_UNORM_INT8 = 0x10D2;
  public final static int CL_UNORM_INT16 = 0x10D3;
  public final static int CL_UNORM_SHORT_565 = 0x10D4;
  public final static int CL_UNORM_SHORT_555 = 0x10D5;
  public final static int CL_UNORM_INT_101010 = 0x10D6;
  public final static int CL_SIGNED_INT8 = 0x10D7;
  public final static int CL_SIGNED_INT16 = 0x10D8;
  public final static int CL_SIGNED_INT32 = 0x10D9;
  public final static int CL_UNSIGNED_INT8 = 0x10DA;
  public final static int CL_UNSIGNED_INT16 = 0x10DB;
  public final static int CL_UNSIGNED_INT32 = 0x10DC;
  public final static int CL_HALF_FLOAT = 0x10DD;
  public final static int CL_FLOAT = 0x10DE;
  public final static int CL_MEM_OBJECT_BUFFER = 0x10F0;
  public final static int CL_MEM_OBJECT_IMAGE2D = 0x10F1;
  public final static int CL_MEM_OBJECT_IMAGE3D = 0x10F2;
  public final static int CL_MEM_TYPE = 0x1100;
  public final static int CL_MEM_FLAGS = 0x1101;
  public final static int CL_MEM_SIZE = 0x1102;
  public final static int CL_MEM_HOST_PTR = 0x1103;
  public final static int CL_MEM_MAP_COUNT = 0x1104;
  public final static int CL_MEM_REFERENCE_COUNT = 0x1105;
  public final static int CL_MEM_CONTEXT = 0x1106;
  public final static int CL_IMAGE_FORMAT = 0x1110;
  public final static int CL_IMAGE_ELEMENT_SIZE = 0x1111;
  public final static int CL_IMAGE_ROW_PITCH = 0x1112;
  public final static int CL_IMAGE_SLICE_PITCH = 0x1113;
  public final static int CL_IMAGE_WIDTH = 0x1114;
  public final static int CL_IMAGE_HEIGHT = 0x1115;
  public final static int CL_IMAGE_DEPTH = 0x1116;
  public final static int CL_ADDRESS_NONE = 0x1130;
  public final static int CL_ADDRESS_CLAMP_TO_EDGE = 0x1131;
  public final static int CL_ADDRESS_CLAMP = 0x1132;
  public final static int CL_ADDRESS_REPEAT = 0x1133;
  public final static int CL_FILTER_NEAREST = 0x1140;
  public final static int CL_FILTER_LINEAR = 0x1141;
  public final static int CL_SAMPLER_REFERENCE_COUNT = 0x1150;
  public final static int CL_SAMPLER_CONTEXT = 0x1151;
  public final static int CL_SAMPLER_NORMALIZED_COORDS = 0x1152;
  public final static int CL_SAMPLER_ADDRESSING_MODE = 0x1153;
  public final static int CL_SAMPLER_FILTER_MODE = 0x1154;
  public final static int CL_MAP_READ = (1 << 0);
  public final static int CL_MAP_WRITE = (1 << 1);
  public final static int CL_PROGRAM_REFERENCE_COUNT = 0x1160;
  public final static int CL_PROGRAM_CONTEXT = 0x1161;
  public final static int CL_PROGRAM_NUM_DEVICES = 0x1162;
  public final static int CL_PROGRAM_DEVICES = 0x1163;
  public final static int CL_PROGRAM_SOURCE = 0x1164;
  public final static int CL_PROGRAM_BINARY_SIZES = 0x1165;
  public final static int CL_PROGRAM_BINARIES = 0x1166;
  public final static int CL_PROGRAM_BUILD_STATUS = 0x1181;
  public final static int CL_PROGRAM_BUILD_OPTIONS = 0x1182;
  public final static int CL_PROGRAM_BUILD_LOG = 0x1183;
  
  public final static int CL_BUILD_SUCCESS = 0;
  public final static int CL_BUILD_NONE = -1;
  public final static int CL_BUILD_ERROR = -2;
  public final static int CL_BUILD_IN_PROGRESS = -3;
  
  public final static int CL_KERNEL_FUNCTION_NAME = 0x1190;
  public final static int CL_KERNEL_NUM_ARGS = 0x1191;
  public final static int CL_KERNEL_REFERENCE_COUNT = 0x1192;
  public final static int CL_KERNEL_CONTEXT = 0x1193;
  public final static int CL_KERNEL_PROGRAM = 0x1194;
  public final static int CL_KERNEL_WORK_GROUP_SIZE = 0x11B0;
  public final static int CL_KERNEL_COMPILE_WORK_GROUP_SIZE = 0x11B1;
  public final static int CL_KERNEL_LOCAL_MEM_SIZE = 0x11B2;
  
  public final static int CL_EVENT_COMMAND_QUEUE = 0x11D0;
  public final static int CL_EVENT_COMMAND_TYPE = 0x11D1;
  public final static int CL_EVENT_REFERENCE_COUNT = 0x11D2;
  public final static int CL_EVENT_COMMAND_EXECUTION_STATUS = 0x11D3;
  
  public final static int CL_COMMAND_NDRANGE_KERNEL = 0x11F0;
  public final static int CL_COMMAND_TASK = 0x11F1;
  public final static int CL_COMMAND_NATIVE_KERNEL = 0x11F2;
  public final static int CL_COMMAND_READ_BUFFER = 0x11F3;
  public final static int CL_COMMAND_WRITE_BUFFER = 0x11F4;
  public final static int CL_COMMAND_COPY_BUFFER = 0x11F5;
  public final static int CL_COMMAND_READ_IMAGE = 0x11F6;
  public final static int CL_COMMAND_WRITE_IMAGE = 0x11F7;
  public final static int CL_COMMAND_COPY_IMAGE = 0x11F8;
  public final static int CL_COMMAND_COPY_IMAGE_TO_BUFFER = 0x11F9;
  public final static int CL_COMMAND_COPY_BUFFER_TO_IMAGE = 0x11FA;
  public final static int CL_COMMAND_MAP_BUFFER = 0x11FB;
  public final static int CL_COMMAND_MAP_IMAGE = 0x11FC;
  public final static int CL_COMMAND_UNMAP_MEM_OBJECT = 0x11FD;
  public final static int CL_COMMAND_MARKER = 0x11FE;
  public final static int CL_COMMAND_WAIT_FOR_EVENTS = 0x11FF;
  public final static int CL_COMMAND_BARRIER = 0x1200;
  public final static int CL_COMMAND_ACQUIRE_GL_OBJECTS = 0x1201;
  public final static int CL_COMMAND_RELEASE_GL_OBJECTS = 0x1202;
  public final static int CL_COMPLETE = 0x0;
  public final static int CL_RUNNING = 0x1;
  public final static int CL_SUBMITTED = 0x2;
  public final static int CL_QUEUED = 0x3;
  public final static int CL_PROFILING_COMMAND_QUEUED = 0x1280;
  public final static int CL_PROFILING_COMMAND_SUBMIT = 0x1281;
  public final static int CL_PROFILING_COMMAND_START = 0x1282;
  public final static int CL_PROFILING_COMMAND_END = 0x1283;
  
  
	public static int sizeForType(Class<?> c)
		{
		if(c==Integer.class || c==Float.class)
			return 4;
		else
			throw new CLException("Unsupported memory type");
		}

  
	protected static void assertSuccess(int ret)
		{
		if(ret!=CL_SUCCESS)
			throw new CLException(ret);
		}
	
	public static CLContext createContext(int deviceType)
		{
		return new CLContext(deviceType);
		}
	

	public static CLPlatform[] getPlatforms()
		{
		int[] ids=_getPlatforms();
		CLPlatform platform[]=new CLPlatform[ids.length];
		for(int i=0;i<ids.length;i++)
			platform[i]=new CLPlatform(ids[i]);
		return platform;
		}
	
	private static native int[] _getPlatforms();
	
	
	protected static void assertNotNull(Object... ob)
		{
		for(Object o:ob)
			if(o==null)
				throw new CLException("Null value");
		}

	}
