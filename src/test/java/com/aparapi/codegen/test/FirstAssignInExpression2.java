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

public class FirstAssignInExpression2 {

    public void run() {
        int value = 1;
        int assignMe;
        int result = 0;
        if (value == value) {
            result = assignMe = value;
        } else {
            assignMe = 1;
            result = 2;
        }
        result++;
        assignMe++;

    }
}
/**{OpenCL{
 typedef struct This_s{

 int passid;
 }This;
 int get_pass_id(This *this){
 return this->passid;
 }
 __kernel void run(
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->passid = passid;
 {
 int value = 1;
 int result=0;
 int assignMe=0;
 if (true){
 result = assignMe = value;
 }else{
 assignMe =1;
 result=2;
 }
 result++;
 return;
 }
 }
 }OpenCL}**/
