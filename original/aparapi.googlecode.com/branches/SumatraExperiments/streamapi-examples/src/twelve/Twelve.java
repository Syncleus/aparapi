package twelve; 

public class Twelve{
   interface SAM{
      void run(int _id);
   }

   public static void For(int _count, SAM _sam){
      for (int id = 0; id<_count; id++){
         _sam.run(id);
      }
   }

   public static void main(String[] _args) {
     For(12, table -> {
       For(12, value ->{
          System.out.printf("%d * %d = %d\n", value, table, (value*table));
          }
       );
       System.out.printf("\n");
       }
     );
   }
}
