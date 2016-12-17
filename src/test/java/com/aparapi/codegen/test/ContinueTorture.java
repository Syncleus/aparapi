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

public class ContinueTorture {

    final static int START_SIZE = 128;

    public int[] values = new int[START_SIZE];

    public int[] results = new int[START_SIZE];
    int myId = 34;

    int actuallyDoIt(int a) {
        return 1;
    }

    int actuallyDoIt2(int a) {
        return -1;
    }

    public void run() {
        int idx = myId;
        while (--idx > 0) {

            if (myId == 0) {
                continue;
            }
            if (myId % 2 == 0) {
                results[myId] = actuallyDoIt(idx);
                continue;
            } else {
                results[myId] = actuallyDoIt2(idx);
                continue;
            }
        }
    }
}
//**{Throws{ClassParseException}Throws}**/
