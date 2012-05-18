package com.amd.aparapi;

import java.util.ArrayList;
import java.util.List;

public class OpenCLPlatform{

   private long platformId;

   private String version;

   private String vendor;

   private String name;

   private List<OpenCLDevice> devices = new ArrayList<OpenCLDevice>();

   OpenCLPlatform(long _platformId, String _version, String _vendor, String _name) {
      platformId = _platformId;
      version = _version;
      vendor = _vendor;
      name = _name;
   }

   public String toString() {
      return ("PlatformId " + platformId + "\nName:" + vendor + "\nVersion:" + version);
   }

   public void add(OpenCLDevice device) {
      devices.add(device);
   }

   public List<OpenCLDevice> getDevices() {
      return (devices);
   }

   public static List<OpenCLPlatform> getPlatforms() {
      if (OpenCLJNI.getJNI().isOpenCLAvailable()) {
         return (OpenCLJNI.getJNI().getPlatforms());
      } else {
         return (new ArrayList<OpenCLPlatform>());
      }

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

}
