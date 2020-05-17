package com.amd.aparapi.test;

public class AssignField{
   int field = 1024;

   public void run(){
      field = 100;
   }
}
/**{Throws{ClassParseException}Throws}**/

/** weird that we got this!
 typedef struct This_s{
 int field;
 int passid;
 }This;

 int get_pass_id(This *this){
 return this->passid;
 }

 __kernel void run(
 int field,
 int passid
 ){
 This thisStruct;
 This* this=&thisStruct;
 this->field = field;
 this->passid = passid;
 {
 this->field=100;
 return;
 }
 }
 **/
