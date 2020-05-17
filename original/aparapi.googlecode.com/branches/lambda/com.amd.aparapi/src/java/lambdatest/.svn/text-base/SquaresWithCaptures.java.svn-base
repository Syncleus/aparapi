package lambdatest;

import com.amd.aparapi.Device;


public class SquaresWithCaptures{


   void go(){
      int[] in = new int[10240];
      int[] out = new int[10240];

      for(int i = 0; i < 10240; i++){
         in[i] = i;
         out[i] = 0;
      }

      Device device = Device.firstGPU();
      device.forEach(in.length, (i) -> {
         out[i] = in[i] * in[i];
      });


      for(int i = 0; i < 64; i++){
         System.out.println(in[i] + " " + out[i]);
      }
   }

   public static void main(String[] args){
      new SquaresWithCaptures().go();

   }
}
