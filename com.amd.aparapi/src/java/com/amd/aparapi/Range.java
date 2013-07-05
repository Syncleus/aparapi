package com.amd.aparapi;

import java.util.Arrays;

import com.amd.aparapi.device.Device;
import com.amd.aparapi.internal.jni.RangeJNI;

/**
 * 
 * A representation of 1, 2 or 3 dimensional range of execution. 
 * 
 * This class uses factory methods to allow one, two or three dimensional ranges to be created. 
 * <br/>
 * For a Kernel operating over the linear range 0..1024 without a specified groups size we would create a one dimensional <code>Range</code> using 
 * <blockquote><pre>Range.create(1024);</pre></blockquote>
 * To request the same linear range but with a groupSize of 64 (range must be a multiple of group size!) we would use
 * <blockquote><pre>Range.create(1024,64);</pre></blockquote>
 * To request a two dimensional range over a grid (0..width)x(0..height) where width==512 and height=256 we would use
 * <blockquote><pre>
 * int width=512;
 * int height=256;
 * Range.create2D(width,height)
 * </pre></blockquote>
 * Again the above does not specify the group size.  One will be chosen for you. If you want to specify the groupSize (say 16x8; 16 wide by 8 high) use
 * <blockquote><pre>
 * int width=512;
 * int height=256;
 * int groupWidth=16;
 * int groupHeight=8;
 * Range.create2D(width, height, groupWidth, groupHeight);
 * </pre></blockquote>
 * Finally we can request a three dimensional range using 
 * <blockquote><pre>
 * int width=512;
 * int height=256;
 * int depth=8;
 * Range.create3D(width, height, depth);
 * </pre></blockquote>
 * And can specify a group size using 
 * <blockquote><pre>
 *  int width=512;
 *  int height=256;
 *  int depth=8;
 *  int groupWidth=8;
 *  int groupHeight=4;
 *  int groupDepth=2
 *  Range.create3D(width, height, depth, groupWidth, groupHeight, groupDepth);
 * </pre></blockquote>
 */
public class Range extends RangeJNI{

   public static final int THREADS_PER_CORE = 16;

   public static final int MAX_OPENCL_GROUP_SIZE = 256;

   public static final int MAX_GROUP_SIZE = Math.max(Runtime.getRuntime().availableProcessors() * THREADS_PER_CORE,
         MAX_OPENCL_GROUP_SIZE);

   private Device device = null;

   private int maxWorkGroupSize;

   private int[] maxWorkItemSize = new int[] {
         MAX_GROUP_SIZE,
         MAX_GROUP_SIZE,
         MAX_GROUP_SIZE
   };

   /**
    * Minimal constructor
    * 
    * @param _device
    * @param _dims
    */
   public Range(Device _device, int _dims) {
      device = _device;
      dims = _dims;

      if (device != null) {
         maxWorkItemSize = device.getMaxWorkItemSize();
         maxWorkGroupSize = device.getMaxWorkGroupSize();
      } else {
         maxWorkGroupSize = MAX_GROUP_SIZE;
      }
   }

   /** 
    * Create a one dimensional range <code>0.._globalWidth</code> which is processed in groups of size _localWidth.
    * <br/>
    * Note that for this range to be valid : </br> <strong><code>_globalWidth > 0 && _localWidth > 0 && _localWidth < MAX_GROUP_SIZE && _globalWidth % _localWidth==0</code></strong>
    * 
    * @param _globalWidth the overall range we wish to process
    * @param _localWidth the size of the group we wish to process.
    * @return A new Range with the requested dimensions
    */
   public static Range create(Device _device, int _globalWidth, int _localWidth) {
      final Range range = new Range(_device, 1);

      range.setGlobalSize_0(_globalWidth);
      range.setLocalSize_0(_localWidth);

      range.setValid((range.getLocalSize_0() > 0) && (range.getLocalSize_0() <= range.getMaxWorkItemSize()[0])
            && (range.getLocalSize_0() <= range.getMaxWorkGroupSize()) && ((range.getGlobalSize_0() % range.getLocalSize_0()) == 0));

      return (range);
   }

