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

public class OverloadMethod extends Kernel {
    int out[] = new int[1];

    public void run() {
        out[0] = foo(2) + foo(2, 3);
    }

    int foo(int n) {
        return n + 1;
    }

    int foo(int a, int b) {
        return min(a, b);
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
 int com_amd_aparapi_test_OverloadMethod__foo(This *this, int a, int b){
 return(min(a, b));
 }
 int com_amd_aparapi_test_OverloadMethod__foo(This *this, int n){
 return((n + 1));
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
 this->out[0]  = com_amd_aparapi_test_OverloadMethod__foo(this, 2) + com_amd_aparapi_test_OverloadMethod__foo(this, 2, 3);
 return;
 }
 }
 }OpenCL}**/
