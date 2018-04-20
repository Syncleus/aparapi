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

public class Ternary {

    float random() {
        return (.1f);
    }

    public void run() {
        @SuppressWarnings("unused") int count = (random() > .5f) ? +1 : -1;
        @SuppressWarnings("unused") int foo = 3;
    }

}
/**{OpenCL{
 typedef struct This_s{
 int passid;
 }This;
 int get_pass_id(This *this){
 return this->passid;
 }
 float com_amd_aparapi_test_Ternary__random(This *this){
 return(0.1f);
 }
 __kernel void run(
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->passid = passid;
 {
 int count = (com_amd_aparapi_test_Ternary__random(this)>0.5f)?1:-1;
 int foo = 3;
 return;
 }
 }
 }OpenCL}**/
