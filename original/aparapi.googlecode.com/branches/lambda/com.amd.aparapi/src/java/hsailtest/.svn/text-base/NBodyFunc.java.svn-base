package hsailtest;

import com.amd.aparapi.Device;
import com.amd.aparapi.HSADevice;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;


public class NBodyFunc {

   public class Body{
      float x,y,z;
      float vx,vy,vz;
      float mass;

      void init(int width, int height, int id){
          float scaledId = ((float)id)/width;
          float theta =  (float)(scaledId * Math.PI * 2);
          float phi =  (float)(scaledId *  Math.PI * 2);
          float radius = scaledId * width / 2;
          mass = (float)(scaledId*20f+10f);
          x = (float) (radius * Math.cos(theta) * Math.sin(phi)) + width / 2;
          y = (float) (radius * Math.sin(theta) * Math.sin(phi)) + height / 2;
          z = (float) (radius * Math.cos(phi));

      }

       void updatePosition(Body[] bodies){
           float accx = 0.f;
           float accy = 0.f;
           float accz = 0.f;
           for(int i = 0; i < bodies.length; i++){
               Body other = bodies[i];
               if (this != other){
                   float dx = other.x-x;
                   float dy = other.y-y;
                   float dz = other.z-z;
                   float dist =  (float) Math.sqrt(((dx * dx) + (dy * dy) + (dz * dz) + .1f /* +.1f in case dx,dy,dz are 0!*/));
                   float invDist = 1f / dist;
                   float massInvDist_3 = other.mass * invDist * invDist * invDist;
                   accx += massInvDist_3 * dx;
                   accy += massInvDist_3 * dy;
                   accz += massInvDist_3 * dz;
               }
           }
           float delT = .05f;
           float delT_2 = delT/2;
           accx *= delT;
           accy *= delT;
           accz *= delT;
           x += vx * delT + accx * delT_2;
           y += vy * delT + accy * delT_2;
           z += vz * delT + accz * delT_2;
           vx+=accx;
           vy+=accy;
           vz+=accz;
       }


       void draw(Screen screen){
           int px =  (int)x;
           int py =  (int)y;

           int rgb = 0xffffff;
           screen.setPixel(px - 1, py, rgb);
           screen.setPixel(px, py, rgb);
           screen.setPixel(px + 1, py, rgb);
           screen.setPixel(px, py - 1, rgb);
           screen.setPixel(px, py + 1, rgb);
       }
   }

   static class Screen {
       int width;
       int height;
       BufferedImage offscreen ;
       int[] offscreenPixels;
       Screen(int _width, int _height){
           width=_width;
           height = _height;
           offscreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
           offscreenPixels= ((DataBufferInt) offscreen.getRaster().getDataBuffer()).getData();
       }
       void clear(){
           Arrays.fill(offscreenPixels, 0);
       }
       void print(int x, int y, String _string){

           offscreen.getGraphics().setColor(Color.WHITE);
           offscreen.getGraphics().drawString(_string, x, y);
       }

       void setPixel(int x, int y,int rgb){
           if (x>=0 && x<width && y>=0 && y<height){
               offscreenPixels[x+y*width]=rgb;
           }
       }
   }

   public static void main(String[] _args){
       (new NBodyFunc()).go(Device.getByName(_args[0]), Integer.parseInt(_args[1]));
       /**
       Body[] bodies = new Body[100];
       Screen screen = null;
       ((HSADevice)Device.hsa()).dump(gid -> {
           Body body = bodies[gid];
           body.updatePosition(bodies);
           body.draw(screen);
       });
        */
   }

   void go(Device device, int bodyCount ){
      float frame = 0f;

      Screen screen = new Screen(Integer.getInteger("width", 1024),  Integer.getInteger("height", 1024));
      JFrame jframe = new JFrame("NBody");
      Body[] bodies = new Body[bodyCount];
      Device.jtp().forEach(bodies.length, body -> {
           bodies[body] = new Body();
       });

       Device.jtp().forEach(bodies.length, body -> {
           bodies[body].init(screen.width, screen.height, body);
       });

      JComponent viewer = new JComponent(){
      };

      viewer.setPreferredSize(new Dimension(screen.width, screen.height));
      jframe.getContentPane().add(viewer);
      jframe.pack();
      jframe.setVisible(true);

      jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      long first = System.currentTimeMillis();
      float fps  =0;
      while(true){
         screen.clear();
         screen.print(100, 100, String.format("fps %5.2f ",fps));
         device.forEach(bodies.length, gid -> {
            Body body = bodies[gid];
            body.updatePosition(bodies);
            body.draw(screen);
         });
         long delta = System.currentTimeMillis()-first;
         frame+=1;
         if (delta > 1000){
             fps =(frame*1000)/delta; 
            
            first = System.currentTimeMillis() ;
            frame=0;
         }
         viewer.getGraphics().drawImage(screen.offscreen, 0, 0, null);
      }
   }
}
