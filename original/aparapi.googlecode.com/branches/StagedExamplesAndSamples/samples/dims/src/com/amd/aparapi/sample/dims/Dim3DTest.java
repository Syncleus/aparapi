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

package com.amd.aparapi.sample.dims;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

/**
 * An example Aparapi application which demonstrates image manipulation via convolution filter
 * 
 * Converted to use int buffer and some performance tweaks by Gary Frost
 * http://processing.org/learning/pixels/
 * 
 * @author Gary Frost
 */
public class Dim3DTest{

   public static abstract class Kernel3D extends Kernel{
      final static int GLOBAL = 0;

      final static int LOCAL_0 = 1;

      final static int LOCAL_1 = 2;

      final static int LOCAL_2 = 3;

      final static int GROUP_0 = 4;

      final static int GROUP_1 = 5;

      final static int GROUP_2 = 6;

      final static int GLOBAL_0 = 7;

      final static int GLOBAL_1 = 8;

      final static int GLOBAL_2 = 9;

      final static int SIZE = 10;

      final int data[]; // private failed here incorrectly

      public Kernel3D(Range _range, EXECUTION_MODE _mode) {
         setExecutionMode(_mode);
         data = new int[_range.getGlobalSize(0) * _range.getGlobalSize(1) * _range.getGlobalSize(2) * SIZE];

      }

      public void track(int gid) {
      };

      public void run() {
         int gid = ((getGlobalSize(0) * getGlobalSize(1) * getGlobalId(2)) + (getGlobalSize(0) * getGlobalId(1)) + getGlobalId(0)); // (w*h*z)+(h*y)+x 
         data[(gid * SIZE) + GLOBAL] = gid;

         data[(gid * SIZE) + LOCAL_0] = getLocalId(0);
         data[(gid * SIZE) + LOCAL_1] = getLocalId(1);
         data[(gid * SIZE) + LOCAL_2] = getLocalId(2);
         data[(gid * SIZE) + GROUP_0] = getGroupId(0);
         data[(gid * SIZE) + GROUP_1] = getGroupId(1);
         data[(gid * SIZE) + GROUP_2] = getGroupId(2);
         data[(gid * SIZE) + GLOBAL_0] = getGlobalId(0);
         data[(gid * SIZE) + GLOBAL_1] = getGlobalId(1);
         data[(gid * SIZE) + GLOBAL_2] = getGlobalId(2);
         track(gid);
      }

   }

   public static class JTPKernel3D extends Kernel3D{
      static Map<Integer, List<Long>> track = new HashMap<Integer, List<Long>>();

      public void track(int gid) {
         synchronized (track) {

            List listOfTimeStamps = track.get(gid);

            if (listOfTimeStamps == null) {
               listOfTimeStamps = new ArrayList<Long>();
               track.put(gid, listOfTimeStamps);
            }
            listOfTimeStamps.add(System.currentTimeMillis());
         }
      };

      JTPKernel3D(Range _range) {
         super(_range, EXECUTION_MODE.JTP);

      }
   }

   public static class GPUKernel3D extends Kernel3D{

      GPUKernel3D(Range _range) {
         super(_range, EXECUTION_MODE.GPU);

      }

   }

   public static void main(String[] _args) {
      final Range range = Range.create3D(8, 16, 32, 2, 4, 2);
      Kernel3D gpu = new GPUKernel3D(range);
      gpu.execute(range);
      Kernel3D jtp = new JTPKernel3D(range);
      jtp.execute(range);

      Map<Integer, List<Long>> track = JTPKernel3D.track;

      for (int gid = 0; gid < range.getGlobalSize(0) * range.getGlobalSize(1) * range.getGlobalSize(2); gid++) {
         if (track.get(gid) == null) {
            System.out.println(gid + "under subscribed!");
         } else {
            if (track.get(gid).size() > 1) {
               System.out.println(gid + "over subscribed!" + track.get(gid).size());
            }
         }
      }

      for (int i = 0; i < range.getGlobalSize(0) * range.getGlobalSize(1) * range.getGlobalSize(2) * Kernel3D.SIZE; i += Kernel3D.SIZE) {
         boolean same = true //
               && gpu.data[i + Kernel3D.GLOBAL] == jtp.data[i + Kernel3D.GLOBAL] //
               && gpu.data[i + Kernel3D.LOCAL_0] == jtp.data[i + Kernel3D.LOCAL_0] //
               && gpu.data[i + Kernel3D.LOCAL_1] == jtp.data[i + Kernel3D.LOCAL_1] //
               && gpu.data[i + Kernel3D.LOCAL_2] == jtp.data[i + Kernel3D.LOCAL_2] //
               && gpu.data[i + Kernel3D.GROUP_0] == jtp.data[i + Kernel3D.GROUP_0] //
               && gpu.data[i + Kernel3D.GROUP_1] == jtp.data[i + Kernel3D.GROUP_1] //
               && gpu.data[i + Kernel3D.GROUP_2] == jtp.data[i + Kernel3D.GROUP_2] //
               && gpu.data[i + Kernel3D.GLOBAL_0] == jtp.data[i + Kernel3D.GLOBAL_0] //
               && gpu.data[i + Kernel3D.GLOBAL_1] == jtp.data[i + Kernel3D.GLOBAL_1] //
               && gpu.data[i + Kernel3D.GLOBAL_2] == jtp.data[i + Kernel3D.GLOBAL_2] //
         ;
         if (!same) {
            System.out.printf("gid        = %6d %6d\n", gpu.data[i + Kernel3D.GLOBAL], jtp.data[i + Kernel3D.GLOBAL]);
            System.out.printf("localId[0] = %6d %6d\n", gpu.data[i + Kernel3D.LOCAL_0], jtp.data[i + Kernel3D.LOCAL_0]);
            System.out.printf("localId[1] = %6d %6d\n", gpu.data[i + Kernel3D.LOCAL_1], jtp.data[i + Kernel3D.LOCAL_1]);
            System.out.printf("localId[2] = %6d %6d\n", gpu.data[i + Kernel3D.LOCAL_2], jtp.data[i + Kernel3D.LOCAL_2]);
            System.out.printf("groupId[0] = %6d %6d\n", gpu.data[i + Kernel3D.GROUP_0], jtp.data[i + Kernel3D.GROUP_0]);
            System.out.printf("groupId[1] = %6d %6d\n", gpu.data[i + Kernel3D.GROUP_1], jtp.data[i + Kernel3D.GROUP_1]);
            System.out.printf("groupId[2] = %6d %6d\n", gpu.data[i + Kernel3D.GROUP_2], jtp.data[i + Kernel3D.GROUP_2]);
            System.out.printf("globalId[0]= %6d %6d\n", gpu.data[i + Kernel3D.GLOBAL_0], jtp.data[i + Kernel3D.GLOBAL_0]);
            System.out.printf("globalId[1]= %6d %6d\n", gpu.data[i + Kernel3D.GLOBAL_1], jtp.data[i + Kernel3D.GLOBAL_1]);
            System.out.printf("globalId[2]= %6d %6d\n", gpu.data[i + Kernel3D.GLOBAL_2], jtp.data[i + Kernel3D.GLOBAL_2]);
         }
      }

   }
}
