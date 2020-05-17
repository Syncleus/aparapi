package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;

import java.util.function.IntConsumer;


public class BooleanLambda {

    static void dump(String type, boolean[] in) {
        System.out.print(type + " ->");
        for (int i = 0; i < in.length; i++) {
            System.out.print("(" + in[i] + "),");
        }
        System.out.println();
    }

     static boolean isOdd(int val){
         boolean result = false;
         if ((val%2)==0){
             result = true;
         }
         return(result);
    }

    public static void main(String[] args) throws AparapiException {
        final int len = 10;
        boolean in[] = new boolean[len];

        IntConsumer ic = gid -> {
          //  in[gid]=false;
           // if ((gid%2)==0){
                in[gid] = isOdd(gid);
           // } else{
          //      in[gid] = false;
          //  }
           // in[gid] = (gid%2)==0;
        };

        Device.hsa().forEach(len, ic);
        dump("hsa", in);
        Device.jtp().forEach(len, ic);
        dump("jtp", in);
        Device.seq().forEach(len, ic);
        dump("seq", in);
    }
}
