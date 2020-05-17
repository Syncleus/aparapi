package example;

import java.util.Random;

import javax.opencl.CLCommandQueue;
import javax.opencl.CLContext;
import javax.opencl.CLDevice;
import javax.opencl.CLKernel;
import javax.opencl.CLMem;
import javax.opencl.CLProgram;
import javax.opencl.OpenCL;

public class ExLeeMazeRouter{

   public static void populate(int[] _grid, int _width, int _height) {
      // we don't have to do this but just for clarity
      for (int x = 0; x < _width; x++) {
         for (int y = 0; y < _height; y++) {
            set(_grid, _width, _height, x, y, 0);
         }
      }

      box(_grid, _width, _height, 20, 20, 24, 24);
      box(_grid, _width, _height, 23, 24, 30, 27);
      box(_grid, _width, _height, 23, 14, 28, 20 );
      box(_grid, _width, _height, 14, 20, 20, 24 );
      set(_grid, _width, _height, 18, 18, 2);
      set(_grid, _width, _height, 28, 28, 3);
   }

   private static void set(int[] _grid, int _width, int _height, int _x, int _y, int _val) {
      if (_x < _width && _y < _height) {
         _grid[(_x * _height) + _y] = _val;
      } else {
         throw new IllegalArgumentException("attempt to set " + _x + "," + _y);
      }
   }

   private static int get(int[] _grid, int _width, int _height, int _x, int _y) {
      int val = 0;
      if (_x < _width && _y < _height) {
         val = _grid[(_x * _height) + _y];
      } else {
         throw new IllegalArgumentException("attempt to get " + _x + "," + _y);
      }
      return (val);
   }

   private static void box(int[] _grid, int _width, int _height, int _x1, int _y1, int _x2, int _y2) {
      for (int x = _x1; x < _x2; x++) {
         for (int y = _y1; y < _y2; y++) {
            set(_grid, _width, _height, x, y, -1);
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

      int width = 32;
      int height = width;
      int[] grid = new int[width * height];
      int[] xy = new int[]{-1,-1}; // intersection will be here

      populate(grid, width, height);

      show(grid, width, height);
      CLMem gridBuffer = clc.createBuffer(OpenCL.CL_MEM_READ_WRITE | OpenCL.CL_MEM_COPY_HOST_PTR, grid);
      CLMem xyBuffer = clc.createBuffer(OpenCL.CL_MEM_READ_WRITE | OpenCL.CL_MEM_COPY_HOST_PTR, xy);

      CLProgram clp = clc.createProgram(ExLeeMazeRouter.class.getResourceAsStream("LeeMazeRouter.cl"));

      clp.build(); // Note: Blocking call

      // Create a kernel from the programm this is the entrypoint
      CLKernel kernel = clp.createKernel("leeMazeRouter");

      // Set the args
      kernel.setKernelArg(0, gridBuffer);
      kernel.setKernelArg(1, xyBuffer);
      kernel.setKernelArg(2, width);
      kernel.setKernelArg(3, height);

      int[] globalSize = new int[] {
         width * height
      };
      int[] localSize = new int[] {
         16
      };

      for (int pass=2; pass < 40; pass+=2){
         kernel.setKernelArg(4, pass);
         // Then we launch the Kernel
         cq.enqueueNDRangeKernel(kernel, 1, null, globalSize, localSize, null);
         // Copy the grid array from the buffer
         cq.enqueueReadBuffer(gridBuffer, true, 0, grid.length, grid, null);
         cq.enqueueReadBuffer(xyBuffer, true, 0, xy.length, xy, null);
         if (xy[0]>=0 && xy[1]>=0){
            System.out.println("Solution passes through "+xy[0]+","+xy[1]);
            set(grid, width, height, xy[0], xy[1], -2);
            show(grid, width, height);
            break;
         }
         show(grid, width, height);
      }


      // We are finished with GPU memory so we can free it
      gridBuffer.release();
   }

   private static void show(int[] _grid, int _width, int _height) {
      for (int x = 0; x < _width; x++) {
         for (int y = 0; y < _height; y++) {
            int val = get(_grid, _width, _height, x, y);
            if (val == -2) {
               System.out.printf("<+>");
            }else if (val == -1) {
               System.out.printf("###");
            }else if (val >0){
               System.out.printf("%3d", val);
            } else {
               System.out.printf(" . ");
            }
         }
         System.out.println();
      }

   }
}
