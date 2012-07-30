
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#ifndef __APPLE__
#include <malloc.h>
#endif

#include <sys/types.h>
#ifndef _WIN32
#include <unistd.h>
#endif

#ifndef __APPLE__
#include <CL/cl.h>
#else
#include <cl.h>
#endif


#if defined (_WIN32)
#include "windows.h"
#define alignedMalloc(size, alignment)\
   _aligned_malloc(size, alignment)
#else
#define alignedMalloc(size, alignment)\
   memalign(alignment, size)
#endif

const char *errString(cl_int status){
   static struct { cl_int code; const char *msg; } error_table[] = {
      { CL_SUCCESS, "success" },
      { CL_DEVICE_NOT_FOUND, "device not found", },
      { CL_DEVICE_NOT_AVAILABLE, "device not available", },
      { CL_COMPILER_NOT_AVAILABLE, "compiler not available", },
      { CL_MEM_OBJECT_ALLOCATION_FAILURE, "mem object allocation failure", },
      { CL_OUT_OF_RESOURCES, "out of resources", },
      { CL_OUT_OF_HOST_MEMORY, "out of host memory", },
      { CL_PROFILING_INFO_NOT_AVAILABLE, "profiling not available", },
      { CL_MEM_COPY_OVERLAP, "memcopy overlaps", },
      { CL_IMAGE_FORMAT_MISMATCH, "image format mismatch", },
      { CL_IMAGE_FORMAT_NOT_SUPPORTED, "image format not supported", },
      { CL_BUILD_PROGRAM_FAILURE, "build program failed", },
      { CL_MAP_FAILURE, "map failed", },
      { CL_INVALID_VALUE, "invalid value", },
      { CL_INVALID_DEVICE_TYPE, "invalid device type", },
      { CL_INVALID_PLATFORM, "invlaid platform",},
      { CL_INVALID_DEVICE, "invalid device",},
      { CL_INVALID_CONTEXT, "invalid context",},
      { CL_INVALID_QUEUE_PROPERTIES, "invalid queue properties",},
      { CL_INVALID_COMMAND_QUEUE, "invalid command queue",},
      { CL_INVALID_HOST_PTR, "invalid host ptr",},
      { CL_INVALID_MEM_OBJECT, "invalid mem object",},
      { CL_INVALID_IMAGE_FORMAT_DESCRIPTOR, "invalid image format descriptor ",},
      { CL_INVALID_IMAGE_SIZE, "invalid image size",},
      { CL_INVALID_SAMPLER, "invalid sampler",},
      { CL_INVALID_BINARY, "invalid binary",},
      { CL_INVALID_BUILD_OPTIONS, "invalid build options",},
      { CL_INVALID_PROGRAM, "invalid program ",},
      { CL_INVALID_PROGRAM_EXECUTABLE, "invalid program executable",},
      { CL_INVALID_KERNEL_NAME, "invalid kernel name",},
      { CL_INVALID_KERNEL_DEFINITION, "invalid definition",},
      { CL_INVALID_KERNEL, "invalid kernel",},
      { CL_INVALID_ARG_INDEX, "invalid arg index",},
      { CL_INVALID_ARG_VALUE, "invalid arg value",},
      { CL_INVALID_ARG_SIZE, "invalid arg size",},
      { CL_INVALID_KERNEL_ARGS, "invalid kernel args",},
      { CL_INVALID_WORK_DIMENSION , "invalid work dimension",},
      { CL_INVALID_WORK_GROUP_SIZE, "invalid work group size",},
      { CL_INVALID_WORK_ITEM_SIZE, "invalid work item size",},
      { CL_INVALID_GLOBAL_OFFSET, "invalid global offset",},
      { CL_INVALID_EVENT_WAIT_LIST, "invalid event wait list",},
      { CL_INVALID_EVENT, "invalid event",},
      { CL_INVALID_OPERATION, "invalid operation",},
      { CL_INVALID_GL_OBJECT, "invalid gl object",},
      { CL_INVALID_BUFFER_SIZE, "invalid buffer size",},
      { CL_INVALID_MIP_LEVEL, "invalid mip level",},
      { CL_INVALID_GLOBAL_WORK_SIZE, "invalid global work size",},
      { 0, NULL },
   };
   static char unknown[25];
   int ii;

   for (ii = 0; error_table[ii].msg != NULL; ii++) {
      if (error_table[ii].code == status) {
         return error_table[ii].msg;
      }
   }
#ifdef _WIN32
   _snprintf(unknown, sizeof unknown, "unknown error %d", status);
#else
   snprintf(unknown, sizeof(unknown), "unknown error %d", status);
#endif
   return unknown;
}

