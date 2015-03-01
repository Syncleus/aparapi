package com.amd.aparapi.test;

// this example gave the following error:
/// com.amd.aparapi.classtools.writer.CodeGenException: composite COMPOSITE_ARBITRARY_SCOPE

import com.amd.aparapi.Kernel;

public class CompositeArbitraryScope extends Kernel{

   void t5() {
      int gid = getGlobalId();
      int numRemaining = 1;
      int thisCount = 0;
      while (numRemaining > 0 && gid > 0) {
         numRemaining += 1;
         thisCount = min(numRemaining, gid);
         numRemaining -= thisCount;
         numRemaining += 1;
      }
      gid -= thisCount;
   }

   void t4() {
      int gid = getGlobalId();
      int numRemaining = 1;
      while (numRemaining > 0 && gid > 0) {
         numRemaining += 1;
         int thisCount = min(numRemaining, gid);
         numRemaining -= thisCount;
         numRemaining += 1;
         gid--;
      }
   }

   void t3() {
      int gid = getGlobalId();
      int numRemaining = 1;
      while (numRemaining > 0) {
         numRemaining += 1;
         int thisCount = min(numRemaining, gid);
         numRemaining -= thisCount;
         numRemaining += 1;
      }
   }

   void t2() {
      int gid = getGlobalId();
      int numRemaining = 1;
      while (numRemaining > 0) {
         {
            int thisCount = min(numRemaining, gid);
            numRemaining -= thisCount;
         }
         numRemaining += 0;
      }
   }

   void t1() {
      int gid = getGlobalId();
      int numRemaining = 1;
      while (numRemaining > 0) {
         numRemaining += 1;
         int thisCount = min(numRemaining, gid);
         numRemaining -= thisCount;
      }
   }

   @Override public void run() {
      int gid = getGlobalId();
      int numRemaining = 1;

      t1();
      t2();
      t3();
      t4();
      t5();

      while (numRemaining > 0) {
         numRemaining += 1;
         {
            int thisCount = min(numRemaining, gid);
            numRemaining -= thisCount;
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

void com_amd_aparapi_test_CompositeArbitraryScope__t5(This *this){
   int gid = get_global_id(0);
   int numRemaining = 1;
   int thisCount = 0;
   for (; numRemaining>0 && gid>0; numRemaining++){
      numRemaining++;
      thisCount = min(numRemaining, gid);
      numRemaining = numRemaining - thisCount;
   }
   gid = gid - thisCount;
   return;
}
void com_amd_aparapi_test_CompositeArbitraryScope__t4(This *this){
   int gid = get_global_id(0);
   int numRemaining = 1;
   while (numRemaining>0 && gid>0){
      numRemaining++;
      {
         int thisCount = min(numRemaining, gid);
         numRemaining = numRemaining - thisCount;
         numRemaining++;
         gid--;
      }
   }
   return;
}
void com_amd_aparapi_test_CompositeArbitraryScope__t3(This *this){
   int gid = get_global_id(0);
   int numRemaining = 1;
   while (numRemaining>0){
      numRemaining++;
      {
         int thisCount = min(numRemaining, gid);
         numRemaining = numRemaining - thisCount;
         numRemaining++;
      }
   }
   return;
}
void com_amd_aparapi_test_CompositeArbitraryScope__t2(This *this){
   int gid = get_global_id(0);
   int numRemaining = 1;
   for (; numRemaining>0; numRemaining){
      {
         int thisCount = min(numRemaining, gid);
         numRemaining = numRemaining - thisCount;
      }
   }
   return;
}
void com_amd_aparapi_test_CompositeArbitraryScope__t1(This *this){
   int gid = get_global_id(0);
   int numRemaining = 1;
   while (numRemaining>0){
      numRemaining++;
      {
         int thisCount = min(numRemaining, gid);
         numRemaining = numRemaining - thisCount;
      }
   }
   return;
}
__kernel void run(
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->passid = passid;
   {
      int gid = get_global_id(0);
      int numRemaining = 1;
      com_amd_aparapi_test_CompositeArbitraryScope__t1(this);
      com_amd_aparapi_test_CompositeArbitraryScope__t2(this);
      com_amd_aparapi_test_CompositeArbitraryScope__t3(this);
      com_amd_aparapi_test_CompositeArbitraryScope__t4(this);
      com_amd_aparapi_test_CompositeArbitraryScope__t5(this);
      while (numRemaining>0){
         numRemaining++;
         {
            int thisCount = min(numRemaining, gid);
            numRemaining = numRemaining - thisCount;
         }
      }
      return;
   }
}
}OpenCL}**/
