package hsailtest;

import com.amd.aparapi.Device;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;


public class NBodyGraphics2D {
   static int frame = 0;

    static final int width = Integer.getInteger("width", 768);
    static final int height =  Integer.getInteger("height", 768);
    static final BufferedImage offscreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    static final int[] rgb = ((DataBufferInt) offscreen.getRaster().getDataBuffer()).getData();


   public static void main(String[] _args){
      JFrame jframe = new JFrame("NBody");

      final int bodies = Integer.getInteger("bodies", 2048);
      final float delT = .005f;
      final float espSqr = 1.0f;
      final float mass = 20f;

      final float[] xyz = new float[bodies * 3]; // positions xy and z of bodies
      final float[] vxyz = new float[bodies * 3]; // velocity component of x,y and z of bodies

     if (false){
      final float maxDist = width / 4;
      for(int body = 0; body < (bodies * 3); body += 3){
         final float theta = (float) (Math.random() * Math.PI * 2);
         final float phi = (float) (Math.random() * Math.PI * 2);
         final float radius = (float) (Math.random() * maxDist);
         xyz[body + 0] = (float) (radius * Math.cos(theta) * Math.sin(phi)) + width / 2;
         xyz[body + 1] = (float) (radius * Math.sin(theta) * Math.sin(phi)) + height / 2;
         xyz[body + 2] = (float) (radius * Math.cos(phi));
      }
     }  else{
      int side = (int)Math.sqrt(bodies)+1;
      int spread = width/side;
      System.out.println("side "+side);
       System.out.println("spread "+spread);

       int x=0;
       int y=0;
       for (int body = 0; body < bodies; body++){
           x += spread;
           if (x>width){
               x = 0;
               y+=spread;
           }

           xyz[body*3 + 0]=x;
           xyz[body*3 + 1]=y;
           xyz[body*3 + 2]=0;
       }
       }

      JComponent viewer = new JComponent(){
      };

      viewer.setPreferredSize(new Dimension(width, height));
      jframe.getContentPane().add(viewer);
      jframe.pack();
      jframe.setVisible(true);
      Device device = Device.hsa();


      jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      for(frame = 0; frame < 10000; frame++){
          int[] rgbCopy = rgb;
          Arrays.fill(rgbCopy, 0);
          int w = width;
          int h = height;
         device.forEach(bodies, gid -> {
            final int count = bodies * 3;
            final int globalId = gid * 3;
            float accx = 0.f;
            float accy = 0.f;
            float accz = 0.f;
            for(int i = 0; i < count; i += 3){
               final float dx = xyz[i + 0] - xyz[globalId + 0];
               final float dy = xyz[i + 1] - xyz[globalId + 1];
               final float dz = xyz[i + 2] - xyz[globalId + 2];
               final float invDist = 1f / (float) Math.sqrt(((dx * dx) + (dy * dy) + (dz * dz) + espSqr));
               accx += mass * invDist * invDist * invDist * dx;
               accy += mass * invDist * invDist * invDist * dy;
               accz += mass * invDist * invDist * invDist * dz;
            }
            accx *= delT;
            accy *= delT;
            accz *= delT;
            xyz[globalId + 0] = xyz[globalId + 0] + (vxyz[globalId + 0] * delT) + (accx * .5f * delT);
            xyz[globalId + 1] = xyz[globalId + 1] + (vxyz[globalId + 1] * delT) + (accy * .5f * delT);
            xyz[globalId + 2] = xyz[globalId + 2] + (vxyz[globalId + 2] * delT) + (accz * .5f * delT);

            vxyz[globalId + 0] = vxyz[globalId + 0] + accx;
            vxyz[globalId + 1] = vxyz[globalId + 1] + accy;
            vxyz[globalId + 2] = vxyz[globalId + 2] + accz;
             int x =  (int)xyz[globalId + 0];
             int y =  (int)xyz[globalId + 1];
             if (x<w-1 && y<h-1 && x>0 && y>0){
                 rgbCopy[x+y*w]=0xffffff;
                 rgbCopy[x+1+y*w]=0xffffff;
                 rgbCopy[x-1+y*w]=0xffffff;
                 rgbCopy[x+(y+1)*w]=0xffffff;
                 rgbCopy[x+(y-1)*w]=0xffffff;
             }


         });

          viewer.getGraphics().drawImage(offscreen, 0, 0, null);
      }
   }


}
