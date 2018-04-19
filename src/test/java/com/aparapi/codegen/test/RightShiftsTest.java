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

import org.junit.Test;

public class RightShiftsTest extends com.aparapi.codegen.CodeGenJUnitBase {

    private static final String[] expectedOpenCL = {
        "typedef struct This_s{\n"
        + " __global int *iout;\n"
        + " int i1;\n"
        + " int i2;\n"
        + " int passid;\n"
        + " }This;\n"
        + " int get_pass_id(This *this){\n"
        + " return this->passid;\n"
        + " }\n"
        + "\n"
        + " __kernel void run(\n"
        + " __global int *iout,\n"
        + " int i1,\n"
        + " int i2,\n"
        + " int passid\n"
        + " ){\n"
        + " This thisStruct;\n"
        + " This* this=&thisStruct;\n"
        + " this->iout = iout;\n"
        + " this->i1 = i1;\n"
        + " this->i2 = i2;\n"
        + " this->passid = passid;\n"
        + " {\n"
        + " this->iout[1]  = this->i1 >> this->i2;\n"
        + " this->iout[2]  = ((unsigned int)this->i1) >> this->i2;\n"
        + " return;\n"
        + " }\n"
        + " }\n"
        + "\n"
        + " "};
    private static final Class<? extends com.aparapi.internal.exception.AparapiException> expectedException = null;

    @Test
    public void RightShiftsTest() {
        test(com.aparapi.codegen.test.RightShifts.class, expectedException, expectedOpenCL);
    }

    @Test
    public void RightShiftsTestWorksWithCaching() {
        test(com.aparapi.codegen.test.RightShifts.class, expectedException, expectedOpenCL);
    }
}
