/*
Copyright (c) 2010-2011, Advanced Micro Devices, Inc.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following
disclaimer. 

Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
disclaimer in the documentation and/or other materials provided with the distribution. 

Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products
derived from this software without specific prior written permission. 

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

If you use the software (in whole or in part), you shall adhere to all applicable U.S., European, and other export
laws, including but not limited to the U.S. Export Administration Regulations ("EAR"), (15 C.F.R. Sections 730 through
774), and E.U. Council Regulation (EC) No 1334/2000 of 22 June 2000.  Further, pursuant to Section 740.6 of the EAR,
you hereby certify that, except pursuant to a license granted by the United States Department of Commerce Bureau of 
Industry and Security or as otherwise permitted pursuant to a License Exception under the U.S. Export Administration 
Regulations ("EAR"), you will not (1) export, re-export or release to a national of a country in Country Groups D:1,
E:1 or E:2 any restricted technology, software, or source code you receive hereunder, or (2) export to Country Groups
D:1, E:1 or E:2 the direct product of such technology or software, if such foreign produced direct product is subject
to national security controls as identified on the Commerce Control List (currently found in Supplement 1 to Part 774
of EAR).  For the most current Country Group listings, or for additional information about the EAR or your obligations
under those regulations, please refer to the U.S. Bureau of Industry and Security's website at http://www.bis.doc.gov/. 

*/

package com.amd.aparapi.sample.info;

import java.util.List;

import com.amd.aparapi.device.Device;
import com.amd.aparapi.device.OpenCLDevice;
import com.amd.aparapi.internal.opencl.OpenCLPlatform;

public class Main{
   public static void main(String[] _args) {
      System.out.println("com.amd.aparapi.sample.info.Main");
      List<OpenCLPlatform> platforms = (new OpenCLPlatform()).getOpenCLPlatforms();
      System.out.println("Machine contains " + platforms.size() + " OpenCL platforms");
      int platformc = 0;
      for (OpenCLPlatform platform : platforms) {
         System.out.println("Platform " + platformc + "{");
         System.out.println("   Name    : \"" + platform.getName() + "\"");
         System.out.println("   Vendor  : \"" + platform.getVendor() + "\"");
         System.out.println("   Version : \"" + platform.getVersion() + "\"");
         List<OpenCLDevice> devices = platform.getOpenCLDevices();
         System.out.println("   Platform contains " + devices.size() + " OpenCL devices");
         int devicec = 0;
         for (OpenCLDevice device : devices) {
            System.out.println("   Device " + devicec + "{");
            System.out.println("       Type                  : " + device.getType());
            System.out.println("       GlobalMemSize         : " + device.getGlobalMemSize());
            System.out.println("       LocalMemSize          : " + device.getLocalMemSize());
            System.out.println("       MaxComputeUnits       : " + device.getMaxComputeUnits());
            System.out.println("       MaxWorkGroupSizes     : " + device.getMaxWorkGroupSize());
            System.out.println("       MaxWorkItemDimensions : " + device.getMaxWorkItemDimensions());
            System.out.println("   }");
            devicec++;
         }
         System.out.println("}");
         platformc++;
      }

      Device bestDevice = OpenCLDevice.best();
      if (bestDevice == null) {
         System.out.println("OpenCLDevice.best() returned null!");
      } else {
         System.out.println("OpenCLDevice.best() returned { ");
         System.out.println("   Type                  : " + bestDevice.getType());
         System.out.println("   GlobalMemSize         : " + ((OpenCLDevice) bestDevice).getGlobalMemSize());
         System.out.println("   LocalMemSize          : " + ((OpenCLDevice) bestDevice).getLocalMemSize());
         System.out.println("   MaxComputeUnits       : " + ((OpenCLDevice) bestDevice).getMaxComputeUnits());
         System.out.println("   MaxWorkGroupSizes     : " + ((OpenCLDevice) bestDevice).getMaxWorkGroupSize());
         System.out.println("   MaxWorkItemDimensions : " + ((OpenCLDevice) bestDevice).getMaxWorkItemDimensions());
         System.out.println("}");
      }

      Device firstCPU = OpenCLDevice.firstCPU();
      if (firstCPU == null) {
         System.out.println("OpenCLDevice.firstCPU() returned null!");
      } else {
         System.out.println("OpenCLDevice.firstCPU() returned { ");
         System.out.println("   Type                  : " + firstCPU.getType());
         System.out.println("   GlobalMemSize         : " + ((OpenCLDevice) firstCPU).getGlobalMemSize());
         System.out.println("   LocalMemSize          : " + ((OpenCLDevice) firstCPU).getLocalMemSize());
         System.out.println("   MaxComputeUnits       : " + ((OpenCLDevice) firstCPU).getMaxComputeUnits());
         System.out.println("   MaxWorkGroupSizes     : " + ((OpenCLDevice) firstCPU).getMaxWorkGroupSize());
         System.out.println("   MaxWorkItemDimensions : " + ((OpenCLDevice) firstCPU).getMaxWorkItemDimensions());
         System.out.println("}");
      }

      Device firstGPU = OpenCLDevice.firstGPU();
      if (firstGPU == null) {
         System.out.println("OpenCLDevice.firstGPU() returned null!");
      } else {
         System.out.println("OpenCLDevice.firstGPU() returned { ");
         System.out.println("   Type                  : " + firstGPU.getType());
         System.out.println("   GlobalMemSize         : " + ((OpenCLDevice) firstGPU).getGlobalMemSize());
         System.out.println("   LocalMemSize          : " + ((OpenCLDevice) firstGPU).getLocalMemSize());
         System.out.println("   MaxComputeUnits       : " + ((OpenCLDevice) firstGPU).getMaxComputeUnits());
         System.out.println("   MaxWorkGroupSizes     : " + ((OpenCLDevice) firstGPU).getMaxWorkGroupSize());
         System.out.println("   MaxWorkItemDimensions : " + ((OpenCLDevice) firstGPU).getMaxWorkItemDimensions());
         System.out.println("}");
      }

   }

}
