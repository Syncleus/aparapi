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

public class SynchronizedMethods {
    int[] ints = new int[1024];

    synchronized int doIt(int a) {
        return (int) (((int) 1) - a);
    }

    int doIt2(int a) {
        return (int) (((int) 1) - a);
    }

    public void run() {
        int foo = 1;
        for (int i = 0; i < 1024; i++) {
            if (i % 2 == 0) {
                ints[i] = doIt(i);
            } else {
                synchronized (this) {
                    ints[i] = doIt2(foo);
                }
            }
        }
    }
}
/**{Throws{ClassParseException}Throws}**/
