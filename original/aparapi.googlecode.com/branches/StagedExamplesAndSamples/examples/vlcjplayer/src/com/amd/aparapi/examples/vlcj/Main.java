package com.amd.aparapi.examples.vlcj;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallbackAdapter;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

public class Main{
   // The size does NOT need to match the mediaPlayer size - it's the size that
   // the media will be scaled to
   // Matching the native size will be faster of course
   private static final int width = 512;

   private static final int height = 512;

   final static class ConvolutionFilter{
      private float[] weights;

      private int offset;

      ConvolutionFilter(float _nw, float _n, float ne, float _w, float _o, float _e, float _sw, float _s, float _se, int _offset) {
         weights = new float[] {
               _nw,
               _w,
               ne,
               _w,
               _o,
               _e,
               _sw,
               _s,
               _se
         };
         offset = _offset;
      }

   }

   private static final ConvolutionFilter NONE = new ConvolutionFilter(0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0);

   private static final ConvolutionFilter EDGE1 = new ConvolutionFilter(1f, -2f, 1f, -2f, 4f, -2f, 1f, -2f, 1f, 0);

   private static final ConvolutionFilter EDGE2 = new ConvolutionFilter(-1f, 0f, -1f, 0f, 7f, 0f, -1f, 0f, -1f, 0);

   private static final ConvolutionFilter EDGE = new ConvolutionFilter(-2f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 2f, 0);

   private static final ConvolutionFilter BLUR = new ConvolutionFilter(1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 0);

   private static final ConvolutionFilter EMBOSS_1 = new ConvolutionFilter(-2f, -1f, 0f, -1f, 1f, 1f, 0f, 1f, 2f, 0);

   private static final ConvolutionFilter EMBOSS_2 = new ConvolutionFilter(2f, 1f, 0f, 1f, 1f, -1f, 0f, -1f, -2f, 0);

   private static final ConvolutionFilter EMBOSS_3 = new ConvolutionFilter(0f, -1f, -2f, 1f, 1f, -1f, 2f, 1f, 0f, 0);

   private static final ConvolutionFilter EMBOSS_4 = new ConvolutionFilter(0f, 1f, 2f, -1f, 1f, 1f, -2f, -1f, 0f, 0);

   private static final Range range = Range.create2D(width, height, 8, 8);

   public static class ConvolutionKernel extends Kernel{

      private final float[] filter = new float[9];

      private int[] inputData;

      private final int[] outputData;

      private final int width;

      private final int height;

      private int offset;

      public ConvolutionKernel(int _width, int _height) {
         outputData = new int[_width * _height];
         inputData = new int[_width * _height];
         width = _width;
         height = _height;
      }

      public void run() {

         int x = getGlobalId(0);
         int y = getGlobalId(1);

         int w = getGlobalSize(0);
         int h = getGlobalSize(1);
         // System.out.println(x+","+y+" "+lx+","+ly+" "+w+","+h);
         if (x > 10 && x < (w - 10) && y > 10 && y < (h - 10)) {

            int result = 0;
            // We handle each color separately using rgbshift as an 8 bit mask for red, green, blue
            for (int rgbShift = 0; rgbShift < 24; rgbShift += 8) { // 0,8,16
               int channelAccum = 0;
               float accum = 0;

               for (int count = 0; count < 9; count++) {
                  int dx = (count % 3) - 1; // 0,1,2 -> -1,0,1
                  int dy = (count / 3) - 1; // 0,1,2 -> -1,0,1

                  int rgb = (inputData[((y + dy) * w) + (x + dx)]);
                  int channelValue = ((rgb >> rgbShift) & 0xff);
                  accum += filter[count];
                  channelAccum += channelValue * filter[count++];

               }
               channelAccum /= accum;
               channelAccum += offset;
               channelAccum = max(0, min(channelAccum, 0xff));
               result |= (channelAccum << rgbShift);
            }
            outputData[y * w + x] = result;
         } else {
            outputData[y * w + x] = inputData[y * w + x];
         }

      }

      public int[] data;

