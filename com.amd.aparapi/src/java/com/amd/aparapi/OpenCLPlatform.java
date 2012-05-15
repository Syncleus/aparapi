package com.amd.aparapi;

import java.util.ArrayList;
import java.util.List;

public class OpenCLPlatform{
   private long platformId;

   private String version;

   private String vendor;

   private List<OpenCLDevice> devices = new ArrayList<OpenCLDevice>();

   OpenCLPlatform(long _platformId, String _version, String _vendor) {
      platformId = _platformId;
      version = _version;
      vendor = _vendor;
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
      if (OpenCLJNI.getJNI().isOpenCLAvailable()){
         return (OpenCLJNI.getJNI().getPlatforms());
      }else{
         return(new ArrayList<OpenCLPlatform>());
      }
      
   }

}
