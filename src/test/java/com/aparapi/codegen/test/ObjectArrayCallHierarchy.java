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

public class ObjectArrayCallHierarchy extends Kernel {

    final static int size = 16;
    int something;

    ;
    DummyOOA dummy[] = null;

    ;

    public ObjectArrayCallHierarchy() {
        something = -1;
        dummy = new DummyOOA[size];
        dummy[0] = new DummyOOA();
    }

    public int bar(int x) {
        return -x;
    }

    public void run() {
        int myId = getGlobalId();
        dummy[myId].intField = bar(2) + dummy[myId].funnyGet();
    }

    static class DummyParent {
        int intField;

        int field2;

        public DummyParent() {
            intField = -3;
            field2 = -4;
        }

        public int getIntField() {
            return intField;
        }

        public void setIntField(int x) {
            intField = x;
        }

        public void call2() {
            setIntField(intField + field2);
        }

    }

    final static class DummyOOA extends DummyParent {
        int intField;

        public void funnyCall() {
            setIntField(intField + getIntField());
            call2();
        }

        public int funnyGet() {
            funnyCall();
            setIntField(intField + getIntField());
            return intField + getIntField();
        }
    }
}

/**{Throws{ClassParseException}Throws}**/
