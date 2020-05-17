package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;

import java.util.function.IntConsumer;


public class OopArray2DLambda {

    public static class P {
        P next;
        int x;
        int y;
        long l;

        P(int _x, int _y) {
            next = null;
            x = _x;
            y = _y;
            l = 0;
        }

        @Override
        public String toString() {
            return ("(" + x + ", " + y + ")");
        }
    }

    static void dump(String type, P[][] points) {
        System.out.print(type + " ->");
        for (int x = 0; x < points.length; x++) {
            System.out.print("[");

            for (int y = 0; y < points[0].length; y++) {
                if (y != 0) {
                    System.out.print(", ");
                }
                System.out.print(points[x][y]);
            }
            System.out.print("]");
        }
        System.out.println();
    }

    public static void main(String[] args) throws AparapiException {
        final int len = 4; // need this to be final, why is len not effectively final?
        P[][] matrix = new P[len][len];
        for (int x = 0; x < len; x++) {
            for (int y = 0; y < len; y++) {
                matrix[x][y] = new P(0, 0);
            }
        }

        IntConsumer ic = gid -> {
            for (int i = 0; i < len; i++) {
                matrix[gid][i].x = gid;
                matrix[gid][i].y = gid;
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
