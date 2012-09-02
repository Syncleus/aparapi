package com.amd.aparapi.test.runtime;
import com.amd.aparapi.Kernel;

abstract class ArrayAccess
{
    protected ArrayAccess(int offset, int length)
    {
        this.offset = offset;
        this.length = length;
    }

    public abstract int[] getIntData();

    public int getOffset()
    {
        return this.offset;
    }

    public int getLength()
    {
        return this.length;
    }

    private int offset;
    private int length;
}

class IntMemoryArrayAccess
    extends ArrayAccess
{
    public IntMemoryArrayAccess(int[] data, int offset, int length)
    {
        super(offset, length);
        this.data = data;
    }

    public int[] getIntData()
    {
        return this.data;
    }

    private int[] data;
}

public class Issue68
{
    // Runnable for calculating the column transforms in parallel
    private class ColumnTableFNTRunnable
        extends Kernel
    {
        public ColumnTableFNTRunnable(int length, boolean isInverse, ArrayAccess arrayAccess, int[] wTable, int[] permutationTable, int modulus)
        {
            this.stride = arrayAccess.getLength() / length;
            this.length = length;               // Transform length
            this.isInverse = isInverse;
            this.data = arrayAccess.getIntData();
            this.offset = arrayAccess.getOffset();
            this.wTable = wTable;
            this.permutationTable = permutationTable;
            this.permutationTableLength = (permutationTable == null ? 0 : permutationTable.length);
            setModulus(modulus);
        }

        public void run()
        {
            if (this.isInverse)
            {
                inverseColumnTableFNT();
            }
            else
            {
                columnTableFNT();
            }
        }

        private void columnTableFNT()
        {
            int nn, istep, mmax, r;

            int offset = this.offset + getGlobalId();
            nn     = length;

            if (nn < 2)
            {
                return;
            }

            r = 1;
            mmax = nn >> 1;
            while (mmax > 0)
            {
                istep = mmax << 1;

                // Optimize first step when wr = 1

                for (int i = offset; i < offset + nn * stride; i += istep * stride)
                {
                    int j = i + mmax * stride;
                    int a = data[i];
                    int b = data[j];
                    data[i] = modAdd(a, b);
                    data[j] = modSubtract(a, b);
                }

                int t = r;

                for (int m = 1; m < mmax; m++)
                {
                    for (int i = offset + m * stride; i < offset + nn * stride; i += istep * stride)
                    {
                        int j = i + mmax * stride;
                        int a = data[i];
                        int b = data[j];
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

        private void inverseColumnTableFNT()
        {
            int nn, istep, mmax, r;

            int offset = this.offset + getGlobalId();
            nn     = length;

            if (nn < 2)
            {
                return;
            }

           // if (permutationTable != null)
           // {
                columnScramble(offset);
          // }

            r = nn;
            mmax = 1;
            istep=0;
            while (nn > mmax)
            {
                istep = mmax << 1;
                r >>= 1;

                // Optimize first step when w = 1

                for (int i = offset; i < offset + nn * stride; i += istep * stride)
                {
                    int j = i + mmax * stride;
                    int wTemp = data[j];
                    data[j] = modSubtract(data[i], wTemp);
                    data[i] = modAdd(data[i], wTemp);
                }

                int t = r;

                for (int m = 1; m < mmax; m++)
                {
                    for (int i = offset + m * stride; i < offset + nn * stride; i += istep * stride)
                    {
                        int j = i + mmax * stride;
                        int wTemp = modMultiply(wTable[t], data[j]);
                        data[j] = modSubtract(data[i], wTemp);
                        data[i] = modAdd(data[i], wTemp);
                    }
                    t += r;
                }
                mmax = istep;
            }
        }

        private void columnScramble(int offset)
        {
            for (int k = 0; k < this.permutationTableLength; k += 2)
            {
                int i = offset + permutationTable[k] * stride,
                    j = offset + permutationTable[k + 1] * stride;
                int tmp = data[i];
                data[i] = data[j];
                data[j] = tmp;
            }
        }

        public final int modMultiply(int a, int b)
        {
            int r1 = a * b - (int) (this.inverseModulus * (float) a * (float) b) * this.modulus,
                r2 = r1 - this.modulus;

            return (r2 < 0 ? r1 : r2);
        }

        private int modAdd(int a, int b)
        {
            int r1 = a + b,
                r2 = r1 - this.modulus;

            return (r2 < 0 ? r1 : r2);
        }

        private int modSubtract(int a, int b)
        {
            int r1 = a - b,
                r2 = r1 + this.modulus;

            return (r1 < 0 ? r2 : r1);
        }

        private void setModulus(int modulus)
        {
            this.inverseModulus = 1.0f / (modulus + 0.5f);    // Round down
            this.modulus = modulus;
        }

        private int stride;
        private int length;
        private boolean isInverse;
        private int[] data;
        private int offset;
        @Constant private int[] wTable;
        @Constant private int[] permutationTable;
        private int permutationTableLength;

        private int modulus;
        private float inverseModulus;
    }

    public static void main(String[] args)
    {
        final int SQRT_LENGTH = 1024;
        final int LENGTH = SQRT_LENGTH * SQRT_LENGTH;
        ArrayAccess arrayAccess = new IntMemoryArrayAccess(new int[LENGTH], 0, LENGTH);
        new Issue68().transformColumns(SQRT_LENGTH, SQRT_LENGTH, false, arrayAccess, new int[SQRT_LENGTH], null);
    }

    private void transformColumns(final int length, final int count, final boolean isInverse, final ArrayAccess arrayAccess, final int[] wTable, final int[] permutationTable)
    {
        Kernel kernel = new ColumnTableFNTRunnable(length, isInverse, arrayAccess, wTable, permutationTable, getModulus());
        kernel.execute(count);
    }

    private int getModulus()
    {
        return 2113929217;
    }
}

