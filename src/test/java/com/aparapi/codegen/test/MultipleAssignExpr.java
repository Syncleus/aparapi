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

public class MultipleAssignExpr {

    int sum(int lhs, int rhs) {
        return (lhs + rhs);
    }

    public void run() {
        @SuppressWarnings("unused") int a = 0;
        @SuppressWarnings("unused") int b = 0;
        @SuppressWarnings("unused") int c = 0;
        a = b = c = sum(1, 2);

    }
}
/**{OpenCL{
 typedef struct This_s{

 int passid;
 }This;
 int get_pass_id(This *this){
 return this->passid;
 }

 int com_amd_aparapi_test_MultipleAssignExpr__sum(This *this, int lhs, int rhs){
 return((lhs + rhs));
 }
 __kernel void run(
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->passid = passid;
 {
 int a = 0;
 int b = 0;
 int c = 0;
 a = b = c = com_amd_aparapi_test_MultipleAssignExpr__sum(this, 1, 2);
 return;
 }
 }
 }OpenCL}**/
