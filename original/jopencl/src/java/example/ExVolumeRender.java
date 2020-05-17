package example;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.opencl.CLCommandQueue;
import javax.opencl.CLContext;
import javax.opencl.CLDevice;
import javax.opencl.CLImageFormat;
import javax.opencl.CLKernel;
import javax.opencl.CLMem;
import javax.opencl.CLProgram;
import javax.opencl.OpenCL;

public class ExVolumeRender
	{

	CLContext clc;

	File volumeFilename = new File("example/Bucky.raw");
	int[] volumeSize = new int[]{32, 32, 32};

	int width = 512, height = 512;
	int[] gridSize = new int[]{width, height};

	//float viewRotation[3];
	//float[] viewTranslation = new float[]{0.0f, 0.0f, -4.0f};
//	float invViewMatrix[12];

	float density = 0.05f;
	float brightness = 1.0f;
	float transferOffset = 0.0f;
	float transferScale = 1.0f;
	boolean linearFiltering = true;

	byte[] hVolume;
	
	CLKernel kernel;
	
  // create transfer function texture
  float transferFunc[] = {
       0.0f, 0.0f, 0.0f, 0.0f, 
       1.0f, 0.0f, 0.0f, 1.0f, 
       1.0f, 0.5f, 0.0f, 1.0f, 
       1.0f, 1.0f, 0.0f, 1.0f, 
       0.0f, 1.0f, 0.0f, 1.0f, 
       0.0f, 1.0f, 1.0f, 1.0f, 
       0.0f, 0.0f, 1.0f, 1.0f, 
       1.0f, 0.0f, 1.0f, 1.0f, 
       0.0f, 0.0f, 0.0f, 0.0f, 
  };

	public static byte[] readFileRaw(File file) throws IOException
		{
		ByteArrayOutputStream os=new ByteArrayOutputStream();
		FileInputStream is=new FileInputStream(file);
		byte[] buf=new byte[1024];
		int ret;
		while((ret=is.read(buf))!=-1)
			os.write(buf, 0, ret);
		return os.toByteArray();
		}

	public void foo()
		{
		
		
		
		try
			{
			//clc=OpenCL.createContext(OpenCL.CL_DEVICE_TYPE_GPU);
			clc=OpenCL.createContext(OpenCL.CL_DEVICE_TYPE_CPU);
			CLDevice device=Common.getFastestDevice(clc);
			CLCommandQueue cq=clc.createCommandQueue(device); //should this be under device?
			
			//Program
			CLProgram prog=clc.createProgram(Common.readFile(new File("example/volumeRenderer.cl")));
			prog.build();
			kernel=prog.createKernel("d_render");
			
			hVolume=readFileRaw(volumeFilename);
	    initOpenCL(hVolume);
	    initPixelBuffer();
			
			
			
			
			
			
			}
		catch (IOException e)
			{
			// TODO Auto-generated catch block
			e.printStackTrace();
			}

		
		
		
		
		}
  
  public static void main(String[] args)
		{
//		foo();
		}
  
	public void initPixelBuffer()
		{
		/*
		 * 
		 *  if (pbo) {
        // delete old buffer
        clReleaseMemObject(pbo_cl);
        glDeleteBuffersARB(1, &pbo);
    }

    // create pixel buffer object for display
    glGenBuffersARB(1, &pbo);
	glBindBufferARB(GL_PIXEL_UNPACK_BUFFER_ARB, pbo);
	glBufferDataARB(GL_PIXEL_UNPACK_BUFFER_ARB, width * height * sizeof(GLubyte) * 4, 0, GL_STREAM_DRAW_ARB);
	glBindBufferARB(GL_PIXEL_UNPACK_BUFFER_ARB, 0);

#ifdef GL_INTEROP
    // create OpenCL buffer from GL PBO
    pbo_cl = clCreateFromGLBuffer(cxGPUContext,CL_MEM_WRITE_ONLY, pbo, &ciErrNum);
#else            
    pbo_cl = clCreateBuffer(cxGPUContext, CL_MEM_WRITE_ONLY, width * height * sizeof(GLubyte) * 4, NULL, &ciErrNum);
#endif

    // calculate new grid size
    gridSize[0] = width;
    gridSize[1] = height;

    ciErrNum |= clSetKernelArg(ckKernel, 0, sizeof(cl_mem), (void *) &pbo_cl);
    ciErrNum |= clSetKernelArg(ckKernel, 1, sizeof(unsigned int), &width);
    ciErrNum |= clSetKernelArg(ckKernel, 2, sizeof(unsigned int), &height);
		 * 
		 * 
		 */
		}
  
  public void initOpenCL(byte[] hVolume)
  	{
  	// create 3D array and copy data to device
  	CLImageFormat volumeFormat=new CLImageFormat(OpenCL.CL_R,OpenCL.CL_UNORM_INT8);
  	CLMem d_volumeArray=clc.createImage3D(OpenCL.CL_MEM_READ_ONLY | OpenCL.CL_MEM_COPY_HOST_PTR, volumeFormat,
  			volumeSize[0],volumeSize[1], volumeSize[2],
  			volumeSize[0],volumeSize[0] * volumeSize[1],
  			hVolume);

  	//copy transfer function
  	CLImageFormat transferFunc_format=new CLImageFormat(OpenCL.CL_RGBA,OpenCL.CL_FLOAT);
  	CLMem d_transferFuncArray = clc.createImage2D(
  			OpenCL.CL_MEM_READ_ONLY | OpenCL.CL_MEM_COPY_HOST_PTR, transferFunc_format,
  			9,1,9*4,transferFunc);

  	kernel.setKernelArg(8, d_volumeArray);
  	kernel.setKernelArg(9, d_transferFuncArray);

  	//init invViewMatrix
  	CLMem d_invViewMatrix=clc.createBuffer(OpenCL.CL_MEM_READ_ONLY, Float.class, 12);

  	kernel.setKernelArg(8, d_invViewMatrix);
  	}


	
	
	
	
	
	/**
	 * 
	 * 
	 * 
	 * 
	 * 


// Constants, defines, typedefs and global declarations
// Uncomment this #define to enable CL/GL Interop
//#define GL_INTEROP

GLuint pbo = 0;                 // OpenGL pixel buffer object
int iGLUTWindowHandle;          // handle to the GLUT window

// OpenCL vars
cl_context cxGPUContext;
cl_device_id device;
cl_command_queue cqCommandQue;
cl_program cpProgram;
cl_kernel ckKernel;
cl_int ciErrNum;
cl_mem pbo_cl;
cl_mem d_volumeArray;
cl_mem d_transferFuncArray;
cl_mem d_invViewMatrix;
cl_sampler d_volumeSampler;
cl_sampler d_transferFuncSampler;
char* cPathAndName = NULL;          // var for full paths to data, src, etc.
char* cSourceCL;                    // Buffer to hold source for compilation 

int iFrameCount = 0;                // FPS count for averaging
int iFrameTrigger = 20;             // FPS trigger for sampling
int iFramesPerSec = 0;              // frames per second
int iTestSets = 2;
int g_Index = 0;
bool g_bQAReadback = false;
bool g_bFBODisplay = false;
shrBOOL bQuickTest = shrFALSE;     
int ox, oy;                         // mouse location vars
int buttonState = 0;                


// render image using OpenCL
void render()
{
    ciErrNum = CL_SUCCESS;

    // Transfer ownership of buffer from GL to CL
#ifdef GL_INTEROP
    // Acquire PBO for OpenCL writing
    ciErrNum |= clEnqueueAcquireGLObjects(cqCommandQue, 1, &pbo_cl, 0, 0, 0);
#endif    
    ciErrNum |= clEnqueueWriteBuffer(cqCommandQue,d_invViewMatrix,CL_FALSE, 0,12*sizeof(float), invViewMatrix, 0, 0, 0);

    // execute OpenCL kernel, writing results to PBO
    ciErrNum |= clSetKernelArg(ckKernel, 3, sizeof(float), &density);
    ciErrNum |= clSetKernelArg(ckKernel, 4, sizeof(float), &brightness);
    ciErrNum |= clSetKernelArg(ckKernel, 5, sizeof(float), &transferOffset);
    ciErrNum |= clSetKernelArg(ckKernel, 6, sizeof(float), &transferScale);
    ciErrNum |= clEnqueueNDRangeKernel(cqCommandQue, ckKernel, 2, NULL, gridSize,NULL, 0, 0, 0);
    shrCheckErrorEX (ciErrNum, CL_SUCCESS, pCleanup);

#ifdef GL_INTEROP
    // Transfer ownership of buffer back from CL to GL    
    ciErrNum |= clEnqueueReleaseGLObjects(cqCommandQue, 1, &pbo_cl, 0, 0, 0);
#else
    // Explicit Copy 
    // map the PBO to copy data from the CL buffer via host
    glBindBufferARB(GL_PIXEL_UNPACK_BUFFER_ARB, pbo);    

    // map the buffer object into client's memory
    GLubyte* ptr = (GLubyte*)glMapBufferARB(GL_PIXEL_UNPACK_BUFFER_ARB,
                                        GL_WRITE_ONLY_ARB);
    clEnqueueReadBuffer(cqCommandQue, pbo_cl, CL_TRUE, 0, sizeof(unsigned int) * height * width, ptr, 0, NULL, NULL);        
    shrCheckErrorEX (ciErrNum, CL_SUCCESS, pCleanup);
    glUnmapBufferARB(GL_PIXEL_UNPACK_BUFFER_ARB); 
#endif
}

// Display callback for GLUT main loop
void DisplayGL()
{
    // use OpenGL to build view matrix
    GLfloat modelView[16];
    glMatrixMode(GL_MODELVIEW);
    glPushMatrix();
    glLoadIdentity();
    glRotatef(-viewRotation[0], 1.0, 0.0, 0.0);
    glRotatef(-viewRotation[1], 0.0, 1.0, 0.0);
    glTranslatef(-viewTranslation[0], -viewTranslation[1], -viewTranslation[2]);
    glGetFloatv(GL_MODELVIEW_MATRIX, modelView);
    glPopMatrix();

    invViewMatrix[0] = modelView[0]; invViewMatrix[1] = modelView[4]; invViewMatrix[2] = modelView[8]; invViewMatrix[3] = modelView[12];
    invViewMatrix[4] = modelView[1]; invViewMatrix[5] = modelView[5]; invViewMatrix[6] = modelView[9]; invViewMatrix[7] = modelView[13];
    invViewMatrix[8] = modelView[2]; invViewMatrix[9] = modelView[6]; invViewMatrix[10] = modelView[10]; invViewMatrix[11] = modelView[14];

    render();

    // display results
    glClear(GL_COLOR_BUFFER_BIT);

    // draw image from PBO
    glDisable(GL_DEPTH_TEST);
    glRasterPos2i(0, 0);
    glBindBufferARB(GL_PIXEL_UNPACK_BUFFER_ARB, pbo);
    glDrawPixels(width, height, GL_RGBA, GL_UNSIGNED_BYTE, 0);
    glBindBufferARB(GL_PIXEL_UNPACK_BUFFER_ARB, 0);

    // flip backbuffer to screen
    glutSwapBuffers();
    glutPostRedisplay();
}

*/
	
	}
