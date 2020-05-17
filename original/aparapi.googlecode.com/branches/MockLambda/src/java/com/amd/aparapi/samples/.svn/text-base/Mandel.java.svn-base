package com.amd.aparapi.samples;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;

import com.amd.aparapi.Aparapi;

public class Mandel{

   static final int maxIterations = 64;

   static void displayMandel(int width, int height, float offsetx, float offsety, float scale, int rgb[], int[] pallette, JComponent viewer, DoorBell paintedDoorBell){
      Aparapi.forEach(rgb, (i, value)->{
         float x = ((((i%width) * scale) - ((scale / 2) * width)) / width) + offsetx;
         float y = ((((i/width) * scale) - ((scale / 2) * height)) / height) + offsety;
         int count = 0;
         float zx = x;
         float zy = y;
         float new_zx = 0f;
         // Iterate until the algorithm converges or until maxIterations are reached.
         while (count < maxIterations && zx * zx + zy * zy < 8) {
            new_zx = zx * zx - zy * zy + x;
            zy = 2 * zx * zy + y;
            zx = new_zx;
            count++;
         }
         // Pull the value out of the palette for this iteration count.
         rgb[i] = pallette[count];
      });
      viewer.repaint();
      paintedDoorBell.waitFor();
   }

   static public class DoorBell<T>{
      T value;
      void set(T _value){
         value = _value;
         press();
      }
      T get(){
         waitFor();
         return(value);
      }
      private volatile boolean pressed=false;
      void waitFor(){
         while(!pressed){
            synchronized (this) {
               try {
                  this.wait();
               } catch (InterruptedException ie) {
               }
            }
         }
         pressed = false;
      }
      void press(){
         pressed = true;
         synchronized (this) {
            this.notify();
         }
      }
   }


   @SuppressWarnings("serial") public static void main(String[] _args) {

      JFrame frame = new JFrame("MandelBrot");

      /** Width of Mandelbrot view. */
      final int width = 768;

      /** Height of Mandelbrot view. */
      final int height = 768;

      final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      final DoorBell paintedDoorBell = new DoorBell();
      JComponent viewer = new JComponent(){
         @Override public void paintComponent(Graphics g) {
            g.drawImage(image, 0, 0, width, height, this);
            paintedDoorBell.press();
         }
      };

      // Set the size of JComponent which displays Mandelbrot image
      viewer.setPreferredSize(new Dimension(width, height));

      final DoorBell<Point> doorBell = new DoorBell<>();

      // Mouse listener which reads the user clicked zoom-in point on the Mandelbrot view 
      viewer.addMouseListener(new MouseAdapter(){
         @Override public void mouseClicked(MouseEvent e) {
            doorBell.set(e.getPoint());
         }
      });

      // Swing housework to create the frame
      frame.getContentPane().add(viewer);
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);

      // Extract the underlying RGB buffer from the image.
      // Pass this to the kernel so it operates directly on the RGB buffer of the image
      final int[] rgb = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

      // create pallette 
      // Initialize palette values
      final int[] pallette = new int[maxIterations+1];

      Aparapi.forEach(maxIterations, (i)->{
         float h = i / (float) maxIterations;
         float b = 1.0f - h * h;
         pallette[i] = Color.HSBtoRGB(h, 1f, b);
      });


      displayMandel(width, height,  -1f, 0f, 3f, rgb, pallette, viewer, paintedDoorBell);

      // Window listener to dispose Kernel resources on user exit.
      frame.addWindowListener(new WindowAdapter(){
         public void windowClosing(WindowEvent _windowEvent) {
            System.exit(0);
         }
      });

      // Wait until the user selects a zoom-in point on the Mandelbrot view.
      while (true) {
         Point to = doorBell.get();
         float x = -1f;
         float y = 0f;
         float defaultScale=3f;
         float scale = 3f;
         float tox = (float) (to.x - width / 2) / width * scale;
         float toy = (float) (to.y - height / 2) / height * scale;

         // This is how many frames we will display as we zoom in and out.
         int frames = 128;
         long startMillis = System.currentTimeMillis();
         for (int sign = -1; sign < 2; sign += 2) {
            for (int i = 0; i < frames - 4; i++) {
               scale = scale + sign * defaultScale / frames;
               x = x - sign * (tox / frames);
               y = y - sign * (toy / frames);
               // Set the scale and offset, execute the kernel and force a repaint of the viewer.
               displayMandel(width, height,  x, y, scale, rgb, pallette, viewer, paintedDoorBell );
            }
         }
      }
   }
}
