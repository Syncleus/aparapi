public class Main{
   public static void main(final String[] args){
      final int in[] = new int[100];
      final int squares[] = new int[100];
      // fill in[]
      Aparapi.forEach(in.length, 
            (gid)->{ int square=in[gid]*in[gid]; squares[gid]=square;}
            );
      // use squares[]
   }
}

