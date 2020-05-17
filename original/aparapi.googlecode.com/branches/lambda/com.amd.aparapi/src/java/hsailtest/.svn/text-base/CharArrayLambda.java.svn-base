package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;
import static com.amd.aparapi.Device.hsaForEach;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntConsumer;



public class CharArrayLambda {


    static void dump(String type, char[][] _strings, boolean[] results) {
        System.out.print(type + " ->");
        boolean first = true;
        for (int i = 0; i < _strings.length; i++) {
            if (results[i]){
            if (!first) {
                System.out.print(", ");
            }   else{
                first = false;
            }

            for (char c:_strings[i]){
                System.out.print(c);
            }
            }

        }
        System.out.println();
    }



    public static void main(String[] args) throws AparapiException, IOException {
        char[][] names = TextTools.buildLowerCaseDictionaryChars(new File("C:\\Users\\user1\\aparapi\\branches\\lambda\\names.txt"));
        int len = names.length;
        char[] text = TextTools.getLowercaseTextChars(new File("C:\\Users\\user1\\aparapi\\branches\\lambda\\moby.txt"));
        int textLen = text.length;
        boolean[] results = new boolean[len];
        IntConsumer ic = gid -> {
            boolean result = false;
            char[] nameChars = names[gid];
            int nameCharLen = nameChars.length;

            for (int i=0; !result && i<=textLen-nameCharLen-1; i++){
                if ((i==0 || text[i-1]==' ') && text[i+nameCharLen]==' ') {
                    result = true; // optimistic!
                    for (int offset=0; result && offset<nameCharLen; offset++){
                        result = nameChars[offset] == text[i+offset];
                    }
                }

            }
            results[gid] = result;
        };
        Arrays.fill(results, false);

        long start = System.currentTimeMillis();
        Device.jtp().forEach(len, ic);
        System.out.println();
        dump("jtp = "+(System.currentTimeMillis()-start), names, results);

        Arrays.fill(results, false);
        start = System.currentTimeMillis();
        hsaForEach(len, ic);
        System.out.println();
        dump("hsa1= "+(System.currentTimeMillis()-start), names, results);
        Arrays.fill(results, false);
        start = System.currentTimeMillis();
        hsaForEach(len, ic);
        System.out.println();
        dump("hsa2= "+(System.currentTimeMillis()-start), names, results);
        Arrays.fill(results, false);
        start = System.currentTimeMillis();
        Device.seq().forEach(len, ic);
        System.out.println();
        dump("seq= "+(System.currentTimeMillis()-start), names, results);
    }
}
