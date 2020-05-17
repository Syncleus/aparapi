#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <malloc.h>
#include <CL/cl.h>
#include <jni.h>

#include "javax_opencl_CLCommandQueue.h"
#include "javax_opencl_CLContext.h"
#include "javax_opencl_CLDevice.h"
#include "javax_opencl_CLKernel.h"
#include "javax_opencl_CLMem.h"
#include "javax_opencl_CLPlatform.h"
#include "javax_opencl_CLProgram.h"
#include "javax_opencl_CLSampler.h"
#include "javax_opencl_OpenCL.h"

/* some */

#define GETCLASS jclass cls=jenv->GetObjectClass(jobj);
#define SETID(X) {jfieldID fid=jenv->GetFieldID(cls, "id",  "I"); jenv->SetIntField(jobj, fid, (jint)X);}

//I believe it is safe to assume cl_ scalar values==j*. Pointers might be different.


/////////////////////// device ////////////////////

JNIEXPORT jstring JNICALL Java_javax_opencl_CLDevice__1getDeviceInfoString
  (JNIEnv *jenv, jobject jobj, jint did, jint param_name)
	{
	size_t size;
	clGetDeviceInfo((cl_device_id)did, param_name, 0, NULL, &size);
	char *str=(char *) malloc(size);
	clGetDeviceInfo((cl_device_id)did, param_name, size, str, NULL);
	jstring ret=jenv->NewStringUTF(str);
	free(str);
	return ret;
	}



/////////////////////// platform ////////////////////

JNIEXPORT jintArray JNICALL Java_javax_opencl_OpenCL__1getPlatforms(JNIEnv *jenv, jclass cls)
	{
	cl_uint numPlatforms;
	clGetPlatformIDs(0,NULL,&numPlatforms);
	cl_platform_id *platforms=(cl_platform_id *)malloc(sizeof(cl_platform_id)*numPlatforms);
	clGetPlatformIDs(numPlatforms,platforms,&numPlatforms);
	jintArray ret=(jintArray)jenv->NewIntArray(numPlatforms);
	for(int i=0;i<numPlatforms;i++)
		{
		jint foo=(jint)platforms[i]; //Stew value into an integer
		jenv->SetIntArrayRegion(ret,i,1,&foo);
		}
	free(platforms);
	return ret;
	}


JNIEXPORT jstring JNICALL Java_javax_opencl_CLPlatform__1getPlatformInfoString
  (JNIEnv *jenv, jobject jobj, jint plid, jint param_name)
	{
	size_t size;
	clGetPlatformInfo((cl_platform_id)plid, param_name, 0, NULL, &size);
	char *str=(char*)malloc(size);
	clGetPlatformInfo((cl_platform_id)plid, param_name, size, str, NULL);
	jstring ret=jenv->NewStringUTF(str);
	free(str);
	return ret;
	}


/////////////////////// kernel ////////////////////


JNIEXPORT jint JNICALL Java_javax_opencl_CLKernel__1createKernel(JNIEnv *jenv, jobject jobj, jint pid, jstring name)
	{
	GETCLASS
   // grf is this really jbyte or char *? i changed to const char
	const char*str = jenv->GetStringUTFChars( name, NULL );
	//printf("-%s-\n",str);
	cl_int ret;
	cl_kernel kid=clCreateKernel ((cl_program)pid, str, &ret);
	jenv->ReleaseStringUTFChars(name, str);
	SETID(kid)
//Java_javax_opencl_CLKernel__1getKernelNumArgs(jenv,jobj,kid);
	return ret;
	}

JNIEXPORT jint JNICALL Java_javax_opencl_CLKernel__1getKernelNumArgs(JNIEnv *jenv, jobject jobj, jint kid)
	{
	cl_uint args;
	cl_int ret=clGetKernelInfo ((cl_kernel)kid,CL_KERNEL_NUM_ARGS,sizeof(args),&args,NULL);
	if(ret!=CL_SUCCESS)
		printf("Error calling getKernelInfo, %d\n",ret);
	return args;
	}

JNIEXPORT jint JNICALL Java_javax_opencl_CLKernel__1getRefCount(JNIEnv *jenv, jobject jobj, jint kid)
	{
	cl_uint args;
	cl_int ret=clGetKernelInfo ((cl_kernel)kid,CL_KERNEL_REFERENCE_COUNT,sizeof(args),&args,NULL);
	if(ret!=CL_SUCCESS)
		printf("Error calling getKernelInfo, %d\n",ret);
	return args;
	}

