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

public class While_If_IfElseElseTest extends com.aparapi.codegen.CodeGenJUnitBase {

    private static final String[] expectedOpenCL = {
        "typedef struct This_s{\n"
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
        + " int a = 0;\n"
        + " int b = 0;\n"
        + " int c = 0;\n"
        + " int d = 0;\n"
        + " int e = 0;\n"
        + " int f = 0;\n"
        + " int g = 0;\n"
        + " int h = 0;\n"
        + " int i = 0;\n"
        + " int j = 0;\n"
        + " int k = 0;\n"
        + " int l = 0;\n"
        + " int m = 0;\n"
        + " int n = 0;\n"
        + " int o = 0;\n"
        + " int p = 0;\n"
        + " int q = 0;\n"
        + " int r = 0;\n"
        + " for (; a==a; ){\n"
        + " b = b;\n"
        + " if (c==c){\n"
        + " d = d;\n"
        + " if (e==e && f==f){\n"
        + " g = g;\n"
        + " }\n"
        + " }\n"
        + "\n"
        + " if (h==h && i==i){\n"
        + " if (j==j){\n"
        + " k = k;\n"
        + " }\n"
        + " if (l==l){\n"
        + " if (m==m){\n"
        + " n = n;\n"
        + " } else {\n"
        + " if (o==o){\n"
        + " p = p;\n"
        + " } else {\n"
        + " q = q;\n"
        + " }\n"
        + " }\n"
        + " r = r;\n"
        + " }\n"
        + " }\n"
        + " }\n"
        + " return;\n"
        + " }\n"
        + " }\n"
        + " "};
    private static final Class<? extends com.aparapi.internal.exception.AparapiException> expectedException = null;

    @Test
    public void While_If_IfElseElseTest() {
        test(com.aparapi.codegen.test.While_If_IfElseElse.class, expectedException, expectedOpenCL);
    }

    @Test
    public void While_If_IfElseElseTestWorksWithCaching() {
        test(com.aparapi.codegen.test.While_If_IfElseElse.class, expectedException, expectedOpenCL);
    }
}