int main(int argc, char **argv){
   cl_int status = CL_SUCCESS;
   cl_uint platformc;

   status = clGetPlatformIDs(0, NULL, &platformc);
   if (status != CL_SUCCESS){
      fprintf(stderr, "clGetPlatformIDs(0,NULL,&platformc) failed!\n%s\n", errString(status));
      exit(1);
   }
   fprintf(stderr, "clGetPlatformIDs(0,NULL,&platformc) OK!\n", errString(status));
   fprintf(stderr, "There %s %d platform%s\n", ((platformc==1)?"is":"are"), platformc, ((platformc==1)?"":"s"));
   cl_platform_id* platformIds = new cl_platform_id[platformc];
   status = clGetPlatformIDs(platformc, platformIds, NULL);
   if (status != CL_SUCCESS){
      fprintf(stderr, "clGetPlatformIDs(platformc,platformIds,NULL) failed!\n%s\n", errString(status));
      exit(1);
   }
   for (unsigned platformIdx = 0; platformIdx < platformc; ++platformIdx) {
      fprintf(stderr, "platform %d{\n", platformIdx);
      char platformVersionName[512];
      status = clGetPlatformInfo(platformIds[platformIdx], CL_PLATFORM_VERSION, sizeof(platformVersionName), platformVersionName, NULL);

      char platformVendorName[512];  
      char platformName[512];  
      status = clGetPlatformInfo(platformIds[platformIdx], CL_PLATFORM_VENDOR, sizeof(platformVendorName), platformVendorName, NULL);
      status = clGetPlatformInfo(platformIds[platformIdx], CL_PLATFORM_NAME, sizeof(platformName), platformName, NULL);
      fprintf(stderr, "   CL_PLATFORM_VENDOR..\"%s\"\n", platformVendorName); 
      fprintf(stderr, "   CL_PLATFORM_VERSION.\"%s\"\n", platformVersionName); 
      fprintf(stderr, "   CL_PLATFORM_NAME....\"%s\"\n", platformName); 
      cl_uint deviceIdc;
      cl_device_type requestedDeviceType =CL_DEVICE_TYPE_CPU |CL_DEVICE_TYPE_GPU ;
      status = clGetDeviceIDs(platformIds[platformIdx], requestedDeviceType, 0, NULL, &deviceIdc);
      fprintf(stderr, "   Platform %d has %d device%s{\n", platformIdx, deviceIdc, ((deviceIdc==1)?"":"s"));
      if (status == CL_SUCCESS && deviceIdc >0 ){
         cl_device_id* deviceIds = new cl_device_id[deviceIdc];
         status = clGetDeviceIDs(platformIds[platformIdx], requestedDeviceType, deviceIdc, deviceIds, NULL);
         if (status == CL_SUCCESS){
            for (unsigned deviceIdx=0; deviceIdx<deviceIdc; deviceIdx++){
               fprintf(stderr, "      Device %d{\n", deviceIdx);

               cl_device_type deviceType;
               status = clGetDeviceInfo(deviceIds[deviceIdx], CL_DEVICE_TYPE,  sizeof(deviceType), &deviceType, NULL);
               fprintf(stderr, "         CL_DEVICE_TYPE..................... ");
               if (deviceType & CL_DEVICE_TYPE_DEFAULT) {
                  deviceType &= ~CL_DEVICE_TYPE_DEFAULT;
                  fprintf(stderr, "Default ");
               }
               if (deviceType & CL_DEVICE_TYPE_CPU) {
                  deviceType &= ~CL_DEVICE_TYPE_CPU;
                  fprintf(stderr, "CPU ");
               }
               if (deviceType & CL_DEVICE_TYPE_GPU) {
                  deviceType &= ~CL_DEVICE_TYPE_GPU;
                  fprintf(stderr, "GPU ");
               }
               if (deviceType & CL_DEVICE_TYPE_ACCELERATOR) {
                  deviceType &= ~CL_DEVICE_TYPE_ACCELERATOR;
                  fprintf(stderr, "Accelerator ");
               }
               fprintf(stderr, "(0x%llx) ", deviceType);
               fprintf(stderr, "\n");

               cl_uint maxComputeUnits;
               status = clGetDeviceInfo(deviceIds[deviceIdx], CL_DEVICE_MAX_COMPUTE_UNITS,  sizeof(maxComputeUnits), &maxComputeUnits, NULL);
               fprintf(stderr, "         CL_DEVICE_MAX_COMPUTE_UNITS........ %u\n", maxComputeUnits);

               cl_uint maxWorkItemDimensions;
               status = clGetDeviceInfo(deviceIds[deviceIdx], CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS,  sizeof(maxWorkItemDimensions), &maxWorkItemDimensions, NULL);
               fprintf(stderr, "         CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS. %u\n", maxWorkItemDimensions);

               size_t *maxWorkItemSizes = new size_t[maxWorkItemDimensions];
               status = clGetDeviceInfo(deviceIds[deviceIdx], CL_DEVICE_MAX_WORK_ITEM_SIZES,  sizeof(size_t)*maxWorkItemDimensions, maxWorkItemSizes, NULL);
               for (unsigned dimIdx=0; dimIdx<maxWorkItemDimensions; dimIdx++){
                  fprintf(stderr, "             dim[%d] = %d\n", dimIdx, maxWorkItemSizes[dimIdx]);
               }

               size_t maxWorkGroupSize;
               status = clGetDeviceInfo(deviceIds[deviceIdx], CL_DEVICE_MAX_WORK_GROUP_SIZE,  sizeof(maxWorkGroupSize), &maxWorkGroupSize, NULL);
               fprintf(stderr, "         CL_DEVICE_MAX_WORK_GROUP_SIZE...... %u\n", maxWorkGroupSize);

               cl_ulong maxMemAllocSize;
               status = clGetDeviceInfo(deviceIds[deviceIdx], CL_DEVICE_MAX_MEM_ALLOC_SIZE,  sizeof(maxMemAllocSize), &maxMemAllocSize, NULL);
               fprintf(stderr, "         CL_DEVICE_MAX_MEM_ALLOC_SIZE....... %lu\n", maxMemAllocSize);

               cl_ulong globalMemSize;
               status = clGetDeviceInfo(deviceIds[deviceIdx], CL_DEVICE_GLOBAL_MEM_SIZE,  sizeof(globalMemSize), &globalMemSize, NULL);
               fprintf(stderr, "         CL_DEVICE_GLOBAL_MEM_SIZE.......... %lu\n", globalMemSize);

               cl_ulong localMemSize;
               status = clGetDeviceInfo(deviceIds[deviceIdx], CL_DEVICE_LOCAL_MEM_SIZE,  sizeof(localMemSize), &localMemSize, NULL);
               fprintf(stderr, "         CL_DEVICE_LOCAL_MEM_SIZE........... %lu\n", localMemSize);



               char profile[2048];
               status = clGetDeviceInfo(deviceIds[deviceIdx], CL_DEVICE_PROFILE,  sizeof(profile), &profile, NULL);
               fprintf(stderr, "         CL_DEVICE_PROFILE.................. %s\n", profile);

               char deviceVersion[2048];
               status = clGetDeviceInfo(deviceIds[deviceIdx], CL_DEVICE_VERSION,  sizeof(deviceVersion), &deviceVersion, NULL);
               fprintf(stderr, "         CL_DEVICE_VERSION.................. %s\n", deviceVersion);

               char driverVersion[2048];
               status = clGetDeviceInfo(deviceIds[deviceIdx], CL_DRIVER_VERSION,  sizeof(driverVersion), &driverVersion, NULL);
               fprintf(stderr, "         CL_DRIVER_VERSION.................. %s\n", driverVersion);

               char cVersion[2048];
               status = clGetDeviceInfo(deviceIds[deviceIdx], CL_DEVICE_OPENCL_C_VERSION,  sizeof(cVersion), &cVersion, NULL);
               fprintf(stderr, "         CL_DEVICE_OPENCL_C_VERSION......... %s\n", cVersion);

               char name[2048];
               status = clGetDeviceInfo(deviceIds[deviceIdx], CL_DEVICE_NAME,  sizeof(name), &name, NULL);
               fprintf(stderr, "         CL_DEVICE_NAME..................... %s\n", name);
               char extensions[2048];
               status = clGetDeviceInfo(deviceIds[deviceIdx], CL_DEVICE_EXTENSIONS,  sizeof(extensions), &extensions, NULL);
               fprintf(stderr, "         CL_DEVICE_EXTENSIONS............... %s\n", extensions);

               fprintf(stderr, "      }\n");
            }

         }
         fprintf(stderr, "   }\n");
      }
      fprintf(stderr, "}\n");
   }
}
