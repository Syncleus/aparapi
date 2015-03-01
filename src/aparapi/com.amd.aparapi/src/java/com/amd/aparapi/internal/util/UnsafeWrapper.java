/*
Copyright (c) 2010-2011, Advanced Micro Devices, Inc.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following
disclaimer. 

Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
disclaimer in the documentation and/or other materials provided with the distribution. 

Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products
derived from this software without specific prior written permission. 

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

If you use the software (in whole or in part), you shall adhere to all applicable U.S., European, and other export
laws, including but not limited to the U.S. Export Administration Regulations ("EAR"), (15 C.F.R. Sections 730 through
774), and E.U. Council Regulation (EC) No 1334/2000 of 22 June 2000.  Further, pursuant to Section 740.6 of the EAR,
you hereby certify that, except pursuant to a license granted by the United States Department of Commerce Bureau of 
Industry and Security or as otherwise permitted pursuant to a License Exception under the U.S. Export Administration 
Regulations ("EAR"), you will not (1) export, re-export or release to a national of a country in Country Groups D:1,
E:1 or E:2 any restricted technology, software, or source code you receive hereunder, or (2) export to Country Groups
D:1, E:1 or E:2 the direct product of such technology or software, if such foreign produced direct product is subject
to national security controls as identified on the Commerce Control List (currently found in Supplement 1 to Part 774
of EAR).  For the most current Country Group listings, or for additional information about the EAR or your obligations
under those regulations, please refer to the U.S. Bureau of Industry and Security's website at http://www.bis.doc.gov/. 

*/
package com.amd.aparapi.internal.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A wrapper around sun.misc.Unsafe for handling atomic operations, copies from fields to arrays and vice versa.
 * 
 * We avoid using <code>sun.misc.Unsafe</code> directly using reflection, mostly just to avoid getting 'unsafe' compiler errors. 
 * 
 * This might need to be changed if we start to see performance issues.
 *
 * @author gfrost
 *
 */

public class UnsafeWrapper{

   private static Object unsafe;

   private static Method getIntVolatileMethod;

   private static Method arrayBaseOffsetMethod;

   private static Method arrayIndexScaleMethod;

   private static Method getObjectMethod;

   private static Method getIntMethod;

   private static Method getFloatMethod;

   private static Method getByteMethod;

   private static Method getBooleanMethod;

   private static Method getLongMethod;

   private static Method objectFieldOffsetMethod;

   private static Method putBooleanMethod;

   private static Method putIntMethod;

   private static Method putFloatMethod;

   private static Method putDoubleMethod;

   private static Method putByteMethod;

   private static Method putLongMethod;

   private static Method compareAndSwapIntMethod;

