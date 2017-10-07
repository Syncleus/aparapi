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

public class ForAndMandelNoInitializeTest extends com.aparapi.codegen.CodeGenJUnitBase {
    private static final String[] expectedOpenCL = {
    "typedef struct This_s{\n" +
" int width;\n" +
" float scale;\n" +
" int maxIterations;\n" +
" int passid;\n" +
" }This;\n" +
" int get_pass_id(This *this){\n" +
" return this->passid;\n" +
" }\n" +
"\n" +
" __kernel void run(\n" +
" int width,\n" +
" float scale,\n" +
" int maxIterations,\n" +
" int passid\n" +
" ){\n" +
" This thisStruct;\n" +
" This* this=&thisStruct;\n" +
" this->width = width;\n" +
" this->scale = scale;\n" +
" this->maxIterations = maxIterations;\n" +
" this->passid = passid;\n" +
" {\n" +
" int tid = 0;\n" +
" int i = tid % this->width;\n" +
" int j = tid / this->width;\n" +
" float x0 = (((float)i * this->scale) - ((this->scale / 2.0f) * (float)this->width)) / (float)this->width;\n" +
" float y0 = (((float)j * this->scale) - ((this->scale / 2.0f) * (float)this->width)) / (float)this->width;\n" +
" float x = x0;\n" +
" float y = y0;\n" +
" float x2 = x * x;\n" +
" float y2 = y * y;\n" +
" float scaleSquare = this->scale * this->scale;\n" +
" int count = 0;\n" +
" int iter = 0;\n" +
" for (; (x2 + y2)<=scaleSquare && iter<this->maxIterations; iter++){\n" +
" y = ((2.0f * x) * y) + y0;\n" +
" x = (x2 - y2) + x0;\n" +
" x2 = x * x;\n" +
" y2 = y * y;\n" +
" count++;\n" +
" }\n" +
" int value = (256 * count) / this->maxIterations;\n" +
" return;\n" +
" }\n" +
" }\n" +
" "};
    private static final Class<? extends com.aparapi.internal.exception.AparapiException> expectedException = null;

    @Test
    public void ForAndMandelNoInitializeTest() {
        test(com.aparapi.codegen.test.ForAndMandelNoInitialize.class, expectedException, expectedOpenCL);
    }

    @Test
    public void ForAndMandelNoInitializeTestWorksWithCaching() {
        test(com.aparapi.codegen.test.ForAndMandelNoInitialize.class, expectedException, expectedOpenCL);
    }
}
