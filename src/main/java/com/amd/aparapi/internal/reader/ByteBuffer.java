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
package com.amd.aparapi.internal.reader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Used to parse ClassFile structure. <br/>
 * 
 * Provides low level access to sequential bytes in a stream given a specific offset.
 * 
 * Does not keep track of accesses.  For this you will need a <code>ByteReader</code>
 * 
 * @see com.amd.aparapi.internal.reader.ByteReader
 * 
 * @author gfrost
 *
 */
public class ByteBuffer{

   private byte[] bytes;

   /**
    * Construct from an <code>InputStream</code>
    * 
    * @param _inputStream
    */
   ByteBuffer(InputStream _inputStream) {
      final ByteArrayOutputStream baos = new ByteArrayOutputStream();
      bytes = new byte[4096];
      int bytesRead = 0;

      try {
         while ((bytesRead = _inputStream.read(bytes)) > 0) {
            baos.write(bytes, 0, bytesRead);
         }

         bytes = baos.toByteArray();
      } catch (final IOException e) {
         bytes = new byte[0];
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   int u2(int _offset) {
      return ((u1(_offset) << 8) | u1(_offset + 1));
   }

   int s2(int _offset) {
      int s2 = u2(_offset);

      if (s2 > 0x7fff) {
         s2 = -(0x10000 - s2);
      }
      return (s2);
   }

   int u4(int _offset) {
      return (((u2(_offset) & 0xffff) << 16) | u2(_offset + 2));
   }

   int s4(int _offset) {
      final int s4 = u4(_offset);
      return (s4);
   }

   ByteBuffer(byte[] _bytes) {
      bytes = _bytes;
   }

   int u1(int _offset) {
      return ((bytes[_offset] & 0xff));
   }

   int size() {
      return (bytes.length);
   }

   double d8(int _offset) {
      return (Double.longBitsToDouble(u8(_offset)));
   }

   float f4(int _offset) {
      return (Float.intBitsToFloat(u4(_offset)));

   }

   long u8(int _offset) {
      return ((u4(_offset) & 0xffffffffL) << 32) | (u4(_offset + 4) & 0xffffffffL);
   }

   int utf8bytes(int _offset) {
      return (2 + u2(_offset));
   }

   byte[] bytes(int _offset, int _length) {
      final byte[] returnBytes = new byte[_length];
      System.arraycopy(bytes, _offset, returnBytes, 0, _length);
      return (returnBytes);
   }

   String utf8(int _offset) {
      final int utflen = u2(_offset);
      _offset += 2;
      final byte[] bytearr = new byte[utflen];
      final char[] chararr = new char[utflen];

      int c, char2, char3;
      int count = 0;
      int chararr_count = 0;

      for (int i = 0; i < utflen; i++) {
         bytearr[i] = b(_offset + i);
      }
      _offset += utflen;

      while (count < utflen) {
         c = bytearr[count] & 0xff;
         if (c > 127) {
            break;
         }
         count++;
         chararr[chararr_count++] = (char) c;
      }

      while (count < utflen) {
         c = bytearr[count] & 0xff;
         switch (c >> 4) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
               /* 0xxxxxxx*/
               count++;
               chararr[chararr_count++] = (char) c;
               break;
            case 12:
            case 13:
               /* 110x xxxx   10xx xxxx*/
               count += 2;
               if (count > utflen) {
                  System.out.println("malformed input: partial character at end");
                  return (null);
               }
               char2 = bytearr[count - 1];
               if ((char2 & 0xC0) != 0x80) {
                  System.out.println("malformed input around byte " + count);
                  return (null);
               }
               chararr[chararr_count++] = (char) (((c & 0x1F) << 6) | (char2 & 0x3F));
               break;
            case 14:
               /* 1110 xxxx  10xx xxxx  10xx xxxx */
               count += 3;
               if (count > utflen) {
                  System.out.println("malformed input: partial character at end");
                  return (null);
               }
               char2 = bytearr[count - 2];
               char3 = bytearr[count - 1];
               if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80)) {
                  System.out.println("malformed input around byte " + (count - 1));
                  return (null);
               }
               chararr[chararr_count++] = (char) (((c & 0x0F) << 12) | ((char2 & 0x3F) << 6) | ((char3 & 0x3F) << 0));
               break;
            default:
               /* 10xx xxxx,  1111 xxxx */
               System.out.println("malformed input around byte " + count);
               return (null);
         }
      }
      // The number of chars produced may be less than utflen
      final String returnString = new String(chararr, 0, chararr_count);
      // System.out.println("returnString.length="+returnString.length()+" byte[]="+bytearr.length);
      return (returnString);
   }

   byte b(int _offset) {
      return bytes[_offset];
   }

}
