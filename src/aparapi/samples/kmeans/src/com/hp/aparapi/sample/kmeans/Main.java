/*
HP All rights reserved...


*/

package com.hp.aparapi.sample.kmeans;

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

import com.amd.aparapi.Kernel;
import com.amd.aparapi.ProfileInfo;
import com.amd.aparapi.Range;

/**
 * An example Aparapi application which displays a view of the Mandelbrot set and lets the user zoom in to a particular point. 
 * 
 * When the user clicks on the view, this example application will zoom in to the clicked point and zoom out there after.
 * On GPU, additional computing units will offer a better viewing experience. On the other hand on CPU, this example 
 * application might suffer with sub-optimal frame refresh rate as compared to GPU. 
 *  
 * @author gfrost
 *
 */

public class Main{


   @SuppressWarnings("serial") public static void main(String[] _args) 
   {
	   
	   int size = Integer.getInteger("size", (1 << 20));
	   int clusters = Integer.getInteger("clusters", 20);
	   int dim = Integer.getInteger("dim", 20);
	   //Range range = Range.create(size);
	   System.out.println("Data size =" + size);
	   System.out.println("Num of Clusters =" + clusters);
	   System.out.println("Dim size =" + dim);
              
       //Range range = Range.create(512);
       
       KMeans km = new KMeans(size, clusters, dim);
       km.genVectors();
       km.populateData();
       //km.runKmeansGPU();       
	   
       long startMillis = System.currentTimeMillis();

        // Set the scale and offset, execute the kernel and force a repaint of the viewer.
       km.runKmeansGPU(); //.execute(range);
             
       long elapsedMillis = System.currentTimeMillis() - startMillis;
       System.out.println("GPU - Elapsed time in milli = " + elapsedMillis);

       startMillis = System.currentTimeMillis();
       km.runKmeansCPU();
       elapsedMillis = System.currentTimeMillis() - startMillis;
       System.out.println("CPU - Elapsed time in milli = " + elapsedMillis);
       
       km.checkResults();       

      }

   }
