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
public class Dim1DTest{

   public static class Kernel1D extends Kernel{
      final static int GLOBAL = 0;

      final static int LOCAL_0 = 1;

      final static int GROUP_0 = 2;

      final static int GLOBAL_0 = 3;

      final static int SIZE = 4;

      private final int data[];

      public Kernel1D(Range _range, EXECUTION_MODE _mode) {
         setExecutionMode(_mode);
         data = new int[_range.getGlobalSize(0) * _range.getGlobalSize(1) * _range.getGlobalSize(2) * SIZE];

      }

      public void run() {
         int gid = ((getGlobalSize(0) * getGlobalSize(1) * getGlobalId(2)) + (getGlobalSize(0) * getGlobalId(1)) + getGlobalId(0)); // (w*h*z)+(h*y)+x 
         data[(gid * SIZE) + GLOBAL] = gid;

         data[(gid * SIZE) + LOCAL_0] = getLocalId(0);
         data[(gid * SIZE) + GROUP_0] = getGroupId(0);
         data[(gid * SIZE) + GLOBAL_0] = getGlobalId(0);
      }

   }

   public static void main(String[] _args) {

      final Range range = Range.create2D(16, 16, 4, 4);
      Kernel1D gpu = new Kernel1D(range, Kernel.EXECUTION_MODE.GPU);
      gpu.execute(range);
      Kernel1D jtp = new Kernel1D(range, Kernel.EXECUTION_MODE.JTP);
      jtp.execute(range);
      for (int i = 0; i < range.getGlobalSize(0) * range.getGlobalSize(1) * range.getGlobalSize(2) * Kernel1D.SIZE; i += Kernel1D.SIZE) {
         boolean same = true //
               && gpu.data[i + Kernel1D.GLOBAL] == jtp.data[i + Kernel1D.GLOBAL] //
               && gpu.data[i + Kernel1D.LOCAL_0] == jtp.data[i + Kernel1D.LOCAL_0] //
               && gpu.data[i + Kernel1D.GROUP_0] == jtp.data[i + Kernel1D.GROUP_0] //
               && gpu.data[i + Kernel1D.GLOBAL_0] == jtp.data[i + Kernel1D.GLOBAL_0] //
         ;
         if (!same) {
            System.out.printf("gid        = %6d %6d\n", gpu.data[i + Kernel1D.GLOBAL], jtp.data[i + Kernel1D.GLOBAL]);
            System.out.printf("localId[0] = %6d %6d\n", gpu.data[i + Kernel1D.LOCAL_0], jtp.data[i + Kernel1D.LOCAL_0]);
            System.out.printf("groupId[0] = %6d %6d\n", gpu.data[i + Kernel1D.GROUP_0], jtp.data[i + Kernel1D.GROUP_0]);
            System.out.printf("globalId[0]= %6d %6d\n", gpu.data[i + Kernel1D.GLOBAL_0], jtp.data[i + Kernel1D.GLOBAL_0]);
         }
      }

   }
}
