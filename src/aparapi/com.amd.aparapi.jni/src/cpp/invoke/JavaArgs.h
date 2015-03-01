#ifndef JAVA_ARGS_H
#define JAVA_ARGS_H

#include "com_amd_aparapi_internal_opencl_OpenCLArgDescriptor.h"
#include "com_amd_aparapi_internal_opencl_OpenCLMem.h"

#define arg(token) (com_amd_aparapi_internal_opencl_OpenCLArgDescriptor_ARG_##token##_BIT)
#define argisset(bits, token) (((bits) & arg(token)) == arg(token))
#define argset(bits, token) (bits) |= arg(token)
#define argreset(bits, token) (bits) &= ~arg(token)

#define mem(token) (com_amd_aparapi_internal_opencl_OpenCLMem_MEM_##token##_BIT)
#define memisset(bits, token) (((bits) & mem(token)) == mem(token))
#define memadd(bits, token) (bits) |= mem(token)
#define memreset(bits, token) (bits) &= ~mem(token)

#endif //JAVA_ARGS_H
