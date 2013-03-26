package com.amd.aparapi.test.runtime;

import com.amd.aparapi.*;
import static org.junit.Assert.assertTrue;
import org.junit.Test;


final class BugDataObject {
   int value = 7;

   public int getValue()
   {
      return value;
   }

   public void setValue(int value)
   {
      this.value = value;
   }
}


public class Issue102 extends Kernel {
   static final int size = 32;
   
   static BugDataObject [] objects = new BugDataObject[size];
   int[] target = new int[size];

   @Override
   public void run() {
      int id = getGlobalId();
      target[id] = objects[id].getValue();
   }

   void validate() {
      for (int i = 0; i < size; i++) {
         System.out.println(target[i] + " ... " + objects[i].getValue());
         assertTrue("target == objects", target[i] == objects[i].getValue());
      }
   }
   
   @Test public void test() {
      execute(size);
      validate();
   }
   
   public static void main(String[] args) {
      Issue102 b = new Issue102();
      b.test();
   }

   public Issue102() {
      for(int i = 0; i < size; ++i) {
         objects[i] = new BugDataObject();
         target[i] = 99;
      }
   }
}