   /**
    * Determine the set of factors for a given value.
    * @param _value The value we wish to factorize. 
    * @param _max an upper bound on the value that can be chosen
    * @return and array of factors of _value
    */

   private static int[] getFactors(int _value, int _max) {
      final int factors[] = new int[MAX_GROUP_SIZE];
      int factorIdx = 0;

      for (int possibleFactor = 1; possibleFactor <= _max; possibleFactor++) {
         if ((_value % possibleFactor) == 0) {
            factors[factorIdx++] = possibleFactor;
         }
      }

      return (Arrays.copyOf(factors, factorIdx));
   }

   /** 
    * Create a one dimensional range <code>0.._globalWidth</code> with an undefined group size.
    * <br/>
    * Note that for this range to be valid :- </br> <strong><code>_globalWidth > 0 </code></strong>
    * <br/>
    * The groupsize will be chosen such that _localWidth > 0 && _localWidth < MAX_GROUP_SIZE && _globalWidth % _localWidth==0 is true
    * 
    * We extract the factors of _globalWidth and choose the highest value.
    * 
    * @param _globalWidth the overall range we wish to process
    * @return A new Range with the requested dimensions
    */
   public static Range create(Device _device, int _globalWidth) {
      final Range withoutLocal = create(_device, _globalWidth, 1);

      if (withoutLocal.isValid()) {
         withoutLocal.setLocalIsDerived(true);
         final int[] factors = getFactors(withoutLocal.getGlobalSize_0(), withoutLocal.getMaxWorkItemSize()[0]);

         withoutLocal.setLocalSize_0(factors[factors.length - 1]);

         withoutLocal.setValid((withoutLocal.getLocalSize_0() > 0)
               && (withoutLocal.getLocalSize_0() <= withoutLocal.getMaxWorkItemSize()[0])
               && (withoutLocal.getLocalSize_0() <= withoutLocal.getMaxWorkGroupSize())
               && ((withoutLocal.getGlobalSize_0() % withoutLocal.getLocalSize_0()) == 0));
      }

      return (withoutLocal);
   }

   public static Range create(int _globalWidth, int _localWidth) {
      final Range range = create(null, _globalWidth, _localWidth);

      return (range);
   }

   public static Range create(int _globalWidth) {
      final Range range = create(null, _globalWidth);

      return (range);
   }

   /** 
    * Create a two dimensional range 0.._globalWidth x 0.._globalHeight using a group which is _localWidth x _localHeight in size.
    * <br/>
    * Note that for this range to be valid  _globalWidth > 0 &&  _globalHeight >0 && _localWidth>0 && _localHeight>0 && _localWidth*_localHeight < MAX_GROUP_SIZE && _globalWidth%_localWidth==0 && _globalHeight%_localHeight==0.
    * 
    *  @param _globalWidth the overall range we wish to process
    * @return
    */
   public static Range create2D(Device _device, int _globalWidth, int _globalHeight, int _localWidth, int _localHeight) {
      final Range range = new Range(_device, 2);

      range.setGlobalSize_0(_globalWidth);
      range.setLocalSize_0(_localWidth);
      range.setGlobalSize_1(_globalHeight);
      range.setLocalSize_1(_localHeight);

      range.setValid((range.getLocalSize_0() > 0) && (range.getLocalSize_1() > 0)
            && (range.getLocalSize_0() <= range.getMaxWorkItemSize()[0])
            && (range.getLocalSize_1() <= range.getMaxWorkItemSize()[1])
            && ((range.getLocalSize_0() * range.getLocalSize_1()) <= range.getMaxWorkGroupSize())
            && ((range.getGlobalSize_0() % range.getLocalSize_0()) == 0)
            && ((range.getGlobalSize_1() % range.getLocalSize_1()) == 0));

      return (range);
   }

