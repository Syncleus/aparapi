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

import org.junit.Ignore;
import org.junit.Test;

public class FirstAssignInExpression2Test extends com.aparapi.codegen.CodeGenJUnitBase {

    private static final String[] expectedOpenCL = {
        "typedef struct This_s{\n"
        + "\n"
        + " int passid;\n"
        + " }This;\n"
        + " int get_pass_id(This *this){\n"
        + " return this->passid;\n"
        + " }\n"
        + " __kernel void run(\n"
        + " int passid\n"
        + " ){\n"
        + " This thisStruct;\n"
        + " This* this=&thisStruct;\n"
        + " this->passid = passid;\n"
        + " {\n"
        + " int value = 1;\n"
        + " int result=0;\n"
        + " int assignMe=0;\n"
        + " if (true){\n"
        + " result = assignMe = value;\n"
        + " }else{\n"
        + " assignMe =1;\n"
        + " result=2;\n"
        + " }\n"
        + " result++;\n"
        + " return;\n"
        + " }\n"
        + " }\n"
        + " "};
    private static final Class<? extends com.aparapi.internal.exception.AparapiException> expectedException = null;

    @Ignore
    @Test
    public void FirstAssignInExpression2Test() {
        test(com.aparapi.codegen.test.FirstAssignInExpression2.class, expectedException, expectedOpenCL);
    }

    @Ignore
    @Test
    public void FirstAssignInExpression2TestWorksWithCaching() {
        test(com.aparapi.codegen.test.FirstAssignInExpression2.class, expectedException, expectedOpenCL);
    }
}
