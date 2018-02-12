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
package com.aparapi.runtime;

import com.aparapi.Kernel;
import org.junit.Ignore;
import org.junit.Test;

public class Issue68Test {
    @Ignore("Ported over but not working yet")
    @Test
    public void test() {
        final int SQRT_LENGTH = 1024;
        final int LENGTH = SQRT_LENGTH * SQRT_LENGTH;
        final ArrayAccess arrayAccess = new IntMemoryArrayAccess(new int[LENGTH], 0, LENGTH);
        new Issue68Test().transformColumns(SQRT_LENGTH, SQRT_LENGTH, false, arrayAccess, new int[SQRT_LENGTH], null);
    }

    private void transformColumns(final int length, final int count, final boolean isInverse, final ArrayAccess arrayAccess,
                                  final int[] wTable, final int[] permutationTable) {
        final Kernel kernel = new ColumnTableFNTRunnable(length, isInverse, arrayAccess, wTable, permutationTable, getModulus());
        kernel.execute(count);
    }

    private int getModulus() {
        return 2113929217;
    }

    // Runnable for calculating the column transforms in parallel
    private static class ColumnTableFNTRunnable extends Kernel {
        private final int stride;
        private final int length;
        private final boolean isInverse;
        private final int[] data;
        private final int offset;
        @Constant
        private final int[] wTable;
        @Constant
        private final int[] permutationTable;
        private final int permutationTableLength;
        private int modulus;
        private float inverseModulus;

        public ColumnTableFNTRunnable(int length, boolean isInverse, ArrayAccess arrayAccess, int[] wTable, int[] permutationTable,
                                      int modulus) {
            stride = arrayAccess.getLength() / length;
            this.length = length; // Transform length
            this.isInverse = isInverse;
            data = arrayAccess.getIntData();
            offset = arrayAccess.getOffset();
            this.wTable = wTable;
            this.permutationTable = permutationTable;
            permutationTableLength = (permutationTable == null ? 0 : permutationTable.length);
            setModulus(modulus);
        }

        @Override
        public void run() {
            if (isInverse) {
                inverseColumnTableFNT();
            } else {
                columnTableFNT();
            }
        }

        private void columnTableFNT() {
            int nn, istep, mmax, r;

            final int offset = this.offset + getGlobalId();
            nn = length;

            if (nn < 2) {
                return;
            }

            r = 1;
            mmax = nn >> 1;
            while (mmax > 0) {
                istep = mmax << 1;

                // Optimize first step when wr = 1

                for (int i = offset; i < (offset + (nn * stride)); i += istep * stride) {
                    final int j = i + (mmax * stride);
                    final int a = data[i];
                    final int b = data[j];
                    data[i] = modAdd(a, b);
                    data[j] = modSubtract(a, b);
                }

                int t = r;

                for (int m = 1; m < mmax; m++) {
                    for (int i = offset + (m * stride); i < (offset + (nn * stride)); i += istep * stride) {
                        final int j = i + (mmax * stride);
                        final int a = data[i];
                        final int b = data[j];
                        data[i] = modAdd(a, b);
                        data[j] = modMultiply(wTable[t], modSubtract(a, b));
                    }
                    t += r;
                }
                r <<= 1;
                mmax >>= 1;
            }

            //if (permutationTable != null)
            // {
            columnScramble(offset);
            //  }
        }

        private void inverseColumnTableFNT() {
            int nn, istep, mmax, r;

            final int offset = this.offset + getGlobalId();
            nn = length;

            if (nn < 2) {
                return;
            }

            // if (permutationTable != null)
            // {
            columnScramble(offset);
            // }

            r = nn;
            mmax = 1;
            istep = 0;
            while (nn > mmax) {
                istep = mmax << 1;
                r >>= 1;

                // Optimize first step when w = 1

                for (int i = offset; i < (offset + (nn * stride)); i += istep * stride) {
                    final int j = i + (mmax * stride);
                    final int wTemp = data[j];
                    data[j] = modSubtract(data[i], wTemp);
                    data[i] = modAdd(data[i], wTemp);
                }

                int t = r;

                for (int m = 1; m < mmax; m++) {
                    for (int i = offset + (m * stride); i < (offset + (nn * stride)); i += istep * stride) {
                        final int j = i + (mmax * stride);
                        final int wTemp = modMultiply(wTable[t], data[j]);
                        data[j] = modSubtract(data[i], wTemp);
                        data[i] = modAdd(data[i], wTemp);
                    }
                    t += r;
                }
                mmax = istep;
            }
        }

        private void columnScramble(int offset) {
            for (int k = 0; k < permutationTableLength; k += 2) {
                final int i = offset + (permutationTable[k] * stride), j = offset + (permutationTable[k + 1] * stride);
                final int tmp = data[i];
                data[i] = data[j];
                data[j] = tmp;
            }
        }

        public final int modMultiply(int a, int b) {
            final int r1 = (a * b) - ((int) (inverseModulus * a * b) * modulus), r2 = r1 - modulus;

            return (r2 < 0 ? r1 : r2);
        }

        private int modAdd(int a, int b) {
            final int r1 = a + b, r2 = r1 - modulus;

            return (r2 < 0 ? r1 : r2);
        }

        private int modSubtract(int a, int b) {
            final int r1 = a - b, r2 = r1 + modulus;

            return (r1 < 0 ? r2 : r1);
        }

        private void setModulus(int modulus) {
            inverseModulus = 1.0f / (modulus + 0.5f); // Round down
            this.modulus = modulus;
        }
    }

    static class IntMemoryArrayAccess extends ArrayAccess {
        private final int[] data;

        public IntMemoryArrayAccess(int[] data, int offset, int length) {
            super(offset, length);
            this.data = data;
        }

        @Override
        public int[] getIntData() {
            return data;
        }
    }

    abstract static class ArrayAccess {
        private final int offset;
        private final int length;

        protected ArrayAccess(int offset, int length) {
            this.offset = offset;
            this.length = length;
        }

        public abstract int[] getIntData();

        public int getOffset() {
            return offset;
        }

        public int getLength() {
            return length;
        }
    }
}
