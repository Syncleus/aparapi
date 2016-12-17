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

public class PreDecArrayItem {

    final static int START_SIZE = 128;

    public int[] values = new int[START_SIZE];

    public int[] results = new int[START_SIZE];

    public void run() {
        int y = 2;
        values[y] = --results[y];
    }
}
/**{OpenCL{
 typedef struct This_s{
 __global int *values;
 __global int *results;
 int passid;
 }This;
 int get_pass_id(This *this){
 return this->passid;
 }

 __kernel void run(
 __global int *values,
 __global int *results,
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->values = values;
 this->results = results;
 this->passid = passid;
 {
 int y = 2;
 this->values[y]  = --this->results[y];
 return;
 }
 }

 }OpenCL}**/
