package javax.opencl;

/**
 * OpenCL device
 * @author Johan Henriksson
 *
 */
public class CLDevice extends OpenCL
	{
	int device_id;
	
	public CLDevice(int device_id)
		{
		this.device_id=device_id;
		}

	
	public String getDeviceName()
		{
		return _getDeviceInfoString(device_id, CL_DEVICE_NAME);
		}

	public String getDevicePlatform()
		{
		return _getDeviceInfoString(device_id, CL_DEVICE_PLATFORM);
		}
	

	public String getDeviceVendor()
		{
		return _getDeviceInfoString(device_id, CL_DEVICE_VENDOR);
		}

	public String getDriverVersion()
		{
		return _getDeviceInfoString(device_id, CL_DRIVER_VERSION);
		}
	
	public String getDeviceProfile()
		{
		return _getDeviceInfoString(device_id, CL_DEVICE_PROFILE);
		}

	public String getDeviceVersion()
		{
		return _getDeviceInfoString(device_id, CL_DEVICE_VERSION);
		}

	public String getDeviceExtensions()
		{
		return _getDeviceInfoString(device_id, CL_DEVICE_EXTENSIONS);
		}

	
	private native String _getDeviceInfoString(int did, int param_name);
	
	
	
	
	/*
	 * 
	 *                                    cl_device_type The OpenCL device type. Currently
CL_DEVICE_TYPE
                                                  supported values are:
                                                  CL_DEVICE_TYPE_CPU,
                                                  CL_DEVICE_TYPE_GPU,
                                                  CL_DEVICE_TYPE_ACCELERATOR,
                                                  CL_DEVICE_TYPE_DEFAULT or a
                                                  combination of the above.
                                   cl_uint        A unique device vendor identifier. An
CL_DEVICE_VENDOR_ID
                                                  example of a unique device identifier
                                                  could be the PCIe ID.
                                   cl_uint        The number of parallel compute cores
CL_DEVICE_MAX_COMPUTE_UNITS
                                                  on the OpenCL device. The minimum
                                                  value is 1.
                                   cl_uint        Maximum dimensions that specify the
CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS
                                                  global and local work-item IDs used
                                                  by the data parallel execution model.
                                                  (Refer to clEnqueueNDRangeKernel).
                                                  The minimum value is 3.
                                   size_t []      Maximum number of work-items that
CL_DEVICE_MAX_WORK_ITEM_SIZES
                                                  can be specified in each dimension of
                                                  the work-group to
                                                  clEnqueueNDRangeKernel.
                                                  Returns n size_t entries, where n is the
                                                  value returned by the query for
                                                  CL_DEVICE_MAX_WORK_ITEM_DI
                                                  MENSIONS.
                                                  The minimum value is (1, 1, 1).
                                   size_t         Maximum number of work-items in a
CL_DEVICE_MAX_WORK_GROUP_SIZE
                                                  work-group executing a kernel using
                                                  the data parallel execution model.
 Last Revision Date: 5/16/09                                               Page 33
                                        (Refer to clEnqueueNDRangeKernel).
                                        The minimum value is 1.
                               cl_uint  Preferred native vector width size for
CL_DEVICE_PREFERRED_
VECTOR_WIDTH_CHAR
                                        built-in scalar types that can be put
                                        into vectors. The vector width is
CL_DEVICE_PREFERRED_
                                        defined as the number of scalar
VECTOR_WIDTH_SHORT
                                        elements that can be stored in the
CL_DEVICE_PREFERRED_
                                        vector.
VECTOR_WIDTH_INT
CL_DEVICE_PREFERRED_
                                        If the cl_khr_fp64 extension is not
VECTOR_WIDTH_LONG
                                        supported,
                                        CL_DEVICE_PREFERRED_VECTOR_WID
CL_DEVICE_PREFERRED_
                                        TH_DOUBLE must return 0.
VECTOR_WIDTH_FLOAT
CL_DEVICE_PREFERRED_
VECTOR_WIDTH_DOUBLE
                               cl_uint  Maximum configured clock frequency
CL_DEVICE_MAX_CLOCK_FREQUENCY
                                        of the device in MHz.
                               cl_uint  The default compute device address
CL_DEVICE_ADDRESS_BITS
                                        space size specified as an unsigned
                                        integer value in bits. Currently
                                        supported values are 32 or 64 bits.
                               cl_ulong Max size of memory object allocation
CL_DEVICE_MAX_MEM_ALLOC_SIZE
                                        in bytes. The minimum value is max
                                        (1/4th of
                                        CL_DEVICE_GLOBAL_MEM_SIZE ,
                                        128*1024*1024)
                               cl_bool  Is CL_TRUE if images are supported
CL_DEVICE_IMAGE_SUPPORT
                                        by the OpenCL device and CL_FALSE
                                        otherwise.
                               cl_uint  Max number of simultaneous image
CL_DEVICE_MAX_READ_IMAGE_ARGS
                                        objects that can be read by a kernel.
                                        The minimum value is 128 if
                                        CL_DEVICE_IMAGE_SUPPORT is
                                        CL_TRUE.
                               cl_uint  Max number of simultaneous image
CL_DEVICE_MAX_WRITE_IMAGE_ARGS
                                        objects that can be written to by a
                                        kernel. The minimum value is 8 if
                                        CL_DEVICE_IMAGE_SUPPORT is
                                        CL_TRUE.
                               size_t   Max width of 2D image in pixels. The
CL_DEVICE_IMAGE2D_MAX_WIDTH
                                        minimum value is 8192 if
                                        CL_DEVICE_IMAGE_SUPPORT is
                                        CL_TRUE.
                               size_t   Max height of 2D image in pixels. The
CL_DEVICE_IMAGE2D_MAX_HEIGHT
 Last Revision Date: 5/16/09                                      Page 34
                                              minimum value is 8192 if
                                              CL_DEVICE_IMAGE_SUPPORT is
                                              CL_TRUE.
                                   size_t     Max width of 3D image in pixels. The
CL_DEVICE_IMAGE3D_MAX_WIDTH
                                              minimum value is 2048 if
                                              CL_DEVICE_IMAGE_SUPPORT is
                                              CL_TRUE.
                                   size_t     Max height of 3D image in pixels. The
CL_DEVICE_IMAGE3D_MAX_HEIGHT
                                              minimum value is 2048 if
                                              CL_DEVICE_IMAGE_SUPPORT is
                                              CL_TRUE.
                                   size_t     Max depth of 3D image in pixels. The
CL_DEVICE_IMAGE3D_MAX_DEPTH
                                              minimum value is 2048 if
                                              CL_DEVICE_IMAGE_SUPPORT is
                                              CL_TRUE.
                                   cl_uint    Maximum number of samplers that
CL_DEVICE_MAX_SAMPLERS
                                              can be used in a kernel. Refer to
                                              section 6.11.8 for a detailed
                                              description on samplers. The
                                              minimum value is 16 if
                                              CL_DEVICE_IMAGE_SUPPORT is
                                              CL_TRUE.
                                   size_t     Max size in bytes of the arguments
CL_DEVICE_MAX_PARAMETER_SIZE
                                              that can be passed to a kernel. The
                                              minimum value is 256.
                                   cl_uint    Describes the alignment in bits of the
CL_DEVICE_MEM_BASE_ADDR_ALIGN
                                              base address of any allocated memory
                                              object.
                                   cl_uint    The smallest alignment in bytes which
CL_DEVICE_MIN_DATA_TYPE_ALIGN_SIZE
                                              can be used for any data type.
                                   cl_device_ Describes single precision floating-
CL_DEVICE_SINGLE_FP_CONFIG
                                   fp_config  point capability of the device. This is
                                              a bit-field that describes one or more
                                              of the following values:
                                              CL_FP_DENORM – denorms are supported
                                              CL_FP_INF_NAN – INF and quiet NaNs are
                                              supported.
                                              CL_FP_ROUND_TO_NEAREST– round to
                                              nearest even rounding mode supported
                                              CL_FP_ROUND_TO_ZERO – round to zero
                                              rounding mode supported
 Last Revision Date: 5/16/09                                             Page 35
                                                    CL_FP_ROUND_TO_INF – round to +ve and
                                                    –ve infinity rounding modes supported
                                                    CL_FP_FMA – IEEE754-2008 fused
                                                    multiply-add is supported.
                                                    The mandated minimum floating-point
                                                    capability is:
                                                    CL_FP_ROUND_TO_NEAREST |
                                                    CL_FP_INF_NAN.
                                     cl_device_mem_ Type of global memory cache
CL_DEVICE_GLOBAL_MEM_CACHE_TYPE
                                     cache_type     supported. Valid values are:
                                                    CL_NONE,
                                                    CL_READ_ONLY_CACHE and
                                                    CL_READ_WRITE_CACHE.
                                     cl_uint        Size of global memory cache line in
CL_DEVICE_GLOBAL_MEM_CACHELINE_SIZE
                                                    bytes.
                                     cl_ulong       Size of global memory cache in bytes.
CL_DEVICE_GLOBAL_MEM_CACHE_SIZE
                                     cl_ulong       Size of global device memory in
CL_DEVICE_GLOBAL_MEM_SIZE
                                                    bytes.
                                     cl_ulong       Max size in bytes of a constant buffer
CL_DEVICE_MAX_CONSTANT_BUFFER_SIZE
                                                    allocation. The minimum value is 64
                                                    KB.
                                     cl_uint        Max number of arguments declared
CL_DEVICE_MAX_CONSTANT_ARGS
                                                    with the __constant qualifier in a
                                                    kernel. The minimum value is 8.
                                     cl_device_     Type of local memory supported.
CL_DEVICE_LOCAL_MEM_TYPE
                                     local_mem_type This can be set to CL_LOCAL implying
                                                    dedicated local memory storage such
                                                    as SRAM, or CL_GLOBAL.
                                     cl_ulong       Size of local memory arena in bytes.
CL_DEVICE_LOCAL_MEM_SIZE
                                                    The minimum value is 16 KB.
                                     cl_bool        Is CL_TRUE if the device implements
CL_DEVICE_ERROR_CORRECTION_SUPPORT
                                                    error correction for the memories,
                                                    caches, registers etc. in the device. Is
                                                    CL_FALSE if the device does not
                                                    implement error correction. This can
                                                    be a requirement for certain clients of
                                                    OpenCL.
                                     size_t         Describes the resolution of device
CL_DEVICE_PROFILING_TIMER_RESOLUTION
                                                    timer. This is measured in
                                                    nanoseconds. Refer to section 5.9 for
 Last Revision Date: 5/16/09                                                    Page 36
                                                  details.
                                 cl_bool          Is CL_TRUE if the OpenCL device is a
CL_DEVICE_ENDIAN_LITTLE
                                                  little endian device and CL_FALSE
                                                  otherwise.
                                 cl_bool          Is CL_TRUE if the device is available
CL_DEVICE_AVAILABLE
                                                  and CL_FALSE if the device is not
                                                  available.
                                 cl_bool          Is CL_FALSE if the implementation
CL_DEVICE_COMPILER_AVAILABLE
                                                  does not have a compiler available to
                                                  compile the program source.
                                                  Is CL_TRUE if the compiler is
                                                  available.
                                                  This can be CL_FALSE for the
                                                  embededed platform profile only.
                                 cl_device_exec_  Describes the execution capabilities of
CL_DEVICE_EXECUTION_CAPABILITIES
                                 capabilities     the device. This is a bit-field that
                                                  describes one or more of the following
                                                  values:
                                                  CL_EXEC_KERNEL –       The OpenCL
                                                  device can execute OpenCL kernels.
                                                  CL_EXEC_NATIVE_KERNEL – The
                                                  OpenCL device can execute native
                                                  kernels.
                                                  The mandated minimum capability is:
                                                  CL_EXEC_KERNEL.
                                 cl_command_      Describes the command-queue
CL_DEVICE_QUEUE_PROPERTIES
                                 queue_properties properties supported by the device.
                                                  This is a bit-field that describes one or
                                                  more of the following values:
                                                  CL_QUEUE_OUT_OF_ORDER_EXEC_
                                                  MODE_ENABLE
                                                  CL_QUEUE_PROFILING_ENABLE
                                                  These properties are described in table
                                                  5.1.
 Last Revision Date: 5/16/09                                                 Page 37
                                                                             The mandated minimum capability is:
                                                                             CL_QUEUE_PROFILING_ENABLE.
                                                         cl_platform_id      The platform associated with this
 4
   The platform profile returns the profile that is implemented by the OpenCL framework. If the platform profile
 returned is FULL_PROFILE, the OpenCL framework will support devices that are FULL_PROFILE and may also
 support devices that are EMBEDDED_PROFILE. The compiler must be available for all devices i.e.
 CL_DEVICE_COMPILER_AVAILABLE is CL_TRUE. If the platform profile returned is
 EMBEDDED_PROFILE, then devices that are only EMBEDDED_PROFILE are supported.
 Last Revision Date: 5/16/09                                                                             Page 38
the following approved extension
names:
cl_khr_fp64
cl_khr_select_fprounding_mode
cl_khr_global_int32_base_atomics
cl_khr_global_int32_extended_atomics
cl_khr_local_int32_base_atomics
cl_khr_local_int32_extended_atomics
cl_khr_int64_base_atomics
cl_khr_int64_extended_atomics
cl_khr_3d_image_writes
cl_khr_byte_addressable_store
cl_khr_fp16
Please refer to section 9 for a detailed
description of these extensions.

	 * 
	 * 
	 */
	}
