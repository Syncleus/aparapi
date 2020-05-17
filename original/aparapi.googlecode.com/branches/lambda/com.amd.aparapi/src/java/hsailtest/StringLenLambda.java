package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;

import java.util.Arrays;
import java.util.function.IntConsumer;


public class StringLenLambda {


    static void dump(String type, String[] _strings, int[] results) {
        System.out.print(type + " ->");
        for (int i = 0; i < _strings.length; i++) {
            if (i != 0) {
                System.out.print(", ");
            }
            System.out.print(_strings[i]+"="+results[i]);
        }
        System.out.println();
    }


    public static void main(String[] args) throws AparapiException {
        String[] strings = new String[]{"cat","mat","dog","horse"};
        int len = strings.length;
        int[] results = new int[len];




        IntConsumer ic = gid -> {
            results[gid]  = strings[gid].length();


        };
        Arrays.fill(results, 0);
        System.out.println(results);
        Device.hsa().forEach(len, ic);
        System.out.println(results);
        dump("hsa", strings, results);

    }
}
