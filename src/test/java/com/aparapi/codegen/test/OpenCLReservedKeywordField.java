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
package com.aparapi.codegen.test;

import com.aparapi.Kernel;
import com.aparapi.Kernel.Local;

public class OpenCLReservedKeywordField extends Kernel {
    @Local final float[] local = new float[16];
    final float[] result = new float[1];

    public void run() {
        int row = getGlobalId(0);
        local[row] = row;
        localBarrier();
        result[0] = local[row];
    }
}
