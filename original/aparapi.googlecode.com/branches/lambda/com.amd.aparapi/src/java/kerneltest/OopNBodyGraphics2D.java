package kerneltest;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;


public class OopNBodyGraphics2D{
   public static final class Body{
      float x, y, z, vX, vY, vZ;

      public Body(float _x, float _y, float _z){
         x = _x;
         y = _y;
         z = _z;
      }

      float getX(){
         return x;
      }

      float getY(){
         return y;
      }

      float getZ(){
         return z;
      }

      float getVX(){
         return vX;
      }

      float getVY(){
         return vY;
      }

      float getVZ(){
         return vZ;
      }

      void setX(float _x){
         x = _x;
      }

      void setY(float _y){
         y = _y;
      }

      void setZ(float _z){
         z = _z;
      }

      void setVX(float _vx){
         vX = _vx;
      }

      void setVY(float _vy){
         vY = _vy;
      }

      void setVZ(float _vz){
         vZ = _vz;
      }

   }

   public static class NBodyKernel extends Kernel{

      final private int width;
      final private int height;
      final float delT = .005f;
      final float espSqr = 1.0f;
      final float mass = 5f;


      private final Range range;
      private final Body[] bodyArr;
      volatile boolean done = false;

      public NBodyKernel(int _width, int _height, int _depth, int _bodies){
         bodyArr = new Body[_bodies];
         width = _width;
         height = _height;

         range = Range.create(_bodies);

         final float maxDist = width / 4;
         for(int body = 0; body < _bodies; body++){
            final float theta = (float) (Math.random() * Math.PI * 2);
            final float phi = (float) (Math.random() * Math.PI * 2);
            final float radius = (float) (Math.random() * maxDist);
            bodyArr[body] = new Body((float) (radius * Math.cos(theta) * Math.sin(phi)) + width / 2, (float) (radius * Math.sin(theta) * Math.sin(phi)) + height / 2, (float) (radius * Math.cos(phi)));


         }

      }

      public void run(){
         //final int body = getGlobalId();
         final int count = getGlobalSize(0);
         final int globalId = getGlobalId(0);
         float accx = 0.f;
         float accy = 0.f;
         float accz = 0.f;
         for(int i = 0; i < count; i++){
            final float dx = bodyArr[i].getX() - bodyArr[globalId].getX();
            final float dy = bodyArr[i].getY() - bodyArr[globalId].getY();
            final float dz = bodyArr[i].getZ() - bodyArr[globalId].getZ();
            final float invDist = rsqrt((dx * dx) + (dy * dy) + (dz * dz) + espSqr);
            accx += mass * invDist * invDist * invDist * dx;
            accy += mass * invDist * invDist * invDist * dy;
            accz += mass * invDist * invDist * invDist * dz;
         }
         accx *= delT;
         accy *= delT;
         accz *= delT;
         bodyArr[globalId].setX(bodyArr[globalId].getX() + (bodyArr[globalId].getVX() * delT) + (accx * .5f * delT));
         bodyArr[globalId].setY(bodyArr[globalId].getY() + (bodyArr[globalId].getVY() * delT) + (accx * .5f * delT));
         bodyArr[globalId].setZ(bodyArr[globalId].getZ() + (bodyArr[globalId].getVZ() * delT) + (accx * .5f * delT));
         bodyArr[globalId].setVX(bodyArr[globalId].getVX() + accx);
         bodyArr[globalId].setVY(bodyArr[globalId].getVY() + accy);
         bodyArr[globalId].setVZ(bodyArr[globalId].getVZ() + accz);

      }

      void next(){
         execute(range);
         done = true;
      }
   }

   static int fps = 0;
   static NBodyKernel kernel;

   public static void main(String[] _args){
      JFrame frame = new JFrame("NBody");
      final int width = 768;
      final int height = 768;
      final int bodies = 512;
      JComponent viewer = new JComponent(){
         @Override
         public void paintComponent(Graphics _g){
            Graphics2D g = (Graphics2D) _g;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, width, height);
            g.setColor(Color.WHITE);

            if(kernel != null && kernel.done){


               for(Body body : kernel.bodyArr){
                  g.fillOval((int) body.getX(), (int) body.getY(), (int) 10, (int) 10);
               }
               g.setColor(Color.WHITE);
               g.drawString("" + fps, 100, 100);
            }
         }
      };
      viewer.setPreferredSize(new Dimension(width, height));
      frame.getContentPane().add(viewer);
      frame.pack();
      frame.setVisible(true);
      kernel = new NBodyKernel(width, height, width, bodies);
      kernel.next();
      viewer.repaint();
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      while(true){
         fps++;
         kernel.next();
         viewer.repaint();
      }
   }


}
