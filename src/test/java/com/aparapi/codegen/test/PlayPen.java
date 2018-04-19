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

public class PlayPen {
    public void run() {
        int testValue = 10;
        @SuppressWarnings("unused") boolean pass = false;

        if ((testValue % 2 == 0 || testValue <= 0 && (testValue >= 100) && testValue % 4 == 0)) {
            pass = true;
        }

        if ((testValue < 3 || testValue > 5) && (testValue < 2 || testValue > 2) || testValue > 5) {
            pass = true;
        }
        boolean a = false, b = false, c = false, d = false, e = false, f = false;
        if ((a || b && c && d) && e || f) {
            pass = true;
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
 int testValue = 10;
 char pass = 0;
 if ((testValue % 2)==0 || testValue<=0 && testValue>=100 && (testValue % 4)==0){
 pass = 1;
 }
 if ((testValue<3 || testValue>5) && (testValue<2 || testValue>2) || testValue>5){
 pass = 1;
 }
 char a = 0;
 char b = 0;
 char c = 0;
 char d = 0;
 char e = 0;
 char f = 0;
 if ((a!=0 || b!=0 && c!=0 && d!=0) && e!=0 || f!=0){
 pass = 1;
 }
 return;
 }
 }
 }OpenCL}**/
