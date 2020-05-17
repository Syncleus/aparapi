package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;
import com.amd.aparapi.HSADevice;

import java.util.function.IntConsumer;


public class FloatSquaresFuncLambda {

    static void dump(String type, float[] in, float[] out) {
        System.out.print(type + " ->");
        for (int i = 0; i < in.length; i++) {
            System.out.print("(" + in[i] + "," + out[i] + "),");
        }
        System.out.println();
    }

    static float mul(float lhs, float rhs){
        return(lhs*rhs);
    }

    static float square(float v){
        return(mul(v,v));
    }

    public static void main(String[] args) throws AparapiException {
        FloatSquaresFuncLambda main = new FloatSquaresFuncLambda();
        final int len = 10;
        float out[] = new float[len];
        float in[] = new float[len];
        for (int i=0; i<len; i++){
            out[i]=0;
            in[i]=i;
        }
        IntConsumer ic = gid -> {
            out[gid] = square(in[gid]);
        };
        ((HSADevice)Device.hsa()).dump(ic);

       if (true){
        Device.hsa().forEach(len, ic);
        dump("hsa", in, out);
        Device.jtp().forEach(len, ic);
        dump("jtp", in, out);
        Device.seq().forEach(len, ic);
        dump("seq", in, out);
       }
    }
}
