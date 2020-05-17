package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;


public class Init {


    static class Person{
        String first;
        String last;
        Person(String _first, String _last){
            first = _first;
            last = _last;
        }


    }

    public static void main(String[] args) throws AparapiException, IOException {

        int N=256000*4;
        Person[] people =new Person[N];
        for (int i=0;i<N; i++){
           people[i] = new Person("one", "two");

        }




        String None="";
        IntConsumer clear = gid ->    {people[gid].first = None;people[gid].last = None;};

        long start;
        start = System.currentTimeMillis();
        Device.hsa().forEach(N, clear);
        System.out.println("hsa1 clear "+(System.currentTimeMillis()-start));

        start = System.currentTimeMillis();
        Device.hsa().forEach(N, clear);
        System.out.println("hsa2 clear "+(System.currentTimeMillis()-start));
        start = System.currentTimeMillis();
        Device.jtp().forEach(N, clear);
        System.out.println("jtp clear "+(System.currentTimeMillis()-start));

        start = System.currentTimeMillis();
        Device.seq().forEach(N, clear);
        System.out.println("seq clear "+(System.currentTimeMillis()-start));

    }
}
