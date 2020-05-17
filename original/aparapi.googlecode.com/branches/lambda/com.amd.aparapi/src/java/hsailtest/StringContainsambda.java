package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;

import java.util.Arrays;
import java.util.function.IntConsumer;


public class StringContainsambda {


    static void dump(String type, String[] _strings, boolean[] indices) {
        System.out.print(type + " ->");
        for (int i = 0; i < _strings.length; i++) {
            if (i != 0) {
                System.out.print(", ");
            }
            System.out.print(_strings[i]+(indices[i]?"*":"?")+"="+indices[i]);
        }
        System.out.println();
    }




    public static void main(String[] args) throws AparapiException {
        String[] strings = new String[]{"cat","mat","dog"};

        int len = strings.length;
        String text = "the cat sat on the mat";

        boolean[] indices = new boolean[len];

        IntConsumer ic = gid -> {
          //  if (text.contains(strings[gid])){
            //    indices[gid]=true;
           // }
            indices[gid] = text.contains(strings[gid]);
        };

        Arrays.fill(indices, false);
        Device.hsa().forEach(len, ic);
        dump("hsa", strings,  indices);

        Arrays.fill(indices, false);
        Device.seq().forEach(len, ic);
        dump("jtp", strings, indices);

        Arrays.fill(indices, false);
        Device.seq().forEach(len, ic);
        dump("seq", strings, indices);

    }
}
