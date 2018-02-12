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

import org.junit.Test;

public class IfElse_And_Or_AndTest extends com.aparapi.codegen.CodeGenJUnitBase {

    private static final String[] expectedOpenCL = {
        "typedef struct This_s{\n"
        + '\n'
        + " int passid;\n"
        + " }This;\n"
        + " int get_pass_id(This *this){\n"
        + " return this->passid;\n"
        + " }\n"
        + '\n'
        + " __kernel void run(\n"
        + " int passid\n"
        + " ){\n"
        + " This thisStruct;\n"
        + " This* this=&thisStruct;\n"
        + " this->passid = passid;\n"
        + " {\n"
        + " int x = 5;\n"
        + " int y = 5;\n"
        + " char pass = 0;\n"
        + " if (x>=0 && x<10 || y>=0 && y<10){\n"
        + " pass = 1;\n"
        + " } else {\n"
        + " pass = 0;\n"
        + " }\n"
        + " return;\n"
        + " }\n"
        + " }\n"
        + ' '};
    private static final Class<? extends com.aparapi.internal.exception.AparapiException> expectedException = null;

    @Test
    public void IfElse_And_Or_AndTest() {
        test(com.aparapi.codegen.test.IfElse_And_Or_And.class, expectedException, expectedOpenCL);
    }

    @Test
    public void IfElse_And_Or_AndTestWorksWithCaching() {
        test(com.aparapi.codegen.test.IfElse_And_Or_And.class, expectedException, expectedOpenCL);
    }
}
