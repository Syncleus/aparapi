package kerneltest;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;


public class NBody{


   public static class NBodyKernel extends Kernel{
      final private int rgb[];
      final private int width;
      final private int height;
      final float delT = .005f;
      final float espSqr = 1.0f;
      final float mass = 50f;
      private final Range range;
      private final float[] xyz; // positions xy and z of bodies
      private final float[] vxyz; // velocity component of x,y and z of bodies

      public NBodyKernel(int _width, int _height, int[] _rgb, int _bodies){
         width = _width;
         height = _height;
         rgb = _rgb;
         range = Range.create(_bodies);
         xyz = new float[range.getGlobalSize(0) * 3];
         vxyz = new float[range.getGlobalSize(0) * 3];
         final float maxDist = width / 2;
         for(int body = 0; body < (range.getGlobalSize(0) * 3); body += 3){
            final float theta = (float) (Math.random() * Math.PI * 2);
            final float phi = (float) (Math.random() * Math.PI * 2);
            final float radius = (float) (Math.random() * maxDist);
            xyz[body + 0] = (float) (radius * Math.cos(theta) * Math.sin(phi)) + width / 2;
            xyz[body + 1] = (float) (radius * Math.sin(theta) * Math.sin(phi)) + height / 2;
            xyz[body + 2] = (float) (radius * Math.cos(phi));
         }
         setExplicit(true);
      }

      public void run(){
         final int body = getGlobalId();
         final int count = getGlobalSize(0) * 3;
         final int globalId = body * 3;
         float accx = 0.f;
         float accy = 0.f;
         float accz = 0.f;
         final float myPosx = xyz[globalId + 0];
         final float myPosy = xyz[globalId + 1];
         final float myPosz = xyz[globalId + 2];
         for(int i = 0; i < count; i += 3){
            final float dx = xyz[i + 0] - myPosx;
            final float dy = xyz[i + 1] - myPosy;
            final float dz = xyz[i + 2] - myPosz;
            final float invDist = rsqrt((dx * dx) + (dy * dy) + (dz * dz) + espSqr);
            final float s = mass * invDist * invDist * invDist;
            accx = accx + (s * dx);
            accy = accy + (s * dy);
            accz = accz + (s * dz);
         }
         accx = accx * delT;
         accy = accy * delT;
         accz = accz * delT;
         xyz[globalId + 0] = myPosx + (vxyz[globalId + 0] * delT) + (accx * .5f * delT);
         xyz[globalId + 1] = myPosy + (vxyz[globalId + 1] * delT) + (accy * .5f * delT);
         xyz[globalId + 2] = myPosz + (vxyz[globalId + 2] * delT) + (accz * .5f * delT);

         vxyz[globalId + 0] = vxyz[globalId + 0] + accx;
         vxyz[globalId + 1] = vxyz[globalId + 1] + accy;
         vxyz[globalId + 2] = vxyz[globalId + 2] + accz;
         int x = (int) xyz[globalId + 0];
         float sample = 1f;
         int y = (int) xyz[globalId + 1];
         if(x < width && x >= 0 && y < height && y >= 0){
            rgb[y * width + x] = 0xffffff;
         }

      }

      volatile boolean done = false;

      void next(){
         execute(range);
         kernel.get(kernel.rgb);
         done = true;
      }
   }

   static NBodyKernel kernel;

   public static void main(String[] _args){
      JFrame frame = new JFrame("NBody");
      final int width = 768;
      final int height = 768;
      final int bodies = 128;
      final BufferedImage offscreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      JComponent viewer = new JComponent(){
         @Override
         public void paintComponent(Graphics g){
            if(kernel != null && kernel.done){

               g.drawImage(offscreen, 0, 0, width, height, this);
            }
         }
      };
      viewer.setPreferredSize(new Dimension(width, height));
      frame.getContentPane().add(viewer);
      frame.pack();
      frame.setVisible(true);
      final int[] rgb = ((DataBufferInt) offscreen.getRaster().getDataBuffer()).getData();
      kernel = new NBodyKernel(width, height, rgb, bodies);
      kernel.next();
      viewer.repaint();
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      while(true){
         kernel.next();
         viewer.repaint();
      }
   }


}
