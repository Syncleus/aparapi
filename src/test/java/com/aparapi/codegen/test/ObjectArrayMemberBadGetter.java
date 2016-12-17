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
package com.aparapi.codegen.test;

import com.aparapi.Kernel;

public class ObjectArrayMemberBadGetter extends Kernel {

    final int size = 64;

    ;
    DummyOOA dummy[] = null;

    public ObjectArrayMemberBadGetter() {
        dummy = new DummyOOA[size];

        dummy[0] = new DummyOOA();
    }

    public void run() {
        int myId = getGlobalId();
        dummy[myId].setFloatField(dummy[myId].getFloatField() + (float) 2.0);
    }

    final class DummyOOA {
        int mem;

        float floatField;

        float theOtherFloatField;

        public float getFloatField() {
            //return floatField;
            return theOtherFloatField;
        }

        public void setFloatField(float x) {
            floatField = x;
        }
    }
}

/**{Throws{ClassParseException}Throws}**/