   /** 
    * Create a two dimensional range <code>0.._globalWidth * 0.._globalHeight</code> choosing suitable values for <code>localWidth</code> and <code>localHeight</code>.
    * <p>
    * Note that for this range to be valid  <code>_globalWidth > 0 &&  _globalHeight >0 && _localWidth>0 && _localHeight>0 && _localWidth*_localHeight < MAX_GROUP_SIZE && _globalWidth%_localWidth==0 && _globalHeight%_localHeight==0</code>.
    * 
    * <p>
    * To determine suitable values for <code>_localWidth</code> and <code>_localHeight</code> we extract the factors for <code>_globalWidth</code> and <code>_globalHeight</code> and then 
    * find the largest product ( <code><= MAX_GROUP_SIZE</code>) with the lowest perimeter.
    * 
    * <p>
    * For example for <code>MAX_GROUP_SIZE</code> of 16 we favor 4x4 over 1x16.
    * 
    * @param _globalWidth the overall range we wish to process
    * @return
    */
   public static Range create2D(Device _device, int _globalWidth, int _globalHeight) {
      final Range withoutLocal = create2D(_device, _globalWidth, _globalHeight, 1, 1);

      if (withoutLocal.isValid()) {
         withoutLocal.setLocalIsDerived(true);
         final int[] widthFactors = getFactors(_globalWidth, withoutLocal.getMaxWorkItemSize()[0]);
         final int[] heightFactors = getFactors(_globalHeight, withoutLocal.getMaxWorkItemSize()[1]);

         withoutLocal.setLocalSize_0(1);
         withoutLocal.setLocalSize_1(1);
         int max = 1;
         int perimeter = 0;

         for (final int w : widthFactors) {
            for (final int h : heightFactors) {
               final int size = w * h;
               if (size > withoutLocal.getMaxWorkGroupSize()) {
                  break;
               }

               if (size > max) {
                  max = size;
                  perimeter = w + h;
                  withoutLocal.setLocalSize_0(w);
                  withoutLocal.setLocalSize_1(h);
               } else if (size == max) {
                  final int localPerimeter = w + h;
                  if (localPerimeter < perimeter) {// is this the shortest perimeter so far
                     perimeter = localPerimeter;
                     withoutLocal.setLocalSize_0(w);
                     withoutLocal.setLocalSize_1(h);
                  }
               }
            }
         }

         withoutLocal.setValid((withoutLocal.getLocalSize_0() > 0) && (withoutLocal.getLocalSize_1() > 0)
               && (withoutLocal.getLocalSize_0() <= withoutLocal.getMaxWorkItemSize()[0])
               && (withoutLocal.getLocalSize_1() <= withoutLocal.getMaxWorkItemSize()[1])
               && ((withoutLocal.getLocalSize_0() * withoutLocal.getLocalSize_1()) <= withoutLocal.getMaxWorkGroupSize())
               && ((withoutLocal.getGlobalSize_0() % withoutLocal.getLocalSize_0()) == 0)
               && ((withoutLocal.getGlobalSize_1() % withoutLocal.getLocalSize_1()) == 0));
      }

      return (withoutLocal);
   }

   public static Range create2D(int _globalWidth, int _globalHeight, int _localWidth, int _localHeight) {
      final Range range = create2D(null, _globalWidth, _globalHeight, _localWidth, _localHeight);

      return (range);
   }

   public static Range create2D(int _globalWidth, int _globalHeight) {
      final Range range = create2D(null, _globalWidth, _globalHeight);

      return (range);
   }

