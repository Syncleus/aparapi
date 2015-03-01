package com.amd.aparapi.test.runtime;

import java.util.Arrays;

public class Util{
   interface Filler{
      void fill(int[] array, int index);
   }

   interface Comparer{
      boolean same(int[] lhs, int[] rhs, int index);
   }

   interface Operator{
      void apply(int[] lhs, int[] rhs, int index);
   }

   static void fill(int[] array, Filler _filler) {
      for (int i = 0; i < array.length; i++) {
         _filler.fill(array, i);
      }
   }

   static boolean same(int[] lhs, int[] rhs, Comparer _comparer) {
      boolean same = lhs != null && rhs != null && lhs.length == rhs.length;
      for (int i = 0; same && i < lhs.length; i++) {
         same = _comparer.same(lhs, rhs, i);
      }
      return (same);
   }

   static void zero(int[] array) {
      Arrays.fill(array, 0);
   }

   static boolean same(int[] lhs, int[] rhs) {
      return (same(lhs, rhs, new Comparer(){

         @Override public boolean same(int[] lhs, int[] rhs, int index) {

            return lhs[index] == rhs[index];
         }
      }));
   }

   static boolean same(boolean[] lhs, boolean[] rhs) {
      boolean same = lhs != null && rhs != null && lhs.length == rhs.length;
      for (int i = 0; same && i < lhs.length; i++) {
         same = lhs[i] == rhs[i];
      }
      return (same);
   }

   static void apply(int[] lhs, int[] rhs, Operator _operator) {
      for (int i = 0; i < lhs.length; i++) {
         _operator.apply(lhs, rhs, i);
      }
   }

}