      public void apply(ConvolutionFilter... _filters) {
         for (ConvolutionFilter _filter : _filters) {
            System.arraycopy(_filter.weights, 0, filter, 0, _filter.weights.length);
            inputData = data;
            offset = _filter.offset;
            execute(range);
            System.arraycopy(outputData, 0, data, 0, width * height);
         }
         //get(outputData);
      }
   }

   ConvolutionKernel kernel = new ConvolutionKernel(width, height);

   // private final int width = 1280;
   // private final int height = 720;

   /**
    * Image to render the video frame data.
    */
   private final BufferedImage image;

   private final MediaPlayerFactory factory;

   private final DirectMediaPlayer mediaPlayer;

   private ImagePane imagePane;

   boolean selected;

   public Main(String media, String[] args) throws InterruptedException, InvocationTargetException {
      image = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()
            .createCompatibleImage(width, height);
      image.setAccelerationPriority(1.0f);

      SwingUtilities.invokeAndWait(new Runnable(){

         @Override public void run() {
            JFrame frame = new JFrame("VLCJ Direct Video Test");
            //  frame.setIconImage(new ImageIcon(getClass().getResource("/icons/vlcj-logo.png")).getImage());
            imagePane = new ImagePane(image);
            imagePane.setSize(width, height);
            imagePane.setMinimumSize(new Dimension(width, height));
            imagePane.setPreferredSize(new Dimension(width, height));
            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add(imagePane, BorderLayout.CENTER);
            JPanel controls = new JPanel();
            frame.getContentPane().add(controls, BorderLayout.SOUTH);
            JToggleButton onOff = new JToggleButton("Toggle");
            controls.add(onOff);
            onOff.addActionListener(new ActionListener(){

               @Override public void actionPerformed(ActionEvent e) {
                  selected = ((JToggleButton) e.getSource()).isSelected();

               }

            });
            frame.pack();
            frame.setResizable(false);
            frame.setVisible(true);

            frame.addWindowListener(new WindowAdapter(){
               public void windowClosing(WindowEvent evt) {
                  mediaPlayer.release();
                  factory.release();
                  System.exit(0);
               }
            });
         }

      });

      factory = new MediaPlayerFactory(args);
      mediaPlayer = factory.newDirectMediaPlayer(width, height, new TestRenderCallback());
      mediaPlayer.setPlaySubItems(true); // So that flv URLs will work
      mediaPlayer.playMedia(media);

      // Just to show regular media player functions still work...
      //   Thread.sleep(5000);
      mediaPlayer.nextChapter();
   }

   public static void main(String[] args) throws InterruptedException, InvocationTargetException {
      if (args.length < 1) {
         System.out.println("Specify a single media URL");
         System.exit(1);
      }

      String[] vlcArgs = (args.length == 1) ? new String[] {} : Arrays.copyOfRange(args, 1, args.length);

      new Main(args[0], vlcArgs);

      // Application will not exit since the UI thread is running
   }

   @SuppressWarnings("serial") private final class ImagePane extends JPanel{

      private final BufferedImage image;

      private final Font font = new Font("Sansserif", Font.BOLD, 36);

      public ImagePane(BufferedImage image) {
         this.image = image;
      }

      @Override public void paint(Graphics g) {
         Graphics2D g2 = (Graphics2D) g;
         g2.drawImage(image, null, 0, 0);
         if (selected) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            g2.setColor(Color.red);
            g2.setComposite(AlphaComposite.SrcOver.derive(0.3f));
            g2.fillRoundRect(100, 100, 100, 80, 32, 32);
            g2.setComposite(AlphaComposite.SrcOver);
            g2.setColor(Color.white);
            g2.setFont(font);
            g2.drawString("vlcj direct media player", 130, 150);
         }
      }
   }

   private final class TestRenderCallback extends RenderCallbackAdapter{

      public TestRenderCallback() {
         super(new int[width * height]);
      }

      @Override public void onDisplay(int[] data) {

         kernel.data = data;
         if (selected) {
            long start = System.currentTimeMillis();
            kernel.apply(EDGE); //EMBOSS_3, EMBOSS_4);
            System.out.println(kernel.getExecutionMode() + " " + (System.currentTimeMillis() - start));
         }

         image.setRGB(0, 0, width, height, data, 0, width);
         imagePane.repaint();
      }
   }
}
