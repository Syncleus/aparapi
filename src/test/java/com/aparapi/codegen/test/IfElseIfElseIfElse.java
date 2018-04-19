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

public class IfElseIfElseIfElse {
    /*
      1:   istore_1   (0:iconst_1)
      3:   istore_2   (2:iconst_1)
      5:   istore_3   (4:iconst_1)
      7:   istore  4  (6:iconst_0)
      10:  ifeq    16 (9:iload_1)      ?
      13:  goto    39                  | +
      17:  ifeq    26 (16:  iload_2)   v | ?
      21:  istore  4  (20:  iconst_1)    | |
      23:  goto    39                    | | +
      27:  ifeq    36 (26:  iload_3)     | v | ?
      31:  istore  4  (30:  iconst_1)    |   | |
      33:  goto    39                    |   | | +
      37:  istore  4  (36:  iconst_1)    |   | v |
      39:  return                        v   v   v
     */
    public void run() {
        boolean a = true;
        boolean b = true;
        boolean c = true;
        @SuppressWarnings("unused") boolean result = false;

        if (a) {
        } else if (b) {
            result = true;
        } else if (c) {
            result = true;
        } else {
            result = true;
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
 char c = 1;
 char result = 0;
 if (a!=0){
 } else {
 if (b!=0){
 result = 1;
 } else {
 if (c!=0){
 result = 1;
 } else {
 result = 1;
 }
 }
 }
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
 __kernel void run(
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->passid = passid;
 {
 char a = 1;
 char b = 1;
 char c = 1;
 char result = 0;
 if (a==0){
 if (b!=0){
 result = 1;
 } else {
 if (c!=0){
 result = 1;
 } else {
 result = 1;
 }
 }
 }
 return;
 }
 }
 }OpenCL}**/
