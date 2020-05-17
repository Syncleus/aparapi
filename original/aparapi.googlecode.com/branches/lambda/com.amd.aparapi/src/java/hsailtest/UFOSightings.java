package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntConsumer;


public class UFOSightings {


    static void dump(String type, Sighting[] _sightings) {
        int count = 0;
        for (int i = 0; i < _sightings.length; i++) {
           if (_sightings[i].matched){
               count++;
           }
        }
        System.out.println(type+" "+count);
    }
    static class Sighting{
        String text;
        boolean matched;
        Sighting(String _text){
            text = _text;
        }
        void containsCheck(String _token){

            matched = text.contains(_token);
        }

    }

    public static void main(String[] args) throws AparapiException, IOException {

        List<Sighting> sightingList = new ArrayList<Sighting>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("c:\\Users\\user1\\ufo-sightings\\chimps_16154-2010-10-20_14-33-35\\ufo_awesome.tsv"))));
        for (String line=br.readLine(); line != null; line=br.readLine()){
            sightingList.add(new Sighting(line.toLowerCase()));
        }
        while (sightingList.size()%256 != 0){
            sightingList.add(new Sighting(""));
        }
        br.close();
        Sighting[] sightings = sightingList.toArray(new Sighting[0]);
        int sightingCount = sightings.length;


        String[] lookups = new String[]{"triangle"};

        IntConsumer lambda =   gid -> {
            for (int i=0; !sightings[gid].matched && i<lookups.length; i++){
                sightings[gid].containsCheck(lookups[i]);
            }



        };

        IntConsumer clear = gid ->    sightings[gid].matched = false;

        long start;
        start = System.currentTimeMillis();
        Device.hsa().forEach(sightingCount, clear);
        System.out.println("hsa1 clear "+(System.currentTimeMillis()-start));

        start = System.currentTimeMillis();
        Device.hsa().forEach(sightingCount, lambda);
        dump("hsa1", sightings);

        start = System.currentTimeMillis();
        Device.hsa().forEach(sightingCount, clear);
        System.out.println("hsa2 clear "+(System.currentTimeMillis()-start));

        start = System.currentTimeMillis();
        Device.hsa().forEach(sightingCount, lambda);
        dump("hsa2 "+(System.currentTimeMillis()-start), sightings);

        start = System.currentTimeMillis();
        Device.hsa().forEach(sightingCount, clear);
        System.out.println("hsa clear "+(System.currentTimeMillis()-start));

        start = System.currentTimeMillis();
        Device.hsa().forEach(sightingCount, lambda);
        dump("hsa3 "+(System.currentTimeMillis()-start), sightings);

        start = System.currentTimeMillis();
        Device.jtp().forEach(sightingCount, clear);
        System.out.println("jtp clear "+(System.currentTimeMillis()-start));

        start = System.currentTimeMillis();
        Device.jtp().forEach(sightingCount, lambda);
        dump("jtp "+(System.currentTimeMillis()-start), sightings);


        start = System.currentTimeMillis();
        Device.seq().forEach(sightingCount, clear);
        System.out.println("seq clear "+(System.currentTimeMillis()-start));

        start = System.currentTimeMillis();
        Device.seq().forEach(sightingCount, lambda);
        dump("seq "+(System.currentTimeMillis()-start), sightings);
    }
}
