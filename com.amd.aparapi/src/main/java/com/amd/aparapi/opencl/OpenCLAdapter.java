package com.amd.aparapi.opencl;

import com.amd.aparapi.ProfileInfo;
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