JNIEXPORT jint JNICALL Java_javax_opencl_CLKernel__1retainKernel(JNIEnv *jenv, jobject jobj, jint kid)
	{
	return clRetainKernel((cl_kernel)kid);
	}

JNIEXPORT jint JNICALL Java_javax_opencl_CLKernel__1releaseKernel(JNIEnv *jenv, jobject jobj, jint kid)
	{
	return clReleaseKernel((cl_kernel)kid);
	}


JNIEXPORT jint JNICALL Java_javax_opencl_CLKernel__1setKernelArg4
  (JNIEnv *jenv, jobject jobj, jint kid, jint index, jint value)
	{
	cl_int setval=value;
   fprintf(stderr, "int arg %d = %d\n", index, value);
	cl_int ret=clSetKernelArg ((cl_kernel)kid, index, sizeof(cl_int), &setval);
	return ret;
	}

JNIEXPORT jint JNICALL Java_javax_opencl_CLKernel__1setKernelArgFloat4
  (JNIEnv *jenv, jobject jobj, jint kid, jint index, jfloat value)
	{
	cl_float setval=value;
   fprintf(stderr, "float arg %d = %f\n", index, value);
	cl_int ret=clSetKernelArg ((cl_kernel)kid, index, sizeof(cl_float), &setval);
	return ret;
	}

JNIEXPORT jint JNICALL Java_javax_opencl_CLKernel__1setKernelArgNull
  (JNIEnv *jenv, jobject jobj, jint kid, jint index, jint size)
	{
	cl_int ret=clSetKernelArg ((cl_kernel)kid, index, size, NULL);
	return ret;
	}



/////////////////////// context ////////////////////

JNIEXPORT jint JNICALL Java_javax_opencl_CLContext__1createContext(JNIEnv *jenv, jobject jobj, jint deviceType)
	{
	GETCLASS
	cl_int ret;
	cl_context context = clCreateContextFromType(0, deviceType, NULL, NULL, &ret);
	SETID(context)
	return ret;
	}


JNIEXPORT jint JNICALL Java_javax_opencl_CLContext__1retainContext(JNIEnv *jenv, jobject jobj, jint cid)
	{
	return clRetainContext((cl_context)cid);
	}

JNIEXPORT jint JNICALL Java_javax_opencl_CLContext__1releaseContext(JNIEnv *jenv, jobject jobj, jint cid)
	{
	return clReleaseContext((cl_context)cid);
	}



JNIEXPORT jintArray JNICALL Java_javax_opencl_CLContext__1getContextInfoDevices(JNIEnv *jenv, jobject jobj, jint cid)
	{
	cl_context context=(cl_context)cid;

	size_t cb;
	clGetContextInfo(context, CL_CONTEXT_DEVICES, 0, NULL, &cb);
	cl_device_id *devices = (cl_device_id *) malloc(cb);
	clGetContextInfo(context, CL_CONTEXT_DEVICES, cb, devices, NULL);
	int numElem=cb/sizeof(cl_device_id);

	jintArray ret=(jintArray)jenv->NewIntArray(numElem);
	for(int i=0;i<numElem;i++)
		{
		jint foo=(jint)devices[i]; //Stew value into an integer
		jenv->SetIntArrayRegion(ret,i,1,&foo);
		}
	free(devices);
	return ret;
	}


/////////////////////// mem ////////////////////


JNIEXPORT jint JNICALL Java_javax_opencl_CLMem__1createBufferFromInt(JNIEnv *jenv, jobject jobj, jint cid, jint memFlags, jintArray initData)
	{
	GETCLASS
	cl_int ret;
	jsize alen = jenv->GetArrayLength(initData);
	jint *abody = jenv->GetIntArrayElements(initData, 0);
	//ASSUMING!!! size of jint same as cl_int
	cl_mem mid=clCreateBuffer((cl_context)cid, memFlags,sizeof(jint)*alen,abody,&ret);
	jenv->ReleaseIntArrayElements(initData, abody, 0);
	SETID(mid)
	return ret;
	}

