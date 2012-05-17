package com.amd.aparapi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface OpenCL<T> {

   public @Target(ElementType.PARAMETER) @Retention(RetentionPolicy.RUNTIME) @interface Put {
   }

   public @Target(ElementType.PARAMETER) @Retention(RetentionPolicy.RUNTIME) @interface Get {
   }

   public @Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME) @interface Source {
      String value();

   }

   public @Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME) @interface Resource {
      String value();
   }

   public @Target(ElementType.METHOD) @Retention(RetentionPolicy.RUNTIME) @interface Kernel {
      String value();
   }

   public @Target(ElementType.PARAMETER) @Retention(RetentionPolicy.RUNTIME) @interface Arg {
      String value();
   }

   public @Target(ElementType.PARAMETER) @Retention(RetentionPolicy.RUNTIME) @interface GlobalReadWrite {
      String value();
   }

   public @Target(ElementType.PARAMETER) @Retention(RetentionPolicy.RUNTIME) @interface GlobalReadOnly {
      String value();
   }

   public @Target(ElementType.PARAMETER) @Retention(RetentionPolicy.RUNTIME) @interface GlobalWriteOnly {
      String value();
   }

   public @Target(ElementType.PARAMETER) @Retention(RetentionPolicy.RUNTIME) @interface Local {
      String value();
   }

   public @Target(ElementType.PARAMETER) @Retention(RetentionPolicy.RUNTIME) @interface Constant {
      String value();
   }

   public T put(float[] array);

   public T put(int[] array);

   public T put(short[] array);
   
   public T put(byte[] array);

   public T put(char[] array);

   public T put(boolean[] array);

   public T put(double[] array);

   public T get(float[] array);

   public T get(int[] array);

   public T get(short[] array);

   public T get(char[] array);

   public T get(boolean[] array);

   public T get(double[] array);
   
   public T get(byte[] array);
   
   public T begin();
   
   public T end();
}
