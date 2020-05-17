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

package com.amd.aparapi.sample.mandel;

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
//import java.util.ArrayList;
import java.util.Arrays;
//import java.util.List;
import java.util.stream.primitive.IntStream;

import javax.swing.JComponent;
import javax.swing.JFrame;


public class Main{

   /** Width of Mandelbrot view. */
   static final int width = 768;

   /** Height of Mandelbrot view. */
   static final int height = 768;

   /** Image for Mandelbrot view. */
   static final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
   static final BufferedImage offscreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

   // Extract the underlying RGB buffer from the image.
   final int[] rgb = ((DataBufferInt) offscreen.getRaster().getDataBuffer()).getData();
   final int[] imageRgb = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

   float defaultScale = 3f;
   /** Maximum iterations for Mandelbrot. */
   final private int maxIterations = 64;

   /** Palette which maps iteration values to RGB values. */
   final int pallette[] = new int[maxIterations + 1];

   /** User selected zoom-in point on the Mandelbrot view. */
   public static volatile Point to = null;

   // This is how many frames we will display as we zoom in and out.
   static final int frames = 128;

   // These are members so zoom out continues from where zoom in stopped
   float scale = defaultScale;
   float x = -1f;
   float y = 0f;

   static int[]	phonyGid = new int[width*height];
   
   // Draw Mandelbrot image
   static JComponent viewer = new JComponent(){
      @Override public void paintComponent(Graphics g) {
         g.drawImage(image, 0, 0, width, height, this);
      }
   };


   enum ZoomDirection {
      ZOOM_IN(-1), ZOOM_OUT(1);

      private int sign;

      private ZoomDirection(int c) { sign = c; }
      public int getSign()         {  return sign; }
   }