JNIEXPORT jint JNICALL Java_javax_opencl_CLMem__1createBufferFromFloat(JNIEnv *jenv, jobject jobj, jint cid, jint memFlags, jfloatArray initData)
	{
	GETCLASS
	cl_int ret;
	jsize alen = jenv->GetArrayLength(initData);
	jfloat *abody = jenv->GetFloatArrayElements(initData, 0);
	cl_mem mid=clCreateBuffer((cl_context)cid, memFlags,sizeof(jfloat)*alen,abody,&ret);
	jenv->ReleaseFloatArrayElements(initData, abody, 0);
	SETID(mid)
	return ret;
	}



JNIEXPORT jint JNICALL Java_javax_opencl_CLMem__1createBuffer(JNIEnv *jenv, jobject jobj, jint cid, jint memFlags, jint size)
	{
	GETCLASS
	cl_int ret;
	cl_mem mid=clCreateBuffer((cl_context)cid, memFlags,size,NULL,&ret);
	SETID(mid)
	return ret;
	}


void toclImFormat(JNIEnv *jenv, jobject jobj, cl_image_format *format)
	{
	GETCLASS
	jfieldID fidOrder=jenv->GetFieldID((jclass)jobj, "image_channel_order",  "I");
	jfieldID fidType=jenv->GetFieldID((jclass)jobj, "image_channel_data_type",  "I");
	format->image_channel_order=jenv->GetIntField((jclass)jobj, fidOrder);
	format->image_channel_data_type=jenv->GetIntField((jclass)jobj, fidType);
	}

JNIEXPORT jint JNICALL Java_javax_opencl_CLMem__1createImage2Dbyte
  (JNIEnv *jenv, jobject jobj, jint cid, jint memFlags, jobject imageFormat, jint imageW, jint imageH, jint imageRowPitch, jbyteArray initData)
	{
	GETCLASS
	cl_int ret;
	jsize alen = jenv->GetArrayLength(initData);
	jbyte *abody = jenv->GetByteArrayElements(initData, 0);

	cl_image_format image_format;
	toclImFormat(jenv, imageFormat, &image_format);
	cl_mem mid=clCreateImage2D((cl_context)cid, memFlags, &image_format, imageW, imageH, 
		imageRowPitch*sizeof(char), abody, &ret);

	jenv->ReleaseByteArrayElements(initData, abody, 0);
	SETID(mid)
	return ret;
	}

JNIEXPORT jint JNICALL Java_javax_opencl_CLMem__1createImage2Dfloat
  (JNIEnv *jenv, jobject jobj, jint cid, jint memFlags, jobject imageFormat, jint imageW, jint imageH, jint imageRowPitch, jfloatArray initData)
	{
	GETCLASS
	cl_int ret;
	jsize alen = jenv->GetArrayLength(initData);
	jfloat *abody = jenv->GetFloatArrayElements(initData, 0);

	cl_image_format image_format;
	toclImFormat(jenv, imageFormat, &image_format);
	cl_mem mid=clCreateImage2D((cl_context)cid, memFlags, &image_format, imageW, imageH, 
		imageRowPitch*sizeof(float), abody, &ret);

	jenv->ReleaseFloatArrayElements(initData, abody, 0);
	SETID(mid)
	return ret;
	}

JNIEXPORT jint JNICALL Java_javax_opencl_CLMem__1createImage3Dbyte
  (JNIEnv *jenv, jobject jobj, jint cid, jint memFlags, jobject imageFormat, jint imageW, jint imageH, 
  jint imageD, jint imageRowPitch, jint imageSlicePitch, jbyteArray initData)
	{
	GETCLASS
	cl_int ret;
	jsize alen = jenv->GetArrayLength(initData);
	jbyte *abody = jenv->GetByteArrayElements( initData, 0);

	cl_image_format image_format;
	toclImFormat(jenv, imageFormat, &image_format);
	cl_mem mid=clCreateImage3D((cl_context)cid, memFlags, &image_format, imageW, imageH, imageD, 
		imageRowPitch*sizeof(char), imageSlicePitch*sizeof(char), abody, &ret);

	jenv->ReleaseByteArrayElements(initData, abody, 0);
	SETID(mid)
	return ret;
	}




