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

public class PreIncByte {

    byte preIncByte(byte a) {
        return ++a;
    }

    public void run() {
        byte initValue = 0;
        @SuppressWarnings("unused") byte result = preIncByte(++initValue);
    }
}
/**{OpenCL{
 typedef struct This_s{
 int passid;
 }This;
 int get_pass_id(This *this){
 return this->passid;
 }
 char com_amd_aparapi_test_PreIncByte__preIncByte(This *this, char a){
 a = (char )(a + 1);
 return(a);
 }
 __kernel void run(
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->passid = passid;
 {
 char initValue = 0;
 char result = com_amd_aparapi_test_PreIncByte__preIncByte(this, ++initValue);
 return;
 }
 }
 }OpenCL}**/

/**{OpenCL{
 typedef struct This_s{
 int passid;
 }This;
 int get_pass_id(This *this){
 return this->passid;
 }
 char com_amd_aparapi_test_PreIncByte__preIncByte(This *this, char a){
 return(a=(char )(a + 1));
 }
 __kernel void run(
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->passid = passid;
 {
 char initValue = 0;
 char result = com_amd_aparapi_test_PreIncByte__preIncByte(this, initValue=(char )(initValue + 1));
 return;
 }
 }
 }OpenCL}**/
