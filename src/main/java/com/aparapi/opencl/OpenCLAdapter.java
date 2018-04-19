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
package com.aparapi.opencl;

import com.aparapi.ProfileInfo;
import java.util.ArrayList;
import java.util.List;

public class OpenCLAdapter<T> implements OpenCL<T>{

   @SuppressWarnings("unchecked") public T put(byte[] array) {
      return ((T) this);
   }

   @SuppressWarnings("unchecked") public T get(byte[] array) {
      return ((T) this);
   }

   @SuppressWarnings("unchecked") public T put(float[] array) {
      return ((T) this);
   }

   @SuppressWarnings("unchecked") public T put(int[] array) {
      return ((T) this);
   }

   @SuppressWarnings("unchecked") public T put(short[] array) {
      return ((T) this);
   }

   @SuppressWarnings("unchecked") public T put(char[] array) {
      return ((T) this);
   }

   @SuppressWarnings("unchecked") public T put(boolean[] array) {
      return ((T) this);
   }

   @SuppressWarnings("unchecked") public T put(double[] array) {
      return ((T) this);
   }

   @SuppressWarnings("unchecked") public T get(float[] array) {
      return ((T) this);
   }

   @SuppressWarnings("unchecked") public T get(int[] array) {
      return ((T) this);
   }

   @SuppressWarnings("unchecked") public T get(short[] array) {
      return ((T) this);
   }

   @SuppressWarnings("unchecked") public T get(char[] array) {
      return ((T) this);
   }

   @SuppressWarnings("unchecked") public T get(boolean[] array) {
      return ((T) this);
   }

   @SuppressWarnings("unchecked") public T get(double[] array) {
      return ((T) this);
   }

   @SuppressWarnings("unchecked") public T begin() {
      return ((T) this);
   }

   @SuppressWarnings("unchecked") public T end() {
      return ((T) this);
   }

   @SuppressWarnings("unchecked") public T dispose() {
      return ((T) this);
   }

   public List<ProfileInfo> getProfileInfo(){
       return(new ArrayList<ProfileInfo>());
   }

}
