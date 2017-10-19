/**
 * Copyright (c) 2016 - 2017 Syncleus, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aparapi.codegen.test;

import com.aparapi.Kernel;
import org.apache.log4j.Logger;

public class FusedMultiplyAdd extends Kernel {
    private static final Logger LOGGER = Logger.getLogger(FusedMultiplyAdd.class);

    public void run() {
        double d1 = 123.0, d2 = 0.456, d3 = 789.0;
        float f1 = 123.0f, f2 = 0.456f, f3 = 789.0f;

        @SuppressWarnings("unused") boolean pass = true;
        if ((fma(d1, d2, d3) != 845.088) || (fma(f1, f2, f3) != 845.088f)) {
            pass = false;
        }
    }
}
