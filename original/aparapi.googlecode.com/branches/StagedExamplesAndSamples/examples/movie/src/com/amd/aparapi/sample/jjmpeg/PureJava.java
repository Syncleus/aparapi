package com.amd.aparapi.sample.jjmpeg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.WritableRaster;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import au.notzed.jjmpeg.io.JJMediaReader;
import au.notzed.jjmpeg.io.JJMediaReader.JJReaderVideo;

/**
 * Code based on Demo of JJVideoScanner class
 *
 * @author notzed
 */
public class PureJava{

   public static void main(final String[] args) {
      java.awt.image.Kernel conv = new java.awt.image.Kernel(3, 3, new float[] {
            0f,
            -10f,
            0f,
            -10f,
            40f,
            -10f,
            0f,
            -10f,
            0f
      });
      final ConvolveOp convOp = new ConvolveOp(conv, ConvolveOp.EDGE_NO_OP, null);
      // String name = "c:\\users\\gfrost\\Desktop\\afds\\MV5BMjEyMjMzODc0MV5BMTFeQW1wNF5BbWU3MDE3NzA0Nzc@.mp4";
// name = "C:\\Users\\gfrost\\Downloads\\HK2207_720p.mp4";
      new JJMPEGPlayer("Faces", args[0], true){
         @Override protected void process(Graphics2D gc, BufferedImage in, BufferedImage out) {

            convOp.filter(in, out);

         }
      };

   }
}
