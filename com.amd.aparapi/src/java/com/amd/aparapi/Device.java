package com.amd.aparapi;

import com.amd.aparapi.OpenCLDevice.DeviceComparitor;
import com.amd.aparapi.OpenCLDevice.DeviceSelector;

public abstract class Device{
   static public enum TYPE {
      UNKNOWN,
      GPU,
      CPU,
      JTP,
      SEQ
   };

   public static Device best() {
      return (OpenCLDevice.select(new DeviceComparitor(){
         @Override public OpenCLDevice select(OpenCLDevice _deviceLhs, OpenCLDevice _deviceRhs) {
            if (_deviceLhs.getType() != _deviceRhs.getType()) {
               if (_deviceLhs.getType() == TYPE.GPU) {
                  return (_deviceLhs);
               } else {
                  return (_deviceRhs);
               }
            }
            if (_deviceLhs.getMaxComputeUnits() > _deviceRhs.getMaxComputeUnits()) {
               return (_deviceLhs);
            } else {
               return (_deviceRhs);
            }

         }
      }));
   }

   public static Device first(final Device.TYPE _type) {
      return (OpenCLDevice.select(new DeviceSelector(){
         @Override public OpenCLDevice select(OpenCLDevice _device) {
            return (_device.getType() == _type ? _device : null);
         }
      }));
   }

   public static Device firstGPU() {
      return (first(Device.TYPE.GPU));
   }

   public static Device firstCPU() {
      return (first(Device.TYPE.CPU));

   }

   protected TYPE type = TYPE.UNKNOWN;

   protected int maxWorkGroupSize;

   protected int maxWorkItemDimensions;

   protected int[] maxWorkItemSize = new int[] {
         0,
         0,
         0
   };

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

}
