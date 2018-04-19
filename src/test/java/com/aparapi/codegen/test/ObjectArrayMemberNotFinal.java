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

class DummyOOANF {
    int mem;

    float floatField;

    long longField;

    boolean boolField;

    byte byteField;

    public DummyOOANF() {
        mem = 8;
    }

    public boolean isBoolField() {
        return boolField;
    }

    public boolean getBoolField() {
        return boolField;
    }

    public void setBoolField(boolean x) {
        //boolField = x & true;
        boolField = x;
    }

    public int getMem() {
        return mem;
    }

    public void setMem(int x) {
        mem = x;
    }

    public float getFloatField() {
        return floatField;
    }

    public void setFloatField(float x) {
        floatField = x;
    }

    public long getLongField() {
        return longField;
    }

    public void setLongField(long x) {
        longField = x;
    }
};

public class ObjectArrayMemberNotFinal extends Kernel {

    final int size = 64;
    int out[] = new int[2];
    int something;
    DummyOOANF dummy[] = null;

    public ObjectArrayMemberNotFinal() {
        something = -1;
        dummy = new DummyOOANF[size];

        dummy[0] = new DummyOOANF();
    }

    public int getSomething() {
        return something;
    }

    public int bar(int x) {
        return -x;
    }

    public void run() {
        int myId = getGlobalId();

        int tmp = dummy[myId].getMem();

        dummy[myId].setMem(dummy[myId].getMem() + 2);
    }
}
/**{Throws{ClassParseException}Throws}**/
