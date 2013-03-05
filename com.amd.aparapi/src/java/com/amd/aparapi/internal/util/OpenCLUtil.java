package com.amd.aparapi.internal.util;

import java.util.List;

import com.amd.aparapi.internal.opencl.OpenCLPlatform;

/**
 * This utility class encapsulates the necessary actions required to query underlying OpenCL information
 */
public class OpenCLUtil{

   /**
    * Retrieve a list of available OpenCL Platforms
    * 
    * @return Available OpenCL Platforms
    */
   public static List<OpenCLPlatform> getOpenCLPlatforms() {
      final OpenCLPlatform ocp = new OpenCLPlatform();
      return ocp.getOpenCLPlatforms();
   }
}
