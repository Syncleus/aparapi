package hsailtest;

import com.amd.aparapi.AparapiException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.IntConsumer;

import static com.amd.aparapi.Device.*;


public class CharArrayLambdaHisto2 {


    static void dump(String type, char[][] _strings, int[] results) {
        System.out.print(type + " ->");
        boolean first = true;
        for (int i = 0; i < _strings.length; i++) {
            if (results[i]>0){
            if (!first) {
                System.out.print(", ");
            }   else{
                first = false;
            }

            for (char c:_strings[i]){
                System.out.print(c);
            }
                System.out.print("=" + results[i]);
            }

        }
        System.out.println();
    }





    public static void main(String[] args) throws AparapiException, IOException {
        char[][] strings = TextTools.buildLowerCaseDictionaryChars(new File("C:\\Users\\user1\\aparapi\\branches\\lambda\\names.txt"));
        int len = strings.length;
        char[] text = TextTools.getLowercaseTextChars(new File("C:\\Users\\user1\\aparapi\\branches\\lambda\\moby.txt"));
        int[] counts = new int[len];
        IntConsumer ic = gid -> {
            char[] chars = strings[gid];
            char firstChar=chars[0];
            int count = 0;
            for (int i=0; i<=text.length-chars.length; i++){
                char prevChar =0;
                if (i>0){
                   prevChar = text[i-1];
                }
                if (firstChar==text[i] && (prevChar<'a' || prevChar>'z')){

                    boolean result = true; // optimistic!
                    for (int offset=1; result && offset<chars.length; offset++){
                       result = chars[offset] == text[i+offset];
                    }
                    char endChar=0;
                    if ((i+chars.length)<text.length){
                        endChar=text[i+chars.length];
                    }
                    if (result && (endChar<'a' || endChar>'z')){
                        count++;
                    }
                }
            }
            counts[gid] = count;
        };

        long start=0L;
        boolean seq = false;
        boolean jtp = true;
        boolean hyb = false;
        boolean hsa = true;

        if (hsa){

        for (int i=0; i<4; i++){
        Arrays.fill(counts, 0);
        start = System.currentTimeMillis();
        hsaForEach(len, ic);
        System.out.println();
        dump("hsa"+i+"= "+(System.currentTimeMillis()-start), strings, counts);
        }


        }
        if (hyb){
        for (float gpushare : new float[]{.88f,.89f,.9f,.91f, .92f,.93f, .94f,.95f, .96f,.97f,.98f }) {
            Arrays.fill(counts, 0);
            start = System.currentTimeMillis();
            hybForEach(len, gpushare, ic);
            System.out.println();
            dump("hyb"+gpushare+"= "+(System.currentTimeMillis()-start), strings, counts);
        }
        }

        if (jtp){
        Arrays.fill(counts, 0);
        start = System.currentTimeMillis();
        jtpForEach(len, ic);
        System.out.println();
        dump("jtp = "+(System.currentTimeMillis()-start), strings, counts);
        }
        if (seq){
        Arrays.fill(counts, 0);
        start = System.currentTimeMillis();
        seqForEach(len, ic);
        System.out.println();
        dump("seq= "+(System.currentTimeMillis()-start), strings, counts);
        }
    }
}
