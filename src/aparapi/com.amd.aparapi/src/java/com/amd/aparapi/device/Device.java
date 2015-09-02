package com.amd.aparapi.device;

import java.util.List;

import com.amd.aparapi.Range;
import com.amd.aparapi.device.OpenCLDevice.DeviceComparitor;
import com.amd.aparapi.device.OpenCLDevice.DeviceSelector;
import com.amd.aparapi.internal.opencl.OpenCLPlatform;

public abstract class Device{

   public static enum TYPE {
      UNKNOWN,
      ACC,
      GPU,
      CPU,
      JTP,
      SEQ
   };
   
   // !!! oren change -> get device using the tuple (platform, deviceType, id)
   
   public static Device getDevice(String platformName, Device.TYPE deviceType, int deviceId)
   {
	   return getDevice(platformName,deviceType.name(),deviceId);
   }

   // get first available device

   public static Device getDevice(String platformName, Device.TYPE deviceType)
   {
	   return getDevice(platformName,deviceType.name(),0);
   }

   public static Device getDevice(String platformName, String deviceTypeName)
   {
	   return getDevice(platformName,deviceTypeName,0);
   }

   public static Device getDevice(String platformName, String deviceTypeName, int deviceId)
   {
      List<OpenCLPlatform> platforms = (new OpenCLPlatform()).getOpenCLPlatformsFilteredBy(platformName); //getOpenCLPlatforms();

      int platformc = 0;
      for (OpenCLPlatform platform : platforms) 
      {
         //if(platform.getName().contains(platformName))
         //{

           System.out.println("Platform " + platformc + "{");

           System.out.println("   Name    : \"" + platform.getName() + "\"");

           System.out.println("   Vendor  : \"" + platform.getVendor() + "\"");

           System.out.println("   Version : \"" + platform.getVersion() + "\"");

           List<OpenCLDevice> devices = platform.getOpenCLDevices();

           System.out.println("   Platform contains " + devices.size() + " OpenCL devices");

           int devicec = 0;

           for (OpenCLDevice device : devices) 
           {
             if( device.getType().name().equalsIgnoreCase(deviceTypeName))
             {

               System.out.println("   Device " + devicec + "{");

               System.out.println("       Type                  : " + device.getType());

               System.out.println("       GlobalMemSize         : " + device.getGlobalMemSize());

               System.out.println("       LocalMemSize          : " + device.getLocalMemSize());

               System.out.println("       MaxComputeUnits       : " + device.getMaxComputeUnits());

               System.out.println("       MaxWorkGroupSizes     : " + device.getMaxWorkGroupSize());

               System.out.println("       MaxWorkItemDimensions : " + device.getMaxWorkItemDimensions());

               System.out.println("   }");
               
               if(deviceId>0 && (devicec!=deviceId))
               {
            	   System.out.println("!!! devicec!=deviceId(" + deviceId + ") => continue search !!!");
            	   continue;
               }
            	   
               // close platform bracket
               System.out.println("}");

               return device; 
             }

             devicec++;
           }
           System.out.println("Device type/id combination not found");

           System.out.println("}");

           platformc++;

       }

     //}
     // return not found !!!
     return null;
   }


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
