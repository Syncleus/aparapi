package lambdatest;

import com.amd.aparapi.Device;


public class Conditional{


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
         if(in[i] % 2 == 0 && in[i] % 3 == 0){
            out[i] = true;
         }else{
            out[i] = false;
         }
      });


      for(int i = 0; i < 64; i++){
         System.out.println(in[i] + " " + out[i]);
      }
   }

   public static void main(String[] args){
      new Conditional().go();

   }
}
