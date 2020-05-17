/*
Copyright (c) 2010-2011, Advanced Micro Devices, Inc.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following
disclaimer. 

Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
disclaimer in the documentation and/or other materials provided with the distribution. 

Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products
derived from this software without specific prior written permission. 

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

If you use the software (in whole or in part), you shall adhere to all applicable U.S., European, and other export
laws, including but not limited to the U.S. Export Administration Regulations ("EAR"), (15 C.F.R. Sections 730 through
774), and E.U. Council Regulation (EC) No 1334/2000 of 22 June 2000.  Further, pursuant to Section 740.6 of the EAR,
you hereby certify that, except pursuant to a license granted by the United States Department of Commerce Bureau of 
Industry and Security or as otherwise permitted pursuant to a License Exception under the U.S. Export Administration 
Regulations ("EAR"), you will not (1) export, re-export or release to a national of a country in Country Groups D:1,
E:1 or E:2 any restricted technology, software, or source code you receive hereunder, or (2) export to Country Groups
D:1, E:1 or E:2 the direct product of such technology or software, if such foreign produced direct product is subject
to national security controls as identified on the Commerce Control List (currently found in Supplement 1 to Part 774
of EAR).  For the most current Country Group listings, or for additional information about the EAR or your obligations
under those regulations, please refer to the U.S. Bureau of Industry and Security's website at http://www.bis.doc.gov/. 

 */

package hsailtest;

import com.amd.aparapi.Device;
import com.amd.aparapi.HSADevice;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.function.IntConsumer;

public class Mandel {

   /**
    * Width of Mandelbrot view.
    */
   static final int width = 768;

   /**
    * Height of Mandelbrot view.
    */
   static final int height = 768;

   /**
    * Image for Mandelbrot view.
    */
   static final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
   static final BufferedImage offscreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

   // Extract the underlying RGB buffer from the image.
   final int[] rgb = ((DataBufferInt) offscreen.getRaster().getDataBuffer()).getData();
   final int[] imageRgb = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

   float defaultScale = 3f;
   /**
    * Maximum iterations for Mandelbrot.
    */
   final private int maxIterations = 64;

   /**
    * Palette which maps iteration values to RGB values.
    */
   final int pallette[] = new int[maxIterations + 1];

   /**
    * User selected zoom-in point on the Mandelbrot view.
    */
   public static volatile Point to = null;

   // This is how many frames we will display as we zoom in and out.
   static final int frames = 128;

   // These are members so zoom out continues from where zoom in stopped
   float scale = defaultScale;
   float x = -1f;
   float y = 0f;

   // Draw Mandelbrot image
   static JComponent viewer = new JComponent(){
      @Override
      public void paintComponent(Graphics g){
         g.drawImage(image, 0, 0, width, height, this);
      }
   };


   enum ZoomDirection{
      ZOOM_IN(-1), ZOOM_OUT(1);

      private int sign;

      private ZoomDirection(int c){
         sign = c;
      }

      public int getSign(){
         return sign;
      }
   }

   static int getMandelCount(float x, float y, int maxIterations ){
       float zx = x;
       float zy = y;
       float new_zx = 0f;
       int count =0;

       // Iterate until the algorithm converges or until maxIterations are reached.
       while(count < maxIterations && zx * zx + zy * zy < 8){
           new_zx = zx * zx - zy * zy + x;
           zy = 2 * zx * zy + y;
           zx = new_zx;
           count++;
       }
       return(count);

   }



   void getNextImage(Device device, final float x_offset, final float y_offset, final float scale){
      final int w = width;
      final int h = height;
      final  int[] rgb = this.rgb;
      final int[] pallette = this.pallette;

      IntConsumer ic =  gid -> {
          /** Translate the gid into an x an y value. */

          float lx = ((((gid % w) * scale) - ((scale / 2) * w)) / w) + x_offset;
          float ly = (((gid / w * scale) - ((scale / 2) * h)) / h) + y_offset;
          int count = getMandelCount(lx, ly, maxIterations);



          rgb[gid] = pallette[count];
      };
       device.forEach(width * height, ic);
       //((HSADevice)device).dump(ic);
   }


   void doZoom(Device device, int sign, float tox, float toy){
      // Zoom in or out per iteration
      for(int i = 0; i < frames - 4; i++){
         scale = scale + sign * defaultScale / frames;
         x = x - sign * (tox / frames);
         y = y - sign * (toy / frames);
         getNextImage(device, x, y, scale);
         System.arraycopy(rgb, 0, imageRgb, 0, rgb.length);
         viewer.repaint();
      }
   }