JNIEXPORT jint JNICALL Java_javax_opencl_CLMem__1getSize(JNIEnv *jenv, jobject jobj, jint mid)
	{
	size_t size;
	int ret=clGetMemObjectInfo((cl_mem)mid,CL_MEM_SIZE,sizeof(size), &size, NULL);
	if(ret!=CL_SUCCESS)
		printf("Error calling getMemObjectInfo, %d\n",ret);
	return size;
	}

JNIEXPORT jint JNICALL Java_javax_opencl_CLMem__1retainMem(JNIEnv *jenv, jobject jobj, jint mid)
	{
	return clRetainMemObject((cl_mem)mid);
	}

JNIEXPORT jint JNICALL Java_javax_opencl_CLMem__1releaseMem(JNIEnv *jenv, jobject jobj, jint mid)
	{
	return clReleaseMemObject((cl_mem)mid);
	}




/**


extern  cl_mem 
clCreateImage3D(cl_context              context,
               cl_mem_flags            flags,
               const cl_image_format * image_format,
               size_t                  image_width, 
               size_t                  image_height,
               size_t                  image_depth, 
               size_t                  image_row_pitch, 
               size_t                  image_slice_pitch, 
               void *                  host_ptr,
               cl_int *                errcode_ret) ;

*/



/////////////////// program //////////////////////


JNIEXPORT jint JNICALL Java_javax_opencl_CLProgram__1createProgram
  (JNIEnv *jenv, jobject jobj, jint cid, jstring src)
	{
	GETCLASS
	cl_int ret;
   // grf was *jbyte
	const char *str = jenv->GetStringUTFChars( src, NULL );
	cl_program pid=clCreateProgramWithSource((cl_context)cid,1,(const char **)&str,NULL,&ret);
	jenv->ReleaseStringUTFChars(src, str);
	SETID(pid)
	return ret;
	}

JNIEXPORT jint JNICALL Java_javax_opencl_CLProgram__1build
  (JNIEnv *jenv, jobject jobj, jint pid)
	{
	cl_int ret=clBuildProgram((cl_program)pid,0,NULL,NULL, NULL, NULL);
	return ret;
	}


JNIEXPORT jint JNICALL Java_javax_opencl_CLProgram__1getBuildStatus(JNIEnv *jenv, jobject jobj, jint pid, jint did)
	{
	cl_build_status size;
	int ret=clGetProgramBuildInfo((cl_program)pid,(cl_device_id)did,
		CL_PROGRAM_BUILD_STATUS,sizeof(size), &size, NULL);
	if(ret!=CL_SUCCESS)
		printf("Error calling getProgramInfo, %d\n",ret);
	return size;
	}


JNIEXPORT jint JNICALL Java_javax_opencl_CLProgram__1getNumDevices(JNIEnv *jenv, jobject jobj, jint pid)
	{
	cl_uint size;
	int ret=clGetProgramInfo((cl_program)pid,CL_PROGRAM_NUM_DEVICES,sizeof(size), &size, NULL);
	if(ret!=CL_SUCCESS)
		printf("Error calling getProgramInfo, %d\n",ret);
	return size;
	}


JNIEXPORT jint JNICALL Java_javax_opencl_CLProgram__1retain
  (JNIEnv *jenv, jobject jobj, jint pid)
	{
	return clRetainProgram((cl_program)pid);
	}

JNIEXPORT jint JNICALL Java_javax_opencl_CLProgram__1release
  (JNIEnv *jenv, jobject jobj, jint pid)
	{
	return clReleaseProgram((cl_program)pid);
	}


////////////////////// cq ////////////////////////////


cl_event *getWaitList(JNIEnv *jenv, jobject jobj, jintArray waitforID, cl_uint *len)
	{
	jsize alen = jenv->GetArrayLength(waitforID);
	jint *abody = jenv->GetIntArrayElements(waitforID, 0);
	if(alen==0)
		{
		(*len)=0;
		return NULL;
		}
	cl_event *events=(cl_event*) malloc(sizeof(cl_event)*alen);
	for(int i=0;i<alen;i++)
		events[i]=(cl_event)abody[i];
	jenv->ReleaseIntArrayElements(waitforID, abody, 0);
	(*len)=alen;
	return events;
	}

JNIEXPORT jint JNICALL Java_javax_opencl_CLCommandQueue__1createCommandQueue(JNIEnv *jenv, jobject jobj, jint cid, jint deviceID)
	{
	GETCLASS
	cl_int ret;
	cl_command_queue cqid=clCreateCommandQueue((cl_context)cid, (cl_device_id)deviceID, 0, &ret);
	SETID(cqid)
   return((jint)ret);
	}