   /** 
    * Create a two dimensional range <code>0.._globalWidth * 0.._globalHeight *0../_globalDepth</code> 
    * in groups defined by  <code>localWidth</code> * <code>localHeight</code> * <code>localDepth</code>.
    * <p>
    * Note that for this range to be valid  <code>_globalWidth > 0 &&  _globalHeight >0 _globalDepth >0 && _localWidth>0 && _localHeight>0 && _localDepth>0 && _localWidth*_localHeight*_localDepth < MAX_GROUP_SIZE && _globalWidth%_localWidth==0 && _globalHeight%_localHeight==0 && _globalDepth%_localDepth==0</code>.
    * 
    * @param _globalWidth the width of the 3D grid we wish to process
    * @param _globalHeight the height of the 3D grid we wish to process
    * @param _globalDepth the depth of the 3D grid we wish to process
    * @param _localWidth the width of the 3D group we wish to process
    * @param _localHeight the height of the 3D group we wish to process
    * @param _localDepth the depth of the 3D group we wish to process
    * @return
    */
   public static Range create3D(Device _device, int _globalWidth, int _globalHeight, int _globalDepth, int _localWidth,
         int _localHeight, int _localDepth) {
      final Range range = new Range(_device, 3);

      range.setGlobalSize_0(_globalWidth);
      range.setLocalSize_0(_localWidth);
      range.setGlobalSize_1(_globalHeight);
      range.setLocalSize_1(_localHeight);
      range.setGlobalSize_2(_globalDepth);
      range.setLocalSize_2(_localDepth);
      range.setValid((range.getLocalSize_0() > 0) && (range.getLocalSize_1() > 0) && (range.getLocalSize_2() > 0)
            && ((range.getLocalSize_0() * range.getLocalSize_1() * range.getLocalSize_2()) <= range.getMaxWorkGroupSize())
            && (range.getLocalSize_0() <= range.getMaxWorkItemSize()[0])
            && (range.getLocalSize_1() <= range.getMaxWorkItemSize()[1])
            && (range.getLocalSize_2() <= range.getMaxWorkItemSize()[2])
            && ((range.getGlobalSize_0() % range.getLocalSize_0()) == 0)
            && ((range.getGlobalSize_1() % range.getLocalSize_1()) == 0)
            && ((range.getGlobalSize_2() % range.getLocalSize_2()) == 0));

      return (range);
   }

   /** 
    * Create a three dimensional range <code>0.._globalWidth * 0.._globalHeight *0../_globalDepth</code> 
    * choosing suitable values for <code>localWidth</code>, <code>localHeight</code> and <code>localDepth</code>.
    * <p>
     * Note that for this range to be valid  <code>_globalWidth > 0 &&  _globalHeight >0 _globalDepth >0 && _localWidth>0 && _localHeight>0 && _localDepth>0 && _localWidth*_localHeight*_localDepth < MAX_GROUP_SIZE && _globalWidth%_localWidth==0 && _globalHeight%_localHeight==0 && _globalDepth%_localDepth==0</code>.
    * 
    * <p>
    * To determine suitable values for <code>_localWidth</code>,<code>_localHeight</code> and <code>_lodalDepth</code> we extract the factors for <code>_globalWidth</code>,<code>_globalHeight</code> and <code>_globalDepth</code> and then 
    * find the largest product ( <code><= MAX_GROUP_SIZE</code>) with the lowest perimeter.
    * 
    * <p>
    * For example for <code>MAX_GROUP_SIZE</code> of 64 we favor 4x4x4 over 1x16x16.
    * 
    * @param _globalWidth the width of the 3D grid we wish to process
    * @param _globalHieght the height of the 3D grid we wish to process
    * @param _globalDepth the depth of the 3D grid we wish to process
    * @return
    */
   public static Range create3D(Device _device, int _globalWidth, int _globalHeight, int _globalDepth) {
      final Range withoutLocal = create3D(_device, _globalWidth, _globalHeight, _globalDepth, 1, 1, 1);

      if (withoutLocal.isValid()) {
         withoutLocal.setLocalIsDerived(true);

         final int[] widthFactors = getFactors(_globalWidth, withoutLocal.getMaxWorkItemSize()[0]);
         final int[] heightFactors = getFactors(_globalHeight, withoutLocal.getMaxWorkItemSize()[1]);
         final int[] depthFactors = getFactors(_globalDepth, withoutLocal.getMaxWorkItemSize()[2]);

         withoutLocal.setLocalSize_0(1);
         withoutLocal.setLocalSize_1(1);
         withoutLocal.setLocalSize_2(1);

         int max = 1;
         int perimeter = 0;

         for (final int w : widthFactors) {
            for (final int h : heightFactors) {
               for (final int d : depthFactors) {
                  final int size = w * h * d;
                  if (size > withoutLocal.getMaxWorkGroupSize()) {
                     break;
                  }

                  if (size > max) {
                     max = size;
                     perimeter = w + h + d;
                     withoutLocal.setLocalSize_0(w);
                     withoutLocal.setLocalSize_1(h);
                     withoutLocal.setLocalSize_2(d);
                  } else if (size == max) {
                     final int localPerimeter = w + h + d;
                     if (localPerimeter < perimeter) { // is this the shortest perimeter so far
                        perimeter = localPerimeter;
                        withoutLocal.setLocalSize_0(w);
                        withoutLocal.setLocalSize_1(w);
                        withoutLocal.setLocalSize_2(d);
                     }
                  }
               }
            }
         }

         withoutLocal.setValid((withoutLocal.getLocalSize_0() > 0)
               && (withoutLocal.getLocalSize_1() > 0)
               && (withoutLocal.getLocalSize_2() > 0)
               && ((withoutLocal.getLocalSize_0() * withoutLocal.getLocalSize_1() * withoutLocal.getLocalSize_2()) <= withoutLocal
                     .getMaxWorkGroupSize()) && (withoutLocal.getLocalSize_0() <= withoutLocal.getMaxWorkItemSize()[0])
               && (withoutLocal.getLocalSize_1() <= withoutLocal.getMaxWorkItemSize()[1])
               && (withoutLocal.getLocalSize_2() <= withoutLocal.getMaxWorkItemSize()[2])
               && ((withoutLocal.getGlobalSize_0() % withoutLocal.getLocalSize_0()) == 0)
               && ((withoutLocal.getGlobalSize_1() % withoutLocal.getLocalSize_1()) == 0)
               && ((withoutLocal.getGlobalSize_2() % withoutLocal.getLocalSize_2()) == 0));
      }

      return (withoutLocal);
   }

