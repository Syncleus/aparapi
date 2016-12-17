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

public class PostIncArrayItemAsParameter {

    final static int START_SIZE = 128;

    public int[] values = new int[START_SIZE];

    public int[] results = new int[START_SIZE];
    int y = 2;

    int actuallyDoIt(int a) {
        return 1;
    }

    public void run() {
        actuallyDoIt(results[y]++);
    }
}
/**{OpenCL{
 typedef struct This_s{
 __global int *results;
 int y;
 int passid;
 }This;
 int get_pass_id(This *this){
 return this->passid;
 }

 int com_amd_aparapi_test_PostIncArrayItemAsParameter__actuallyDoIt(This *this, int a){
 return(1);
 }
 __kernel void run(
 __global int *results,
 int y,
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->results = results;
 this->y = y;
 this->passid = passid;
 {
 com_amd_aparapi_test_PostIncArrayItemAsParameter__actuallyDoIt(this, this->results[this->y]++);
 return;
 }
 }
 }OpenCL}**/