   void getNextImage(float x, float y, float scale) {
      
// Here is some explanation of the sequence of calls to implement the parallel forEach
//      
//       Arrays.parallel delegates to  public Stream<E> parallel() { return Streams.parallel(a); }
//        
//           public static <T> Stream<T> parallel(T[] source, int offset, int length) {
//               return new ValuePipeline<>(new ArraySpliterator<>(source, offset, length),
//                                    StreamOpFlags.IS_SIZED | StreamOpFlags.IS_ORDERED | StreamOpFlags.IS_PARALLEL);
//           }
//       
//           calls
//            
//           public<S> ValuePipeline(Spliterator<S> spliterator, int sourceFlags) {
//              super(spliterator, sourceFlags, StreamShape.VALUE);
//           }
//       
//        calls
//        
//        protected AbstractPipeline(Spliterator<?> spliterator, int sourceFlags, StreamShape shape);
//        
//      so now we have a ValuePipeline stream which is the reciever for forEach
//      
//           @Override
//           public void forEach(Block<? super U> block) {
//               pipeline(ForEachOp.make(block));
//           }
//      
//      creates a ForEachOp for the Block (the block is the code in the statement lambda)
//      
//      public static<T> ForEachOp<T> make(final Block<? super T> block)
//         ...
//         return new ForEachOp<>(new TerminalSink<T, Void>() {
//
//
//              public<V> Stream<V> pipeline(IntermediateOp<U, V> op) {
//                 // @@@ delegate to shape to do instantiation
//                 return new ValuePipeline<>(this, op);
//              }
//              
//         using 
//         
//              public ValuePipeline(AbstractPipeline<?, T> upstream, IntermediateOp<T, U> op) {
//                 super(upstream, op);
//              }
//      
//         ForEachOp is a subclass of TerminalOp
//            
//         share/classes/java/util/streams/AbstractPipeline.java:
      
//              public<R> R pipeline(TerminalOp<E_OUT, R> terminal) {
//                 assert getShape() == terminal.inputShape();
//                 return evaluate(terminal);
//              }
         
//      This stream is parallel, remember parallel flag was set back in Arrays.parallel:
//      
//      protected<R> R evaluate(TerminalOp<E_OUT, R> terminal) {
//         // @@@ NYI If the source size estimate is small, don't bother going parallel
//         if (StreamOpFlags.PARALLEL.isKnown(sourceFlags)) {
//             return evaluateParallel(terminal);
//         }   
//         else
//             return evaluateSequential(terminal);
//     }
      
//      private <R> R evaluateParallel(Node<?> node,
//            Spliterator<?> spliterator,
//            int sourceFlags,
//            int[] opsFlags,
//            IntermediateOp[] ops, int from, int upTo,
//            StreamOp<?, R> terminal) {
//         return (R) terminal.evaluateParallel(new ParallelImplPipelineHelper(node, spliterator, sourceFlags,
//                                                     opsFlags, ops, from, upTo));
//      }  
//      
//      Note ParallelImplPipelineHelper is an inner class in AbstractPipeline
//      terminal is the ForEachOp
//      
//      Back to ForEachOp:
//         
//              @Override
//              public <S> Void evaluateParallel(ParallelPipelineHelper<S, T> helper) {
//                  OpUtils.parallelForEach(helper, helper.wrapSink(sink));
//                  return null;
//              }
//
//      share/classes/java/util/streams/ops/OpUtils.java:
//         
//           public static<P_IN, P_OUT> void parallelForEach(ParallelPipelineHelper<P_IN, P_OUT> helper, Sink<P_IN> sink) {
//              helper.invoke(new ForEachTask<>(helper, sink));
//           }
//      
//
//      Note ForEachTask is a inner class in OpUtils
//      ForEachTask is a subclass of "AbstractTask extends CountedCompleter" which 
//      is a j.u.c.ForkJoinTask
//      share/classes/java/util/streams/ops/AbstractTask.java
//      
//      ParallelImplPipelineHelper.invoke(ForkJoinTask<FJ_R> task) {
//                   return task.invoke();
//      }
//     
//      Now we are in java.util.concurrent running a ForkJoinTask waiting for completion on the main thread
//      So the java stack trace at that point is:
//      
//      "main" #1 prio=5 os_prio=0 tid=0x00007fdab4009000 nid=0x39d1 in Object.wait() [0x00007fdabd771000]
//            java.lang.Thread.State: WAITING (on object monitor)
//           at java.lang.Object.wait(Native Method)
//           - waiting on <0x00000007c3676b48> (a java.util.streams.ops.OpUtils$ForEachTask)
//           at java.lang.Object.wait(Object.java:502)
//           at java.util.concurrent.ForkJoinTask.externalAwaitDone(ForkJoinTask.java:296)
//           - locked <0x00000007c3676b48> (a java.util.streams.ops.OpUtils$ForEachTask)
//           at java.util.concurrent.ForkJoinTask.doInvoke(ForkJoinTask.java:362)
//           at java.util.concurrent.ForkJoinTask.invoke(ForkJoinTask.java:668)
//           at java.util.streams.AbstractPipeline$ParallelImplPipelineHelper.invoke(AbstractPipeline.java:396)
//           at java.util.streams.ops.OpUtils.parallelForEach(OpUtils.java:83)
//           at java.util.streams.ops.ForEachOp.evaluateParallel(ForEachOp.java:74)
//           at java.util.streams.ops.ForEachOp.evaluateParallel(ForEachOp.java:37)
//           at java.util.streams.AbstractPipeline.evaluateParallel(AbstractPipeline.java:189)
//           at java.util.streams.AbstractPipeline.evaluateParallel(AbstractPipeline.java:177)
//           at java.util.streams.AbstractPipeline.evaluate(AbstractPipeline.java:131)
//           at java.util.streams.AbstractPipeline.pipeline(AbstractPipeline.java:487)
//           at java.util.streams.ValuePipeline.forEach(ValuePipeline.java:89)
//           at com.amd.aparapi.sample.mandel.Main.getNextImage(Main.java:244)
//
      IntStream str = Arrays.parallel(phonyGid);
      //Primitives.parallel(phonyGid).forEach(p -> {
         str.forEach(p -> {
         
//         The Block that gets executed here becomes:
//         
//         private void lambda$0(float, float, float, int);
//
//         The stack of a thread in the pool doing the lambda is:
//            
//         "ForkJoinPool.commonPool-worker-13" #28 daemon prio=5 os_prio=0 tid=0x00007fdab4246800 nid=0x3a12 runnable [0x00007fda9fafb000]
//               java.lang.Thread.State: RUNNABLE
//               at com.amd.aparapi.sample.mandel.Main.lambda$0(Main.java:261)
//               at com.amd.aparapi.sample.mandel.Main$$Lambda$1.apply(Unknown Source)
//               at java.util.streams.ops.ForEachOp$1.accept(ForEachOp.java:52)
//               at java.util.streams.Sink.apply(Sink.java:58)
//               at java.util.streams.ops.ForEachOp$1.apply(ForEachOp.java)
//         
//     Now we are getting closer to doing some work:    
//         
//      ArraySpliterator.forEach(Block<? super T> block) {
//            traversing = true;
//            for (int i= curOffset; i<endOffset; i++) {
//                block.apply(elements[i]);
//            }   
//            // update only once; reduce heap write traffic
//            curOffset = endOffset;
//        }   
//          
//               at java.util.streams.Streams$ArraySpliterator.forEach(Streams.java:550)
         
// The public interface Spliterator<T> is used to break data structures into chunks that
// can be processed independently.
         
//               at java.util.streams.ops.OpUtils.intoWrapped(OpUtils.java:78)
//               at java.util.streams.ops.OpUtils$ForEachTask.doLeaf(OpUtils.java:107)

//   Each task can be a leaf, actually calling the user lambda block, or a task further 
//   splitting the work and forking new tasks which may further split or be leaves.
         
//               at java.util.streams.ops.OpUtils$ForEachTask.doLeaf(OpUtils.java:86)
//               at java.util.streams.ops.AbstractTask.compute(AbstractTask.java:90)
//               at java.util.streams.ops.AbstractTask.compute(AbstractTask.java:110)
//               at java.util.concurrent.CountedCompleter.exec(CountedCompleter.java:484)
//               at java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:260)
//               at java.util.concurrent.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1055)
//               at java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1708)
//               at java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:104)
//
//     The thread pool is not named like the lambda's class -- seems like  
//     that would be helpful for debugging.
         
         /** Determine which RGB value we are going to process (0..RGB.length). */
         int gid = p;

         /** Translate the gid into an x an y value. */
         float lx = (((gid % width * scale) - ((scale / 2) * width)) / width) + x;
         float ly = (((gid / width * scale) - ((scale / 2) * height)) / height) + y;

         //int count = getCount(lx,ly);
         int count = 0;
         {
            float zx = lx;
            float zy = ly;
            float new_zx = 0f;

            // Iterate until the algorithm converges or until maxIterations are reached.
            while (count < maxIterations && zx * zx + zy * zy < 8) {
               new_zx = zx * zx - zy * zy + lx;
               zy = 2 * zx * zy + ly;
               zx = new_zx;
               count++;
            }
         }
         // Pull the value out of the palette for this iteration count.
         rgb[gid] = pallette[count];
      });
   }


