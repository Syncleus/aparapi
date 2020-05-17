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
package com.amd.aparapi;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Diff{

   static int[] hash(String[] lines) {
      int[] val = new int[lines.length];
      for (int i = 0; i < lines.length; i++) {
         val[i] = lines[i].hashCode();
      }
      return (val);
   }

   static void costDiag(List<Point>[][] flags, int x, int y) {
      if (x == 0 || y == 0 || flags[x - 1][y - 1] == null) {
         if (x < (flags.length - 2) && y < (flags[0].length - 2)) {
            flags[x][y] = new ArrayList<Point>();
            flags[x][y].add(new Point(x, y));
         }
      } else {
         flags[x - 1][y - 1].add(new Point(x, y));
         flags[x][y] = flags[x - 1][y - 1];
      }
   }

   static void cleanIslands(List<Point>[][] flags, int x, int y) {
      flags[x][y] = null;
      if (x > 0 && y > 0 && flags[x - 1][y - 1] != null && flags[x - 1][y - 1].size() == 1) {
         flags[x - 1][y - 1] = null;
      }
   }

   public static class DiffResult{

      public static enum TYPE {
         SAME,
         LEFT,
         RIGHT
      };

      public static class Block{
         int lhsFrom;

         int rhsFrom;

         int lhsTo;

         int rhsTo;

         TYPE type;

         public Block(TYPE _type, int _lhsFrom, int _rhsFrom) {
            lhsFrom = lhsTo = _lhsFrom;
            rhsFrom = rhsTo = _rhsFrom;
            type = _type;
         }

         public void extend(int _lhsTo, int _rhsTo) {
            lhsTo = _lhsTo;
            rhsTo = _rhsTo;
         }

         public String toString(String[] _lhs, String[] _rhs) {
            StringBuilder sb = new StringBuilder();
            sb.append(type).append("\n");

            switch (type) {
               case SAME:
                  for (int i = lhsFrom; i <= lhsTo; i++) {
                     sb.append("  ==" + _lhs[i]).append("\n");
                  }
                  break;
               case LEFT:
                  for (int i = lhsFrom; i <= lhsTo; i++) {
                     sb.append("  <" + _lhs[i]).append("\n");
                  }
                  break;
               case RIGHT:
                  for (int i = rhsFrom; i <= rhsTo; i++) {
                     sb.append("  >" + _rhs[i]).append("\n");
                  }
                  break;
            }
            return (sb.toString());
         }

      }

      List<Block> blocks = new ArrayList<Block>();

      private String[] rhs;

      private String[] lhs;

      public void add(TYPE _type, int lhs, int rhs) {
         if (false) {
            if (blocks.size() > 0) {
               Block lastBlock = blocks.get(blocks.size() - 1);
               if (lastBlock.type == _type) {
                  lastBlock.extend(lhs, rhs);
               } else {
                  blocks.add(new Block(_type, lhs, rhs));
               }
            } else {
               blocks.add(new Block(_type, lhs, rhs));
            }
         }
         blocks.add(new Block(_type, lhs, rhs));
      }

      DiffResult(String[] _lhs, String[] _rhs) {
         lhs = _lhs;
         rhs = _rhs;
      }

      public String[] getLhs() {
         return lhs;
      }

      public String[] getRhs() {
         return rhs;
      }

      public String toString() {
         StringBuilder sb = new StringBuilder();
         for (Block block : blocks) {
            sb.append(block.toString(lhs, rhs)).append("\n");
         }
         return (sb.toString());
      }
   }

   @SuppressWarnings("unchecked") public static DiffResult diff(String[] lhsString, String[] rhsString) {
      DiffResult diffResult = new DiffResult(lhsString, rhsString);
      int[] lhsHash = hash(lhsString);
      int[] rhsHash = hash(rhsString);
      int lhsLength = lhsHash.length; // number of lines of first file
      int rhsLength = rhsHash.length; // number of lines of second file

      // opt[i][j] = length of LCS of x[i..M] and y[j..N]
      int[][] opt = new int[lhsLength + 1][rhsLength + 1];
      List<Point>[][] flags = new ArrayList[lhsLength + 1][rhsLength + 1];

      // compute length of LCS and all subproblems via dynamic programming
      for (int i = 0; i < lhsLength; i++) {
         for (int j = 0; j < rhsLength; j++) {
            if (lhsHash[i] == rhsHash[j]) {
               // We are the same so continue the diagonal is intact
               if (i == 0 || j == 0) {
                  opt[i][j] = 0;
               } else {
                  opt[i][j] = opt[i - 1][j - 1] + 1;
               }
               costDiag(flags, i, j);
            } else {
               cleanIslands(flags, i, j);
               if (i == 0 || j == 0) {
                  opt[i][j] = 0;
               } else {
                  opt[i][j] = Math.max(opt[i - 1][j], opt[i][j - 1]);
               }
            }
         }
      }

      // recover LCS itself and print out non-matching lines to standard output
      int i = 0, j = 0;
      while (i < lhsLength && j < rhsLength) {
         // if the diagonal is in tact walk it
         if (lhsHash[i] == rhsHash[j]) {
            diffResult.add(DiffResult.TYPE.SAME, i, j);
            i++;
            j++;
         }
         // otherwise walk along the xx or y axis which is the longer
         // this is not always the best approach. 
         // we need to find the shortest path between {i,j} and the {i+ii,j+jj} which 
         // connects us to the next diagonal run
         else if (opt[i + 1][j] >= opt[i][j + 1]) {
            diffResult.add(DiffResult.TYPE.LEFT, i, j);
            System.out.println("lhs:" + i + "< " + lhsString[i++]);
         } else {
            diffResult.add(DiffResult.TYPE.RIGHT, i, j);
            System.out.println("rhs:" + j + "> " + rhsString[j++]);
         }
      }

      // dump out one remainder of one string if the other is exhausted
      while (i < lhsLength || j < rhsLength) {
         if (i == lhsLength) {
            diffResult.add(DiffResult.TYPE.RIGHT, i, j);
            System.out.println("lhs:" + i + "> " + rhsString[j++]);
         } else if (j == rhsLength) {
            diffResult.add(DiffResult.TYPE.LEFT, i, j);
            System.out.println("rhs:" + j + "< " + lhsString[i++]);
         }
      }
      return (diffResult);
   }

   public static boolean same(String left, String right) {
      StringBuilder leftAll = new StringBuilder();

      for (String s : left.replace("\n", "").split("  *")) {
         leftAll.append(s);
      }

      StringBuilder rightAll = new StringBuilder();
      for (String s : right.replace("\n", " ").split("  *")) {
         rightAll.append(s);
      }
      boolean same = leftAll.toString().equals(rightAll.toString());
      if (!same) {
         String[] lhs = left.split("\n");
         for (int i = 0; i < lhs.length; i++) {
            lhs[i] = lhs[i].trim();
         }
         String[] rhs = right.split("\n");
         for (int i = 0; i < rhs.length; i++) {
            rhs[i] = rhs[i].trim();
         }
         diff(lhs, rhs);
      }
      return (same);
   }

}
