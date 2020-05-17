package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntConsumer;


public class StringContainsLambda {


    static void dump(String type, String[] _strings, boolean[] results) {
        System.out.print(type + " ->");
        for (int i = 0; i < _strings.length; i++) {
            if (i != 0) {
                System.out.print(", ");
            }
            System.out.print(_strings[i]+(results[i]?"*":"?"));
        }
        System.out.println();
    }

    static String getText(File _file) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(_file)));
        for (String line=br.readLine(); line != null; line=br.readLine()){
            sb.append(" ").append(line.toLowerCase());
        }
        return(sb.toString());
    }




    static String[] buildDictionary(File _file) throws IOException {
        List<String> list = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(_file)));
        for (String line=br.readLine(); line != null; line=br.readLine()){
            if (!line.trim().startsWith("//")){
                list.add(line.trim().toLowerCase()) ;
            }else{
                System.out.println("Comment -> "+line);
            }
        }
        while(list.size()%64==0){
            list.add("xxxxx");
        }

        return(list.toArray(new String[0]));
    }



    public static void main(String[] args) throws AparapiException, IOException {
        File dir = new File("C:\\Users\\user1\\aparapi\\branches\\lambda");
        String[] strings = buildDictionary(new File(dir, "names.txt"));

        int len = strings.length;
        String text =    getText(new File(dir, "alice.txt"));

        boolean[] results = new boolean[len];

        IntConsumer ic = gid -> {
            results[gid] = text.contains(strings[gid]);
        };

        Arrays.fill(results, false);
        long start = System.currentTimeMillis();
        Device.hsa().forEach(len, ic);
        dump("hsa= "+(System.currentTimeMillis()-start), strings,  results);

        Arrays.fill(results, false);
        start = System.currentTimeMillis();
        Device.hsa().forEach(len, ic);
        dump("hsa2= "+(System.currentTimeMillis()-start), strings,  results);

        Arrays.fill(results, false);
        start = System.currentTimeMillis();
        Device.jtp().forEach(len, ic);
        dump("jtp "+(System.currentTimeMillis()-start), strings, results);

        Arrays.fill(results, false);
        start = System.currentTimeMillis();
        Device.seq().forEach(len, ic);
        dump("seq "+(System.currentTimeMillis()-start), strings, results);



    }
}
