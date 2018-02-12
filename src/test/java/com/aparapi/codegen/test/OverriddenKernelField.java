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

class OverriddenKernelFieldParent extends Kernel {
    int out[] = new int[1];

    int foo(int n) {
        out[0] = n + 1;
        return out[0];
    }

    @Override
    public void run() {
        out[0] = foo(3);
    }
}

public class OverriddenKernelField extends OverriddenKernelFieldParent {
    int out[] = new int[1];

    @Override
    public void run() {
        out[0] = foo(2);
    }

    @Override
    int foo(int n) {
        return super.foo(n + 1);
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
 int com_amd_aparapi_test_OverriddenKernelFieldParent__foo(This *this, int n){
 this->out[0]  = n + 1;
 return(this->out[0]);
 }
 int com_amd_aparapi_test_OverriddenKernelField__foo(This *this, int n){
 return(com_amd_aparapi_test_OverriddenKernelFieldParent__foo(this, (n + 1)));
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
 this->out[0]  = com_amd_aparapi_test_OverriddenKernelField__foo(this, 2);
 return;
 }
 }
 }OpenCL}**/
