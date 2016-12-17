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

public class PostIncArrayItemFieldIndex {

    final static int START_SIZE = 128;

    public int[] values = new int[START_SIZE];

    public int[] results = new int[START_SIZE];

    public int a = 10;

    public void run() {
        values[a] = results[a]++;
    }
}
/**{OpenCL{
 typedef struct This_s{
 __global int *values;
 int a;
 __global int *results;
 int passid;
 }This;
 int get_pass_id(This *this){
 return this->passid;
 }

 __kernel void run(
 __global int *values,
 int a,
 __global int *results,
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->values = values;
 this->a = a;
 this->results = results;
 this->passid = passid;
 {
 this->values[this->a]  = this->results[this->a]++;
 return;
 }
 }
 }OpenCL}**/
