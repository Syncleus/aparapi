package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;

import java.util.function.IntConsumer;


public class SquaresLambda {

    static void dump(String type, int[] in, int[] out) {
        System.out.print(type + " ->");
        for (int i = 0; i < in.length; i++) {
            System.out.print("(" + in[i] + "," + out[i] + "),");
        }
        System.out.println();
    }

    public static void main(String[] args) throws AparapiException {
        final int len = 10;
        int in[] = new int[len];
        int out[] = new int[len];
        for (int i=0; i<len; i++){
            out[i]=0;
            in[i]=i;
        }
        IntConsumer ic = gid -> {
            out[gid] = in[gid] * in[gid];
        };
        Device.hsa().forEach(len, ic);
        dump("hsa", in, out);
        Device.jtp().forEach(len, ic);
        dump("jtp", in, out);
        Device.seq().forEach(len, ic);
        dump("seq", in, out);
    }
}
