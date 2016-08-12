package com.amd.aparapi.examples.movie;

import java.awt.Graphics2D;
import java.awt.image.ConvolveOp;

import java.awt.image.BufferedImage;

public class PureJavaSolution{

   public static void main(final String[] _args) {
      String fileName = _args.length == 1 ? _args[0] : "Leo720p.wmv";

      float[] convMatrix3x3 = new float[] {
            0f,
            -10f,
            0f,
            -10f,
            41f,
            -10f,
            0f,
            -10f,
            0f
      };

      new JJMPEGPlayer("lab_6.alternate", fileName, convMatrix3x3){

         @Override protected void processFrame(Graphics2D _gc, float[] _convMatrix3x3, BufferedImage _in, BufferedImage _out) {
            java.awt.image.Kernel conv = new java.awt.image.Kernel(3, 3, _convMatrix3x3);
            ConvolveOp convOp = new ConvolveOp(conv, ConvolveOp.EDGE_NO_OP, null);
            convOp.filter(_in, _out);
         }
      };

   }
}
