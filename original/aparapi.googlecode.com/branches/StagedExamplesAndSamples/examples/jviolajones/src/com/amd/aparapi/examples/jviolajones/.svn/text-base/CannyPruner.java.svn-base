package com.amd.aparapi.examples.jviolajones;

/**
This project is based on the open source jviolajones project created by Simon
Houllier and is used with his permission. Simon's jviolajones project offers 
a pure Java implementation of the Viola-Jones algorithm.

http://en.wikipedia.org/wiki/Viola%E2%80%93Jones_object_detection_framework

The original Java source code for jviolajones can be found here
http://code.google.com/p/jviolajones/ and is subject to the
gnu lesser public license  http://www.gnu.org/licenses/lgpl.html

Many thanks to Simon for his excellent project and for permission to use it 
as the basis of an Aparapi example.
**/

public class CannyPruner{

   public int[] getIntegralCanny(int[] grayImage, int width, int height) {
      StopWatch stopWatch = new StopWatch();

      int[] canny = new int[grayImage.length];
      final StopWatch timer = new StopWatch();
      timer.start();
      for (int i = 2; i < width - 2; i++) {
         for (int j = 2; j < height - 2; j++) {
            int sum = 0;
            sum += 2 * grayImage[i - 2 + (j - 2) * width];
            sum += 4 * grayImage[i - 2 + (j - 1) * width];
            sum += 5 * grayImage[i - 2 + (j + 0) * width];
            sum += 4 * grayImage[i - 2 + (j + 1) * width];
            sum += 2 * grayImage[i - 2 + (j + 2) * width];
            sum += 4 * grayImage[i - 1 + (j - 2) * width];
            sum += 9 * grayImage[i - 1 + (j - 1) * width];
            sum += 12 * grayImage[i - 1 + (j + 0) * width];
            sum += 9 * grayImage[i - 1 + (j + 1) * width];
            sum += 4 * grayImage[i - 1 + (j + 2) * width];
            sum += 5 * grayImage[i + 0 + (j - 2) * width];
            sum += 12 * grayImage[i + 0 + (j - 1) * width];
            sum += 15 * grayImage[i + 0 + (j + 0) * width];
            sum += 12 * grayImage[i + 0 + (j + 1) * width];
            sum += 5 * grayImage[i + 0 + (j + 2) * width];
            sum += 4 * grayImage[i + 1 + (j - 2) * width];
            sum += 9 * grayImage[i + 1 + (j - 1) * width];
            sum += 12 * grayImage[i + 1 + (j + 0) * width];
            sum += 9 * grayImage[i + 1 + (j + 1) * width];
            sum += 4 * grayImage[i + 1 + (j + 2) * width];
            sum += 2 * grayImage[i + 2 + (j - 2) * width];
            sum += 4 * grayImage[i + 2 + (j - 1) * width];
            sum += 5 * grayImage[i + 2 + (j + 0) * width];
            sum += 4 * grayImage[i + 2 + (j + 1) * width];
            sum += 2 * grayImage[i + 2 + (j + 2) * width];

            canny[i + j * width] = sum / 159;
            //System.out.println(canny[i][j]);
         }
      }
      timer.print("canny convolution");
      timer.start();
      int[] grad = new int[grayImage.length];
      for (int i = 1; i < width - 1; i++) {
         for (int j = 1; j < height - 1; j++) {
            int grad_x = -canny[i - 1 + (j - 1) * width] + canny[i + 1 + (j - 1) * width] - 2 * canny[i - 1 + (j) * width] + 2
                  * canny[i + 1 + (j) * width] - canny[i - 1 + (j + 1) * width] + canny[i + 1 + (j + 1) * width];
            int grad_y = canny[i - 1 + (j - 1) * width] + 2 * canny[i + (j - 1) * width] + canny[i + 1 + (j - 1) * width]
                  - canny[i - 1 + (j + 1) * width] - 2 * canny[i + (j + 1) * width] - canny[i + 1 + (j + 1) * width];
            grad[i + j * width] = Math.abs(grad_x) + Math.abs(grad_y);
            //System.out.println(grad[i][j]);
         }
      }
      timer.print("canny convolution 2");
      timer.start();
      //JFrame f = new JFrame();
      //f.setContentPane(new DessinChiffre(grad));
      //f.setVisible(true);
      for (int i = 0; i < width; i++) {
         int col = 0;
         for (int j = 0; j < height; j++) {
            int value = grad[i + j * width];
            canny[i + j * width] = (i > 0 ? canny[i - 1 + j * width] : 0) + col + value; // NOT data parallel
            col += value;
         }
      }
      timer.print("canny convolution 3");
      stopWatch.print("canny integral");
      return canny;

   }

}
