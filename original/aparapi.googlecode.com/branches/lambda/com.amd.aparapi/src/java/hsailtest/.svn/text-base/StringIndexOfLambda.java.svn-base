package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;

import java.util.Arrays;
import java.util.function.IntConsumer;


public class StringIndexOfLambda {


    static void dump(String type, String[] _strings, int[] indices) {
        System.out.print(type + " ->");
        for (int i = 0; i < _strings.length; i++) {
            if (i != 0) {
                System.out.print(", ");
            }
            System.out.print(_strings[i]+(indices[i]>-1?"*":"?")+"="+indices[i]);
        }
        System.out.println();
    }




    public static void main(String[] args) throws AparapiException {
        String[] strings = new String[]{"cat","mat","dog"};

        int len = strings.length;
        String text = "the cat sat on the mat";

        int[] indices = new int[len];

        IntConsumer ic = gid -> {
            indices[gid] = text.indexOf(strings[gid]);
        };

        Arrays.fill(indices, -1);
        Device.hsa().forEach(len, ic);
        dump("hsa", strings,  indices);

        Arrays.fill(indices, -1);
        Device.seq().forEach(len, ic);
        dump("jtp", strings, indices);

        Arrays.fill(indices, -1);
        Device.seq().forEach(len, ic);
        dump("seq", strings, indices);

    }
}
