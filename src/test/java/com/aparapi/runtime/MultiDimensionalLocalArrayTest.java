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
package com.aparapi.runtime;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

    import com.aparapi.Kernel;
    import com.aparapi.Range;

public class MultiDimensionalLocalArrayTest
{
    @Test
    public void singleDimensionTest()
    {
        final int SIZE = 16;
        final float[] RESULT = new float[] {1};
        Kernel kernel = new Kernel()
        {
            @Local
            final float[] localArray = new float[SIZE*SIZE];

            @Override
            public void run()
            {
                int row = getGlobalId(0);
                int column = getGlobalId(1);
                localArray[row + column*SIZE] = row + column;
                localBarrier();
                float value = 0;
                for (int x = 0; x < SIZE; x++)
                {
                    for (int y = 0; y < SIZE; y++)
                    {
                        value += localArray[x + y*SIZE];
                    }
                }
                RESULT[0] = value;
            }
        };
        kernel.execute(Range.create2D(SIZE, SIZE, SIZE, SIZE));
        assertEquals(3840, RESULT[0], 1E-6F);
    }

    @Ignore("Known bug, ignoring until fixed")
    @Test
    public void twoDimensionTest()
    {
        final int SIZE = 16;
        final float[] RESULT = new float[] {1};
        Kernel kernel = new Kernel()
        {
            @Local
            final float[][] localArray = new float[SIZE][SIZE];

            @Override
            public void run()
            {
                int row = getGlobalId(0);
                int column = getGlobalId(1);
                localArray[row][column] = row + column;
                localBarrier();
                float value = 0;
                for (int x = 0; x < SIZE; x++)
                {
                    for (int y = 0; y < SIZE; y++)
                    {
                        value += localArray[x][y];
                    }
                }
                RESULT[0] = value;
            }
        };
        kernel.execute(Range.create2D(SIZE, SIZE, SIZE, SIZE));
        assertEquals(3840, RESULT[0], 1E-6F);
    }
}
