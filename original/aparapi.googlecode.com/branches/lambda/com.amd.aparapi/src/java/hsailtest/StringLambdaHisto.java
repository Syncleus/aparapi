package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntConsumer;


public class StringLambdaHisto {


    static void dump(String type, String[] _strings, int[] results) {
        System.out.print(type + " ->");
        boolean first = true;
        for (int i = 0; i < _strings.length; i++) {
            if (results[i]>0){
            if (!first) {
                System.out.print(", ");
            }   else{
                first = false;
            }


                System.out.print(_strings[i]+"=" + results[i]);
            }

        }
        System.out.println();
    }



    public static void main(String[] args) throws AparapiException, IOException {
        File dir = new File("C:\\Users\\user1\\aparapi\\branches\\lambda");
        String[] strings = TextTools.buildLowerCaseDictionary(new File(dir, "names.txt"));
        int len = strings.length;
        String text = TextTools.getLowercaseText(new File(dir, "moby.txt"));
        int[] counts = new int[len];
        IntConsumer ic = gid -> {
            String chars = strings[gid];
            int count = 0;
            int textLen = text.length();
            int charsLen = chars.length();
            for (int i=0; i<=textLen-charsLen; i++){
                if (i==0 || text.charAt(i-1)<'a' || text.charAt(i-1)>'z'){
                    boolean result = true; // optimistic!
                    for (int offset=0; result && offset<charsLen; offset++){
                       result = chars.charAt(offset) == text.charAt(i+offset);
                    }
                    if (result && !(i+charsLen<textLen && (text.charAt(i+charsLen)>='a' && text.charAt(i+charsLen)<='z'))){
                        count++;
                    }
                }
            }
            counts[gid] = count;
        };
        Arrays.fill(counts, 0);

        long start = System.currentTimeMillis();
        Device.jtp().forEach(len, ic);
        dump("jtp = "+(System.currentTimeMillis()-start), strings, counts);
        Arrays.fill(counts, 0);
        start = System.currentTimeMillis();
        Device.hsa().forEach(len, ic);
        dump("hsa1= "+(System.currentTimeMillis()-start), strings, counts);
        Arrays.fill(counts, 0);
        start = System.currentTimeMillis();
        Device.hsa().forEach(len, ic);
        System.out.println();
        dump("hsa2= "+(System.currentTimeMillis()-start), strings, counts);
        Arrays.fill(counts, 0);
        start = System.currentTimeMillis();
        Device.seq().forEach(len, ic);
        System.out.println();
        dump("seq= "+(System.currentTimeMillis()-start), strings, counts);
    }
}
