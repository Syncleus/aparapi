package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;

import java.util.Arrays;
import java.util.function.IntConsumer;


public class MatchedStringLambda {

    static class MatchableString{
        String value;
        boolean matched;
        MatchableString(String _value){
           value = _value;
        }
        void containsCheck(String text){

            matched = text.contains(value);
        }

    }


    static void dump(String type, MatchableString[] _strings) {
        System.out.print(type + " ->");
        for (int i = 0; i < _strings.length; i++) {
            if (i != 0) {
                System.out.print(", ");
            }
            System.out.print(_strings[i].value+((_strings[i].matched)?"*":"?"));
        }
        System.out.println();
    }


    public static void main(String[] args) throws AparapiException {
        MatchableString[] strings = new MatchableString[]{new MatchableString("cat"),new MatchableString("mat"),new MatchableString("dog")};
        int len = strings.length;
        String text = "the cat sat on the mat";
        boolean[] results = new boolean[len];


        Arrays.fill(results, false);
        Device.hsa().forEach(len, gid -> {
            strings[gid].containsCheck(text);
        });
        dump("hsa", strings);
        Arrays.fill(results, false);
        Device.jtp().forEach(len, gid -> {
            strings[gid].containsCheck(text);
        });
        dump("jtp", strings);
        Arrays.fill(results, false);
        Device.seq().forEach(len, gid -> {
            strings[gid].containsCheck(text);
        });
        dump("seq", strings);
    }
}
