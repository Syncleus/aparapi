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
package com.aparapi.internal.util;

import java.util.List;

import com.aparapi.internal.opencl.OpenCLPlatform;

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
