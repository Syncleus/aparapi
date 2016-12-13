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
package com.aparapi;

public class ProfileInfo{

   // !!! oren change -> we need access to type at upper levels
   //private 
public static enum TYPE {
      R,
      X,
      W
   }; // 0 = write, 1 = execute, 2 = read

   private final TYPE type;

   private final String label;

   private final long start;

   private final long end;

   private final long submit;

   private final long queued;

   /**
    * Minimal constructor
    * 
    * @param _label
    * @param _type
    * @param _start
    * @param _end
    * @param _submit
    * @param _queued
    */
   public ProfileInfo(String _label, int _type, long _start, long _end, long _submit, long _queued) {
      type = TYPE.values()[_type];
      label = _label == null ? "exec()" : _label;
      start = _start;
      end = _end;
      submit = _submit;
      queued = _queued;
   }

   public long getStart() {
      return start;
   }

   public long getEnd() {
      return end;
   }

   public long getSubmit() {
      return submit;
   }

   public long getQueued() {
      return queued;
   }

   public String getLabel() {
      return (label);
   }

   public TYPE getType() {
      return (type);
   }

   @Override public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append("ProfileInfo[");
      sb.append(type);
      sb.append(" '");
      sb.append(label);
      sb.append("' start=");
      sb.append(start);
      sb.append(", end=");
      sb.append(end);
      sb.append(", submit=");
      sb.append(submit);
      sb.append(", queued=");
      sb.append(queued);
      sb.append(", duration=");
      sb.append((end - start));
      sb.append("]");

      return sb.toString();
   }
}
