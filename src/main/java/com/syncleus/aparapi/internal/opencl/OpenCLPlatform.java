package com.syncleus.aparapi.internal.opencl;

import com.syncleus.aparapi.device.*;
import com.syncleus.aparapi.internal.jni.*;

import java.util.*;

public class OpenCLPlatform extends OpenCLJNI{

   private long platformId;

   private final String version;

   private final String vendor;

   private final String name;

   private final List<OpenCLDevice> devices = new ArrayList<OpenCLDevice>();

   private static List<OpenCLPlatform> platforms;

   /**
    * Default constructor
    */
   public OpenCLPlatform() {
      version = "";
      vendor = "";
      name = "";
   }

   /**
    * Full constructor
    * 
    * @param _platformId
    * @param _version
    * @param _vendor
    * @param _name
    */
   public OpenCLPlatform(long _platformId, String _version, String _vendor, String _name) {
      platformId = _platformId;
      version = _version;
      vendor = _vendor;
      name = _name;
   }

   public void addOpenCLDevice(OpenCLDevice device) {
      devices.add(device);
   }

   public List<OpenCLDevice> getOpenCLDevices() {
      return (devices);
   }

   public List<OpenCLPlatform> getOpenCLPlatforms() {
      if (platforms == null) {
         if (OpenCLLoader.isOpenCLAvailable()) {
            platforms = getPlatforms();
         } else {
            return (Collections.EMPTY_LIST);
         }
      }
      return platforms;
   }

   public String getName() {
      return (name);
   }

   public String getVersion() {
      return (version);
   }

   public String getVendor() {
      return (vendor);
   }

   @Override public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append("PlatformId ");
      sb.append("\nName:");
      sb.append(vendor);
      sb.append("\nVersion:");
      sb.append(version);

      return sb.toString();
   }
}