   void zoomInAndOut(Device device, Point to, int[] rgb, int[] imageRgb){
      float tox = (float) (to.x - width / 2) / width * defaultScale;
      float toy = (float) (to.y - height / 2) / height * defaultScale;

      // This cannot be parallel lambda or you will get a headache!!
      // It will zoom in on the clicked point, then zoom out back to the start position


      // NOTE: in the future we will use a (non-parallel) lambda here when stream API gets into JDK8.
      // Arrays.stream(ZoomDirection.values()).forEach( e -> {
      for(ZoomDirection e : ZoomDirection.values()){

         //     Here is the stack at this point of running the lambda
         //
         //         at com.amd.aparapi.sample.mandel.Main.doZoom(Main.java:277)
         //         at com.amd.aparapi.sample.mandel.Main.lambda$1(Main.java:291)
         //         at com.amd.aparapi.sample.mandel.Main$$Lambda$2.apply(Unknown Source)
         //         at java.util.streams.ops.ForEachOp$1.accept(ForEachOp.java:52)
         //         at java.util.streams.Sink.apply(Sink.java:58)
         //         at java.util.streams.ops.ForEachOp$1.apply(ForEachOp.java)
         //         at java.util.streams.Streams$ArraySpliterator.forEach(Streams.java:550)
         //         at java.util.streams.AbstractPipeline$AbstractPipelineHelper.into(AbstractPipeline.java:256)
         //         at java.util.streams.AbstractPipeline$SequentialImplPipelineHelper.into(AbstractPipeline.java:321)

         //     Note there are SequentialImplPipelineHelper here and ParallelImplPipelineHelper
         //     in the parallel case above.

         //         at java.util.streams.ops.ForEachOp.evaluateSequential(ForEachOp.java:69)
         //         at java.util.streams.ops.ForEachOp.evaluateSequential(ForEachOp.java:37)
         //         at java.util.streams.AbstractPipeline.evaluateSequential(AbstractPipeline.java:206)
         //         at java.util.streams.AbstractPipeline.evaluate(AbstractPipeline.java:134)
         //         at java.util.streams.AbstractPipeline.pipeline(AbstractPipeline.java:487)
         //         at java.util.streams.ValuePipeline.forEach(ValuePipeline.java:89)
         //         at com.amd.aparapi.sample.mandel.Main.zoomInAndOut(Main.java:290)
         //         at com.amd.aparapi.sample.mandel.Main.main(Main.java:361)

         doZoom(device, e.getSign(), tox, toy);
         System.out.println("inner displaying, sign=" + e.getSign());
      }
   }

   void doIt(){
      JFrame frame = new JFrame("MandelBrot");
      // Set the size of JComponent which displays Mandelbrot image
      viewer.setPreferredSize(new Dimension(width, height));
      final Object doorBell = new Object();
      // Mouse listener which reads the user clicked zoom-in point on the Mandelbrot view
      viewer.addMouseListener(new MouseAdapter(){
         @Override
         public void mouseClicked(MouseEvent e){
            to = e.getPoint();
            synchronized(doorBell){
               doorBell.notify();
            }
         }
      });

      // Swing housework to create the frame
      frame.getContentPane().add(viewer);
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);

      //Initialize palette values
      for(int i = 0; i < maxIterations; i++){
         float h = i / (float) maxIterations;
         float b = 1.0f - h * h;
         pallette[i] = Color.HSBtoRGB(h, 1f, b);
      }

      Device device = Device.hsa();

      getNextImage(device, x, y, scale);

      System.arraycopy(rgb, 0, imageRgb, 0, rgb.length);
      viewer.repaint();

      // Window listener to dispose Kernel resources on user exit.
      frame.addWindowListener(new WindowAdapter(){
         public void windowClosing(WindowEvent _windowEvent){
            System.exit(0);
         }
      });
      boolean allowZoom = true;
      if(allowZoom){
         // Wait until the user selects a zoom-in point on the Mandelbrot view.
         while(true){
            // Wait for the user to click somewhere
            while(to == null){
               synchronized(doorBell){
                  try{
                     doorBell.wait();
                  }catch(InterruptedException ie){
                     ie.getStackTrace();
                  }
               }
            }

            long startMillis = System.currentTimeMillis();
            zoomInAndOut(device, to, rgb, imageRgb);
            long elapsedMillis = System.currentTimeMillis() - startMillis;
            System.out.println("FPS = " + frames * 1000 / elapsedMillis);
            to = null;
         }
      }
   }

   public static void main(String[] _args){
      (new Mandel()).doIt();
   }
}
