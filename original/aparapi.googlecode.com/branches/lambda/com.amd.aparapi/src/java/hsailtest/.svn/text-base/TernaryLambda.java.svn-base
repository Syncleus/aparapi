package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;

import java.util.function.IntConsumer;


public class TernaryLambda {

    static void dump(String type, boolean[] in) {
        System.out.print(type + " ->");
        for (int i = 0; i < in.length; i++) {
            System.out.print("(" + in[i] + "),");
        }
        System.out.println();
    }





    public static void main(String[] args) throws AparapiException {
        final int len = 10;
        boolean in[] = new boolean[len];

        IntConsumer ic = gid -> {
            in[gid] = gid%2==0;
        };

        Device.hsa().forEach(len, ic);
        dump("hsa", in);
        Device.jtp().forEach(len, ic);
        dump("jtp", in);
        Device.seq().forEach(len, ic);
        dump("seq", in);
    }
}
