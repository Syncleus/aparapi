/**
 * Copyright (c) 2016 - 2017 Syncleus, Inc.
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
package com.aparapi.internal.opencl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.jni.OpenCLJNI;

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

   public static List<OpenCLPlatform> getUncachedOpenCLPlatforms(){
       platforms = null;
       platforms = new OpenCLPlatform().getOpenCLPlatforms();
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
