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

public class CallObject extends Kernel {
    private static final Logger LOGGER = Logger.getLogger(CallObject.class);
    Dummy dummy = new Dummy();

    ;
    int out[] = new int[2];

    public void run() {
        out[0] = dummy.foo();
    }

    static class Dummy {
        private static final Logger LOGGER = Logger.getLogger(Dummy.class);

        public int foo() {
            return 42;
        }
    }
}
/**{Throws{ClassParseException}Throws}**/
