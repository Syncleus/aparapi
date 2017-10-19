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

interface IFoo {
    public int bar(int n);
}

public class ImplementsInterface extends Kernel implements IFoo {
    private static final Logger LOGGER = Logger.getLogger(ImplementsInterface.class);
    int out[] = new int[1];

    int ival = 3;

    public int bar(int n) {
        return n + ival;
    }

    public void run() {
        out[0] = bar(1);
        @SuppressWarnings("unused") boolean pass = false;
    }
}
/**{OpenCL{
 typedef struct This_s{
 int ival;
 __global int *out;
 int passid;
 }This;
 int get_pass_id(This *this){
 return this->passid;
 }

 int com_amd_aparapi_test_ImplementsInterface__bar(This *this, int n){
 return((n + this->ival));
 }
 __kernel void run(
 int ival,
 __global int *out,
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->ival = ival;
 this->out = out;
 this->passid = passid;
 {
 this->out[0]  = com_amd_aparapi_test_ImplementsInterface__bar(this, 1);
 char pass = 0;
 return;
 }
 }
 }OpenCL}**/
