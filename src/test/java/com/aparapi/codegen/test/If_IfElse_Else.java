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

public class If_IfElse_Else {
    public void run() {
        boolean a = true;
        boolean b = true;
        @SuppressWarnings("unused") boolean result = false;

        if (a) {
            if (b) {
                result = true;
            } else {
                result = true;
            }
        } else {
            result = false;
        }

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
 char a = 1;
 char b = 1;
 char result = 0;
 if (a!=0){
 if (b!=0){
 result = 1;
 } else {
 result = 1;
 }
 } else {
 result = 0;
 }
 return;
 }
 }
 }OpenCL}**/
