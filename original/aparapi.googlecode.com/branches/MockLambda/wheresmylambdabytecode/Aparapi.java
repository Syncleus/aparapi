public class Aparapi{
   public interface SAM{
      void run(int gid);
   }
   public static void forEach(int range, SAM sam){
      for (int i=0; i<range; i++){
         sam.run(i);
      }
   }
}

