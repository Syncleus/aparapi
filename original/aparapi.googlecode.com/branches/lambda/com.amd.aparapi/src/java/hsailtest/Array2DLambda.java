package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;

import java.util.function.IntConsumer;


public class Array2DLambda {


    public static void dump(String type, int[][] array){
        System.out.print(type+" ->");
        for (int x = 0; x < array.length; x++) {
            System.out.print("[");
            for (int y = 0; y < array[0].length; y++) {
                if (y != 0) {
                    System.out.print(", ");
                }
                System.out.print(array[x][y]);
            }
            System.out.print("]");
        }
        System.out.println();
    }


    public static void main(String[] args) throws AparapiException {
        final int len = 6; // need this to be final, why is len not effectively final?
        int[][] matrix = new int[len][len];

        IntConsumer ic = gid -> {
            for (int i = 0; i < matrix[0].length; i++) {
                matrix[gid][i] = i;
            }
        };
        Device.hsa().forEach(len, ic);
        dump("hsa", matrix);
        Device.jtp().forEach(len, ic);
        dump("jtp", matrix);
        Device.seq().forEach(len, ic);
        dump("seq", matrix);
    }
}