   public static Range create3D(int _globalWidth, int _globalHeight, int _globalDepth) {
      final Range range = create3D(null, _globalWidth, _globalHeight, _globalDepth);

      return (range);
   }

   public static Range create3D(int _globalWidth, int _globalHeight, int _globalDepth, int _localWidth, int _localHeight,
         int _localDepth) {
      final Range range = create3D(null, _globalWidth, _globalHeight, _globalDepth, _localWidth, _localHeight, _localDepth);
      return (range);
   }

   /**
    * Override {@link #toString()}
    */
   @Override public String toString() {
      final StringBuilder sb = new StringBuilder();

      switch (dims) {
         case 1:
            sb.append("global:" + globalSize_0 + " local:" + (localIsDerived ? "(derived)" : "") + localSize_0);
            break;
         case 2:
            sb.append("2D(global:" + globalSize_0 + "x" + globalSize_1 + " local:" + (localIsDerived ? "(derived)" : "")
                  + localSize_0 + "x" + localSize_1 + ")");
            break;
         case 3:
             sb.append("3D(global:" + globalSize_0 + "x" + globalSize_1 + "x" + globalSize_2 + " local:"
                  + (localIsDerived ? "(derived)" : "") + localSize_0 + "x" + localSize_1 + "x" + localSize_2 + ")");
            break;
      }

      return (sb.toString());
   }

   /**
    * Get the localSize (of the group) given the requested dimension
    * 
    * @param _dim 0=width, 1=height, 2=depth
    * @return The size of the group give the requested dimension
    */
   public int getLocalSize(int _dim) {
      return (_dim == 0 ? localSize_0 : (_dim == 1 ? localSize_1 : localSize_2));
   }

   /**
    * Get the globalSize (of the range) given the requested dimension
    * 
    * @param _dim 0=width, 1=height, 2=depth
    * @return The size of the group give the requested dimension
    */
   public int getGlobalSize(int _dim) {
      return (_dim == 0 ? globalSize_0 : (_dim == 1 ? globalSize_1 : globalSize_2));
   }

