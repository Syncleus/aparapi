package hsailtest;

import com.amd.aparapi.AparapiException;

import java.io.*;
import java.util.Arrays;
import java.util.function.IntConsumer;

import static com.amd.aparapi.Device.*;


public class CharArrayLambdaHisto {


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




    static final int NON_ALPHA =0;
    static final int PARTIAL_MATCH =1;
    static final int FINAL_CHECK=2;
    static final int ALPHA=3;
    public static void main(String[] args) throws AparapiException, IOException {
        char[][] strings = TextTools.buildLowerCaseDictionaryChars(new File("C:\\Users\\user1\\aparapi\\branches\\lambda\\names.txt"));
        int len = strings.length;
        char[] longText = TextTools.getLowercaseTextChars(new File("C:\\Users\\user1\\aparapi\\branches\\lambda\\moby.txt"));
        char[] text =  longText;//Arrays.copyOf(longText, 5000);
        int[] counts = new int[len];
        IntConsumer ic = gid -> {

            char[] chars = strings[gid];
            char firstChar=chars[0];
            int count = 0;
            int state= NON_ALPHA;
            int chIndex =0;
            for (int i=0; i<text.length; i++){
                char ch = text[i];
                if (state == PARTIAL_MATCH){
                   if (chars[chIndex]==ch){
                       chIndex++;
                       if (chIndex==chars.length){
                           state = FINAL_CHECK;
                       }
                   }else if (ch<'a'|| ch>'z'){
                       state= NON_ALPHA;
                   }else{
                       state=ALPHA;
                   }
                }else if (state == NON_ALPHA && firstChar==text[i]){
                       state= PARTIAL_MATCH;
                       chIndex = 1;

                } else if (state == ALPHA &&  ch<'a'|| ch>'z'){
                    state= NON_ALPHA;
                }  else if (state == FINAL_CHECK){
                    if ( ch<'a'|| ch>'z'){
                        count++;
                        state = NON_ALPHA;
                    }else{
                        state = ALPHA;
                    }
                }
            }
            if (state == FINAL_CHECK){
                count++;
            }
            counts[gid] = count;

        };

        long start=0L;
        boolean seq = false;
        boolean jtp =true;
        boolean hyb = false;
        boolean hsa = false;

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
