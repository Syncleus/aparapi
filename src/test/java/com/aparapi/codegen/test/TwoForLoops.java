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

public class TwoForLoops extends Kernel {
    final int size = 100;
    int a[] = new int[size];

    public void run() {
        for (int i = 0; i < size; i++) {
            a[i] = i;
        }

        int sum = 0;
        for (int i = 0; i < size; i++) {
            sum += a[i];
        }
    }

}
/**{OpenCL{
 typedef struct This_s{
 __global int *a;
 int passid;
 }This;
 int get_pass_id(This *this){
 return this->passid;
 }

 __kernel void run(
 __global int *a,
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->a = a;
 this->passid = passid;
 {
 for (int i = 0; i<100; i++){
 this->a[i]  = i;
 }
 int sum = 0;
 for (int i = 0; i<100; i++){
 sum = sum + this->a[i];
 }
 return;
 }
 }
 }OpenCL}**/
