package hsailtest;

import com.amd.aparapi.*;
import com.amd.aparapi.HSAILMethod;


public class VectorMultiplyAddLambda {


    public void test() throws ClassParseException {
        int[] in = new int[100];
        int[] out = new int[in.length];
        int m = 2;
        int a = 100;
        for (int i = 0; i < in.length; i++) {
            in[i] = i;
            out[i] = 0;
        }

        Device.hsa().forEach(in.length, id->{
            out[id] = in[id] * m + a;
        });

        for (int i = 0; i < in.length; i++) {
            System.out.print("(" + in[i] + "," + out[i] + "),");
        }
    }


    public static void main(String[] args) throws AparapiException {
        (new VectorMultiplyAddLambda()).test();

    }
}
