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

public class UseObject extends Kernel {
    Dummy dummy = new Dummy();

    ;
    int out[] = new int[2];
    int plainInt = -1;

    @Override
    public void run() {
        out[0] = dummy.n;
        out[1] = plainInt;
    }

    static class Dummy {
        public int n;
    }

}
/**{Throws{ClassParseException}Throws}**/
