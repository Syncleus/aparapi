/**
 * Copyright (c) 2016 - 2017 Syncleus, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aparapi.codegen.test;

import com.aparapi.Kernel;
import org.apache.log4j.Logger;

class AnotherClass {
    static public int foo() {
        return 42;
    }
};

public class CallStaticInAnotherClass extends Kernel {
    private static final Logger LOGGER = Logger.getLogger(CallStaticInAnotherClass.class);

    int out[] = new int[2];

    public int doodoo() {
        return AnotherClass.foo();
    }

    public void run() {
        out[0] = AnotherClass.foo() + doodoo();
    }
}

/**{OpenCL{
 typedef struct This_s{
 __global int *out;
 int passid;
 }This;
 int get_pass_id(This *this){
 return this->passid;
 }
 int com_amd_aparapi_test_AnotherClass__foo(){
 return(42);
 }
 int com_amd_aparapi_test_CallStaticInAnotherClass__doodoo(This *this){
 return(com_amd_aparapi_test_AnotherClass__foo());
 }
 __kernel void run(
 __global int *out,
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->out = out;
 this->passid = passid;
 {
 this->out[0]  = com_amd_aparapi_test_AnotherClass__foo() + com_amd_aparapi_test_CallStaticInAnotherClass__doodoo(this);
 return;
 }
 }

 }OpenCL}**/