   /**
    * Get the number of groups for the given dimension. 
    * 
    * <p>
    * This will essentially return globalXXXX/localXXXX for the given dimension (width, height, depth)
    * @param _dim The dim we are interested in 0, 1 or 2
    * @return the number of groups for the given dimension. 
    */
   public int getNumGroups(int _dim) {
      return (_dim == 0 ? (globalSize_0 / localSize_0) : (_dim == 1 ? (globalSize_1 / localSize_1) : (globalSize_2 / localSize_2)));
   }

   /**
    * 
    * @return The product of all valid localSize dimensions
    */
   public int getWorkGroupSize() {
      return localSize_0 * localSize_1 * localSize_2;
   }

   public Device getDevice() {
      return (device);
   }

   /**
    * @return the globalSize_0
    */
   public int getGlobalSize_0() {
      return globalSize_0;
   }

   /**
    * @param globalSize_0
    *          the globalSize_0 to set
    */
   public void setGlobalSize_0(int globalSize_0) {
      this.globalSize_0 = globalSize_0;
   }

   /**
    * @return the localSize_0
    */
   public int getLocalSize_0() {
      return localSize_0;
   }

   /**
    * @param localSize_0
    *          the localSize_0 to set
    */
   public void setLocalSize_0(int localSize_0) {
      this.localSize_0 = localSize_0;
   }

   /**
    * @return the globalSize_1
    */
   public int getGlobalSize_1() {
      return globalSize_1;
   }

   /**
    * @param globalSize_1
    *          the globalSize_1 to set
    */
   public void setGlobalSize_1(int globalSize_1) {
      this.globalSize_1 = globalSize_1;
   }

   /**
    * @return the localSize_1
    */
   public int getLocalSize_1() {
      return localSize_1;
   }

   /**
    * @param localSize_1
    *          the localSize_1 to set
    */
   public void setLocalSize_1(int localSize_1) {
      this.localSize_1 = localSize_1;
   }

   /**
    * @return the globalSize_2
    */
   public int getGlobalSize_2() {
      return globalSize_2;
   }

   /**
    * @param globalSize_2
    *          the globalSize_2 to set
    */
   public void setGlobalSize_2(int globalSize_2) {
      this.globalSize_2 = globalSize_2;
   }

   /**
    * @return the localSize_2
    */
   public int getLocalSize_2() {
      return localSize_2;
   }

   /**
    * @param localSize_2
    *          the localSize_2 to set
    */
   public void setLocalSize_2(int localSize_2) {
      this.localSize_2 = localSize_2;
   }

   /**
    * Get the number of dims for this Range.  
    * 
    * @return 0, 1 or 2 for one dimensional, two dimensional and three dimensional range respectively.
    */
   public int getDims() {
      return dims;
   }

   /**
    * @param dims
    *          the dims to set
    */
   public void setDims(int dims) {
      this.dims = dims;
   }

   /**
    * @return the valid
    */
   public boolean isValid() {
      return valid;
   }

   /**
    * @param valid
    *          the valid to set
    */
   public void setValid(boolean valid) {
      this.valid = valid;
   }

   /**
    * @return the localIsDerived
    */
   public boolean isLocalIsDerived() {
      return localIsDerived;
   }

   /**
    * @param localIsDerived
    *          the localIsDerived to set
    */
   public void setLocalIsDerived(boolean localIsDerived) {
      this.localIsDerived = localIsDerived;
   }

   /**
    * @return the maxWorkGroupSize
    */
   public int getMaxWorkGroupSize() {
      return maxWorkGroupSize;
   }

   /**
    * @param maxWorkGroupSize
    *          the maxWorkGroupSize to set
    */
   public void setMaxWorkGroupSize(int maxWorkGroupSize) {
      this.maxWorkGroupSize = maxWorkGroupSize;
   }

   /**
    * @return the maxWorkItemSize
    */
   public int[] getMaxWorkItemSize() {
      return maxWorkItemSize;
   }

   /**
    * @param maxWorkItemSize
    *          the maxWorkItemSize to set
    */
   public void setMaxWorkItemSize(int[] maxWorkItemSize) {
      this.maxWorkItemSize = maxWorkItemSize;
   }
}
