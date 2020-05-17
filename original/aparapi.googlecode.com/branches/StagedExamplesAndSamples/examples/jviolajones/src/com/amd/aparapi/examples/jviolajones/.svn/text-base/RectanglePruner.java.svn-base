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

import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;

public class RectanglePruner{

   public static List<Rectangle> merge(List<Rectangle> rects, int min_neighbors) {

      List<Rectangle> retour = new LinkedList<Rectangle>();
      int[] ret = new int[rects.size()];
      int nb_classes = 0;
      for (int i = 0; i < rects.size(); i++) {
         boolean found = false;
         for (int j = 0; j < i; j++) {
            if (equals(rects.get(j), rects.get(i))) {
               found = true;
               ret[i] = ret[j];
            }
         }
         if (!found) {
            ret[i] = nb_classes;
            nb_classes++;
         }
      }
      //System.out.println(Arrays.toString(ret));
      int[] neighbors = new int[nb_classes];
      Rectangle[] rect = new Rectangle[nb_classes];
      for (int i = 0; i < nb_classes; i++) {
         neighbors[i] = 0;
         rect[i] = new Rectangle(0, 0, 0, 0);
      }
      for (int i = 0; i < rects.size(); i++) {
         neighbors[ret[i]]++;
         rect[ret[i]].x += rects.get(i).x;
         rect[ret[i]].y += rects.get(i).y;
         rect[ret[i]].height += rects.get(i).height;
         rect[ret[i]].width += rects.get(i).width;
      }
      for (int i = 0; i < nb_classes; i++) {
         int n = neighbors[i];
         if (n >= min_neighbors) {
            Rectangle r = new Rectangle(0, 0, 0, 0);
            r.x = (rect[i].x * 2 + n) / (2 * n);
            r.y = (rect[i].y * 2 + n) / (2 * n);
            r.width = (rect[i].width * 2 + n) / (2 * n);
            r.height = (rect[i].height * 2 + n) / (2 * n);
            retour.add(r);
         }
      }

      return retour;

   }

   public static boolean equals(Rectangle r1, Rectangle r2) {

      int distance = (int) (r1.width * 0.2);

      if (r2.x <= r1.x + distance && r2.x >= r1.x - distance && r2.y <= r1.y + distance && r2.y >= r1.y - distance
            && r2.width <= (int) (r1.width * 1.2) && (int) (r2.width * 1.2) >= r1.width) {

         return true;
      }
      if (r1.x >= r2.x && r1.x + r1.width <= r2.x + r2.width && r1.y >= r2.y && r1.y + r1.height <= r2.y + r2.height) {

         return true;
      }

      return false;

   }
}
