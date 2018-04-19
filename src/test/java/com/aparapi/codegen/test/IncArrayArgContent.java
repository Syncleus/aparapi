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

public class IncArrayArgContent {

    int arr[] = new int[10];

    public void run() {

        incit(arr);
    }

    public void incit(int[] arr) {
        arr[0]++;

    }
}
/**{OpenCL{
 typedef struct This_s{
 __global int *arr;
 int passid;
 }This;
 int get_pass_id(This *this){
 return this->passid;
 }

 void com_amd_aparapi_test_IncArrayArgContent__incit(This *this,  __global int* arr){
 arr[0]  = arr[0] + 1;
 return;
 }
 __kernel void run(
 __global int *arr,
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->arr = arr;
 this->passid = passid;
 {
 com_amd_aparapi_test_IncArrayArgContent__incit(this, this->arr);
 return;
 }
 }
 }OpenCL}**/
