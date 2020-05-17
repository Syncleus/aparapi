package convolution;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

@SuppressWarnings("serial") public abstract class ConvolutionViewer extends JFrame{

   private int height;

   private int width;

   private BufferedImage outputImage;

   private BufferedImage inputImage;

   private byte[] inBytes;

   private byte[] outBytes;

   private Graphics2D gc;

   private float[] convMatrix3x3;

   public ConvolutionViewer(File _file, float[] _convMatrix3x3) {

      JFrame frame = new JFrame("Convolution Viewer");

      convMatrix3x3 = _convMatrix3x3;
      try {
         inputImage = ImageIO.read(_file);

         // System.out.println(inputImage);

         height = inputImage.getHeight();

         width = inputImage.getWidth();

         outputImage = new BufferedImage(width, height, inputImage.getType());

         gc = outputImage.createGraphics();

         inBytes = ((DataBufferByte) inputImage.getRaster().getDataBuffer()).getData();
         outBytes = ((DataBufferByte) outputImage.getRaster().getDataBuffer()).getData();

         final JLabel imageLabel = new JLabel();
         imageLabel.setIcon(new ImageIcon(outputImage));

         ConvMatrix3x3Editor editor = new ConvMatrix3x3Editor(_convMatrix3x3){
            @Override protected void updated(float[] _convMatrix3x3) {
               convMatrix3x3 = _convMatrix3x3;
               long start = System.currentTimeMillis();

               applyConvolution(convMatrix3x3, inBytes, outBytes, width, height);
               long end = System.currentTimeMillis();
               gc.setColor(Color.BLACK);
               gc.fillRect(0, 0, 50, 40);
               gc.setColor(Color.YELLOW);
               gc.drawString("" + (end - start) + "ms", 10, 20);

               imageLabel.repaint();
            }
         };
         frame.getContentPane().add(editor.component, BorderLayout.WEST);

         frame.getContentPane().add(imageLabel, BorderLayout.CENTER);
         frame.pack();
         frame.setVisible(true);
         frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

         applyConvolution(convMatrix3x3, inBytes, outBytes, width, height);

         imageLabel.repaint();
      } catch (IOException e1) {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      }

   }

   abstract protected void applyConvolution(float[] convMatrix3x3, byte[] _inBytes, byte[] _outBytes, int _width, int _height);

}
