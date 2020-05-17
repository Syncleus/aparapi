package kerneltest;

import com.amd.aparapi.Device;
import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

public class Simple{

   void go(){
      int[] in = new int[1024];
      int[] out = new int[1024];
      for(int i = 0; i < 1024; i++){
         in[i] = i;
         out[i] = 0;
      }
      Device device = Device.firstGPU();
      Range range = device.createRange(in.length);
      Kernel kernel = new Kernel(){
         int twoTimes(int _i){
            return (in[_i] * 2);
         }

         public void run(){
            int i = getGlobalId();
            out[i] = twoTimes(i);
         }


      };
      kernel.execute(range);
      for(int i = 0; i < 64; i++){
         System.out.println(in[i] + " " + out[i]);
      }
      new Kernel(){
         public void run(){
            int i = getGlobalId();
            out[i] = in[i] * in[i];
         }
      }.execute(range);
      for(int i = 0; i < 64; i++){
         System.out.println(in[i] + " " + out[i]);
      }
   }

   public static void main(String[] args){
      new Simple().go();

   }

}
