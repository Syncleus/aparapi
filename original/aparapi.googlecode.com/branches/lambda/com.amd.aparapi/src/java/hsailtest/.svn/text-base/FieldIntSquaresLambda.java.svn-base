package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;

import java.util.function.IntConsumer;


public class FieldIntSquaresLambda {
    public final int len = 10;
    public int in[] = new int[len];
    public int out[] = new int[len];

    static void dump(String type, int[] in, int[] out) {
        System.out.print(type + " ->");
        for (int i = 0; i < in.length; i++) {
            System.out.print("(" + in[i] + "," + out[i] + "),");
        }
        System.out.println();
    }

    public void test(){
        IntConsumer ic = gid -> {
            in[gid] = gid;
            out[gid] = in[gid] * in[gid];
        };
        Device.hsa().forEach(len, ic);
        dump("hsa", in, out);
        Device.jtp().forEach(len, ic);
        dump("jtp", in, out);
        Device.seq().forEach(len, ic);
        dump("seq", in, out);
    }

    public static void main(String[] args) throws AparapiException {
        (new FieldIntSquaresLambda()).test();

    }
}
