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

import java.io.InputStream;

/**
 * Primarily used to parse various ClassFile structures. This class provides low level access to sequential bytes in a stream given stream.
 * <p>
 * Basically wraps a <code>ByteBuffer</code> and keeps track of the current offset. All requests on 
 * this <code>ByteReader</code> will be delegated to wrapped<code>ByteBuffer</code>.
 * </p>
 * @see com.amd.aparapi.internal.reader.ByteBuffer
 * 
 * @author gfrost
 *
 */
public class ByteReader{

   private final ByteBuffer byteBuffer;

   private int offset;

   /**
    * Construct form a given ByteBuffer.
    * 
    * @param _byteBuffer an existing <code>ByteBuffer</code>
    */
   public ByteReader(ByteBuffer _byteBuffer) {
      byteBuffer = _byteBuffer;
   }

   /**
    * Construct form an array of bytes.
    * 
    * @param _bytes an existing byte array
    */
   public ByteReader(byte[] _bytes) {
      this(new ByteBuffer(_bytes));
   }

   /**
    * Construct form an input stream (say a ClassFile).
    * 
    * @param _inputStream a stream of bytes
    */
   public ByteReader(InputStream _inputStream) {
      this(new ByteBuffer(_inputStream));
   }

   public int u1() {
      final int value = byteBuffer.u1(offset);
      offset += 1;
      return (value);
   }

   public int u2() {
      final int value = byteBuffer.u2(offset);
      offset += 2;
      return (value);
   }

   public int s2() {
      final int value = byteBuffer.s2(offset);
      offset += 2;
      return (value);
   }

   public int peekU2() {
      return (byteBuffer.u2(offset));
   }

   public int u4() {
      final int value = byteBuffer.u4(offset);
      offset += 4;
      return (value);
   }

   public int s4() {
      final int value = byteBuffer.s4(offset);
      offset += 4;
      return (value);
   }

   public long u8() {
      final long value = byteBuffer.u8(offset);
      offset += 8;
      return (value);
   }

   public float f4() {
      final float value = byteBuffer.f4(offset);
      offset += 4;
      return (value);
   }

   public double d8() {
      final double value = byteBuffer.d8(offset);
      offset += 8;
      return (value);
   }

   public String utf8() {
      final String utf8 = byteBuffer.utf8(offset);
      offset += byteBuffer.utf8bytes(offset);
      return (utf8);
   }

   public byte[] bytes(int _length) {
      final byte[] bytes = byteBuffer.bytes(offset, _length);
      offset += _length;
      return (bytes);
   }

   public void skip(int _length) {
      offset += _length;
   }

   public int getOffset() {
      return (offset);
   }

   public void setOffset(int _offset) {
      offset = _offset;
   }

   public boolean hasMore() {
      return (getOffset() < byteBuffer.size());
   }
}