JNIEXPORT jint JNICALL Java_javax_opencl_CLCommandQueue__1retain
  (JNIEnv *jenv, jobject jobj, jint qid)
	{
	return clRetainCommandQueue((cl_command_queue)qid);
	}

JNIEXPORT jint JNICALL Java_javax_opencl_CLCommandQueue__1release
  (JNIEnv *jenv, jobject jobj, jint qid)
	{
	return clReleaseCommandQueue((cl_command_queue)qid);
	}

JNIEXPORT jint JNICALL Java_javax_opencl_CLCommandQueue__1flush
  (JNIEnv *jenv, jobject jobj, jint qid)
	{
	return clFlush((cl_command_queue)qid);
	}

JNIEXPORT jint JNICALL Java_javax_opencl_CLCommandQueue__1finish(JNIEnv *jenv, jobject jobj, jint qid)
	{
	return clFinish((cl_command_queue)qid);
	}

cl_bool bool2cl(jboolean b)
	{
	return b;
	}


JNIEXPORT jint JNICALL Java_javax_opencl_CLCommandQueue__1enqueueBarrier
  (JNIEnv *jenv, jobject jobj, jint qid)
	{
	return clEnqueueBarrier((cl_command_queue)qid);
	}

JNIEXPORT jint JNICALL Java_javax_opencl_CLCommandQueue__1enqueueReadBufferInt
  (JNIEnv *jenv, jobject jobj, jint cqid, jint mid, jboolean blocking, jint offset, jint numElem, jintArray buffer, jintArray waitforID)
	{
	cl_uint waitlistlen=0;
	cl_event *waitlist=NULL;
	if(waitforID) waitlist=getWaitList(jenv,jobj,waitforID,&waitlistlen);

	jsize alen = jenv->GetArrayLength(buffer);
	jint *abody = jenv->GetIntArrayElements(buffer, 0);
	int ret=clEnqueueReadBuffer((cl_command_queue)cqid, (cl_mem)mid, bool2cl(blocking), offset, sizeof(jint)*alen, abody, waitlistlen,waitlist,NULL);

	jenv->ReleaseIntArrayElements(buffer, abody, 0);

	if (waitlist){ free(waitlist); }
	return ret;
	}

JNIEXPORT jint JNICALL Java_javax_opencl_CLCommandQueue__1enqueueReadBufferFloat
  (JNIEnv *jenv, jobject jobj, jint cqid, jint mid, jboolean blocking, jint offset, jint numElem, jfloatArray buffer, jintArray waitforID)
	{
	//cl_uint waitlistlen=0;
	//cl_event *waitlist=NULL;
	cl_uint waitlistlen=1;
	cl_event waitlist[1];
	//if(waitforID) waitlist=getWaitList(jenv,jobj,waitforID,&waitlistlen);

	jsize alen = jenv->GetArrayLength(buffer);
	jfloat *abody = jenv->GetFloatArrayElements(buffer, 0);
	//int ret=clEnqueueReadBuffer((cl_command_queue)cqid, (cl_mem)mid, bool2cl(blocking), offset, sizeof(jfloat)*alen, abody, waitlistlen,waitlist,NULL);
	int ret=clEnqueueReadBuffer((cl_command_queue)cqid, (cl_mem)mid, bool2cl(blocking), offset, sizeof(jfloat)*alen, abody, 0, NULL, &waitlist[0]);
        clWaitForEvents(1, &waitlist[0]);


        //for (int i=0; i<alen; i+=4){
        //   fprintf(stdout, "%d %f,%f,%f,%f\n", i/4, abody[i], abody[i+1], abody[i+2], abody[i+3]);
        //}
	jenv->ReleaseFloatArrayElements(buffer, abody, 0);


	//if (waitlist){ free(waitlist); }
	return ret;
	}

