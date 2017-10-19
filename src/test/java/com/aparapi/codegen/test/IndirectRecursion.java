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

public class IndirectRecursion extends Kernel {
    private static final Logger LOGGER = Logger.getLogger(IndirectRecursion.class);

    int intout[] = new int[1];

    public void run() {
        intout[0] = foo(10);
        @SuppressWarnings("unused") boolean pass = false;
    }

    int foo(int n) {
        if (n > 0) {
            return bar(n);
        }
        return -1;
    }

    int bar(int n) {
        return foo(--n);
    }

}
/**{Throws{ClassParseException}Throws}**/
