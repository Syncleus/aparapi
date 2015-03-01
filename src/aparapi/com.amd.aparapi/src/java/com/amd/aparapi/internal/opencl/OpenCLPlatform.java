package com.amd.aparapi.internal.opencl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.amd.aparapi.Config;
import com.amd.aparapi.device.OpenCLDevice;
import com.amd.aparapi.internal.jni.OpenCLJNI;

public class OpenCLPlatform extends OpenCLJNI{

   private long platformId;

   private final String version;

   private final String vendor;

   private final String name;

   private final List<OpenCLDevice> devices = new ArrayList<OpenCLDevice>();

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
      if (OpenCLLoader.isOpenCLAvailable()) {
         return (getPlatforms());
      } else {
         return (new ArrayList<OpenCLPlatform>());
      }
   }

   //!!! oren change 2.15.15 -> allow choosing a platform when multiple platforms are available
   // Currently aparapi does not offer a way to choose a platform
   public List<OpenCLPlatform> getOpenCLPlatformsFilteredByConfig()
   {
	   return getOpenCLPlatformsFilteredBy(Config.platformHint);
   }

   public List<OpenCLPlatform> getOpenCLPlatformsFilteredBy(String filter) 
   {
      if (OpenCLLoader.isOpenCLAvailable()) 
      {
    	  List<OpenCLPlatform> platformList = (getPlatforms());
    	  if(filter==null)
    	  {
        	  System.out.println("Not Filtering Platforms. Platform filter is empty!");
    	  }
    	  else
    	  {
    		  System.out.println("Filtering Platforms using: " + filter );
    		  for (Iterator<OpenCLPlatform> iterator = platformList.iterator(); iterator.hasNext(); ) 
    		  {
    			  String platformIName = iterator.next().getName();
    			  System.out.println("Checking Platform: " + platformIName );
    			  if (!platformIName.contains(filter)) 
    			  {
    				  System.out.println("Filtering Out Platform: " + platformIName);
    				  iterator.remove();
    			  }
    		  }
    	  }
    	  return (platformList);
   	  } 
      else 
      {
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
