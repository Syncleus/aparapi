package com.amd.aparapi.test;

public class If_IfElse_Else_IfElse_{
   /*
      1:  istore_1   ( 0:   iconst_1)
      3:  istore_2   ( 2:   iconst_1)
      5:  istore_3   ( 4:   iconst_1)
      7:  istore  4  ( 6:   iconst_1)
     10:  istore  5  ( 9:   iconst_0)
     13:  ifeq    50 (12:  iload_1)              ?
     17:  ifeq    36 (16:  iload_2)              | ?
     21:  ifeq    30 (20:  iload_3)              | | ?
     25:  istore  5  (24:  iconst_1)             | | |
     27:  goto    50                             | | | +
     31:  istore  5  (30:  iconst_2)             | | v |
     33:  goto    50                             | |   | +
     38:  ifeq    47 (36:  iload   4)            | v   | | ?            !!!!!!!  
     42:  istore  5  (41:  iconst_3)             |     | | |
     44:  goto    50                             |     | | | +
     48:  istore  5  (47:  iconst_4)             |     | | v |
     50:  return                                 v     v v   v



      1:  istore_1   ( 0:   iconst_1)
      3:  istore_2   ( 2:   iconst_1)
      5:  istore_3   ( 4:   iconst_1)
      7:  istore  4  ( 6:   iconst_1)
     10:  istore  5  ( 9:   iconst_0)
     13:  ifeq    50 (12:  iload_1)              ?
     17:  ifeq    36 (16:  iload_2)              | ?
     21:  IFELSE                                 | | 
     33:  goto    50                             | | | +
     38:  ifeq    47 (36:  iload   4)            | v | | ?              !!!!!!!!
     42:  istore  5  (41:  iconst_3)             |   | | |
     44:  goto    50                             |   | | | +
     48:  istore  5  (47:  iconst_4)             |   | | v |
     50:  return                                 v   v v   v

      1:  istore_1   ( 0:   iconst_1)
      3:  istore_2   ( 2:   iconst_1)
      5:  istore_3   ( 4:   iconst_1)
      7:  istore  4  ( 6:   iconst_1)
     10:  istore  5  ( 9:   iconst_0)
     13:  ifeq    50 (12:  iload_1)              ?
     17:  ifeq    36 (16:  iload_2)              | ? <----   ignore this
     21:  IFELSE                                 | | 
     33:  goto    50                             | | +
     38:  IFELSE                                 | v | 
     50:  return                                 v   v   

      1:  istore_1   ( 0:   iconst_1)
      3:  istore_2   ( 2:   iconst_1)
      5:  istore_3   ( 4:   iconst_1)
      7:  istore  4  ( 6:   iconst_1)
     10:  istore  5  ( 9:   iconst_0)
     13:  ifeq    50 (12:  iload_1)              ?
     21:  IFELSE                                 |  
     33:  goto    50                             | +
     38:  IFELSE                                 | |
     50:  return                                 v v
     */
   public void run(){
      boolean a = true;
      boolean b = true;
      boolean c = true;
      boolean d = true;
      int count = 0;
      if(a){
         if(b){
            if(c){
               count = 1;
            }else{
               count = 2;
            }
         }else{
            if(d){
               count = 3;
            }else{
               count = 4;
            }
         }
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
 int i_1 = 1;
 int i_2 = 1;
 int i_3 = 1;
 int i_4 = 1;
 int i_5 = 0;
 if (i_1!=0){
 if (i_2!=0){
 if (i_3!=0){
 i_5 = 1;
 } else {
 i_5 = 2;
 }
 } else {
 if (i_4!=0){
 i_5 = 3;
 } else {
 i_5 = 4;
 }
 }
 }
 return;
 }
 }
 }OpenCL}**/