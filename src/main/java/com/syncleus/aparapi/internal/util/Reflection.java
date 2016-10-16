package com.syncleus.aparapi.internal.util;

/**
 * Created by Barney on 03/09/2015.
 */
public class Reflection {

   /** Avoids getting dumb empty names for anonymous inners. */
   public static String getSimpleName(Class<?> klass) {
      String simpleName = klass.getSimpleName();
      if (simpleName.isEmpty()) {
         String fullName = klass.getName();
         int index = fullName.lastIndexOf('.');
         simpleName = (index < 0) ? fullName : fullName.substring(index + 1);
      }
      return simpleName;
   }
}
