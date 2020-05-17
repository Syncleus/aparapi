package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;

import java.util.Arrays;
import java.util.function.IntConsumer;


public class StringHashLambda {


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
        String[] strings = new String[]{"here","is", "my", "string"};
        int len = strings.length;
        int[] results = new int[len];




        IntConsumer ic = gid -> {
            results[gid]  = strings[gid].hashCode();


        };
        Arrays.fill(results, 0);
        System.out.println(results);
        Device.hsa().forEach(len, ic);
        System.out.println(results);
        dump("hsa", strings, results);
        Arrays.fill(results, 0);
        strings[0]="here";
        strings[1]="is";
        strings[2]="my";
        strings[3]="string";
        Device.seq().forEach(len, ic);
        System.out.println(results);
        dump("seq", strings, results);

    }
}
