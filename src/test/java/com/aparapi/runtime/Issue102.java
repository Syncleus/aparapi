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
package com.aparapi.runtime;

import com.aparapi.Kernel;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


final class BugDataObject {
    int value = 7;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}


public class Issue102 extends Kernel {
    static final int size = 32;

    static BugDataObject[] objects = new BugDataObject[size];
    int[] target = new int[size];

    public Issue102() {
        for (int i = 0; i < size; ++i) {
            objects[i] = new BugDataObject();
            target[i] = 99;
        }
    }

    public static void main(String[] args) {
        Issue102 b = new Issue102();
        b.test();
    }

    @Override
    public void run() {
        int id = getGlobalId();
        target[id] = objects[id].getValue();
    }

    void validate() {
        for (int i = 0; i < size; i++) {
            System.out.println(target[i] + " ... " + objects[i].getValue());
            assertTrue("target == objects", target[i] == objects[i].getValue());
        }
    }

    @Test
    public void test() {
        execute(size);
        validate();
    }
}
