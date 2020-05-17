package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.IntConsumer;

import static com.amd.aparapi.Device.hsaForEach;


public class CharArrayHistoLambda3 {


    static void dump(String type, char[][] _strings, int[] _results) {
        System.out.print(type + " ->");
        boolean first = true;
        for (int i = 0; i < _strings.length; i++) {
            if (_results[i]>0){
            if (!first) {
                System.out.print(", ");
            }   else{
                first = false;
            }

            for (char c:_strings[i]){
                System.out.print(c);
            }
            System.out.print("("+_results[i]+")");
            }

        }
        System.out.println();
    }



    public static void main(String[] args) throws AparapiException, IOException {
        char[][] names = TextTools.buildLowerCaseDictionaryChars(new File("C:\\Users\\user1\\aparapi\\branches\\lambda\\names.txt"));
        int len = names.length;
        char[] text = TextTools.getLowercaseTextCharsOnly(new File("C:\\Users\\user1\\aparapi\\branches\\lambda\\moby.txt"));
        int textLen = text.length;
        int[] results = new int[len];
        IntConsumer ic = gid -> {
            int result = 0;
            char[] nameChars = names[gid];
            int nameCharLen = nameChars.length;

            for (int i=0;  i<=textLen-nameCharLen-1; i++){
                if ((i==0 || text[i-1]==' ') && text[i+nameCharLen]==' ') {
                    boolean found = true; // optimistic!
                    for (int offset=0; found && offset<nameCharLen; offset++){
                        found = nameChars[offset] == text[i+offset];
                    }
                    if (found){
                        result++;
                    }
                }

            }
            results[gid] = result;
        };
        Arrays.fill(results, 0);

        long start = System.currentTimeMillis();
        Device.jtp().forEach(len, ic);
        System.out.println();
        dump("jtp = "+(System.currentTimeMillis()-start), names, results);

        Arrays.fill(results, 0);
        start = System.currentTimeMillis();
        hsaForEach(len, ic);
        System.out.println();
        dump("hsa1= "+(System.currentTimeMillis()-start), names, results);
        Arrays.fill(results, 0);
        start = System.currentTimeMillis();
        hsaForEach(len, ic);
        System.out.println();
        dump("hsa2= "+(System.currentTimeMillis()-start), names, results);
        Arrays.fill(results, 0);
        start = System.currentTimeMillis();
        Device.seq().forEach(len, ic);
        System.out.println();
        dump("seq= "+(System.currentTimeMillis()-start), names, results);
    }
}
