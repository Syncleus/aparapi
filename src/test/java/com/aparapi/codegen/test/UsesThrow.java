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

public class UsesThrow {
    int[] ints = new int[1024];

    int doIt(int a) throws Exception {
        if (a < 0) {
            throw new Exception("Zoinks!");
        }
        return (int) (((int) 1) - a);
    }

    public void run() {
        @SuppressWarnings("unused") int foo = 1;
        try {
            for (int i = 0; i < 1024; i++) {
                if (i % 2 == 0) {
                    ints[i] = doIt(i);
                }
            }
        } catch (Exception e) {
            // nothing
        }
    }
}
/**{Throws{ClassParseException}Throws}**/
