package example;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Random;

import javax.opencl.CLCommandQueue;
import javax.opencl.CLContext;
import javax.opencl.CLDevice;
import javax.opencl.CLKernel;
import javax.opencl.CLMem;
import javax.opencl.CLProgram;
import javax.opencl.OpenCL;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ExNBodyWithUI{

   public static class NBodyViewPanel extends JPanel{
      private float[] scaled;

      private long elapsedns;

      private int frame;

      public void paintComponent(Graphics g) {
         super.paintComponent(g);
         Graphics2D g2d = (Graphics2D) g;
         g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

         for (int i = 0; i < scaled.length; i += 4) {
            g2d.fillOval((int) scaled[i], (int) scaled[i + 1], (int) scaled[i + 3], (int) scaled[i + 3]);
         }
         int elapsedMs = (int) (elapsedns / 1000000L);
         int elapsedS = elapsedMs / 1000;
         int framesPerSecond = elapsedS > 0 ? frame / elapsedS : 0;
         String info = String.format("Frame %4d, ElapsedMs %6d, Frames/Sec %4d", frame, elapsedMs, framesPerSecond);
         g2d.drawString(info, 10, 20);

      }

      public void update(float _scaled[], int _frame, long _elapsedns) {
         scaled = _scaled;
         frame = _frame;
         elapsedns = _elapsedns;
         repaint();
      }
   }

   public static String source;

   private static Random random = new Random();

   public static float random(float _min, float _max) {
      return (_min + random.nextFloat() * (_max - _min));
   }

   public static void main(String[] args) {

      CLContext clc = OpenCL.createContext(OpenCL.CL_DEVICE_TYPE_CPU);

      // Then we can get the list of GPU devices associated with this context
      CLDevice[] devices = clc.getContextDevices();

      // Create a command-queue on the first GPU device
      CLCommandQueue cq = clc.createCommandQueue(devices[0]);

      // Allocate GPU memory for the source vectors and initialize with the CPU memory
      int bodies = 512;
      int groups = 64;
      float[] position = new float[bodies * 4]; // we really want a float4 vector
      float[] velocity = new float[bodies * 4];
      
      ExNBody.populatePositionAndVelocity(bodies, position, velocity);

   
      float[] scaled = new float[bodies * 4];
      CLMem positionBuffer = clc.createBuffer(OpenCL.CL_MEM_READ_WRITE | OpenCL.CL_MEM_COPY_HOST_PTR, position);
      CLMem velocityBuffer = clc.createBuffer(OpenCL.CL_MEM_READ_WRITE | OpenCL.CL_MEM_COPY_HOST_PTR, velocity);
      CLMem scaledBuffer = clc.createBuffer(OpenCL.CL_MEM_READ_WRITE | OpenCL.CL_MEM_COPY_HOST_PTR, scaled);
      float deltaTime = 0.005f;
      float epsSqr = 50.00f;
      int width = 800;
      int height = 800;

      CLProgram clp = clc.createProgram(ExNBodyWithUI.class.getResourceAsStream("NBody.cl"));

      clp.build(); // Note: Blocking call

      System.out.println("# devices associated: " + clp.getNumDevices());
      System.out.println("build status: " + clp.getBuildStatus(devices[0]));

      // Then we can create a handle to the compiled OpenCL function (Kernel)
      CLKernel kernel = clp.createKernel("nbody_sim");

      System.out.println("#refcount: " + kernel.getRefCount());
      System.out.println("#args: " + kernel.getKernelNumArgs());

      // In the next step we associate the GPU memory with the Kernel arguments
      kernel.setKernelArg(0, positionBuffer);
      kernel.setKernelArg(1, scaledBuffer);
      kernel.setKernelArg(2, velocityBuffer);
      kernel.setKernelArg(3, bodies);
      kernel.setKernelArg(4, deltaTime);
      kernel.setKernelArg(5, epsSqr);
      kernel.setKernelArg(6, width);
      kernel.setKernelArg(7, height);
      kernel.setKernelArg(8, Float.class, bodies * 4);

      NBodyViewPanel nBodyViewPanel = new NBodyViewPanel();
      nBodyViewPanel.setBackground(Color.black);
      nBodyViewPanel.setForeground(Color.white);

      nBodyViewPanel.setPreferredSize(new Dimension(width, height));

      JFrame frame = new JFrame();
      frame.getContentPane().add(nBodyViewPanel);
      frame.pack();
      frame.setVisible(true);
      long start = System.nanoTime();
      int[] globalSize = new int[] {
            bodies
      };
      int[] localSize = new int[] {
            groups
      };
      for (int i = 0; i < 1000; i++) {
         // Then we launch the Kernel on the GPU
         cq.enqueueNDRangeKernel(kernel, 1, null, globalSize, localSize, null);
         
         // Copy the output in GPU memory back to CPU memory

         cq.enqueueReadBuffer(scaledBuffer, true, 0, bodies * 4, scaled, null);

         nBodyViewPanel.update(scaled, i, System.nanoTime() - start);
         if (false) {
            for (int c = 0; c < bodies; c++) {
               int offset = c * 4;
               float x = scaled[offset];

               float y = scaled[offset + 1];
               float radius = scaled[offset + 2];
               System.out.println(c + " " + x + "," + y + "," + radius);
            }
         }
      }
      // We are finished with GPU memory so we can free it
      positionBuffer.release();
      scaledBuffer.release();
      velocityBuffer.release();
      

   }
}
