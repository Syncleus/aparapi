package example;

import java.util.Random;

import javax.opencl.CLCommandQueue;
import javax.opencl.CLContext;
import javax.opencl.CLDevice;
import javax.opencl.CLKernel;
import javax.opencl.CLMem;
import javax.opencl.CLProgram;
import javax.opencl.OpenCL;

public class ExNBody{

   private static Random random = new Random();

   public static float random(float _min, float _max) {
      return (_min + random.nextFloat() * (_max - _min));
   }

   public static void populatePositionAndVelocity(int _bodies, float[] _position, float[] _velocity) {
      for (int i = 0; i < _bodies; ++i) {
         int index = 4 * i;
         _position[index + 0] = random(20, 50); // x
         _position[index + 1] = random(20, 50); // y
         _position[index + 2] = random(300, 500); // z
         _position[index + 3] = random(200, 1000); // mass
         // First 3 values are velocity in x,y and z direction the last is unused
         for (int j = 0; j < 4; ++j) {
            _velocity[index + j] = 0.0f;
         }
      }

   }

   public static void main(String[] args) {
      // Create a context
      CLContext clc = OpenCL.createContext(OpenCL.CL_DEVICE_TYPE_CPU);

      // Then we can get the list of GPU devices associated with this context
      CLDevice[] devices = clc.getContextDevices();

      // Create a command-queue on the first GPU device
      CLCommandQueue cq = clc.createCommandQueue(devices[0]);

      int bodies = 512;
      int groups = 64;
      float[] position = new float[bodies * 4];
      float[] velocity = new float[bodies * 4];
      populatePositionAndVelocity(bodies, position, velocity);
      float[] scaled = new float[bodies * 4];
      CLMem positionBuffer = clc.createBuffer(OpenCL.CL_MEM_READ_WRITE | OpenCL.CL_MEM_COPY_HOST_PTR, position);
      CLMem velocityBuffer = clc.createBuffer(OpenCL.CL_MEM_READ_WRITE | OpenCL.CL_MEM_COPY_HOST_PTR, velocity);
      CLMem scaledBuffer = clc.createBuffer(OpenCL.CL_MEM_READ_WRITE | OpenCL.CL_MEM_COPY_HOST_PTR, scaled);

      CLProgram clp = clc.createProgram(ExNBody.class.getResourceAsStream("NBody.cl"));

      clp.build(); // Note: Blocking call

      // Create a kernel from the programm this is the entrypoint
      CLKernel kernel = clp.createKernel("nbody_sim");

      // Set the args
      kernel.setKernelArg(0, positionBuffer);
      kernel.setKernelArg(1, scaledBuffer);
      kernel.setKernelArg(2, velocityBuffer);
      kernel.setKernelArg(3, bodies);
      kernel.setKernelArg(4, 0.005f); // deltaTime
      kernel.setKernelArg(5, 50.00f); // epsSqr
      kernel.setKernelArg(6, 800); // width
      kernel.setKernelArg(7, 800); // height
      kernel.setKernelArg(8, Float.class, bodies * 4);

      int[] globalSize = new int[] {
         bodies
      };
      int[] localSize = new int[] {
         groups
      };

      // Then we launch the Kernel
      cq.enqueueNDRangeKernel(kernel, 1, null, globalSize, localSize, null);

      // Copy the scaled data out of the program
      cq.enqueueReadBuffer(scaledBuffer, true, 0, bodies * 4, scaled, null);

      for (int c = 0; c < bodies; c++) {
         System.out.println(c + " " + scaled[c * 4] + "," + scaled[c * 4 + 1] + "," + scaled[c * 4 + 2]);
      }

      // We are finished with GPU memory so we can free it
      positionBuffer.release();
      scaledBuffer.release();
      velocityBuffer.release();

   }

}
