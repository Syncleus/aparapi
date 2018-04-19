/**
 * Copyright (c) 2016 - 2018 Syncleus, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aparapi.device;

import com.aparapi.*;
import com.aparapi.internal.kernel.*;

public abstract class Device{

   public static enum TYPE {
      UNKNOWN(Integer.MAX_VALUE),
      GPU(2),
      CPU(3),
      JTP(5),
      SEQ(6),
      ACC(1),
      ALT(4);

      /** Heuristic ranking of device types, lower is better. */
      public final int rank;

      TYPE(int rank) {
         this.rank = rank;
      }
   };

   /** @deprecated  use {@link KernelManager#bestDevice()}
    *  @see com.aparapi.device
    */
   @Deprecated
   public static Device best() {
      return KernelManager.instance().bestDevice();
   }

   /**
    *  @see com.aparapi.device
    */
   @SuppressWarnings("deprecation")
   @Deprecated
   public static Device bestGPU() {
      return firstGPU();
   }

   /**
    *  @see com.aparapi.device
    */
   @Deprecated
   public static Device first(final Device.TYPE _type) {
      return KernelManager.DeprecatedMethods.firstDevice(_type);
   }

   /**
    *  @see com.aparapi.device
    */
   @SuppressWarnings("deprecation")
   @Deprecated
   public static Device firstGPU() {
      return KernelManager.DeprecatedMethods.firstDevice(TYPE.GPU);
   }

   /**
    *  @see com.aparapi.device
    */
   @SuppressWarnings("deprecation")
   @Deprecated
   public static Device firstCPU() {
      return KernelManager.DeprecatedMethods.firstDevice(TYPE.CPU);
   }

   /**
    *  @see com.aparapi.device
    */
   @Deprecated
   public static Device bestACC() {
      throw new UnsupportedOperationException();
   }

   protected TYPE type = TYPE.UNKNOWN;

   protected int maxWorkGroupSize;

   protected int maxWorkItemDimensions;

   protected int[] maxWorkItemSize = new int[] {
         0,
         0,
         0
   };

   public abstract String getShortDescription();

   public TYPE getType() {
      return type;
   }

   public void setType(TYPE type) {
      this.type = type;
   }

   public int getMaxWorkItemDimensions() {
      return maxWorkItemDimensions;
   }

   public void setMaxWorkItemDimensions(int _maxWorkItemDimensions) {
      maxWorkItemDimensions = _maxWorkItemDimensions;
   }

   public int getMaxWorkGroupSize() {
      return maxWorkGroupSize;
   }

   public void setMaxWorkGroupSize(int _maxWorkGroupSize) {
      maxWorkGroupSize = _maxWorkGroupSize;
   }

   public int[] getMaxWorkItemSize() {
      return maxWorkItemSize;
   }

   public void setMaxWorkItemSize(int[] maxWorkItemSize) {
      this.maxWorkItemSize = maxWorkItemSize;
   }

   public Range createRange(int _globalWidth) {
      return (Range.create(this, _globalWidth));
   }

   public Range createRange(int _globalWidth, int _localWidth) {
      return (Range.create(this, _globalWidth, _localWidth));
   }

   public Range createRange2D(int _globalWidth, int _globalHeight) {
      return (Range.create2D(this, _globalWidth, _globalHeight));
   }

   public Range createRange2D(int _globalWidth, int _globalHeight, int _localWidth, int _localHeight) {
      return (Range.create2D(this, _globalWidth, _globalHeight, _localWidth, _localHeight));
   }

   public Range createRange3D(int _globalWidth, int _globalHeight, int _globalDepth) {
      return (Range.create3D(this, _globalWidth, _globalHeight, _globalDepth));
   }

   public Range createRange3D(int _globalWidth, int _globalHeight, int _globalDepth, int _localWidth, int _localHeight,
         int _localDepth) {
      return (Range.create3D(this, _globalWidth, _globalHeight, _globalDepth, _localWidth, _localHeight, _localDepth));
   }

   public abstract long getDeviceId();

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (!(o instanceof Device)) {
         return false;
      }

      Device device = (Device) o;

      return getDeviceId() == device.getDeviceId();
   }

   @Override
   public int hashCode() {
      return Long.valueOf(getDeviceId()).hashCode();
   }
}
