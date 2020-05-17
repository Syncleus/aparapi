package javax.opencl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import example.ExNBody;

/**
 * OpenCL context
 * 
 * @author Johan Henriksson
 *
 */
public class CLContext extends OpenCL{
   int id;

   /**
    * Create an OpenCL context
    * @param deviceType Any of CL_DEVICE_TYPE_*
    */
   protected CLContext(int deviceType) {
      int ret = _createContext(deviceType);
      assertSuccess(ret);
   }

   public void retain() {
      int ret = _retainContext(id);
      assertSuccess(ret);
   }

   public void release() {
      int ret = _releaseContext(id);
      assertSuccess(ret);
   }

   private native int _createContext(int deviceType);

   private native int _retainContext(int contextID);

   private native int _releaseContext(int contextID);

   private native int[] _getContextInfoDevices(int contextID);

   /*
   
   extern  cl_context 
   clCreateContext(cl_context_properties * properties,
                   cl_uint                 num_devices,
                   const cl_device_id *    devices,
                   void (*pfn_notify)(const char *, const void *, size_t, void *), 
                   void *                  user_data,
                   cl_int *                errcode_ret) ;

   extern  cl_context 
   clCreateContextFromType(cl_context_properties * properties,
                           cl_device_type          device_type,
                           void (*pfn_notify)(const char *, const void *, size_t, void *) ,
                           void *                  user_data,
                           cl_int *                errcode_ret) ;

   */

   public CLDevice[] getContextDevices() {
      int[] d = _getContextInfoDevices(id);
      if (d == null)
         throw new CLException("failing to get info");
      CLDevice[] devs = new CLDevice[d.length];
      for (int i = 0; i < d.length; i++)
         devs[i] = new CLDevice(d[i]);
      return devs;
   }

   public CLCommandQueue createCommandQueue(CLDevice deviceID) {
      return new CLCommandQueue(this, deviceID);
   }

   public CLMem createBuffer(int memFlags, int[] initData) {
      return CLMem.createBuffer(this, memFlags, initData);
   }

   public CLMem createBuffer(int memFlags, float[] initData) {
      return CLMem.createBuffer(this, memFlags, initData);
   }

   public CLMem createBuffer(int memFlags, Class<?> cls, int numElem) {
      return CLMem.createBuffer(this, memFlags, cls, numElem);
   }

   public CLMem createImage2D(int memFlags, CLImageFormat imageFormat, int imageW, int imageH, int imageRowPitch, byte[] buffer) {
      return CLMem.createImage2D(this, memFlags, imageFormat, imageW, imageH, imageRowPitch, buffer);
   }

   public CLMem createImage2D(int memFlags, CLImageFormat imageFormat, int imageW, int imageH, int imageRowPitch, float[] buffer) {
      return CLMem.createImage2D(this, memFlags, imageFormat, imageW, imageH, imageRowPitch, buffer);
   }

   public CLMem createImage3D(int memFlags, CLImageFormat imageFormat, int imageW, int imageH, int imageD, int imageRowPitch,
         int imageSlicePitch, byte[] buffer) {
      return CLMem.createImage3D(this, memFlags, imageFormat, imageW, imageH, imageD, imageRowPitch, imageSlicePitch, buffer);
   }

   public CLProgram createProgram(String source) {
      return new CLProgram(this, source);
   }

   public CLProgram createProgram(InputStream source)
   {

      StringBuilder sb = new StringBuilder();

      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(source));
      try {
         for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
            sb.append(line).append("\n");
         }
      } catch (IOException ioe) {

      }
      return createProgram(sb.toString());

   }

   public CLSampler createSampler(boolean normalized, int adressingMode, int filterMode) {
      return new CLSampler(this, normalized, adressingMode, filterMode);
   }

}