   static {
      try {
         final Class<?> uc = Class.forName("sun.misc.Unsafe");

         final Field field = uc.getDeclaredField("theUnsafe");
         field.setAccessible(true);
         unsafe = field.get(uc);
         getIntVolatileMethod = uc.getDeclaredMethod("getIntVolatile", Object.class, long.class);
         arrayBaseOffsetMethod = uc.getDeclaredMethod("arrayBaseOffset", Class.class);
         arrayIndexScaleMethod = uc.getDeclaredMethod("arrayIndexScale", Class.class);
         getObjectMethod = uc.getDeclaredMethod("getObject", Object.class, long.class);
         getIntMethod = uc.getDeclaredMethod("getInt", Object.class, long.class);
         getFloatMethod = uc.getDeclaredMethod("getFloat", Object.class, long.class);
         getByteMethod = uc.getDeclaredMethod("getByte", Object.class, long.class);
         getBooleanMethod = uc.getDeclaredMethod("getBoolean", Object.class, long.class);
         getLongMethod = uc.getDeclaredMethod("getLong", Object.class, long.class);
         objectFieldOffsetMethod = uc.getDeclaredMethod("objectFieldOffset", Field.class);
         putBooleanMethod = uc.getDeclaredMethod("putBoolean", Object.class, long.class, boolean.class);
         putIntMethod = uc.getDeclaredMethod("putInt", Object.class, long.class, int.class);
         putFloatMethod = uc.getDeclaredMethod("putFloat", Object.class, long.class, float.class);
         putDoubleMethod = uc.getDeclaredMethod("putDouble", Object.class, long.class, double.class);
         putLongMethod = uc.getDeclaredMethod("putLong", Object.class, long.class, long.class);
         putByteMethod = uc.getDeclaredMethod("putByte", Object.class, long.class, byte.class);
         compareAndSwapIntMethod = uc.getDeclaredMethod("compareAndSwapInt", Object.class, long.class, int.class, int.class);
      } catch (final SecurityException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final NoSuchFieldException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final IllegalArgumentException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final IllegalAccessException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final ClassNotFoundException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final NoSuchMethodException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   public static int atomicAdd(int[] _arr, int _index, int _delta) {
      if ((_index < 0) || (_index >= _arr.length)) {
         throw new IndexOutOfBoundsException("index " + _index);
      }

      final long rawIndex = intArrayBase + ((long) _index * intArrayScale);
      while (true) {
         int current;
         try {
            current = (Integer) getIntVolatileMethod.invoke(unsafe, _arr, rawIndex);
            final int next = current + _delta;
            if ((Boolean) compareAndSwapIntMethod.invoke(unsafe, _arr, rawIndex, current, next)) {
               return current;
            }
         } catch (final IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         } catch (final IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         } catch (final InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
   }

   public static int arrayBaseOffset(Class<?> _arrayClass) {
      int offset = 0;

      try {
         offset = (Integer) (arrayBaseOffsetMethod.invoke(unsafe, _arrayClass));
      } catch (final IllegalArgumentException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final IllegalAccessException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final InvocationTargetException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      return (offset);
   }

   public static int arrayIndexScale(Class<?> _arrayClass) {
      int scale = 0;
      try {
         scale = (Integer) (arrayIndexScaleMethod.invoke(unsafe, _arrayClass));
      } catch (final IllegalArgumentException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final IllegalAccessException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final InvocationTargetException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return scale;
   }

   private static int intArrayBase = arrayBaseOffset(int[].class);

   private static int intArrayScale = arrayIndexScale(int[].class);

   public static Object getObject(Object _object, long _offset) {
      Object object = null;
      try {
         object = getObjectMethod.invoke(unsafe, _object, _offset);
      } catch (final IllegalArgumentException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final IllegalAccessException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final InvocationTargetException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return (object);
   }

   public static int getInt(Object _object, long _offset) {
      int value = 0;
      try {
         value = (Integer) getIntMethod.invoke(unsafe, _object, _offset);
      } catch (final IllegalArgumentException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final IllegalAccessException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final InvocationTargetException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return value;
   }

   public static float getFloat(Object _object, long _offset) {
      float value = 0;
      try {
         value = (Float) getFloatMethod.invoke(unsafe, _object, _offset);
      } catch (final IllegalArgumentException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final IllegalAccessException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final InvocationTargetException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return value;
   }

   public static byte getByte(Object _object, long _offset) {
      byte value = 0;
      try {
         value = (Byte) getByteMethod.invoke(unsafe, _object, _offset);
      } catch (final IllegalArgumentException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final IllegalAccessException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final InvocationTargetException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return value;
   }

   public static boolean getBoolean(Object _object, long _offset) {
      boolean value = false;
      try {
         value = (Boolean) getBooleanMethod.invoke(unsafe, _object, _offset);
      } catch (final IllegalArgumentException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final IllegalAccessException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final InvocationTargetException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return value;
   }

   public static long getLong(Object _object, long _offset) {
      long value = 0;
      try {
         value = (Long) getLongMethod.invoke(unsafe, _object, _offset);
      } catch (final IllegalArgumentException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final IllegalAccessException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final InvocationTargetException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return value;
   }

   public static void putBoolean(Object _object, long _offset, boolean _boolean) {
      try {
         putBooleanMethod.invoke(unsafe, _object, _offset, _boolean);
      } catch (final IllegalArgumentException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final IllegalAccessException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final InvocationTargetException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   public static void putFloat(Object _object, long _offset, float _float) {
      try {
         putFloatMethod.invoke(unsafe, _object, _offset, _float);
      } catch (final IllegalArgumentException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final IllegalAccessException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final InvocationTargetException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   public static void putInt(Object _object, long _offset, int _int) {
      try {
         putIntMethod.invoke(unsafe, _object, _offset, _int);
      } catch (final IllegalArgumentException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final IllegalAccessException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final InvocationTargetException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   public static void putDouble(Object _object, long _offset, double _double) {
      try {
         putDoubleMethod.invoke(unsafe, _object, _offset, _double);
      } catch (final IllegalArgumentException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final IllegalAccessException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final InvocationTargetException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   public static void putByte(Object _object, long _offset, byte _byte) {
      try {
         putByteMethod.invoke(unsafe, _object, _offset, _byte);
      } catch (final IllegalArgumentException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final IllegalAccessException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final InvocationTargetException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   public static void putLong(Object _object, long _offset, long _long) {
      try {
         putLongMethod.invoke(unsafe, _object, _offset, _long);
      } catch (final IllegalArgumentException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final IllegalAccessException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final InvocationTargetException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   public static long objectFieldOffset(Field _field) {
      long offset = 0l;
      try {
         offset = (Long) objectFieldOffsetMethod.invoke(unsafe, _field);
      } catch (final IllegalArgumentException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final IllegalAccessException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (final InvocationTargetException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return offset;
   }
}
