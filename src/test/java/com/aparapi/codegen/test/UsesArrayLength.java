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

public class UsesArrayLength extends Kernel {

    boolean[] values;

    boolean[] results;

    boolean[] results2;

    boolean actuallyDoIt(int index) {
        int x = 0;

        // in array index
        @SuppressWarnings("unused") boolean y = values[values.length - index];

        // in addition
        x = index + results.length;

        // in subtraction
        return (results.length - x > 0);
    }

    public void run() {
        int myId = 0;

        // in comparison
        boolean x = (values.length > 0);

        // in bit AND and as argument
        results[myId] = x & actuallyDoIt(values.length);

        // Note results2.length is not used so there should not
        // be a results2__javaArrayLength in the emitted source
        results2[myId] = !results[myId];
    }
}
/**{OpenCL{
 typedef struct This_s{
 __global char  *values;
 int values__javaArrayLength;
 __global char  *results;
 int results__javaArrayLength;
 __global char  *results2;
 int passid;
 }This;
 int get_pass_id(This *this){
 return this->passid;
 }

 char com_amd_aparapi_test_UsesArrayLength__actuallyDoIt(This *this, int index){
 int x = 0;
 char y = this->values[(this->values__javaArrayLength - index)];
 x = index + this->results__javaArrayLength;
 return(((this->results__javaArrayLength - x)>0)?1:0);
 }
 __kernel void run(
 __global char  *values,
 int values__javaArrayLength,
 __global char  *results,
 int results__javaArrayLength,
 __global char  *results2,
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->values = values;
 this->values__javaArrayLength = values__javaArrayLength;
 this->results = results;
 this->results__javaArrayLength = results__javaArrayLength;
 this->results2 = results2;
 this->passid = passid;
 {
 int myId = 0;
 char x = (this->values__javaArrayLength>0)?1:0;
 this->results[myId]  = x & com_amd_aparapi_test_UsesArrayLength__actuallyDoIt(this, this->values__javaArrayLength);
 this->results2[myId]  = (this->results[myId]==0)?1:0;
 return;
 }
 }
 }OpenCL}**/

/**{OpenCL{
 typedef struct This_s{
 __global char  *values;
 int values__javaArrayLength;
 __global char  *results;
 int results__javaArrayLength;
 __global char  *results2;
 int passid;
 }This;
 int get_pass_id(This *this){
 return this->passid;
 }
 char com_amd_aparapi_test_UsesArrayLength__actuallyDoIt(This *this, int index){
 int x = 0;
 char y = this->values[(this->values__javaArrayLength - index)];
 x = index + this->results__javaArrayLength;
 if ((this->results__javaArrayLength - x)>0){
 return(1);
 }
 return(0);
 }
 __kernel void run(
 __global char  *values,
 int values__javaArrayLength,
 __global char  *results,
 int results__javaArrayLength,
 __global char  *results2,
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->values = values;
 this->values__javaArrayLength = values__javaArrayLength;
 this->results = results;
 this->results__javaArrayLength = results__javaArrayLength;
 this->results2 = results2;
 this->passid = passid;
 {
 int myId = 0;
 char x = (this->values__javaArrayLength>0)?1:0;
 this->results[myId]  = x & com_amd_aparapi_test_UsesArrayLength__actuallyDoIt(this, this->values__javaArrayLength);
 this->results2[myId]  = (this->results[myId]!=0)?0:1;
 return;
 }
 }
 }OpenCL}**/
