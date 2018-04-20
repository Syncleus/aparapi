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

public class ObjectArrayCommonSuper extends Kernel {

    final static int size = 16;
    DummyBrother db[] = new DummyBrother[size];

    ;
    DummySister ds[] = new DummySister[size];

    ;

    public ObjectArrayCommonSuper() {
        db[0] = new DummyBrother();
        ds[0] = new DummySister();
    }

    ;

    public void run() {
        int myId = getGlobalId();
        db[myId].intField = db[myId].getIntField() + db[myId].getBrosInt();
        ds[myId].intField = ds[myId].getIntField() + ds[myId].getSisInt();
    }

    static class DummyParent {
        int intField;

        public DummyParent() {
            intField = -3;
        }

        public int getIntField() {
            return intField;
        }
    }

    final static class DummyBrother extends DummyParent {
        int brosInt;

        public int getBrosInt() {
            return brosInt;
        }
    }

    final static class DummySister extends DummyParent {
        int sisInt;

        public int getSisInt() {
            return sisInt;
        }
    }
}

/**{Throws{ClassParseException}Throws}**/
