package test; 

import agent.Agent;


public class Test{
   static interface SAM{
      void apply(int id);
   }

   public static void For(int from, int to, SAM sam){
     byte[] classBytes = Agent.getBytes(sam.getClass());
     System.out.println("sam class is "+sam.getClass()+" "+classBytes.length);

     for (int id=from; id<to; id++){
         sam.apply(id);
     }
   }
   
   public static void main(String[] _args) {
     
     For(0,12, table -> {
          For(0,12, value -> {
             System.out.printf("%d * %d = %d\n", value, table, (value*table));
          });
       System.out.printf("\n");
       });
   }
}
