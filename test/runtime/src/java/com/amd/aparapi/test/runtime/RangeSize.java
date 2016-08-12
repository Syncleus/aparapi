package com.amd.aparapi.test.runtime;

import com.amd.aparapi.Range;

import static org.junit.Assert.*;

import org.junit.Test;

public class RangeSize{

   @Test public void test384x384() {
      Range range = Range.create2D(384, 384);
      System.out.println("local[0] " + range.getLocalSize(0));
      System.out.println("local[1] " + range.getLocalSize(1));
      System.out.println("workGroupSize " + range.getWorkGroupSize());
      assertTrue("Range > max work size", range.getLocalSize(0) * range.getLocalSize(1) <= range.getWorkGroupSize());
   }

   @Test public void test384x320() {
      Range range = Range.create2D(384, 320);
      System.out.println("local[0] " + range.getLocalSize(0));
      System.out.println("local[1] " + range.getLocalSize(1));
      System.out.println("workGroupSize " + range.getWorkGroupSize());
      assertTrue("Range > max work size", range.getLocalSize(0) * range.getLocalSize(1) <= range.getWorkGroupSize());
   }

}