   void doZoom(int sign, float tox, float toy) {
      // Zoom in or out per iteration 
      for (int i = 0; i < frames - 4; i++) {
         scale = scale + sign * defaultScale / frames;
         x = x - sign * (tox / frames);
         y = y - sign * (toy / frames);
         getNextImage(x, y, scale);
         System.arraycopy(rgb, 0, imageRgb, 0, rgb.length);
         viewer.repaint();
      }
   }


   void zoomInAndOut(Point to, int[] rgb, int[] imageRgb) {
      float tox = (float) (to.x - width / 2) / width * defaultScale;
      float toy = (float) (to.y - height / 2) / height * defaultScale;
      
      // This cannot be parallel lambda or you will get a headache!!
      // It will zoom in on the clicked point, then zoom out back to the start position
      Arrays.stream(ZoomDirection.values()).forEach( e -> {

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
         
         doZoom(e.getSign(), tox, toy); 
         System.out.println("inner done, sign=" + e.getSign() );          
      } );
   }

   void doIt() {
      JFrame frame = new JFrame("MandelBrot");
      // Set the size of JComponent which displays Mandelbrot image
      viewer.setPreferredSize(new Dimension(width, height));
      final Object doorBell = new Object();
      // Mouse listener which reads the user clicked zoom-in point on the Mandelbrot view 
      viewer.addMouseListener(new MouseAdapter(){
         @Override public void mouseClicked(MouseEvent e) {
            to = e.getPoint();
            synchronized (doorBell) {
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
      for (int i = 0; i < maxIterations; i++) {
         float h = i / (float) maxIterations;
         float b = 1.0f - h * h;
         pallette[i] = Color.HSBtoRGB(h, 1f, b);
      }

      // Used to find the index in the rgb array when processing 
      // each element in the lambda  
      for(int i=0; i<width*height; i++) {
    	 phonyGid[i] = i;
      }

      getNextImage(x, y, scale);

      System.arraycopy(rgb, 0, imageRgb, 0, rgb.length);
      viewer.repaint();

      // Window listener to dispose Kernel resources on user exit.
      frame.addWindowListener(new WindowAdapter(){
         public void windowClosing(WindowEvent _windowEvent) {
            System.exit(0);
         }
      });

      // Wait until the user selects a zoom-in point on the Mandelbrot view.
      while (true) {

         // Wait for the user to click somewhere
         while (to == null) {
            synchronized (doorBell) {
               try {
                  doorBell.wait();
               } catch (InterruptedException ie) {
                  ie.getStackTrace();
               }
            }
         }

         long startMillis = System.currentTimeMillis();

         zoomInAndOut(to, rgb, imageRgb);

         long elapsedMillis = System.currentTimeMillis() - startMillis;
         System.out.println("FPS = " + frames * 1000 / elapsedMillis);

         // Reset zoom-in point.
         to = null;
      }
      
   }

   public static void main(String[] _args) {
      (new Main()).doIt();
   }
}
