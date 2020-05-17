package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;

import java.util.Arrays;
import java.util.function.IntConsumer;


public class StringCharAtLambda {


    static void dump(String type,  char[] results) {
        System.out.print(type + " ->");
        for (int i = 0; i < results.length; i++) {
            if (i != 0) {
                System.out.print(", ");
            }
            System.out.print(results[i]);
        }
        System.out.println();
    }


    public static void main(String[] args) throws AparapiException {
        String string = "here is my string";
        int len = string.length();
        char[] results = new char[len];

        IntConsumer ic = gid -> {
            results[gid]  = string.charAt(gid);


        };
        Arrays.fill(results, '?');
        System.out.println(results);
        Device.hsa().forEach(len, ic);
        System.out.println(results);
        dump("hsa",  results);

        Arrays.fill(results, '?');
        System.out.println(results);
        Device.seq().forEach(len, ic);
        System.out.println(results);
        dump("seq",  results);

    }
}
