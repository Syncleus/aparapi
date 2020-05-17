package lambdatest;

import com.amd.aparapi.Device;


public class Ternary{


   static int[] in = new int[10240];
   static boolean[] out = new boolean[10240];

   static{
      for(int i = 0; i < 10240; i++){
         in[i] = i;
         out[i] = false;
      }
   }

   void go(){

      Device device = Device.firstGPU();
      device.forEach(in.length, (i) -> {
         out[i] = (in[i] % 2 == 0) ? true : false;
      });


      for(int i = 0; i < 64; i++){
         System.out.println(in[i] + " " + out[i]);
      }
   }

   public static void main(String[] args){
      new Ternary().go();

   }
}
