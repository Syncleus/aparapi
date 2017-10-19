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

public class ObjectWithinObject extends Kernel {
    private static final Logger LOGGER = Logger.getLogger(ObjectWithinObject.class);

    final int size = 8;

    ;
    DummyOOA dummy[] = new DummyOOA[size];

    public void run() {
        int myId = getGlobalId();
        dummy[myId].mem = dummy[myId].next.mem + 4;
    }

    final static class DummyOOA {
        private static final Logger LOGGER = Logger.getLogger(DummyOOA.class);
        int mem;

        float floatField;

        DummyOOA next;

    }
}

/**{Throws{ClassParseException}Throws}**/
