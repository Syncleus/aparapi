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

public class ArrayCreation {

    public float[][] results = new float[128][2];
    int y = 2;

    float[] method(float[] a) {
        a[0] = a[0] + 1;
        return a;
    }

    public void run() {
        float[] a = new float[2];

        float[] b = new float[16];

        //float[][] c = new float[16][16];

        float[] d = method(a);

        results[y] = b;
    }
}
/**{OpenCL{
 typedef struct This_s{
 __global float *results;
 int results__javaArrayLength0;
 int results__javaArrayDimension0;
 int results__javaArrayLength1;
 int results__javaArrayDimension1;
 int y;
 int passid;
 }This;
 int get_pass_id(This *this){
 return this->passid;
 }
 __global float* com_aparapi_codegen_test_VectorCreation__method(This *this,  __global float* a){
 a[0]  = a[0] + 1.0f;
 return(a);
 }
 __kernel void run(
 __global float *results,
 int results__javaArrayLength0,
 int results__javaArrayDimension0,
 int results__javaArrayLength1,
 int results__javaArrayDimension1,
 int y,
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->results = results;
 this->results__javaArrayLength0 = results__javaArrayLength0;
 this->results__javaArrayDimension0 = results__javaArrayDimension0;
 this->results__javaArrayLength1 = results__javaArrayLength1;
 this->results__javaArrayDimension1 = results__javaArrayDimension1;
 this->y = y;
 this->passid = passid;
 {
 float a[2];
 float b[16];
 __global float* d = com_aparapi_codegen_test_VectorCreation__method(this, a);
 this->results[this->y]  = b;
 return;
 }
 }

 }OpenCL}**/
