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

final class ReturnArrayNewTestSupport {

    private ReturnArrayNewTestSupport() {
    }

    static String[] expectedOpenCL(String openCLType, String className, String methodName) {
        String qualifiedMethodName = "com_aparapi_codegen_test_" + className + "__" + methodName;
        return new String[] {
            "typedef struct This_s{\n"
                + "   int passid;\n"
                + "}This;\n"
                + "int get_pass_id(This *this){\n"
                + "   return this->passid;\n"
                + "}\n"
                + " __global " + openCLType + "* " + qualifiedMethodName + "(This *this){\n"
                + "   " + openCLType + " returnArray5[1024];\n"
                + "   return(returnArray5);\n"
                + "}\n"
                + "__kernel void run(\n"
                + "   int passid\n"
                + "){\n"
                + "   This thisStruct;\n"
                + "   This* this=&thisStruct;\n"
                + "   this->passid = passid;\n"
                + "   {\n"
                + "      " + qualifiedMethodName + "(this);\n"
                + "      return;\n"
                + "   }\n"
                + "}"
        };
    }
}