JNIEXPORT jint JNICALL Java_javax_opencl_CLCommandQueue__1enqueueReadBufferFloatSafe
  (JNIEnv *jenv, jobject jobj, jint cqid, jint mid, jboolean blocking, jint offset, jint numElem, jfloatArray buffer, jintArray waitforID)
	{
	cl_uint waitlistlen=1;
	cl_event waitlist[1];
   // grf i had to add this cast.  I think that there needs to be a separate method for floats
	//if(waitforID) waitlist=getWaitList(jenv,jobj,(jintArray)waitforID,&waitlistlen);

	jsize alen = jenv->GetArrayLength(buffer);
        fprintf(stderr, "len is %d\n", alen);
        jfloat *abody = (float*)jenv->GetPrimitiveArrayCritical((jarray)buffer, 0);
	//jfloat *abody = jenv->GetFloatArrayElements((jfloatArray)buffer, 0);
        //jfloat *abody = (jfloat*) memalign(16, alen*sizeof(float));
        int address = (int)abody;
        fprintf(stdout, "abody = %08x", address);
        if ((address & ~0x7) == address){
           fprintf(stdout, " 16 byte  aligned \n");
        }  else{
           fprintf(stdout, " NOT 16 byte  aligned \n");
}
        fprintf(stderr, "got abody alright!\n"); fflush(stderr); 
	int ret=clEnqueueReadBuffer((cl_command_queue)cqid, (cl_mem)mid, bool2cl(blocking), offset, sizeof(jfloat)*alen, abody, 0, NULL, &waitlist[0]);
        fprintf(stderr, "queue copy buffer!\n"); fflush(stderr); 
        clWaitForEvents(1, &waitlist[0]);
        fprintf(stderr, "wait finished!\n"); fflush(stderr); 
        fprintf(stderr, "copied to array in memory!!\n"); fflush(stderr); 

	//jenv->ReleaseFloatArrayElements((jfloatArray)buffer, abody, 0);
        jenv->ReleasePrimitiveArrayCritical((jarray)buffer, abody, 0);

	
	return ret;
	}



size_t *getSizetArray(JNIEnv *jenv, jobject jobj, jintArray list)
	{
	jsize alen = jenv->GetArrayLength(list);
	jint *abody = jenv->GetIntArrayElements( list, 0);
	size_t *out=(size_t *) malloc(sizeof(size_t)*alen);
	for(int i=0;i<alen;i++)
		out[i]=abody[i];
	jenv->ReleaseIntArrayElements( list, abody, 0);
	//(*len)=alen;
	return out;
	}


JNIEXPORT jint JNICALL Java_javax_opencl_CLCommandQueue__1enqueueNDRangeKernel
  (JNIEnv *jenv, jobject jobj, jint cqid, jint kid, jint workDim, jintArray globalOffset, jintArray globalSize,
  jintArray localSize, jintArray waitforID)
	{
	cl_uint waitlistlen=0;
	cl_event *waitlist=NULL;
	if(waitforID) waitlist=getWaitList(jenv,jobj,waitforID,&waitlistlen);

	size_t *global_work_offset=NULL;
	size_t *global_work_size=NULL;
	size_t *local_work_size=NULL;
	if(globalOffset) global_work_offset=getSizetArray(jenv,jobj,globalOffset);
	if(globalSize) global_work_size=getSizetArray(jenv,jobj,globalSize);
	if(localSize) local_work_size=getSizetArray(jenv,jobj,localSize);
	cl_int ret=clEnqueueNDRangeKernel((cl_command_queue)cqid, (cl_kernel)kid,
			workDim, global_work_offset,global_work_size,local_work_size,
			waitlistlen,waitlist, NULL) ;
	free(waitlist);
	free(global_work_offset);
	free(global_work_size);
	free(local_work_size);

	return ret;
	}



/////////////////////// sampler ////////////////////



JNIEXPORT jint JNICALL Java_javax_opencl_CLSampler__1createSampler
  (JNIEnv *jenv, jobject jobj, jint cid, jboolean normalized, jint adressingMode, jint filterMode)
	{
	GETCLASS
	cl_int ret;
	cl_sampler sid=clCreateSampler((cl_context)cid, normalized, adressingMode, filterMode, &ret);
	SETID(sid)
	return ret;
	}

JNIEXPORT jint JNICALL Java_javax_opencl_CLSampler__1retain
  (JNIEnv *jenv, jobject jobj, jint sid)
	{
	return clRetainSampler((cl_sampler)sid);
	}

JNIEXPORT jint JNICALL Java_javax_opencl_CLSampler__1release
  (JNIEnv *jenv, jobject jobj, jint sid)
	{
	return clReleaseSampler((cl_sampler)sid);
	}


