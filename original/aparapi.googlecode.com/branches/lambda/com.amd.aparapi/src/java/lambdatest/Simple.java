package lambdatest;

import com.amd.aparapi.Device;


public class Simple{

   static int div3(int _val){
      return (_val / 3);
   }

   static int[] in = new int[10240];
   static int[] out = new int[10240];

   static{
      for(int i = 0; i < 10240; i++){
         in[i] = i;
         out[i] = 0;
      }
   }

   void go(){

      Device device = Device.firstGPU();
      device.forEach(in.length, (i) -> {
         out[i] = in[i] * 2;
      });
      device.forEach(in.length, (i) -> {
         in[i] = Math.abs(out[i]) * 2;
      });

      for(int i = 0; i < 64; i++){
         System.out.println(in[i] + " " + out[i]);
      }
   }

   public static void main(String[] args){
      new Simple().go();

   }
}
